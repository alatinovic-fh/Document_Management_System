package at.bif.swen.paperlessrest.service.impl;

import at.bif.swen.paperlessrest.controller.request.CreateDocRequest;
import at.bif.swen.paperlessrest.controller.request.UpdateDocRequest;
import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.entity.Image;
import at.bif.swen.paperlessrest.persistence.entity.DocumentSearchDto;
import at.bif.swen.paperlessrest.persistence.repository.DocRepository;
import at.bif.swen.paperlessrest.service.DocService;
import at.bif.swen.paperlessrest.service.exception.DuplicateDocumentNameException;
import at.bif.swen.paperlessrest.service.exception.NotFoundException;
import at.bif.swen.paperlessrest.service.messaging.OcrJobPublisher;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.sql.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocDetailService implements DocService {
    private final DocRepository docRepository;
    private final OcrJobPublisher ocrJobPublisher;
    private final FileStorageService fileStorageService;
    private final ElasticsearchClient elasticsearchClient;
    private final ImageExtractionDetailService imageExtractionDetailService;
    private final ElasticsearchService elasticsearchService;

    @SneakyThrows
    @Transactional
    public Document create(Document document, byte[] content) {

        if (docRepository.existsByOriginalFilename(document.getOriginalFilename())) {
            throw new DuplicateDocumentNameException(document.getOriginalFilename());
        }

        Document saved = docRepository.save(document);

        fileStorageService.upload(saved.getOriginalFilename(), content);

        List<Image> images = imageExtractionDetailService.extractAndStore(content, saved);

        saved.getImages().addAll(images);

        ocrJobPublisher.sendOcrJob(saved, content);

        return docRepository.save(saved);
    }

    public Document get(long id) {
        return docRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    public List<Document> list() {
        return docRepository.findAll();
    }

    @Transactional
    public Document update(long id, Document updateDocument) {

        if (docRepository.existsByOriginalFilename(updateDocument.getOriginalFilename())) {
            log.info("Document with name {} already exists. Updating...", updateDocument.getOriginalFilename());
            throw new DuplicateDocumentNameException(updateDocument.getOriginalFilename());

        }

        Document toUpdate = this.get(id);

        log.info("Updated document: {}", toUpdate);

        fileStorageService.rename(toUpdate.getOriginalFilename(), updateDocument.getOriginalFilename());
        toUpdate.setOriginalFilename(updateDocument.getOriginalFilename());

        Document updated = docRepository.save(toUpdate);
        elasticsearchService.updateDocumentTitle(id, updated.getOriginalFilename());
        return updated;
    }

    @Transactional
    public void delete(long id) {
        if (!docRepository.existsById(id)) {
            throw new NotFoundException("Document " + id + " not found");
        }
        String filename = docRepository.findById(id).get().getOriginalFilename();
        fileStorageService.delete(filename);

        // delete images form minio
        List<Image> images = docRepository.findById(id).get().getImages();
        for (Image image : images) {
            fileStorageService.delete(image.getObjectKey());
        }

        docRepository.deleteById(id);
        elasticsearchService.deleteDocument(id);

        log.info("Document {} deleted from database and search index", id);

    }

    public List<Long> searchDocuments(String searchTerm) {
        return elasticsearchService.searchDocuments(searchTerm);
    }

    public byte[] download(long id) {
        Document doc = docRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found: " + id));
        return fileStorageService.download(doc.getOriginalFilename());
    }
}
