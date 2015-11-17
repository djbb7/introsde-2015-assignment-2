package introsde.assignment2.ehealth.model;

import java.util.List;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="measureTypes")
@Entity
public class MeasureTypeList {
	
	private List<MeasureType> measures;
	
	public MeasureTypeList(){
		measures = MeasureType.getAll();
	}
	
	@XmlElement(name="measureType")
	public List<MeasureType> getMeasureTypes(){
		return measures;
	}
	
	public void setMeasureTypes(List<MeasureType> list){
		measures = list;
	}
}