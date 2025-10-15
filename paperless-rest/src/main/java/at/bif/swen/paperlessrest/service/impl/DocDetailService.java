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

@Service
@RequiredArgsConstructor
public class DocDetailService implements DocService {
    private final DocRepository docRepository;

    @Transactional
    public Document create(CreateDocRequest req) {
        Document d = new Document();
        d.setOriginalFilename(req.getOriginalFilename());
        d.setContentType(req.getContentType());
        d.setSize(req.getSize());
        return docRepository.save(d);
    }

    public Document get(long id) {
        return docRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    public List<Document> list() { return docRepository.findAll(); }

    @Transactional
    public Document update(long id, UpdateDocRequest req) {
        Document d = get(id);
        d.setOriginalFilename(req.getOriginalFilename());
        return docRepository.save(d);
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

