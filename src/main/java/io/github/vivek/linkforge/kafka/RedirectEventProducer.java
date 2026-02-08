package io.github.vivek.linkforge.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedirectEventProducer {

    private static final String TOPIC = "redirect-events";

    private final KafkaTemplate<@NonNull String,@NonNull String> kafkaTemplate;


    public void send(String shortCode) {
        kafkaTemplate.send(TOPIC, shortCode)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish redirect event for {}", shortCode, ex);
                    } else {
                        log.debug("Redirect event published for {}", shortCode);
                    }
                });
    }
}
