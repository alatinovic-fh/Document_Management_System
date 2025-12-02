package dev.paperlessocr.bl;

import java.io.InputStream;

public interface FileStorage {
    InputStream getFile(String fileName);
}
