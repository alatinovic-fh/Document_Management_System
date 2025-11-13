package dev.paperlessocr.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static dev.paperlessocr.config.RabbitConfig.OCR_JOB_QUEUE;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrJobListener {

    private final OcrResultPublisher resultPublisher;

    @RabbitListener(queues = OCR_JOB_QUEUE)
    public void handleOcrJob(OcrJobMessage msg) {

        log.info("📥 OCR-Job empfangen: documentId={}, filename={}, bytes={}",
                msg.getDocumentId(),
                msg.getOriginalFilename(),
                msg.getContent() != null ? msg.getContent().length : 0
        );

        // "Fake"-OCR: hartkodiertes Ergebnis erzeugen
        String fakeText = "Dies ist ein hartcodiertes OCR-Ergebnis für Dokument "
                + msg.getDocumentId() + " (" + msg.getOriginalFilename() + ")";

        OcrResultMessage result = new OcrResultMessage(
                msg.getDocumentId(),
                fakeText,
                true,
                null
        );

        // Ergebnis zurück an RESULT_QUEUE schicken
        resultPublisher.sendResult(result);

        log.info("📤 OCR-Resultat versendet für documentId={}", msg.getDocumentId());
    }
}
