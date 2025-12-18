package dev.paperlessocr.services.ocr.impl;

import dev.paperlessocr.services.ocr.FileStorage;
import dev.paperlessocr.config.MinIOConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.InputStream;


@Service
public class FileStorageService  implements FileStorage {
    private final MinIOConfig minIOConfig;
    private final MinioClient minioClient;


    @Autowired
    public FileStorageService(MinIOConfig minIOConfig, MinioClient minioClient) {
        this.minIOConfig = minIOConfig;
        this.minioClient = minioClient;
    }

    public InputStream getFile (String filename){
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(filename)
                            .build()
            );
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
