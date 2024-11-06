package guru.springframework.spring6reactiveexamples.repository;

import guru.springframework.spring6reactiveexamples.model.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

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

        personMono.map(person -> {
            return person.getFirstName();
        }).subscribe(firstName -> {
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
            System.out.println(person);
            //assertNotNull(person);
            //assertEquals(1, person.getId());
        });
    }

    @Test
    void testFluxFindAllSubscriberNotFound() {
        Flux<Person> personFlux = personRepository.findAll();
        
        final Integer id = 8;
        
        Mono<Person> personMono = personFlux.filter(person -> 
            person.getId() == id).single()
            .doOnError(throwable -> {
                System.out.println("1 an error occured" + throwable);
            });

        personMono.subscribe(person -> {
            System.out.println(person);
        }, throwable -> {
            System.out.println("2 an error occured" + throwable);
            assertInstanceOf(NoSuchElementException.class, throwable);
        });
    }

    @Test
    void testFluxFindAllSubscriberWithMapOperation() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(person -> {
            return person.getFirstName();
        }).subscribe(firstName -> {
            System.out.println(firstName);
            //assertNotNull(firstName);
            //assertEquals("Michael", firstName);
        });
    }

    @Test
    void testFluxFindAllSubscriberWithListOperation() {
        Flux<Person> personFlux = personRepository.findAll();
        
        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(personList -> {
            assertEquals(4, personList.size());
            personList.forEach(person -> { System.out.println(person); });
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
