package at.bif.swen.paperlessrest.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDocRequest {
    @NotBlank
    private String originalFilename;
}

