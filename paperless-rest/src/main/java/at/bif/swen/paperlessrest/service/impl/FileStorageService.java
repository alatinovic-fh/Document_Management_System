package at.bif.swen.paperlessrest.service.impl;


import at.bif.swen.paperlessrest.config.MinIOConfig;
import at.bif.swen.paperlessrest.service.FileStorage;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileStorageService implements FileStorage {
    private final MinIOConfig minIOConfig;
    private final MinioClient minioClient;

    @Autowired
    FileStorageService(MinIOConfig minIOConfig, MinioClient minioClient) {
        this.minIOConfig = minIOConfig;
        this.minioClient = minioClient;
    }


    @Override
    public void upload(String filename, byte[] content ) {
        try{
            boolean hasBucketWithName =
                    minioClient.bucketExists(
                            BucketExistsArgs
                                    .builder()
                                    .bucket(minIOConfig.getBucketName())
                                    .build()
                    );

            if (!hasBucketWithName){
                minioClient.makeBucket(
                        MakeBucketArgs
                                .builder()
                                .bucket(minIOConfig.getBucketName())
                                .build()
                );
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(filename)
                            .stream(new ByteArrayInputStream(content), content.length, -1)
                            .build()
            );

        }catch(MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("Http trace" + e.httpTrace());
        }catch(IOException | NoSuchAlgorithmException | InvalidKeyException e){
            throw new RuntimeException(e);
        }
    }


    public byte[] download(String filename) {
        return new byte[0];
    }
}
