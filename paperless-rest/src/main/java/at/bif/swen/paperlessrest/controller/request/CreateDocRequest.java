package at.bif.swen.paperlessrest.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDocRequest {
    @NotBlank
    private String originalFilename;
    @NotBlank private String contentType;
    @Min(0)   private long size;
}
