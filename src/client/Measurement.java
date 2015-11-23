package client;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for handling Measurement response from Server
 */
@XmlRootElement
public class Measurement {
	
	public int mid;
	
	public Date created;
	
	public String measure;
	
	public String value;
	
	public String units;
	
	public Measurement(){
		
	}
	
	public Measurement(String m, String v){
		measure = m;
		value = v;
	}
	
	public Measurement(String m, String v, Date d){
		measure = m;
		value = v;
		created = d;
	}
}
