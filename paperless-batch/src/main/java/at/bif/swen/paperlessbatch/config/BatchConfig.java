package at.bif.swen.paperlessbatch.config;

import at.bif.swen.paperlessbatch.dto.AccessStatisticsDto;
import at.bif.swen.paperlessbatch.entity.AccessStatistics;
import at.bif.swen.paperlessbatch.repository.AccessStatisticsRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final AccessStatisticsRepository repository;

    @Value("${paperless.batch.archive-dir}")
    private String archiveDir;

    @Bean
    public Job accessStatisticsJob(Step importStep) {
        return new JobBuilder("accessStatisticsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(importStep)
                .end()
                .listener(fileArchivingListener())
                .build();
    }

    @Bean
    public Step importStep(StaxEventItemReader<AccessStatisticsDto.RecordDto> reader,
            ItemProcessor<AccessStatisticsDto.RecordDto, AccessStatistics> processor,
            JpaItemWriter<AccessStatistics> writer) {
        return new StepBuilder("importStep", jobRepository).<AccessStatisticsDto.RecordDto, AccessStatistics>chunk(10,
                transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<AccessStatisticsDto.RecordDto> xmlItemReader(
            @Value("#{jobParameters['fullPath']}") String fullPath) throws MalformedURLException {

        log.info("Creating Reader for file: {}", fullPath);

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(AccessStatisticsDto.RecordDto.class);

        return new StaxEventItemReaderBuilder<AccessStatisticsDto.RecordDto>()
                .name("xmlItemReader")
                .resource(new UrlResource("file:" + fullPath))
                .unmarshaller(marshaller)
                .addFragmentRootElements("record")
                .build();
    }

    @Bean
    public ItemProcessor<AccessStatisticsDto.RecordDto, AccessStatistics> processor() {
        return item -> {
            // log.debug("Processing item: {}", item); // Verbose
            LocalDateTime processingDate = LocalDateTime.parse(item.getProcessingDate(),
                    DateTimeFormatter.ISO_DATE_TIME);

            // Skip if already exists
            if (repository.findBySourceSystemIdAndDocumentIdAndProcessingDate(
                    item.getSourceSystemId(), item.getDocumentId(), processingDate).isPresent()) {
                return null;
            }

            return AccessStatistics.builder()
                    .documentId(item.getDocumentId())
                    .accessCount(item.getAccessCount())
                    .sourceSystemId(item.getSourceSystemId())
                    .processingDate(processingDate)
                    .build();
        };
    }

    @Bean
    public JpaItemWriter<AccessStatistics> writer() {
        return new JpaItemWriterBuilder<AccessStatistics>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public JobExecutionListener fileArchivingListener() {
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                if (jobExecution.getStatus().isUnsuccessful()) {
                    log.error("Job failed, not archiving file.");
                    return;
                }

                String fullPath = jobExecution.getJobParameters().getString("fullPath");
                if (fullPath != null) {
                    try {
                        Path source = Paths.get(fullPath);
                        Path targetDir = Paths.get(archiveDir);
                        if (!Files.exists(targetDir)) {
                            Files.createDirectories(targetDir);
                        }
                        Path target = targetDir.resolve(source.getFileName());
                        // Replace if exists, or simple move
                        Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        log.info("Archived file {} to {}", source, target);
                    } catch (IOException e) {
                        log.error("Failed to archive file: " + fullPath, e);
                    }
                }
            }
        };
    }
}
