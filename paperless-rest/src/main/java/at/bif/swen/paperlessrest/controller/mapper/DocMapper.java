package at.bif.swen.paperlessrest.controller.mapper;

import at.bif.swen.paperlessrest.controller.dto.DocDto;
import at.bif.swen.paperlessrest.controller.request.CreateDocRequest;
import at.bif.swen.paperlessrest.controller.request.UpdateDocRequest;
import at.bif.swen.paperlessrest.persistence.entity.Document;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocMapper {
    DocDto toDto(Document entity);
    List<DocDto> toDtoList(List<Document> entities);
    Document fromCreateDtoToEntity(CreateDocRequest req);
    Document fromUpdateDtoToEntity(UpdateDocRequest req);

}

