package dev.paperlessocr.bl.ocr;

import dev.paperlessocr.bl.OcrService;
import dev.paperlessocr.bl.TesseractFactory;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class TesseractOcrService implements OcrService {
    private final TesseractFactory tesseractFactory;

    @Autowired
    public TesseractOcrService(TesseractFactory tesseractFactory) {
        this.tesseractFactory = tesseractFactory;
    }

    @Override
    public String doOcr(File tempFile) throws Exception {
        Tesseract tesseract = tesseractFactory.create();
        return tesseract.doOCR(tempFile);
    }

    public File createTempFile(String storagePath, InputStream is) throws IOException {
        File tempFile = File.createTempFile(StringUtils.getFilename(storagePath), "." + StringUtils.getFilenameExtension(storagePath));
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

}
