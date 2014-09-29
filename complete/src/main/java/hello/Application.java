package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class Application implements CommandLineRunner {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private AuditorProvider auditorProvider;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //Change Default Auditor
        auditorProvider.setCurrentAuditor("Peter");      

        Customer createdCustomer;

        // save a couple of customers
        createdCustomer = repository.save(new Customer("Alice", "Smith"));

        System.out.println("Created a new Customer with auditor as " + auditorProvider.getCurrentAuditor());
        System.out.println("-------------------------------");
        System.out.println(createdCustomer);

        createdCustomer = repository.save(new Customer("Bob", "Smith"));
        System.out.println("\nCreated another new Customer with auditor as " + auditorProvider.getCurrentAuditor());
        System.out.println("-------------------------------");
        System.out.println(createdCustomer);
        
        //Change the Auditor
        auditorProvider.setCurrentAuditor("Jhon");

        createdCustomer.setFirstName("Bob Modified");
        Customer updatedCustomer = repository.save(createdCustomer);
        System.out.println("\nUpdated a Customer with auditor as " + auditorProvider.getCurrentAuditor());
        System.out.println("-------------------------------");
        System.out.println(updatedCustomer);

        // fetch all customers
        System.out.println("\nCustomers found with findAll():");
        System.out.println("-------------------------------");
        for (Customer customer : repository.findAll()) {
            System.out.println(customer);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("\nCustomer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Alice"));

        System.out.println("\nCustomers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Customer customer : repository.findByLastName("Smith")) {
            System.out.println(customer);
        }
        
        System.out.println("\nCustomers who was not modified since creation");
        System.out.println("--------------------------------");
        for (Customer customer : repository.findUnModifiedCustomers()) {
            System.out.println(customer);
        }

        repository.deleteAll();
        System.out.println("\nDeleted All the Customers");

    }

}
