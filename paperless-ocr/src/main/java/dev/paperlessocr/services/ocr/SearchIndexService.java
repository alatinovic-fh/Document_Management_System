package dev.paperlessocr.services.ocr;

import dev.paperlessocr.messaging.Document;

import co.elastic.clients.elasticsearch._types.Result;
import java.util.Optional;

public interface SearchIndexService {

    Result indexDocument(Document document) throws Exception;
    Optional<Document> getDocumentById(long id);
    boolean deleteDocumentById(long id);
}
