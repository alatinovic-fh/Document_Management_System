package dev.paperlessocr.services.ocr;

import dev.paperlessocr.messaging.OcrJobListener;
import dev.paperlessocr.messaging.OcrJobMessage;
import dev.paperlessocr.messaging.OcrResultMessage;
import dev.paperlessocr.messaging.OcrResultPublisher;
import dev.paperlessocr.services.genai.impl.GenAIDetailService;
import dev.paperlessocr.services.ocr.impl.FileStorageService;
import dev.paperlessocr.services.ocr.impl.TesseractOcrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OcrJobListenerTest {
    /**
    OcrResultPublisher resultPublisher;
    FileStorageService fileStorageService;
    TesseractOcrService tesseractOcrService;
    GenAIDetailService genAIDetailService;
    OcrJobListener listener;

    @BeforeEach
    void setup() {
        resultPublisher = mock(OcrResultPublisher.class);
        fileStorageService = mock(FileStorageService.class);
        tesseractOcrService = mock(TesseractOcrService.class);
        genAIDetailService = mock(GenAIDetailService.class);

        listener = new OcrJobListener(resultPublisher, fileStorageService, tesseractOcrService, genAIDetailService);
    }

    @Test
    void handleOcrJob_success_sendsResultWithText() throws Exception {
        byte[] content = "dummy".getBytes();
        InputStream is = new ByteArrayInputStream(content);
        when(fileStorageService.getFile("doc.pdf")).thenReturn(is);

        File tempFile = File.createTempFile("doc", ".pdf");
        tempFile.deleteOnExit();
        when(tesseractOcrService.createTempFile("doc.pdf", is)).thenReturn(tempFile);
        when(tesseractOcrService.doOcr(tempFile)).thenReturn("recognized text");

        OcrJobMessage msg = new OcrJobMessage(123L, "doc.pdf", "application/pdf", content.length, content);
        listener.handleOcrJob(msg);

        ArgumentCaptor<OcrResultMessage> captor = ArgumentCaptor.forClass(OcrResultMessage.class);
        verify(resultPublisher, times(1)).sendResult(captor.capture());
        OcrResultMessage result = captor.getValue();

        assertEquals(123L, result.getDocumentId());
        assertTrue(result.isSuccess());
        assertEquals("recognized text", result.getText());
        assertNull(result.getErrorMessage());
    }

    @Test
    void handleOcrJob_error_sendsFailureResult() throws Exception {
        when(fileStorageService.getFile("doc.pdf")).thenThrow(new RuntimeException("minio down"));

        byte[] content = "dummy".getBytes();
        OcrJobMessage msg = new OcrJobMessage(456L, "doc.pdf", "application/pdf", content.length, content);
        listener.handleOcrJob(msg);

        ArgumentCaptor<OcrResultMessage> captor = ArgumentCaptor.forClass(OcrResultMessage.class);
        verify(resultPublisher, times(1)).sendResult(captor.capture());
        OcrResultMessage result = captor.getValue();

        assertEquals(456L, result.getDocumentId());
        assertFalse(result.isSuccess());
        assertNull(result.getText());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("minio down"));
    }
    **/
}
