package dev.paperlessocr.messaging;

import dev.paperlessocr.services.genai.GenAIService;
import dev.paperlessocr.services.ocr.impl.FileStorageService;
import dev.paperlessocr.services.ocr.impl.TesseractOcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

import static dev.paperlessocr.config.RabbitConfig.OCR_JOB_QUEUE;

@Service
@Slf4j
@RequiredArgsConstructor
public class OcrJobListener {

    private final OcrResultPublisher resultPublisher;
    private final FileStorageService fileStorageService;
    private final TesseractOcrService tesseractOcrService;
    private final GenAIService genAIService;

    @RabbitListener(queues = OCR_JOB_QUEUE)
    public void handleOcrJob(OcrJobMessage msg) {

        log.info("OCR-Job empfangen: documentId={}, filename={}, bytes={}",
                msg.getDocumentId(),
                msg.getOriginalFilename(),
                msg.getContent() != null ? msg.getContent().length : 0
        );

        try{
            InputStream is = fileStorageService.getFile(msg.getOriginalFilename());
            log.info("Datei gefunden: {}", msg.getOriginalFilename());

            File tempFile = tesseractOcrService.createTempFile(msg.getOriginalFilename(), is);

            String text = tesseractOcrService.doOcr(tempFile);
            log.info("OCR-Ergebnis: {}", text);
            String aiSummary = genAIService.createSummary(text);
            log.info("Summary: {}", aiSummary);
            OcrResultMessage result = new OcrResultMessage(msg.getDocumentId(), text, true, null, aiSummary);
            resultPublisher.sendResult(result);
        }catch (Exception e){
            log.error("Fehler beim OCR-Vorgang: {}", e.getMessage());
            OcrResultMessage result = new OcrResultMessage(msg.getDocumentId(), null, false, e.getMessage(), null);
            resultPublisher.sendResult(result);


        }


    }
}
