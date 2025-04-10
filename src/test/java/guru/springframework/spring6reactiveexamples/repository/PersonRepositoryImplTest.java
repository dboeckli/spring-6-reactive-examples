package guru.springframework.spring6reactiveexamples.repository;

import guru.springframework.spring6reactiveexamples.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class PersonRepositoryImplTest {
    
    PersonRepository personRepository = new PersonRepositoryImpl();

    @Test
    void testMonoFindByIdBlocking() {
        Mono<Person> personMono = personRepository.findById(1);
        
        Person person = personMono.block();
        
        assertNotNull(person);
        assertEquals(1, person.getId());
    }

    @Test
    void testMonoFindByIdBlockingWithId3() {
        Mono<Person> personMono = personRepository.findById(3);

        Person person = personMono.block();

        assertNotNull(person);
        assertEquals(3, person.getId());
    }

    @Test
    void testMonoFindByIdWithId3WithStepVerifier() {
        Mono<Person> personMono = personRepository.findById(3);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.subscribe(person -> {
            assertNotNull(person);
            assertEquals(3, person.getId());
        });
    }

    @Test
    void testMonoFindByIdBlockingWithId8NotFound() {
        Mono<Person> personMono = personRepository.findById(8);

        Person person = personMono.block();

        assertNull(person);
    }

    @Test
    void testMonoFindByIdWithId8WithStepVerifier() {
        Mono<Person> personMono = personRepository.findById(8);

        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();

        personMono.subscribe(person -> {
            System.out.println(person);
            assertNull(person);
        });
    }

    @Test
    void testMonoFindByIdSubscriber() {
        Mono<Person> personMono = personRepository.findById(1);

        personMono.subscribe(person -> {
            assertNotNull(person);
            assertEquals(1, person.getId());
        });
    }

    @Test
    void testMonoFindByIdSubscriberWithMapOperation() {
        Mono<Person> personMono = personRepository.findById(1);

        personMono.map(Person::getFirstName).subscribe(firstName -> {
            assertNotNull(firstName);
            assertEquals("Michael", firstName);
        });
    }

    @Test
    void testFluxFindAllBlockingFirst() {
        Flux<Person> personFlux = personRepository.findAll();
        
        Person person = personFlux.blockFirst();

        assertNotNull(person);
        assertEquals(1, person.getId());
    }

    @Test
    void testFluxFindAllSubscriber() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(person -> {
            log.info("Person: {}", person);
            assertNotNull(person);
            assertEquals(1, person.getId());
        });
    }

    @Test
    void testFluxFindAllSubscriberNotFound() {
        Flux<Person> personFlux = personRepository.findAll();
        
        final Integer id = 8;
        
        Mono<Person> personMono = personFlux.filter(person ->
                Objects.equals(person.getId(), id)).single()
            .doOnError(throwable -> log.error("1 an error occured: " + throwable, throwable));

        personMono.subscribe(person -> log.info("Person: {}", person), throwable -> {
            log.error("2 an error occured" + throwable, throwable);
            assertInstanceOf(NoSuchElementException.class, throwable);
        });
    }

    @Test
    void testFluxFindAllSubscriberWithMapOperation() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(Person::getFirstName).subscribe(firstName -> {
            log.info("First Name: {}", firstName);
            assertNotNull(firstName);
            assertEquals("Michael", firstName);
        });
    }

    @Test
    void testFluxFindAllSubscriberWithListOperation() {
        Flux<Person> personFlux = personRepository.findAll();
        
        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(personList -> {
            assertEquals(4, personList.size());
            personList.forEach(person -> log.info("Person: {}", person));
        });
    }
    
    @Test
    void testfindAllFilterOnName() {
        personRepository.findAll()
            .filter(person -> person.getFirstName().equals("Fiona"))  
            .subscribe(person -> {
                System.out.println(person);
                assertEquals("Fiona", person.getFirstName());
            });
    }

    @Test
    void testfindAllFilterOnName2() {
        Mono<Person> fionaMono = personRepository.findAll()
            .filter(person -> person.getFirstName().equals("Fiona"))
            .next();

        fionaMono.subscribe(person -> {
            System.out.println(person);
            assertEquals("Fiona", person.getFirstName());
        });
    }
}
