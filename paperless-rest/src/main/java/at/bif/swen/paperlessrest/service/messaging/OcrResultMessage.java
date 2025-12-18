package at.bif.swen.paperlessrest.service.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrResultMessage {
    private Long documentId;
    private String text;          // erkannter Text (jetzt hardcodiert)
    private boolean success;      // true/false
    private String errorMessage; // falls success=false
    private String summary;
}
