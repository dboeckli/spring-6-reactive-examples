package guru.springframework.spring6reactiveexamples.health;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureMetrics
@Slf4j
class ActuatorInfoTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BuildProperties buildProperties;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void actuatorInfoTest() {
        EntityExchangeResult<byte[]> result = webTestClient.get().uri("/actuator/info")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.git.commit.id.abbrev").isNotEmpty()
            .jsonPath("$.java.version").isEqualTo("21.0.5")
            .jsonPath("$.build.artifact").isEqualTo(buildProperties.getArtifact())
            .jsonPath("$.build.group").isEqualTo(buildProperties.getGroup())
            .returnResult();

        String jsonResponse = new String(Objects.requireNonNull(result.getResponseBody()));
        log.info("Response:\n{}", pretty(jsonResponse));
    }


    @Test
    void actuatorHealthTest() {
        EntityExchangeResult<byte[]> result = webTestClient.get().uri("/actuator/health/readiness")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP")
            .returnResult();

        String jsonResponse = new String(Objects.requireNonNull(result.getResponseBody()));
        log.info("Response:\n{}", pretty(jsonResponse));
    }

    @Test
    void actuatorPrometheusTest() {
        EntityExchangeResult<byte[]> result = webTestClient.get().uri("/actuator/prometheus")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .returnResult();

        String response = new String(Objects.requireNonNull(result.getResponseBody()));
        log.info("Response:\n{}", response);

    }

    private String pretty(String jsonResponse) {
        try {
            Object json = objectMapper.readValue(jsonResponse, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            // Falls kein valides JSON: unverändert zurückgeben
            return jsonResponse;
        }
    }

}
