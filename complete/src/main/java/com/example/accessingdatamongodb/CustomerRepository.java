package com.example.accessingdatamongodb;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

	@Nullable
	Customer findByFirstName(String firstName);

	List<Customer> findByLastName(String lastName);

}
