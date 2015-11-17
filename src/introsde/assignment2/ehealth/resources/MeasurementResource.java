package introsde.assignment2.ehealth.resources;
import introsde.assignment2.ehealth.model.MeasureTypeList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless // only used if the the application is deployed in a Java EE container
@LocalBean // only used if the the application is deployed in a Java EE container
@Path("/measureTypes")
public class MeasurementResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	
    // will work only inside a Java EE application
    @PersistenceUnit(unitName="introsde-2015-assignment-2-jpa")
    EntityManager entityManager; // only used if the application is deployed in a Java EE container
    
    // will work only inside a Java EE application
    @PersistenceContext(unitName = "introsde-2015-assignment-2-jpa",type=PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;
    
    // Return the list of people to the user in the browser
    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public MeasureTypeList getMeasureTypes() {
        //List<MeasureType> list = MeasureType.getAll();
        MeasureTypeList list = new MeasureTypeList();
    	return list;
    }
   

}
