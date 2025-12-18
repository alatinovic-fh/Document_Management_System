package at.bif.swen.paperlessrest.controller.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class DocDto {
    private Long id;
    private String originalFilename;
    private String contentType;
    private long size;
    private Date uploadDate;
    private String summary;
}
