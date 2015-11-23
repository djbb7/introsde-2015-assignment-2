package introsde.assignment2.ehealth.resources;

import introsde.assignment2.ehealth.model.Person;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
@LocalBean
public class PersonResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;

    EntityManager entityManager; 

    public PersonResource(UriInfo uriInfo, Request request,int id, EntityManager em) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.entityManager = em;
    }

    public PersonResource(UriInfo uriInfo, Request request,int id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    /**
     * Get a Person by his id
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Person getPerson() {
        Person person = this.getPersonById(id);
        if (person == null)
        	throw new NotFoundException("Get: Person with " + id + " not found");
        return person;
    }

    /**
     * Update a Person. HTTP status code set to 200 in case of success.
     * @param person
     * @return A copy of the updated Person
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putPerson(Person person) {
        Response res;
        Person existing = getPersonById(this.id);

        if (existing == null) {
            res = Response.noContent().build();
        } else {
            person.setId(this.id);
            person.setHealthProfile(existing.getHealthProfile());
            person = Person.updatePerson(person);
            res = Response.ok(person).contentLocation(uriInfo.getAbsolutePath()).build();
        }
        return res;
    }
    

    /**
     * Delete a Person. HTTP status code set to 204 if success.
     */
    @DELETE
    public void deletePerson() {
        Person c = getPersonById(id);
        if (c == null)
            throw new NotFoundException("Delete: Person with " + id
                    + " not found");
        Person.removePerson(c);
    }

    //Auxiliary method
    private Person getPersonById(int personId) {
        Person person = Person.getPersonById(personId);
        if(person != null)
        	System.out.println("Person: "+person.toString());
        return person;
    }
}