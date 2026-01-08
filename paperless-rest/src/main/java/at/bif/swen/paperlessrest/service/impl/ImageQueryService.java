package at.bif.swen.paperlessrest.service.impl;

import at.bif.swen.paperlessrest.controller.dto.ImageResponse;
import at.bif.swen.paperlessrest.persistence.entity.Image;
import at.bif.swen.paperlessrest.persistence.repository.ImageRepository;
import at.bif.swen.paperlessrest.service.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageQueryService {

    private final ImageRepository imageRepository;
    private final FileStorage fileStorage;

    public ImageResponse loadImage(Long imageId) {

        Image img = imageRepository.findById(imageId).orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

        byte[] bytes = fileStorage.download(img.getObjectKey());

        return new ImageResponse(bytes, img.getContentType());
    }
}


