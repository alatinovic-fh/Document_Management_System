package dev.paperlessocr.messaging;

import dev.paperlessocr.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OcrResultPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendResult(OcrResultMessage result) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.OCR_EXCHANGE,
                RabbitConfig.OCR_RESULT_ROUTING_KEY,
                result
        );
    }
}
