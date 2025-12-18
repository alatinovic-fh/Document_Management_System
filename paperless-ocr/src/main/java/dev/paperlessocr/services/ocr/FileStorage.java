package dev.paperlessocr.services.ocr;

import java.io.InputStream;

public interface FileStorage {
    InputStream getFile(String fileName);
}
