package at.bif.swen.paperlessrest.persistence.repository;

import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByDocumentId(Long documentId);

}
