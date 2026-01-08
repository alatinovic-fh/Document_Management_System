package at.bif.swen.paperlessrest.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSearchDto {
    private Long id;
    private String originalFilename;
    private String content;

}