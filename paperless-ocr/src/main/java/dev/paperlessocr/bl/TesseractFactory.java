package dev.paperlessocr.bl;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TesseractFactory {
    private final String tessDataPath;
    private final String language;

    public TesseractFactory(@Value("${tesseract.data}") String tessDataPath,
                            @Value("${tesseract.lang}") String language) {
        this.tessDataPath = tessDataPath;
        this.language = language;
    }


    public Tesseract create() {
        Tesseract t = new Tesseract();
        t.setDatapath(tessDataPath);
        t.setLanguage(language);
        return t;
    }
}
