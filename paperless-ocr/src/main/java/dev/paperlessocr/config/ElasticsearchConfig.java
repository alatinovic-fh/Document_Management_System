package dev.paperlessocr.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class ElasticsearchConfig {

    public static final String DOCUMENTS_INDEX_NAME = "documents";

    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private int port;

    @Bean
    public org.elasticsearch.client.RestClient getRestClient() {
        return org.elasticsearch.client.RestClient.builder(
                new HttpHost(host, port)).build();
    }

    @Bean
    public ElasticsearchTransport getElasticsearchTransport() {
        JacksonJsonpMapper mapper = new JacksonJsonpMapper();
        mapper.objectMapper().registerModule(new JavaTimeModule());
        return new RestClientTransport(
                getRestClient(), mapper);
    }


    @Bean
    public ElasticsearchClient getElasticsearchClient(){
        return new ElasticsearchClient(getElasticsearchTransport());
    }






}
