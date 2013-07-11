<#assign project_id="gs-accessing-data-mongo">

Getting Started: Accessing Data with Mongo
==========================================

What you'll build
-----------------

This guide walks you through the process of building an application with Mongo's data store using the powerful Spring Data Mongo library to store and retrieve POJOs.

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

### Create a Maven POM

    <@snippet path="pom.xml" prefix="complete"/>


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

Here you have a `Customer` class with two attributes, the `firstName` and the `lastName`. There is also an `id`, but it is mostly for internal use by Mongo. You also have a single constructor to populate the entities when creating a new instance.

> Note: In this guide, the typical getters and setters have been left out for brevity.

The convenient `toString()` method will print out the details about a customer.

Create simple queries
----------------------------
Spring Data Mongo focuses on storing data in Mongo. It also inherits powerful functionality from the Spring Data Commons project, such as the ability to derive queries. Essentially, you don't have to learn the query language of Mongo; you can simply write a handful of methods and the queries are written for you.

To see how this works, create an interface that queries `Customer` nodes.

    <@snippet path="src/main/java/hello/CustomerRepository.java" prefix="complete"/>
    
`CustomerRepository` extends the `MongoRepository` interface and plugs in the type of values and id it works with: `Customer` and `String`. Out-of-the-box, this interface comes with many operations, including standard CRUD (change-replace-update-delete).

You can define other queries as needed by simply declaring their method signature. In this case, you add `findByFirstName`, which essentially seeks nodes of type `Customer` and find the one that matches on `firstName`.

You also have:
- `findByLastName` to find a list of people by last name

Let's wire this up and see what it looks like!

Create an application class
---------------------------
Here you create an Application class with all the components.

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

In the configuration, you need to add the `@EnableMongoRepositories` annotation.

You also need to define a `Mongo` connection. Since you are running the Mongo server locally, this one points at localhost.

Spring Data Mongo also needs a `MongoTemplate`. You can use the template for more complex operations, but this guide is keeping things simple and only using the generated queries.

You autowire an instance of `CustomerRepository` that you just defined. Spring Data Mongo will dynamically create a concrete class that implements that interface and will plug in the needed query code to meet the interface's obligations.

Store and fetch data
-------------------------
The `public static void main` method includes code to create an application context and then define people.

In this case, you are creating tow customers, **Alice Smith** and **Bob Smith**. Then you save them in Mongo.

Now you run several queries. The first looks up everyone by name. Then you execute a handful of queries to find adults, babies, and teens, all using the age attribute. With the logging turned up, you can see the queries Spring Data GemFire writes on your behalf.

## <@build_the_application/>
    
<@run_the_application/>
    
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
Congratulations! You set up a Mongo server, stored simple entities, and developed quick queries.