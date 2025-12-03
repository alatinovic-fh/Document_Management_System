package at.bif.swen.paperlessrest.service.impl;

import at.bif.swen.paperlessrest.controller.request.CreateDocRequest;
import at.bif.swen.paperlessrest.controller.request.UpdateDocRequest;
import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.repository.DocRepository;
import at.bif.swen.paperlessrest.service.DocService;
import at.bif.swen.paperlessrest.service.exception.DuplicateDocumentNameException;
import at.bif.swen.paperlessrest.service.exception.NotFoundException;
import at.bif.swen.paperlessrest.service.messaging.OcrJobPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.sql.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocDetailService implements DocService {
    private final DocRepository docRepository;
    private final OcrJobPublisher ocrJobPublisher;
    private final FileStorageService fileStorageService;

    @Transactional
    public Document create(Document document, byte[] content) {

        if(docRepository.existsByOriginalFilename(document.getOriginalFilename())) {
            throw new DuplicateDocumentNameException(document.getOriginalFilename());
        }

        Document saved = docRepository.save(document);

        fileStorageService.upload(saved.getOriginalFilename(), content);
        System.out.println(saved.getOriginalFilename());
        ocrJobPublisher.sendOcrJob(saved, content);

        return saved;
    }

    public Document get(long id) {
        return docRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    public List<Document> list() { return docRepository.findAll(); }

    @Transactional
    public Document update(long id, Document updateDocument) {

        if(docRepository.existsByOriginalFilename(updateDocument.getOriginalFilename())) {
            log.info("Document with name {} already exists. Updating...", updateDocument.getOriginalFilename());
            throw new DuplicateDocumentNameException(updateDocument.getOriginalFilename());

        }

        Document toUpdate = this.get(id);

        log.info("Updated document: {}", toUpdate);

        fileStorageService.rename(toUpdate.getOriginalFilename(), updateDocument.getOriginalFilename());
        toUpdate.setOriginalFilename(updateDocument.getOriginalFilename());


        return docRepository.save(toUpdate);
    }

    @Transactional
    public void delete(long id) {
        if (!docRepository.existsById(id)) {
            throw new NotFoundException("Document " + id + " not found");
        }
        String filename = docRepository.findById(id).get().getOriginalFilename();
        fileStorageService.delete(filename);
        docRepository.deleteById(id);

    }

    /**
     * DUMMY METHOD WILL BE IMPLEMENTED IN FUTURE SPRINTS
     * ElasticSearch etc.
     *
     * @param text
     * @return
     */
    public List<Document> search(String text) {
        return null;
    }

    public byte[] download(long id) {
        Document doc = docRepository.findById(id).orElseThrow(() -> new NotFoundException("Document not found: " + id));
        return fileStorageService.download(doc.getOriginalFilename());
    }
}

