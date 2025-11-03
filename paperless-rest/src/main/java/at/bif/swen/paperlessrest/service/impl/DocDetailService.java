package at.bif.swen.paperlessrest.service.impl;

import at.bif.swen.paperlessrest.controller.request.CreateDocRequest;
import at.bif.swen.paperlessrest.controller.request.UpdateDocRequest;
import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.repository.DocRepository;
import at.bif.swen.paperlessrest.service.DocService;
import at.bif.swen.paperlessrest.service.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.sql.Date;


@Service
@RequiredArgsConstructor
public class DocDetailService implements DocService {
    private final DocRepository docRepository;

    @Transactional
    public Document create(Document document) {
        return docRepository.save(document);
    }

    public Document get(long id) {
        return docRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    public List<Document> list() { return docRepository.findAll(); }

    @Transactional
    public Document update(long id, Document updateDocument) {
        Document toUpdate = this.get(id);
        toUpdate.setOriginalFilename(updateDocument.getOriginalFilename());
        return docRepository.save(toUpdate);
    }

    @Transactional
    public void delete(long id) {
        if (!docRepository.existsById(id)) {
            throw new NotFoundException("Document " + id + " not found");
        }
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
}

