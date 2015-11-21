package client;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class Person {

	public int id;
	
	public String firstname;
	
	public String lastname;
	
	public Date birthdate;
	
    @XmlElementWrapper(name="healthProfile")
    @XmlElement(name="measurement")
    @JsonProperty("healthProfile")
	public List<Measurement> healthProfile;
	
	public Person(){
		
	}
}
