package at.bif.swen.paperlessrest.controller;

import at.bif.swen.paperlessrest.controller.dto.DocDto;
import at.bif.swen.paperlessrest.controller.mapper.DocMapper;
import at.bif.swen.paperlessrest.controller.request.CreateDocRequest;
import at.bif.swen.paperlessrest.controller.request.UpdateDocRequest;
import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.service.impl.DocDetailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Validated
@Slf4j
public class DocController {

    private final DocDetailService docDetailService;
    private final DocMapper docMapper;

    @PostMapping
    public ResponseEntity<DocDto> addDocument(@RequestPart("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Document toSave = new Document();
        toSave.setOriginalFilename(file.getOriginalFilename());
        toSave.setContentType(file.getContentType());
        toSave.setSize(file.getSize());
        toSave.setUploadDate(new java.sql.Date(System.currentTimeMillis()));

        Document saved = docDetailService.create(toSave, file.getBytes());
        DocDto body = docMapper.toDto(saved);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DocDto> getDocument(@PathVariable @Positive long id) {
        DocDto body = docMapper.toDto(docDetailService.get(id));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable @Positive long id) {
        DocDto body = docMapper.toDto(docDetailService.get(id));
        String filename = body.getOriginalFilename();
        byte[] fileData = docDetailService.download(id);


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(fileData);
    }


    @GetMapping
    public ResponseEntity<List<DocDto>> listAllDocuments() {
        List<DocDto> body = docMapper.toDtoList(docDetailService.list());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DocDto> updateDocument(@PathVariable @Positive long id,
                                                 @Valid @RequestBody UpdateDocRequest req) {

        Document updated = docDetailService.update(id, docMapper.fromUpdateDtoToEntity(req));
        DocDto body = docMapper.toDto(updated);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable @Positive long id) {
        docDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{searchText}")
    public ResponseEntity<List<Long>> searchDocuments(@PathVariable String searchText) {
        try {
            List<Long> ids = docDetailService.searchDocuments(searchText);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ids);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
