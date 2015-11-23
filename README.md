# introsde-2015-assignment-2

###Classmate: Federico Fiorini https://github.com/federico-fiorini/introsde-2015-assignment-2/

This project defines a REST Web Service for managing a database of people and the history of their health measurements (i.e. weight, height, steps walked).

The project is implemented in Java using Jersey, JAXB and JPA. The database is a simple sqlite file. It is an assignment for the Introduction to Service Design and Engineering lecture at UNITN, Winter Semester 2015-16.

The project includes a client as well, which runs several test cases, with the particularity that the client connects to a different classmate's server. The classmate is Federico Fiorini.


##Package Structure

The project is divided into 5 packages. Each package contains:

`introsde.assignment2.ehealth`: Standalone server and Javax WS configuration.

`introsde.assignment2.ehealth.dao`: Handling Java Persistence Entity Manager.

`introsde.assignment2.ehealth.model`: The model files which are used by JPA to interact with the database, as well as by Jersey/JAXB to do the marshalling and unmarshalling into JSON/XML.

`introsde.assignment2.ehealth.resources`: The web service endpoints.

`client`: The client and necessary files for parsing the content.

##Files included

The project contains some additional files.

`lifestyle.sqlite`: Database file

`client-server-xml.log`: Output log of running test's against Fiorini's server with Accept and Content-Type headers set to "application/xml".

`client-server-json.log`: Output log of running test's against Fiorini's server with Accept and Content-Type headers set to "application/json".

##Execution

This project contains a `build.xml` file which can be run by `ant`. It will download all the required dependencies using ivy. It will also download ivy if it is not installed.


First step is to compile everything
```
ant install
```

To start the standalone server run:
```
ant run
```

To execute the client run:
```
ant execute.client
```

or either one of:
```
ant execute.client.json
ant execute.client.xml
```