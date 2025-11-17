package dev.paperlessocr.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String OCR_EXCHANGE = "ocr.exchange";

    public static final String OCR_JOB_QUEUE = "ocr.queue";
    public static final String OCR_RESULT_QUEUE = "ocr.result.queue";

    public static final String OCR_JOB_ROUTING_KEY = "ocr.job";
    public static final String OCR_RESULT_ROUTING_KEY = "ocr.result";

    @Bean
    public DirectExchange ocrExchange() {
        return new DirectExchange(OCR_EXCHANGE);
    }

    @Bean
    public Queue ocrJobQueue() {
        return new Queue(OCR_JOB_QUEUE, true);
    }

    @Bean
    public Queue ocrResultQueue() {
        return new Queue(OCR_RESULT_QUEUE, true);
    }

    @Bean
    public Binding ocrJobBinding(Queue ocrJobQueue, DirectExchange ocrExchange) {
        return BindingBuilder.bind(ocrJobQueue)
                .to(ocrExchange)
                .with(OCR_JOB_ROUTING_KEY);
    }

    @Bean
    public Binding ocrResultBinding(Queue ocrResultQueue, DirectExchange ocrExchange) {
        return BindingBuilder.bind(ocrResultQueue)
                .to(ocrExchange)
                .with(OCR_RESULT_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        return template;
    }
}