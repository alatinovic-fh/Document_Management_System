package at.bif.swen.paperlessbatch.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@XmlRootElement(name = "access_statistics")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessStatisticsDto {

    @XmlElement(name = "record")
    private List<RecordDto> records;

    @Data
    @XmlRootElement(name = "record")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RecordDto {
        @XmlElement(name = "document_id")
        private Long documentId;

        @XmlElement(name = "access_count")
        private Long accessCount;

        @XmlElement(name = "source_system_id")
        private String sourceSystemId;

        @XmlElement(name = "processing_date")
        private String processingDate;
    }
}
