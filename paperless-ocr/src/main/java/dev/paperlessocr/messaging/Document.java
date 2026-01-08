package dev.paperlessocr.messaging;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private long id;
    private String originalFilename;
    private String summary;
    private String content;
}
