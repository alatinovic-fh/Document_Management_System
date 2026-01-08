package at.bif.swen.paperlessrest.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageResponse {


    private final byte[] data;
    private final String contentType;

}
