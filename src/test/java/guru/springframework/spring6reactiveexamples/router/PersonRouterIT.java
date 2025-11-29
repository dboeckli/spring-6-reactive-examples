package guru.springframework.spring6reactiveexamples.router;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import guru.springframework.spring6reactiveexamples.model.Person;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureMetrics
class PersonRouterIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void listPersons() {
        webTestClient.get().uri("/persons")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Person.class)
            .hasSize(4);
    }

    @Test
    void test_logsMessage() {
        Logger logger = (Logger) LoggerFactory.getLogger(PersonRouter.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        webTestClient.get().uri("/persons")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk();

        List<ILoggingEvent> logEvents = listAppender.list;
        assertAll(
            () -> assertNotNull(logEvents),
            () -> assertEquals(1, logEvents.size()),
            () -> assertThat(logEvents.getFirst().getFormattedMessage()).isEqualTo("routePerson called"),
            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("traceId")).isNotBlank().matches("[0-9a-f]{32}"),
            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("spanId")).as("span_id").isNotBlank().matches("[0-9a-f]{16}")
        );

        logger.detachAppender(listAppender);
        listAppender.stop();
    }
}