package introsde.assignment2.ehealth.model;

import introsde.assignment2.ehealth.dao.PersonHealthDao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;



/**
 * The persistent class for the "MeasureType" database table.
 * 
 */
@Table(name="MeasureDefinition")
@NamedQuery(name="MeasureType.findAll", query="SELECT m FROM MeasureType m")
@XmlRootElement(name="measureType")
@Entity
public class MeasureType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="name")
	private String name;
	
	@Column(name="units")
	private String units;

	public MeasureType() {
	}

	@XmlValue
	public String getName() {
		return this.name;
	}

	public void setName(String measureName) {
		this.name = measureName;
	}

	@XmlTransient
	public String getMeasureUnits(){
		return this.units;
	}

	public void setMeasureUnits(String units){
		this.units = units;
	}

	// database operations
	public static MeasureType getMeasureTypeByName(String name) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		MeasureType p = em.find(MeasureType.class, name);
		PersonHealthDao.instance.closeConnections(em);
		return p;
	}
	
	public static List<MeasureType> getAll() {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
	    List<MeasureType> list = em.createNamedQuery("MeasureType.findAll", MeasureType.class).getResultList();
	    PersonHealthDao.instance.closeConnections(em);
	    return list;
	}
	
	public static MeasureType saveMeasureType(MeasureType p) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
	    PersonHealthDao.instance.closeConnections(em);
	    return p;
	}
	
	public static MeasureType updateMeasureType(MeasureType p) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p=em.merge(p);
		tx.commit();
	    PersonHealthDao.instance.closeConnections(em);
	    return p;
	}
	
	public static void removeMeasureType(MeasureType p) {
		EntityManager em = PersonHealthDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    PersonHealthDao.instance.closeConnections(em);
	}
	
	@JsonValue 
	public String toString() {
		return getName();
	}

	
	/*public static List<MeasureType> getMeasureTypes(){
		
	}*/
	

}
