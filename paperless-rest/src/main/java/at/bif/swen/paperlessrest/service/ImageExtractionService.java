package at.bif.swen.paperlessrest.service;

import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.entity.Image;

import java.io.IOException;
import java.util.List;

public interface ImageExtractionService {

    List<Image>  extractAndStore(byte[] pdfBytes, Document document) throws IOException;
}
