package at.bif.swen.paperlessrest.persistence.repository;

import at.bif.swen.paperlessrest.persistence.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocRepository extends JpaRepository<Document, Long> {
    boolean existsByOriginalFilename(String filename);
}
