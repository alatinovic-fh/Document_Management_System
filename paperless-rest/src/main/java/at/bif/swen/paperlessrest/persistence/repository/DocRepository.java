package at.bif.swen.paperlessrest.persistence.repository;

import at.bif.swen.paperlessrest.persistence.entity.Document;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocRepository extends JpaRepository<Document, Long> {
    boolean existsByOriginalFilename(String filename);


    @Modifying
    @Transactional
    @Query("""
        UPDATE Document d
        SET d.summary = :summary
        WHERE d.id = :id
    """)
    void updateSummaryById(
            @Param("id") Long id,
            @Param("summary") String summary
    );
}
