package dev.paperlessocr.services.ocr;

import dev.paperlessocr.services.TesseractFactory;
import dev.paperlessocr.services.ocr.impl.TesseractOcrService;
import net.sourceforge.tess4j.Tesseract;
import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TesseractOcrServiceTest {


    @Test
    void createTempFile_writesContentsAndReturnsFile() throws Exception {
        TesseractFactory dummyFactory = mock(TesseractFactory.class);
        TesseractOcrService ocr = new TesseractOcrService(dummyFactory);

        String content = "This is a test PDF content (fake).";
        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        File temp = ocr.createTempFile("some/folder/test.pdf", is);
        assertNotNull(temp);
        assertTrue(temp.exists());
        assertTrue(temp.length() > 0);

        String read = StreamUtils.copyToString(new java.io.FileInputStream(temp), StandardCharsets.UTF_8);
        assertTrue(read.contains("This is a test"));


        temp.delete();
    }

    @Test
    void doOcr_callsTesseract_doOCR() throws Exception {
        TesseractFactory factory = mock(TesseractFactory.class);
        Tesseract tesseract = mock(Tesseract.class);
        when(factory.create()).thenReturn(tesseract);

        when(tesseract.doOCR(any(File.class))).thenReturn("recognized text from mocked tess4j");

        TesseractOcrService ocrService = new TesseractOcrService(factory);

        File tempFile = File.createTempFile("unit-test", ".txt");
        tempFile.deleteOnExit();


        String result = ocrService.doOcr(tempFile);

        assertNotNull(result);
        assertEquals("recognized text from mocked tess4j", result);

        verify(factory, times(1)).create();
        verify(tesseract, times(1)).doOCR(tempFile);
    }




}
