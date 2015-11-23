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

@Stateless
@LocalBean
@Path("/measureTypes")
public class MeasurementResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

    @PersistenceUnit(unitName="introsde-2015-assignment-2-jpa")
    EntityManager entityManager;
    
    @PersistenceContext(unitName = "introsde-2015-assignment-2-jpa",type=PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;
    
    // Return the list of valid Measure Type's
    @GET
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public MeasureTypeList getMeasureTypes() {
        MeasureTypeList list = new MeasureTypeList();
    	return list;
    }
   

}
