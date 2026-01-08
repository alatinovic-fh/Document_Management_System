package dev.paperlessocr.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultMessage {
    private Long documentId;
    private String text;
    private boolean success;      // true/false
    private String errorMessage;  // falls success=fa´lse
    private String summary; //GenAI summary
}
