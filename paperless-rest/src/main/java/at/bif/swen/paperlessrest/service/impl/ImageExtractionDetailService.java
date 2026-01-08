package at.bif.swen.paperlessrest.service.impl;

import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.entity.Image;
import at.bif.swen.paperlessrest.service.FileStorage;
import at.bif.swen.paperlessrest.service.ImageExtractionService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageExtractionDetailService implements ImageExtractionService {


    private final FileStorage fileStorageService;

    public List<Image> extractAndStore(byte[] pdfBytes, Document document) throws IOException {

        List<Image> images = new ArrayList<>();

        try (PDDocument pdf = PDDocument.load(pdfBytes)) {

            for (PDPage page : pdf.getPages()) {

                PDResources resources = page.getResources();
                if (resources == null) continue;

                for (COSName name : resources.getXObjectNames()) {

                    PDXObject xObject = resources.getXObject(name);

                    if (xObject instanceof PDImageXObject image) {

                        String format = image.getSuffix();
                        if (format == null) {
                            format = "png";
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image.getImage(), format, baos);
                        byte[] bytes = baos.toByteArray();

                        String objectKey = String.format(
                                "%s%s.%s",
                                document.getOriginalFilename(),
                                java.util.UUID.randomUUID(),
                                format
                        );

                        fileStorageService.upload(objectKey, bytes);

                        Image documentImage = new Image();
                        documentImage.setObjectKey(objectKey);
                        documentImage.setContentType("image/" + format);
                        documentImage.setSize(bytes.length);
                        documentImage.setDocument(document);

                        images.add(documentImage);
                    }
                }
            }
        }

        return images;
    }

}
