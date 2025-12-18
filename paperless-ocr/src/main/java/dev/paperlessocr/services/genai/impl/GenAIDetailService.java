package dev.paperlessocr.services.genai.impl;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import dev.paperlessocr.config.GenAIConfig;
import dev.paperlessocr.services.genai.GenAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenAIDetailService implements GenAIService {

    private final String apiKey;
    private final String model;

    public GenAIDetailService(GenAIConfig genAIConfig) {
        this.apiKey = genAIConfig.getApiKey();
        this.model = genAIConfig.getModel();
    }

    @Override
    public String createSummary(String content) {
        String prompt = String.format(SUMMARY_PROMPT, content);

        log.info("Sending summary request to gemini");
        Client client = new Client.Builder().apiKey(this.apiKey).build();

        GenerateContentResponse response =
                client.models.generateContent(
                        this.model,
                        prompt,
                        null);

        return response.text();
    }
}
