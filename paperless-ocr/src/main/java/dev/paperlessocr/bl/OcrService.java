package dev.paperlessocr.bl;
import java.io.File;

public interface OcrService {
    String doOcr(File tempFile) throws Exception;
}
