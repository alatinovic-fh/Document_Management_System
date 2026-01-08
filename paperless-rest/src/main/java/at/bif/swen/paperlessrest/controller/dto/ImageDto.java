package at.bif.swen.paperlessrest.controller.dto;

import lombok.Data;

@Data
public class ImageDto {
    private Long id;
    private String objectKey;
    private String contentType;
    private long size;
}
