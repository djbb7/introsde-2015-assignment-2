package introsde.assignment2.ehealth.model;

import introsde.assignment2.ehealth.dao.PersonHealthDao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The persistent class for the "Measurement" database table.
 * A Measurement is a specific value of a Measure Type for a
 * specific Person.
 */
@Entity
@Table(name = "Measurement")
@NamedQueries(value={
		@NamedQuery(name="Measurement.findAll", query="SELECT m FROM Measurement m"),
		
		@NamedQuery(name="Measurement.findMostRecentForPerson", 
			query="SELECT m "
				+ "FROM Measurement m "
				+ "WHERE m.person=:person AND m.date="
				+ "(SELECT MAX(mm.date) "
				+ "FROM Measurement mm "
				+ "WHERE mm.person=m.person AND mm.measureDefinition=m.measureDefinition) "
				+ "GROUP BY m.measureDefinition"),
			
		@NamedQuery(name="Measurement.findForPersonByType",
			query="SELECT m "
				+ "FROM Measurement m "
				+ "WHERE m.person=:person AND m.measureDefinition.name=:measurementType "
				+ "ORDER BY m.date DESC"
		),
		
		@NamedQuery(name="Measurement.findMeasurement",
			query="SELECT m "
				+ "FROM Measurement m "
				+ "WHERE m.person=:person "
				+ "AND m.measureDefinition.name=:measurementType "
				+ "AND m.id=:id"
		)
})
@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement(name="measure")
@XmlType(propOrder={"id", "date", "measureName", "value", "measureUnits"})
public class Measurement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="sqlite_measurement")
	@TableGenerator(name="sqlite_measurement", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="Measurement")
	@Column(name = "id")
	private int id;

	@Column(name = "value")
	private String value;
	
    @Temporal(TemporalType.DATE) // defines the precision of the date attribute
	@Column(name = "timestamp")
	private Date date;
	
	@OneToOne
	@JoinColumn(name = "nameMeasureDefinition", referencedColumnName = "name", insertable = true, updatable = true)
	private MeasureType measureDefinition;
	
	@ManyToOne
	@JoinColumn(name="idPerson",referencedColumnName="id")
	private Person person;

	public Measurement() {
	}

	@XmlElement(name="mid")
	public int getId() {
		return this.id;
	}

	public void setId(int idMeasure) {
		this.id = idMeasure;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlElement(name="created")
	public void setDate(Date date){
		this.date = date;
	}
	
	public Date getDate(){
		return this.date;
	}
	
	@XmlElement(name="measure")
	public String getMeasureName(){
		return measureDefinition.getName();
	}
	
	public void setMeasureName(String name){
		setMeasureDefinition(MeasureType.getMeasureTypeByName(name));
	}
	
	@XmlElement(name="units")
	public String getMeasureUnits(){
		return measureDefinition.getMeasureUnits();
	}

	@XmlTransient
	public MeasureType getMeasureDefinition() {
		return measureDefinition;
	}

	public void setMeasureDefinition(MeasureType param) {
		this.measureDefinition = param;
	}

	// we make this transient for JAXB to avoid and infinite loop on serialization
	@XmlTransient
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	
	// Database operations
	
	/**
	 * Given an id, retrieve the corresponding Measurement
	 * @param measureId
	 * @return
	 */
	public static Measurement getMeasureById(int measureId) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		Measurement m = em.find(Measurement.class, measureId);
		PersonHealthDao.instance.closeConnections(em);
		return m;
	}
	
	/**
	 * Get the list of all Measurements for all Person's
	 * @return
	 */
	public static List<Measurement> getAll() {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
	    List<Measurement> list = em.createNamedQuery("Measurement.findAll", Measurement.class).getResultList();
	    PersonHealthDao.instance.closeConnections(em);
	    return list;
	}
	
	/**
	 * Save a new Measurement in the database
	 * @param m Measurement to be created
	 * @return Measurement with id set
	 */
	public static Measurement saveMeasure(Measurement m) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(m);
		tx.commit();
	    PersonHealthDao.instance.closeConnections(em);
	    return m;
	}
	
	/**
	 * Update a Measurement
	 * @param m Measurement to be updated
	 * @return Measurement with updated fields
	 */
	public static Measurement updateMeasure(Measurement m) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		m = em.merge(m);
		tx.commit();
	    PersonHealthDao.instance.closeConnections(em);
	    return m;
	}
	
	/**
	 * Delete a Measurement from the database
	 * @param m Measurement to be deleted
	 */
	public static void removeMeasure(Measurement m) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    m = em.merge(m);
	    em.remove(m);
	    tx.commit();
	    PersonHealthDao.instance.closeConnections(em);
	}
}
