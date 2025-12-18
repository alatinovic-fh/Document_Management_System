package dev.paperlessocr.services.ocr;
import java.io.File;

public interface OcrService {
    String doOcr(File tempFile) throws Exception;
}
