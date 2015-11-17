package introsde.assignment2.ehealth.resources;
import introsde.assignment2.ehealth.model.MeasureHistory;
import introsde.assignment2.ehealth.model.Measurement;
import introsde.assignment2.ehealth.model.Person;

import java.io.IOException;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
@Path("/person")
public class PersonCollectionResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // will work only inside a Java EE application
    @PersistenceUnit(unitName="introsde-2015-assignment-2-jpa")
    EntityManager entityManager;

    // will work only inside a Java EE application
    @PersistenceContext(unitName = "introsde-2015-assignment-2-jpa",type=PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;

    // Return the list of people to the user in the browser
    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public List<Person> getPersonsBrowser() {
        System.out.println("Getting list of people...");
        List<Person> people = Person.getAll();
        return people;
    }

    // retuns the number of people
    // to get the total number of records
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        System.out.println("Getting count...");
        List<Person> people = Person.getAll();
        int count = people.size();
        return String.valueOf(count);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public Person newPerson(Person person) throws IOException {
        System.out.println("Creating new person...123");            
        return Person.savePerson(person);
    }
    

    @GET
    @Path("{personId}/{measureType}")
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public MeasureHistory getMeasurementHistory(
    		@PathParam("personId") int personId, 
    		@PathParam("measureType") String measureType ){
    	Person p = Person.getPersonById(personId);
    	MeasureHistory mH = new MeasureHistory(p, measureType);
    	return mH;
    }

    @GET
    @Path("{personId}/{measureType}/{measurementId}")
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Measurement getMeasurementValue(
    		@PathParam("personId") int personId, 
    		@PathParam("measureType") String measureType,
    		@PathParam("measurementId") int measurementId){
    	Person p = Person.getPersonById(personId);
    	Measurement m = p.getMeasurement(measureType, measurementId);
    	return m;
	}

    
    // Defines that the next path parameter after the base url is
    // treated as a parameter and passed to the PersonResources
    // Allows to type http://localhost:599/base_url/1
    // 1 will be treaded as parameter todo and passed to PersonResource
    @Path("{personId}")
    public PersonResource getPerson(@PathParam("personId") int id) {
        return new PersonResource(uriInfo, request, id);
    }
    

}