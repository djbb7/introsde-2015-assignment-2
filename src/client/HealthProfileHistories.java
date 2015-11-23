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
public class HealthProfileHistories {

	@XmlElement(name="measure")
	private List<MeasurementWithId> measurements;
	
	public HealthProfileHistories(){
		
	}

	
	@JsonValue
	public List<MeasurementWithId> getMeasurements(){
		return measurements;
	}

	public void setMeasureHistory(List<MeasurementWithId> history){
		this.measurements = history;
	}
	
	
}
