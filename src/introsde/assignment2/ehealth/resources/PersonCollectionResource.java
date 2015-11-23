package introsde.assignment2.ehealth.resources;
import introsde.assignment2.ehealth.model.MeasureHistory;
import introsde.assignment2.ehealth.model.MeasureType;
import introsde.assignment2.ehealth.model.Measurement;
import introsde.assignment2.ehealth.model.Person;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

@Stateless
@LocalBean
@Path("/person")
public class PersonCollectionResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @PersistenceUnit(unitName="introsde-2015-assignment-2-jpa")
    EntityManager entityManager;

    @PersistenceContext(unitName = "introsde-2015-assignment-2-jpa",type=PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;

    /**
     * @return  Returns the list of all People
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public List<Person> getPersonsBrowser() {
        List<Person> people = Person.getAll();
        return people;
    }

    /**
     * @return Returns the number of People records
     */
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        System.out.println("Getting count...");
        List<Person> people = Person.getAll();
        int count = people.size();
        return String.valueOf(count);
    }

    /**
     * Create a new Person. 
     * On success returns status 201 and Location header set to the URI
     * of the newly created Person.
     * 
     * @param person
     * @return Returns the created Person
     * @throws IOException
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public Response newPerson(Person person) throws IOException {
    	for(Measurement m :person.getList()){
    		m.setPerson(person);
    		if(m.getDate()==null){
    			m.setDate(new Date());
    		}
    	}
    	Response res;
    	person = Person.savePerson(person);
    	URI location = null;
    	try {
    		location = new URI(uriInfo.getAbsolutePath().toString()+"/"+person.getId());
    	} catch (URISyntaxException e){
    	}
    	res = Response.created(location).entity(person).build();
		return res;
    }
    

    /**
     * Get the Measure History of a specific Measure Type for a specific Person
     * @param personId
     * @param measureType
     * @return
     */
    @GET
    @Path("{personId}/{measureType}")
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public MeasureHistory getMeasurementHistory(
    		@PathParam("personId") int personId, 
    		@PathParam("measureType") String measureType ){
    	Person p = Person.getPersonById(personId);
    	MeasureType mT = MeasureType.getMeasureTypeByName(measureType);
    	
    	if(p == null)
    		throw new NotFoundException("Get: Person with " + personId + " not found");
    	
    	if(mT == null)
    		throw new NotFoundException("Get: Invalid measureType");
    	
    	MeasureHistory mH = new MeasureHistory(p, measureType);
    	return mH;
    }

    /**
     * Get a specific Measurement by its id
     * @param personId
     * @param measureType
     * @param measurementId
     * @return
     */
    @GET
    @Path("{personId}/{measureType}/{measurementId}")
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public Measurement getMeasurementValue(
    		@PathParam("personId") int personId, 
    		@PathParam("measureType") String measureType,
    		@PathParam("measurementId") int measurementId){
    	Person p = Person.getPersonById(personId);
    	MeasureType mT = MeasureType.getMeasureTypeByName(measureType);
    	
    	if(p == null)
    		throw new NotFoundException("Get: Person with " + personId + " not found");
    	
    	if(mT == null)
    		throw new NotFoundException("Get: Invalid measureType");
    	
		Measurement m = p.getMeasurement(measureType, measurementId);
		if(m == null)
			throw new NotFoundException("Get: Invalid measureType");
    	
		return m;
	}

    /**
     * Create a new Measurement for a Person
     * @param m 
     * @param personId
     * @param measureType
     * @return
     */
    @POST
    @Path("{personId}/{measureType}")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public Response addMeasurement(
    		Measurement m, 
    		@PathParam("personId") int personId, 
    		@PathParam("measureType") String measureType){
    	URI location = null;
    	
    	Person p = Person.getPersonById(personId);
    	
    	if(p == null){
    		return Response.status(Status.NOT_FOUND).build();
    	}
    	
    	m.setPerson(p);
    	m.setMeasureDefinition(MeasureType.getMeasureTypeByName(measureType));
    	if(m.getDate() == null){
    		m.setDate(new Date());
    	}
    	Measurement measure = Measurement.saveMeasure(m);

    	try {
    		location = new URI(uriInfo.getAbsolutePath().toString()+"/"+measure.getId());
    	} catch (URISyntaxException e){
    	}
    	
    	return Response.created(location).entity(measure).build();
    }
    

    
    // Additional operation on Person
    @Path("{personId}")
    public PersonResource getPerson(@PathParam("personId") int id) {
        return new PersonResource(uriInfo, request, id);
    }
    

}