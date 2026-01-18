package at.bif.swen.paperlessbatch.repository;

import at.bif.swen.paperlessbatch.entity.AccessStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccessStatisticsRepository extends JpaRepository<AccessStatistics, Long> {
    Optional<AccessStatistics> findBySourceSystemIdAndDocumentIdAndProcessingDate(
            String sourceSystemId, Long documentId, LocalDateTime processingDate);
}
