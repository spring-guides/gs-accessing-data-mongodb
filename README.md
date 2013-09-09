This guide walks you through the process of using [Spring Data Mongo](http://www.springsource.org/spring-data/mongodb) to build an application that stores data in and retrieves it from [MongoDB](http://www.mongodb.org/), a document-based database.


What you'll build
-----------------

You will store `Person` POJOs in a MongoDB database using Spring Data Mongo.

What you'll need
----------------

 - About 15 minutes
 - A favorite text editor or IDE
 - [JDK 6][jdk] or later
 - [Gradle 1.7+][gradle] or [Maven 3.0+][mvn]
 - You can also import the code from this guide as well as view the web page directly into [Spring Tool Suite (STS)][gs-sts] and work your way through it from there.

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[gradle]: http://www.gradle.org/
[mvn]: http://maven.apache.org/download.cgi
[gs-sts]: /guides/gs/sts

How to complete this guide
--------------------------

Like all Spring's [Getting Started guides](/guides/gs), you can start from scratch and complete each step, or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Set up the project](#scratch).

To **skip the basics**, do the following:

 - [Download][zip] and unzip the source repository for this guide, or clone it using [Git][u-git]:
`git clone https://github.com/spring-guides/gs-accessing-data-mongo.git`
 - cd into `gs-accessing-data-mongo/initial`.
 - Jump ahead to [Install and launch Mongo](#initial).

**When you're finished**, you can check your results against the code in `gs-accessing-data-mongo/complete`.
[zip]: https://github.com/spring-guides/gs-accessing-data-mongo/archive/master.zip
[u-git]: /understanding/Git


<a name="scratch"></a>
Set up the project
------------------

First you set up a basic build script. You can use any build system you like when building apps with Spring, but the code you need to work with [Gradle](http://gradle.org) and [Maven](https://maven.apache.org) is included here. If you're not familiar with either, refer to [Building Java Projects with Gradle](/guides/gs/gradle/) or [Building Java Projects with Maven](/guides/gs/maven).

### Create the directory structure

In a project directory of your choosing, create the following subdirectory structure; for example, with `mkdir -p src/main/java/hello` on *nix systems:

    └── src
        └── main
            └── java
                └── hello


### Create a Gradle build file
Below is the [initial Gradle build file](https://github.com/spring-guides/gs-accessing-data-mongo/blob/master/initial/build.gradle). But you can also use Maven. The pom.xml file is included [right here](https://github.com/spring-guides/gs-accessing-data-mongo/blob/master/initial/pom.xml). If you are using [Spring Tool Suite (STS)][gs-sts], you can import the guide directly.

`build.gradle`
```gradle
buildscript {
    repositories {
        maven { url "http://repo.springsource.org/libs-snapshot" }
        mavenLocal()
    }
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'idea'

jar {
    baseName = 'gs-acessing-data-mongo'
    version =  '0.1.0'
}

repositories {
    mavenCentral()
    maven { url "http://repo.springsource.org/libs-snapshot" }
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web:0.5.0.M2")
    compile("org.springframework.data:spring-data-mongodb:1.2.1.RELEASE")
    testCompile("junit:junit:4.11")
}

task wrapper(type: Wrapper) {
    gradleVersion = '1.7'
}
```
    
[gs-sts]: /guides/gs/sts    


<a name="initial"></a>
Install and launch Mongo
------------------------
With your project set up, you can install and launch the MongoDB database.


If you are using a Mac with homebrew, this is as simple as:

    $ brew install mongodb
    
With MacPorts:

    $ port install mongodb
    
For other systems with package management, such as Redhat, Ubuntu, Debian, CentOS, and Windows, see instructions at http://docs.mongodb.org/manual/installation/.

After you install Mongo, launch it in a console window. This command also starts up a server process. 


    $ mongod
    
You probably won't see much more than this:

```sh
all output going to: /usr/local/var/log/mongodb/mongo.log
```
    
Define a simple entity
------------------------
MongoDB is a NoSQL document store. In this example, you store `Customer` objects.

`src/main/java/hello/Customer.java`
```java
package hello;

import org.springframework.data.annotation.Id;


public class Customer {

    @Id
    private String id;

    private String firstName;
    private String lastName;

    public Customer() {}

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

}

```

Here you have a `Customer` class with three attributes, `id`, `firstName`, and `lastName`. The `id` is mostly for internal use by Mongo. You also have a single constructor to populate the entities when creating a new instance.


> **Note:** In this guide, the typical getters and setters have been left out for brevity.

`id` fits the standard name for a MongoDB id so it doesn't require any special annotation to tag it for Spring Data Mongo.

The other two properties, `firstName` and `lastName`, are left unannotated. It is assumed that they'll be mapped to columns that share the same name as the properties themselves.


The convenient `toString()` method will print out the details about a customer.

> **Note:** MongoDB stores data in collections. Spring Data Mongo will map the class `Customer` into a collection called _customer_. If you want to change the name of the collection, you can use Spring Data Mongo's [`@Document`](http://docs.spring.io/spring-data/data-mongodb/docs/current/api/org/springframework/data/mongodb/core/mapping/Document.html) annotation on the class.


Create simple queries
----------------------------
Spring Data Mongo focuses on storing data in Mongo. It also inherits functionality from the Spring Data Commons project, such as the ability to derive queries. Essentially, you don't have to learn the query language of Mongo; you can simply write a handful of methods and the queries are written for you.


To see how this works, create a repository interface that queries `Customer` documents.

`src/main/java/hello/CustomerRepository.java`
```java
package hello;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    public Customer findByFirstName(String firstName);
    public List<Customer> findByLastName(String lastName);

}
```
    
`CustomerRepository` extends the `MongoRepository` interface and plugs in the type of values and id it works with: `Customer` and `String`. Out-of-the-box, this interface comes with many operations, including standard CRUD operations (change-replace-update-delete).

You can define other queries as needed by simply declaring their method signature. In this case, you add `findByFirstName`, which essentially seeks documents of type `Customer` and finds the one that matches on `firstName`.

You also have `findByLastName` to find a list of people by last name.

In a typical Java application, you write a class that implements `CustomerRepository` and craft the queries yourself. What makes Spring Data Mongo so useful is the fact that you don't have to create this implementation. Spring Data Mongo creates it on the fly when you run the application.

Let's wire this up and see what it looks like!

Create an Application class
---------------------------
Here you create an Application class with all the components.

`src/main/java/hello/Application.java`
```java
package hello;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;


@Configuration
@EnableMongoRepositories
public class Application {

    @Autowired
    CustomerRepository customerRepository;

    @Bean
    Mongo mongo() throws UnknownHostException {
        return new Mongo("localhost");
    }

    @Bean
    MongoTemplate mongoTemplate(Mongo mongo) {
        return new MongoTemplate(mongo, "gs-accessing-data-mongo");
    }

    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        CustomerRepository repository = context.getBean(CustomerRepository.class);

        repository.deleteAll();

        // save a couple of customers
        repository.save(new Customer("Alice", "Smith"));
        repository.save(new Customer("Bob", "Smith"));

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Customer customer : repository.findAll()) {
            System.out.println(customer);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Alice"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Customer customer : repository.findByLastName("Smith")) {
            System.out.println(customer);
        }

        context.close();
    }

}
```

In the configuration, you need to add the `@EnableMongoRepositories` annotation. This annotation tells Spring Data Mongo to seek out any interface that extends `org.springframework.data.repository.Repository` and to automatically generate an implementation. By extending `MongoRepository`, your `CustomerRepository` interface transitively extends `Repository`. Therefore, Spring Data Mongo will find it and create an implementation for you.


* The `Mongo` connection links the application to your MongoDB server
* Spring Data Mongo uses the `MongoTemplate` to execute the queries behind your `find*` methods. You can use the template yourself for more complex queries, but this guide doesn't cover that.
* Finally, you autowire an instance of `CustomerRepository`. Spring Data Mongo dynamically creates a proxy and injects it there.

`Application` includes a `main()` method that puts the `CustomerRepository` through a few tests. First, it fetches the `CustomerRepository` from the Spring application context. Then it saves a handful of `Customer` objects, demonstrating the `save()` method and setting up some data to work with. Next, it calls `findAll()` to fetch all `Customer` objects from the database. Then it calls `findByFirstName()` to fetch a single `Customer` by her first name. Finally, it calls `findByLastName()` to find all customers whose last name is "Smith".

Build an executable JAR
-----------------------
Now that your `Application` class is ready, you simply instruct the build system to create a single, executable jar containing everything. This makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

Below are the Gradle steps, but if you are using Maven, you can find the updated pom.xml [right here](https://github.com/spring-guides/gs-accessing-data-mongo/blob/master/complete/pom.xml) and build it by typing `mvn clean package`.

Update your Gradle `build.gradle` file's `buildscript` section, so that it looks like this:

```groovy
buildscript {
    repositories {
        maven { url "http://repo.springsource.org/libs-snapshot" }
        mavenLocal()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:0.5.0.M2")
    }
}
```

Further down inside `build.gradle`, add the following to the list of applied plugins:

```groovy
apply plugin: 'spring-boot'
```
You can see the final version of `build.gradle` [right here]((https://github.com/spring-guides/gs-accessing-data-mongo/blob/master/complete/build.gradle).

The [Spring Boot gradle plugin][spring-boot-gradle-plugin] collects all the jars on the classpath and builds a single "über-jar", which makes it more convenient to execute and transport your service.
It also searches for the `public static void main()` method to flag as a runnable class.

Now run the following command to produce a single executable JAR file containing all necessary dependency classes and resources:

```sh
$ ./gradlew build
```

If you are using Gradle, you can run the JAR by typing:

```sh
$ java -jar build/libs/gs-accessing-data-mongo-0.1.0.jar
```

If you are using Maven, you can run the JAR by typing:

```sh
$ java -jar target/gs-accessing-data-mongo-0.1.0.jar
```

[spring-boot-gradle-plugin]: https://github.com/SpringSource/spring-boot/tree/master/spring-boot-tools/spring-boot-gradle-plugin

> **Note:** The procedure above will create a runnable JAR. You can also opt to [build a classic WAR file](/guides/gs/convert-jar-to-war/) instead.
    
Run the service
-------------------
If you are using Gradle, you can run your service at the command line this way:

```sh
$ ./gradlew clean build && java -jar build/libs/gs-accessing-data-mongo-0.1.0.jar
```

> **Note:** If you are using Maven, you can run your service by typing `mvn clean package && java -jar target/gs-accessing-data-mongo-0.1.0.jar`.

    
You should see something like this (with other stuff like queries as well):
```
Customers found with findAll():
-------------------------------
Customer[id=51df1b0a3004cb49c50210f8, firstName='Alice', lastName='Smith']
Customer[id=51df1b0a3004cb49c50210f9, firstName='Bob', lastName='Smith']

Customer found with findByFirstName('Alice'):
--------------------------------
Customer[id=51df1b0a3004cb49c50210f8, firstName='Alice', lastName='Smith']
Customers found with findByLastName('Smith'):
--------------------------------
Customer[id=51df1b0a3004cb49c50210f8, firstName='Alice', lastName='Smith']
Customer[id=51df1b0a3004cb49c50210f9, firstName='Bob', lastName='Smith']
```

Summary
-------
Congratulations! You set up a MongoDB server and wrote a simple application that uses Spring Data Mongo to save objects to and fetch them from a database -- all without writing a concrete repository implementation.
