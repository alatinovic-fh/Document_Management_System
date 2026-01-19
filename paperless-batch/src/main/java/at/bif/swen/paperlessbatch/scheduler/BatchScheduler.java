package at.bif.swen.paperlessbatch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job accessStatisticsJob;

    @Value("${paperless.batch.input-dir}")
    private String inputDir;

    // Run every day at 01:00 AM
    @Scheduled(cron = "0 0 1 * * ?")
    // Also run on startup for demo purposes
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void runBatchJob() {
        log.info("Starting scheduled batch run checking directory: {}", inputDir);

        Path dirPath = Paths.get(inputDir);
        if (!Files.exists(dirPath)) {
            log.warn("Input directory does not exist: {}", inputDir);
            return;
        }

        try (Stream<Path> files = Files.list(dirPath)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xml"))
                    .forEach(path -> {
                        try {
                            log.info("Launching job for file: {}", path);
                            JobParameters params = new JobParametersBuilder()
                                    .addString("fullPath", path.toAbsolutePath().toString())
                                    .addLong("time", System.currentTimeMillis()) // Unique param to ensure validity
                                    .toJobParameters();

                            jobLauncher.run(accessStatisticsJob, params);

                        } catch (Exception e) {
                            log.error("Failed to launch job for file: " + path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Error listing files in: " + inputDir, e);
        }
    }
}
