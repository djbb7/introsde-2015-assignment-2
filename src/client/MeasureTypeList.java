package client;

import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for handling Measure Types response
 * from server
 */
@XmlRootElement(name="measureTypes")
@Entity
public class MeasureTypeList {
	
	private List<String> measures;
	
	public MeasureTypeList(){
	}
	
	@XmlElement(name="measureType")
	public List<String> getMeasureTypes(){
		return measures;
	}
	
	public void setMeasureTypes(List<String> list){
		measures = list;
	}
}