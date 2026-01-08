package at.bif.swen.paperlessrest.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String objectKey;

    private String contentType;
    private long size;

    @ManyToOne(fetch = FetchType.LAZY)
    private Document document;
}
