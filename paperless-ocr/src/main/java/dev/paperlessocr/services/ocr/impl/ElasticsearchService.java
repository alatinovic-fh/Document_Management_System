package dev.paperlessocr.services.ocr.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import dev.paperlessocr.config.ElasticsearchConfig;
import dev.paperlessocr.messaging.Document;
import dev.paperlessocr.services.ocr.SearchIndexService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import co.elastic.clients.elasticsearch._types.Result;

import java.io.IOException;
import java.util.Optional;




@Component
@Slf4j
public class ElasticsearchService implements SearchIndexService {
    private final ElasticsearchClient esClient;

    @Autowired
    public ElasticsearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }


    @PostConstruct
    public void init() {
        try {
            boolean exists = esClient.indices()
                    .exists(i -> i.index(ElasticsearchConfig.DOCUMENTS_INDEX_NAME))
                    .value();

            if (!exists) {
                esClient.indices()
                        .create(c -> c.index(ElasticsearchConfig.DOCUMENTS_INDEX_NAME));

            } else {
                log.info("Elasticsearch index already exists");
            }
        } catch (Exception e) {
            log.info("Elasticsearch index doesnt work exists");
        }
    }

    @Override
    public Result indexDocument(Document document) throws IOException {

        IndexResponse response = esClient.index(i -> i
                .index(ElasticsearchConfig.DOCUMENTS_INDEX_NAME)
                .id(String.valueOf(document.getId()))
                .document(document)
        );


        if (response.result() == Result.Created || response.result() == Result.Updated) {
            log.info("Document {} indexed successfully ({})", document.getId(), response.result());
        } else {
            log.warn("Unexpected indexing result for document {}: {}", document.getId(), response.result());
        }
        return response.result();
    }

    @Override
    public Optional<Document> getDocumentById(long id) {
        try {
            GetResponse<Document> response = esClient.get(g -> g
                            .index(ElasticsearchConfig.DOCUMENTS_INDEX_NAME)
                            .id(String.valueOf(id)),
                    Document.class
            );
            return (response.found() && response.source() != null) ? Optional.of(response.source()) : Optional.empty();
        } catch (IOException e) {
            log.info("oh index already exists");
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteDocumentById(long id) {
        try {
            DeleteResponse result = esClient.delete(d -> d
                    .index(ElasticsearchConfig.DOCUMENTS_INDEX_NAME)
                    .id(String.valueOf(id))
            );
            return result.result() == Result.Deleted;
        } catch (IOException e) {
            log.info("doest work index already exists");
            return false;
        }
    }



}
