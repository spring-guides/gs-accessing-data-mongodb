<#assign project_id="gs-accessing-data-mongo">
This guide walks you through the process of building an application Spring Data Mongo to store and retrieve data in mongo's document-based database.

What you'll build
-----------------

You will store `Person` POJOs in a Mongo database using Spring Data Mongo.

What you'll need
----------------

 - About 15 minutes
 - <@prereq_editor_jdk_buildtools/>

## <@how_to_complete_this_guide jump_ahead='Install and launch Mongo'/>


<a name="scratch"></a>
Set up the project
------------------

<@build_system_intro/>

<@create_directory_structure_hello/>

### Create a Gradle build file

    <@snippet path="build.gradle" prefix="initial"/>


<a name="initial"></a>
Install and launch Mongo
------------------------
With your project setup, the next step is to install and launch the Mongo database.

If you are using a Mac with homebrew, this is as simple as:

    $ brew install mongodb
    
With MacPorts:

    $ port install mongodb
    
For other systems with package management, like Redhat, Ubuntu, Debian, CentOS, Windows, and others, more instructions are included at http://docs.mongodb.org/manual/installation/.

Once you have installed it, you can launch it immediately in a console window:

    $ mongod
    
You probably won't see much more than this:

```sh
all output going to: /usr/local/var/log/mongodb/mongo.log
```
    
This starts up a server process. 


Define a simple entity
------------------------
Mongo is a NoSQL document store. In this example, you store `Customer` objects.

    <@snippet path="src/main/java/hello/Customer.java" prefix="complete"/>

Here you have a `Customer` class with three attributes, the `id, the `firstName` and the `lastName`. The `id` is mostly for internal use by Mongo. You also have a single constructor to populate the entities when creating a new instance.

> Note: In this guide, the typical getters and setters have been left out for brevity.

`id` fits the standard name for a Mongo id so it doesn't require any special annotation to tag it for Spring Data Mongo.

The other two properties, `firstName` and `lastName` are left unannotated. It is assumed that they'll be mapped to columns that share the same name as the properties themselves.

The convenient `toString()` method will print out the details about a customer.

> **Note:** Mongo stores data in collections. Spring Data Mongo will map the class `Customer` into a collection called **customer**. If you want to change the name of the collection, you can use Spring Data Mongo's [`@Document`](http://static.springsource.org/spring-data/data-mongodb/docs/current/api/org/springframework/data/mongodb/core/mapping/Document.html) annotation on the class.

Create simple queries
----------------------------
Spring Data Mongo focuses on storing data in Mongo. It also inherits powerful functionality from the Spring Data Commons project, such as the ability to derive queries. Essentially, you don't have to learn the query language of Mongo; you can simply write a handful of methods and the queries are written for you.

To see how this works, create a repository interface that queries `Customer` documents.

    <@snippet path="src/main/java/hello/CustomerRepository.java" prefix="complete"/>
    
`CustomerRepository` extends the `MongoRepository` interface and plugs in the type of values and id it works with: `Customer` and `String`. Out-of-the-box, this interface comes with many operations, including standard CRUD operations (change-replace-update-delete).

You can define other queries as needed by simply declaring their method signature. In this case, you add `findByFirstName`, which essentially seeks documents of type `Customer` and finds the one that matches on `firstName`.

You also have:
- `findByLastName` to find a list of people by last name

In a typical Java application, you would write a class that implements `CustomerRepository` and craft the queries yourself. What makes Spring Data Mongo so powerful is the fact that you don't have to create this implementation. Spring Data Mongo creates it on the fly when you run the application.

Let's wire this up and see what it looks like!

Create an application class
---------------------------
Here you create an Application class with all the components.

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

In the configuration, you need to add the `@EnableMongoRepositories` annotation. This tells Spring Data Mongo that it should seek out any interface that extends `org.springframework.data.repository.Repository` and to automatically generate an implementation. By extending `MongoRepository`, your `CustomerRepository` interface transitively extends `Repository`. Therefore, Spring Data Mongo will find it and create an implementation for you.

* The `Mongo` connection links the application to your Mongo database server
* The `MongoTemplate` is needed by Spring Data Mongo to execute the queries behind your `find*` methods. You can use the template yourself for more complex queries, but this guide won't go into that.
* Finally, you autowire an instance of `CustomerRepository`. Spring Data Mongo dynamically creates a proxy and injects it there.

Finally, `Application` includes a `main()` method that puts the `CustomerRepository` through a few tests. First, it fetches the `CustomerRepository` from the Spring application context. Then it saves a handful of `Customer` objects, demonstrating the `save()` method and setting up some data to work with. Next, it calls `findAll()` to fetch all `Customer` objects from the database. Then it calls `findByFirstName()` to fetch a single `Customer` by her first name. Finally, it calls `findByLastName()` to find all customers whose last name is "Smith".

## <@build_an_executable_jar_with_gradle/>
    
<@run_the_application_with_gradle/>
    
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
Congratulations! You set up a Mongo server, written a simple application that uses Spring Data Mongo to save some objects to a database, and to fetch them; all without writing a concrete repository implementation.