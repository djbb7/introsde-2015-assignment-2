package client;


import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * POJO for handling Measurement history response
 * from server.
 */
@XmlRootElement
public class MeasureHistory {

	@XmlElement(name="measure")
	private List<Measurement> measurements;
	
	public MeasureHistory(){
		
	}

	
	@JsonValue
	public List<Measurement> getMeasurements(){
		return measurements;
	}

	public void setMeasureHistory(List<Measurement> history){
		this.measurements = history;
	}
	
	
}
