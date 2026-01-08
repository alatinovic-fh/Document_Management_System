package at.bif.swen.paperlessrest.service;

import java.util.List;

public interface Elasticsearch {

    boolean deleteDocument(long documentId);
    List<Long> searchDocuments(String searchTerm);
}
