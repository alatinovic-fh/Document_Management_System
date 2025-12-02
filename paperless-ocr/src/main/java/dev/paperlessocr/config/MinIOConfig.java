package dev.paperlessocr.config;

import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {
    @Getter
    @Value("${minio.bucket}")
    String bucketName;
    @Value("${minio.access.name}")
    String accessName;
    @Value("${minio.access.secret}")
    String accessSecret;
    @Value("${minio.endpoint.host}")
    String minioEndpoint;
    @Value("${minio.endpoint.port}")
    int minioPort;


    @Bean
    public MinioClient generateMinioClient(){
        try{
            return MinioClient.builder()
                    .endpoint(minioEndpoint, minioPort, false)
                    .credentials(accessName, accessSecret)
                    .build();
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());

        }
    }




}

