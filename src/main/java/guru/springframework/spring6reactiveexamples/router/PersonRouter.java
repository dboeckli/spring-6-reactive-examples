package guru.springframework.spring6reactiveexamples.router;

import guru.springframework.spring6reactiveexamples.model.Person;
import guru.springframework.spring6reactiveexamples.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
@AllArgsConstructor
@Slf4j
public class PersonRouter {

    private final PersonRepository personRepository;

    @Bean
    public RouterFunction<ServerResponse> routePerson() {
        return route(GET("/persons").and(accept(MediaType.APPLICATION_JSON)),
            request -> {
                log.info("routePerson called");
                return ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(personRepository.findAll(), Person.class);
            });
    }

}
