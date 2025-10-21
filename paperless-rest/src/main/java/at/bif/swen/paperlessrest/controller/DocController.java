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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Validated
public class DocController {

    private final DocDetailService docDetailService;
    private final DocMapper docMapper;

    @PostMapping
    public ResponseEntity<DocDto> addDocument(@Valid @RequestBody CreateDocRequest req) {
        Document toSave = docMapper.fromCreateDtoToEntity(req);
        Document saved = docDetailService.create(toSave);
        DocDto body = docMapper.toDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED)
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
    /**
     * NO RESPONSE FUNCTIONALITY IN FUTURE SPRINT
     */
    public ResponseEntity<List<DocDto>> searchDocuments(@PathVariable String searchText) {
        List<DocDto> body = docMapper.toDtoList(docDetailService.search(searchText));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
