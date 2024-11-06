package guru.springframework.spring6reactiveexamples.repository;

import guru.springframework.spring6reactiveexamples.model.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {
    
    Mono<Person> findById(int id);
    
    Flux<Person> findAll();
    
}
