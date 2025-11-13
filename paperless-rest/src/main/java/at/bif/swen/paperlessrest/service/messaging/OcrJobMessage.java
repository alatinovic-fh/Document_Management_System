package at.bif.swen.paperlessrest.service.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrJobMessage {
    private Long documentId;
    private String originalFilename;
    private String contentType;
    private long size;
    private byte[] content;
}
