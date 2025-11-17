package at.bif.swen.paperlessrest.service;

import at.bif.swen.paperlessrest.controller.request.CreateDocRequest;
import at.bif.swen.paperlessrest.controller.request.UpdateDocRequest;
import at.bif.swen.paperlessrest.persistence.entity.Document;

import java.util.List;

public interface DocService {

    Document create(Document req, byte[] content);

    Document get(long id);

    List<Document> search(String title);

    Document update (long id, Document req);

    void delete(long id);
}
