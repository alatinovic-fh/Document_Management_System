package at.bif.swen.paperlessrest.service.messaging;

import at.bif.swen.paperlessrest.config.RabbitConfig;
import at.bif.swen.paperlessrest.persistence.entity.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrJobPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendOcrJob(Document doc, byte[] content) {
        OcrJobMessage msg = new OcrJobMessage(
                doc.getId(),
                doc.getOriginalFilename(),
                doc.getContentType(),
                doc.getSize(),
                content
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.OCR_EXCHANGE,
                RabbitConfig.OCR_JOB_ROUTING_KEY,
                msg
        );

        log.info("OCR-Job gesendet für Dokument " + doc.getId());
    }
}
