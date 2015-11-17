package introsde.assignment2.ehealth.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonValue;

@XmlRootElement
public class MeasureHistory {

	@XmlElement(name="measure")
	private List<Measurement> measureHistory;
	
	public MeasureHistory(){
		
	}
	
	public MeasureHistory(Person p, String type){
		measureHistory = p.getMeasureHistory(type);
	}
	
	@JsonValue
	public List<Measurement> getMeasureHistory(){
		return measureHistory;
	}

	
	
}
