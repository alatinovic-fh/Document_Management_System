package dev.paperlessocr.config;

import dev.paperlessocr.services.genai.GenAIService;
import dev.paperlessocr.services.genai.impl.GenAIDetailService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GenAIConfig {
    @Value( "${gemini.apikey}")
    private String apiKey;
    @Value("${gemini.model}")
    private String model;
}
