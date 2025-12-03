package at.bif.swen.paperlessrest.service.impl;


import at.bif.swen.paperlessrest.config.MinIOConfig;
import at.bif.swen.paperlessrest.service.FileStorage;
import io.minio.*;
import io.minio.errors.*;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class FileStorageService implements FileStorage {
    private final MinIOConfig minIOConfig;
    private final MinioClient minioClient;

    @Autowired
    FileStorageService(MinIOConfig minIOConfig, MinioClient minioClient) {
        this.minIOConfig = minIOConfig;
        this.minioClient = minioClient;
    }


    @Override
    public void upload(String filename, byte[] content) {
        try {
            boolean hasBucketWithName =
                    minioClient.bucketExists(
                            BucketExistsArgs
                                    .builder()
                                    .bucket(minIOConfig.getBucketName())
                                    .build()
                    );

            if (!hasBucketWithName) {
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


        } catch (MinioException e) {
            log.error("Error occurred: " + e);
            log.error("Http trace" + e.httpTrace());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String filename) {

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(filename)
                            .build()
            );
        } catch (MinioException e) {
            log.error("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void rename(String oldFilename, String newFilename) {
        try {
            CopySource source = CopySource.builder()
                    .bucket(minIOConfig.getBucketName())
                    .object(oldFilename)
                    .build();


            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(newFilename)
                            .source(source)
                            .build()
            );


            delete(oldFilename);
        } catch (MinioException e) {
            log.error("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] download(String fileName) {
        try {
            return IOUtils.toByteArray(minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(fileName)
                            .build()
            ));
        } catch (ServerException | InvalidResponseException | InsufficientDataException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | ErrorResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }
}


