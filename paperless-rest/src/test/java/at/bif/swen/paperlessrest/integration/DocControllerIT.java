package at.bif.swen.paperlessrest.integration;

import at.bif.swen.paperlessrest.persistence.entity.Document;
import at.bif.swen.paperlessrest.persistence.repository.DocRepository;
import at.bif.swen.paperlessrest.service.FileStorage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class DocControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocRepository docRepository;

    @Autowired
    private FileStorage fileStorage;

    // Define Containers
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static RabbitMQContainer rabbitmq = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"));

    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.16.0"))
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    static GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse("minio/minio"))
            .withEnv("MINIO_ROOT_USER", "admin")
            .withEnv("MINIO_ROOT_PASSWORD", "adminpassword")
            .withCommand("server /data")
            .withExposedPorts(9000);

    @BeforeAll
    static void startContainers() {
        postgres.start();
        rabbitmq.start();
        elasticsearch.start();
        minio.start();
    }

    @AfterAll
    static void stopContainers() {
        minio.stop();
        elasticsearch.stop();
        rabbitmq.stop();
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Postgres
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // RabbitMQ
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);

        // Elasticsearch
        registry.add("elasticsearch.host", elasticsearch::getHost);
        registry.add("elasticsearch.port", () -> elasticsearch.getMappedPort(9200));

        // MinIO
        registry.add("minio.endpoint.host", minio::getHost);
        registry.add("minio.endpoint.port", () -> minio.getMappedPort(9000));
        registry.add("minio.access.name", () -> "admin");
        registry.add("minio.access.secret", () -> "adminpassword");
        registry.add("minio.bucket", () -> "documents");
    }

    @Test
    void shouldUploadDocumentSuccessfully() throws Exception {
        // Given
        String filename = "integration-test-doc.pdf";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                filename,
                MediaType.APPLICATION_PDF_VALUE,
                "%PDF-1.4\n1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << >> >>\nendobj\nxref\n0 4\n0000000000 65535 f\n0000000010 00000 n\n0000000060 00000 n\n0000000117 00000 n\ntrailer\n<< /Size 4 /Root 1 0 R >>\nstartxref\n223\n%%EOF"
                        .getBytes());

        // When
        mockMvc.perform(multipart("/api/v1/documents")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFilename").value(filename))
                .andExpect(jsonPath("$.contentType").value(MediaType.APPLICATION_PDF_VALUE));

        // Then
        Document savedDoc = docRepository.findAll().stream()
                .filter(d -> d.getOriginalFilename().equals(filename))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Document not found in database"));

        assertThat(savedDoc.getContentType()).isEqualTo(MediaType.APPLICATION_PDF_VALUE);
        assertThat(savedDoc.getSize()).isEqualTo(file.getSize());

        // Verify MinIO storage
        byte[] storedContent = fileStorage.download(filename);
        assertThat(storedContent).isEqualTo(file.getBytes());
    }
}
