package introsde.assignment2.ehealth.model;


import introsde.assignment2.ehealth.dao.PersonHealthDao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;
@Entity  // indicates that this class is an entity to persist in DB
@Table(name="Person") // to whole table must be persisted 
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@XmlType(propOrder={"id", "firstname", "lastname", "birthdate", "healthProfile"})
@XmlRootElement
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id // defines this attributed as the one that identifies the entity
    @GeneratedValue(generator="sqlite_person")
    @TableGenerator(name="sqlite_person", table="sqlite_sequence",
        pkColumnName="name", valueColumnName="seq",
        pkColumnValue="Person")
    @Column(name="id")
    private int id;
    
    @Column(name="lastname")
    private String lastname;
    
    @Column(name="firstname")
    private String firstname;
    
    @Temporal(TemporalType.DATE) // defines the precision of the date attribute
    @Column(name="birthdate")
    private Date birthdate; 
    
    // mappedBy must be equal to the name of the attribute in Measurement that maps this relation
    @OneToMany(mappedBy="person",cascade=CascadeType.ALL,fetch=FetchType.EAGER)    
    private List<Measurement> healthProfile;

    // add below all the getters and setters of all the private attributes
    
    // getters
    public int getId(){
        return id;
    }

    public String getLastname(){
        return lastname;
    }
    public String getFirstname(){
        return firstname;
    }
    public Date getBirthdate(){
        return birthdate;
    }
    // setters
    public void setId(int idPerson){
        this.id = idPerson;
    }
    public void setLastname(String lastname){
        this.lastname = lastname;
    }
    public void setFirstname(String name){
        this.firstname = name;
    }
    public void setBirthdate(Date birthdate){
        this.birthdate = birthdate;
    }
    
    @XmlElementWrapper(name="healthProfile")
    @XmlElement(name="measurement")
    @JsonProperty("healthProfile")
    public List<Measurement> getHealthProfile() {
    	EntityManager em = PersonHealthDao.instance.createEntityManager();
    	List<Measurement> list =  em.createNamedQuery("Measurement.findMostRecentForPerson", Measurement.class)
    		.setParameter("person", this)
        	.getResultList();
        PersonHealthDao.instance.closeConnections(em);
        return list;        
    }
    
    @XmlTransient
    public List<Measurement> getList(){
    	return healthProfile;
    }
    
    public void setHealthProfile(List<Measurement> list){
    	this.healthProfile = list;
    }
    
    public List<Measurement> getMeasureHistory(String type){
    	EntityManager em = PersonHealthDao.instance.createEntityManager();
        List<Measurement> list = em.createNamedQuery("Measurement.findForPersonByType", Measurement.class)
        		.setParameter("person", this)
        		.setParameter("measurementType", type)
        		.getResultList();
        PersonHealthDao.instance.closeConnections(em);
        return list;
    }
    
    public Measurement getMeasurement(String measurementType, int measurementId){
    	EntityManager em = PersonHealthDao.instance.createEntityManager();
    	return em.createNamedQuery("Measurement.findMeasurement", Measurement.class)
    		.setParameter("person", this)
    		.setParameter("id", measurementId)
    		.setParameter("measurementType", measurementType)
    		.getSingleResult();
    }
    
    public static Person getPersonById(int personId) {
        EntityManager em = PersonHealthDao.instance.createEntityManager();
        Person p = em.find(Person.class, personId);
        PersonHealthDao.instance.closeConnections(em);
        return p;
    }

    public static List<Person> getAll() {
        EntityManager em = PersonHealthDao.instance.createEntityManager();
        List<Person> list = em.createNamedQuery("Person.findAll", Person.class)
            .getResultList();
        PersonHealthDao.instance.closeConnections(em);
        return list;
    }

    public static Person savePerson(Person p) {
        EntityManager em = PersonHealthDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(p);
        tx.commit();
        PersonHealthDao.instance.closeConnections(em);
        return p;
    } 

    public static Person updatePerson(Person p) {
        EntityManager em = PersonHealthDao.instance.createEntityManager(); 
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        tx.commit();
        PersonHealthDao.instance.closeConnections(em);
        return p;
    }

    public static void removePerson(Person p) {
        EntityManager em = PersonHealthDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        em.remove(p);
        tx.commit();
        PersonHealthDao.instance.closeConnections(em);
    }
    
}