package at.bif.swen.paperlessrest.service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static at.bif.swen.paperlessrest.config.RabbitConfig.OCR_RESULT_QUEUE;

@Service
@Slf4j
public class OcrResultListener {

    @RabbitListener(queues = OCR_RESULT_QUEUE)
    public void handleOcrResult(OcrResultMessage msg) {
        if (msg.isSuccess()) {
            log.info("OCR-Ergebnis empfangen für documentId {}: {}",
                    msg.getDocumentId(), msg.getText());
        } else {
            log.warn("OCR fehlgeschlagen für documentId {}: {}",
                    msg.getDocumentId(), msg.getErrorMessage());
        }

        // TODO: auf DB migrieren
    }
}