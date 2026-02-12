package com.example.accessingdatamongodb;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Example;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public class CustomerRepositoryTests {

	@Container
	@ServiceConnection
	static MongoDBContainer container = new MongoDBContainer("mongo:7.0.2");

	@Autowired
	CustomerRepository repository;

	Customer dave, oliver, carter;

	@BeforeEach
	public void setUp() {
		repository.deleteAll();

		dave = repository.save(new Customer("Dave", "Matthews"));
		oliver = repository.save(new Customer("Oliver August", "Matthews"));
		carter = repository.save(new Customer("Carter", "Beauford"));
	}

	@Test
	public void setsIdOnSave() {
		Customer dave = repository.save(new Customer("Dave", "Matthews"));
		assertThat(dave.id).isNotNull();
	}

	@Test
	public void findsByLastName() {
		List<Customer> result = repository.findByLastName("Beauford");
		assertThat(result).hasSize(1).extracting("firstName").contains("Carter");
	}

	@Test
	public void findsByExample() {
		Customer probe = new Customer(null, "Matthews");
		List<Customer> result = repository.findAll(Example.of(probe));
		assertThat(result).hasSize(2).extracting("firstName").contains("Dave", "Oliver August");
	}

}
