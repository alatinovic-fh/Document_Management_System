package dev.paperlessocr.bl.ocr;

import dev.paperlessocr.config.MinIOConfig;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    MinIOConfig minIOConfig;
    MinioClient minioClient;
    FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        minIOConfig = mock(MinIOConfig.class);
        when(minIOConfig.getBucketName()).thenReturn("test-bucket");

        minioClient = mock(MinioClient.class);

        fileStorageService = new FileStorageService(minIOConfig, minioClient);
    }

    @Test
    void getFile_returnsStream_whenMinioReturns() throws Exception {
        byte[] bytes = "hello world".getBytes();


        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);

        when(getObjectResponse.readAllBytes()).thenReturn(bytes);

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        InputStream result = fileStorageService.getFile("file.pdf");
        assertNotNull(result);

        byte[] read = result.readAllBytes();
        assertArrayEquals(bytes, read);


        ArgumentCaptor<GetObjectArgs> captor = ArgumentCaptor.forClass(GetObjectArgs.class);
        verify(minioClient, times(1)).getObject(captor.capture());
        GetObjectArgs args = captor.getValue();
        assertEquals("test-bucket", args.bucket());
        assertEquals("file.pdf", args.object());
    }

    @Test
    void getFile_throwsRuntimeException_whenMinioThrows() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new RuntimeException("minio failure"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> fileStorageService.getFile("x"));
        assertTrue(ex.getMessage().contains("minio failure"));
    }
}
