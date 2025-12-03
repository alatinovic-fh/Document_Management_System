package at.bif.swen.paperlessrest;

import at.bif.swen.paperlessrest.controller.request.CreateDocRequest;
import at.bif.swen.paperlessrest.controller.request.UpdateDocRequest;
import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.repository.DocRepository;
import at.bif.swen.paperlessrest.service.exception.NotFoundException;
import at.bif.swen.paperlessrest.service.impl.DocDetailService;
import at.bif.swen.paperlessrest.service.impl.FileStorageService;
import at.bif.swen.paperlessrest.service.messaging.OcrJobPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DocDetailServiceTest {
    @Mock
    private DocRepository docRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private OcrJobPublisher ocrJobPublisher;

    @InjectMocks
    private DocDetailService docDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveAndReturnDocument() {
        // give
        Document mockToSave = new Document(1L, "test.pdf", "application/pdf", 1234L, java.sql.Date.valueOf("2021-01-01"));

        when(docRepository.save(any(Document.class))).thenReturn(mockToSave);

        doNothing().when(fileStorageService).upload(anyString(), any());
        doNothing().when(ocrJobPublisher).sendOcrJob(any(Document.class), any());

        // when
        Document result = docDetailService.create(mockToSave, new byte[2]);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOriginalFilename()).isEqualTo("test.pdf");
        verify(docRepository, times(1)).save(any(Document.class));
    }

    @Test
    void get_ShouldReturnDocument_WhenFound() {
        Document d = new Document(1L, "doc.pdf", "application/pdf", 500L, java.sql.Date.valueOf("2021-01-01"));
        when(docRepository.findById(1L)).thenReturn(Optional.of(d));

        Document result = docDetailService.get(1L);

        assertThat(result.getOriginalFilename()).isEqualTo("doc.pdf");
        verify(docRepository).findById(1L);
    }

    @Test
    void get_ShouldThrow_WhenNotFound() {
        when(docRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> docDetailService.get(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void list_ShouldReturnAllDocuments() {
        List<Document> docs = List.of(
                new Document(1L, "a.pdf", "application/pdf", 1L, java.sql.Date.valueOf("2021-01-01")),
                new Document(2L, "b.pdf", "application/pdf", 2L, java.sql.Date.valueOf("2021-01-02"))
        );
        when(docRepository.findAll()).thenReturn(docs);

        List<Document> result = docDetailService.list();

        assertThat(result).hasSize(2);
        verify(docRepository).findAll();
    }

    @Test
    void update_ShouldModifyAndSaveDocument() {
        Document existing = new Document(1L, "old.pdf", "application/pdf", 123L, java.sql.Date.valueOf("2021-01-01"));
        Document updatedDocument = new Document();
        updatedDocument.setOriginalFilename("new.pdf");

        when(docRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(docRepository.save(any(Document.class))).thenAnswer(i -> i.getArgument(0));

        Document updated = docDetailService.update(1, updatedDocument);

        assertThat(updated.getOriginalFilename()).isEqualTo("new.pdf");
        verify(docRepository).save(existing);
    }

    @Test
    void delete_ShouldCallRepositoryDelete_WhenExists() {
        when(docRepository.existsById(1L)).thenReturn(true);

        Document doc = new Document(1L, "test.pdf", "application/pdf", 1234L, java.sql.Date.valueOf("2021-01-01"));
        when(docRepository.findById(1L)).thenReturn(Optional.of(doc));



        docDetailService.delete(1L);

        verify(docRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        when(docRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> docDetailService.delete(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
