package client;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Measurement {
	
	public int mid;
	
	public Date created;
	
	public String measure;
	
	public String value;
	
	public String units;
	
}
