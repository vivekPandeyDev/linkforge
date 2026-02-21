package io.github.vivek.linkforge.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class KafkaFailFastChecker {

    private final KafkaAdmin kafkaAdmin;

    @PostConstruct
    public void checkKafkaConnection() {
        try (AdminClient adminClient =
                     AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            adminClient.listTopics(
                    new ListTopicsOptions()
            ).names().get();

            log.info("Kafka connection verified");
        } catch (InterruptedException ex) {

            // restore interrupt status
            Thread.currentThread().interrupt();
            log.error("Kafka startup check interrupted", ex);
            throw new IllegalStateException(
                    "Kafka startup check interrupted", ex
            );

        } catch (Exception ex) {
            log.error("Kafka is unavailable. Failing fast.", ex);
            throw new IllegalStateException(
                    "Kafka is required but not available", ex
            );
        }
    }
}
