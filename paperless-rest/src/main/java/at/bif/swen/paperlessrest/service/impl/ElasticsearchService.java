package at.bif.swen.paperlessrest.service.impl;

import at.bif.swen.paperlessrest.persistence.entity.DocumentSearchDto;
import at.bif.swen.paperlessrest.service.Elasticsearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;
    private static final String INDEX_NAME = "documents";

    public List<Long> searchDocuments(String searchTerm) {
        try {
            log.info("Searching Elasticsearch for: '{}'", searchTerm);

            SearchResponse<DocumentSearchDto> response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("originalFilename", "content")
                                            .query(searchTerm)
                                            .fuzziness("AUTO")
                                    )
                            )
                            .source(src -> src.filter(f -> f.includes("id"))),
                    DocumentSearchDto.class
            );

            List<Long> ids = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(java.util.Objects::nonNull)
                    .map(DocumentSearchDto::getId)
                    .collect(Collectors.toList());

            log.info("Found {} documents matching '{}'", ids.size(), searchTerm);
            return ids;

        } catch (IOException e) {
            log.error("Error searching documents in Elasticsearch", e);
            return List.of();
        }
    }

    public boolean deleteDocument(Long documentId) {
        try {
            DeleteResponse response = elasticsearchClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(String.valueOf(documentId))
            );

            boolean deleted = response.result() == Result.Deleted;
            if (deleted) {
                log.info("Document {} deleted from Elasticsearch", documentId);
            } else {
                log.warn("Document {} not found in Elasticsearch", documentId);
            }
            return deleted;

        } catch (Exception e) {
            log.error("Failed to delete document {} from Elasticsearch: {}", documentId, e.getMessage());
            return false;
        }
    }

    public boolean updateDocumentTitle(Long documentId, String newTitle) {
        try {
            UpdateResponse<DocumentSearchDto> response = elasticsearchClient.update(u -> u
                            .index(INDEX_NAME)
                            .id(String.valueOf(documentId))
                            .doc(Map.of("originalFilename", newTitle)),
                    DocumentSearchDto.class
            );

            boolean updated = response.result() == Result.Updated;
            if (updated) {
                log.info("Document {} title updated in Elasticsearch to '{}'", documentId, newTitle);
            } else {
                log.warn("Document {} not found in Elasticsearch for update", documentId);
            }
            return updated;

        } catch (Exception e) {
            log.error("Failed to update document {} title in Elasticsearch: {}", documentId, e.getMessage());
            return false;
        }
    }
}
