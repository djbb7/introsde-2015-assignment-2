package client;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class eHealthClient {
	
	static WebTarget service;
	
	static String outputFormatPUT = "Request #%d: %s %s Accept: %s Content-type: %s\n"
								 +"=> Result: %s\n"
								 +"=> HTTP Status: %d\n"
								 +"%s\n";

	static String outputFormatGET = "Request #%d: %s %s Accept: %s\n"
								 +"=> Result: %s\n"
								 +"=> HTTP Status: %d\n"
								 +"%s\n";

	static String outputFormatDELETE = "Request #%d: %s %s\n"
								 +"=> Result: %s\n"
								 +"=> HTTP Status: %d\n";

	static String mediaTypeHeader = "\n###################################################################\n"+
									"#####\n"+
									"###   Running tests with Accept and Content-type: %s\n"+
									"#\n";
			
	static String testHeader = "\n###################################################################\n"+
							   "## TEST %s \n"+
							   "#-----------------------------------------------------------------\n";
	
	private static Person firstPerson, lastPerson, createdPerson;

	//For passing one test's results to the next
	private static String foundMeasureType;
	private static int foundMeasureId;
	private static int foundPersonId;
	
	private static MeasureTypeList measureTypes;
	
	public static void main(String[] args){
		if(args.length < 2){
			System.err.println("Must specify web service endpoint location "+
					"and media type (\"application/xml\", \"application/json\")");
			return;
		}
		
		String serverURL = args[0];
		String mediaType = args[1];
		
		//#######################
		//####    SET SERVER
		//#######################
		
		ClientConfig clientConfig = new ClientConfig();
		//Long timeout in case heroku server is sleeping
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60*1000);
		Client client = ClientBuilder.newClient(clientConfig);
		service = client.target(args[0]);
		
		System.out.println(String.format("Working with server: %s\n", serverURL));
		
		
		//#######################
		//####    RUN TESTS
		//#######################
		
		System.out.println(String.format(mediaTypeHeader, mediaType.toUpperCase()));
		
		//Read all people, save firstPerson and lastPerson 's ids
		testStep__3_1(mediaType);
		
		//Read firstPerson
		testStep__3_2(mediaType);
		
		//Update firstPerson's firstname 
		testStep__3_3(mediaType);
		
		//Create person	"Chuck Norris"
		testStep__3_4(mediaType);
		
		//Delete person	"Chuck Norris" and check he was deleted
		testStep__3_5(mediaType);
		
		//Read measure types
		testStep__3_6(mediaType);
		
		//Read measure history for firstPerson,secondPerson and every measureType
		//Save a valid measurement's id
		testStep__3_7(mediaType);
		
		//Read saved measurement by id
		testStep__3_8(mediaType);
		
		//Count measurements for person and measureType;
		//Create new measurement;
		//Check count increased
		testStep__3_9(mediaType);
		
	
	}

	/**
	 * GET all people, save first and last Person
	 * @param mediaType
	 */
	private static void testStep__3_1(String mediaType){
		System.out.println(String.format(testHeader, "3.1"));

		Response r = testGeneric("/person", "GET", mediaType, null);

		List<Person> people = r.readEntity(new GenericType<List<Person>>() {});
		
		String status = (people.size()>2)? "OK" : "ERROR";
		printResult(1, "GET", "/person", mediaType, status, r);
		
		if(people.size()>0){
			firstPerson = people.get(0);
			lastPerson = people.get(people.size()-1);
		} else {
			System.err.println("Error: empty people database. Cannot proceed.");
			System.exit(0);
		}
	}
	
	/**
	 * GET first Person by id
	 * @param mediaType
	 */
	private static void testStep__3_2(String mediaType){
		System.out.println(String.format(testHeader, "3.2"));
		
		Response r = testGeneric("/person/"+firstPerson.id, "GET", mediaType, null);
		
		String status = (r.getStatus() == 200 || r.getStatus() == 202)? "OK" : "ERROR";
		
		printResult(2, "GET", "/person/"+firstPerson.id, mediaType, status, r);
	}

	/**
	 * PUT first Person, changing the firstname
	 * @param mediaType
	 */
	private static void testStep__3_3(String mediaType){
		System.out.println(String.format(testHeader, "3.3"));

		String newName = new StringBuilder(firstPerson.firstname).reverse().toString();
		firstPerson.firstname = newName;
		firstPerson.measure = null;
		Response r = testGeneric("/person/"+firstPerson.id, "PUT", mediaType, firstPerson);
		Person responsePerson = r.readEntity(Person.class);
		String status = (responsePerson.firstname.equals(newName))? "OK" : "ERROR";
		
		printResult(3, "PUT", "/person/"+firstPerson.id, mediaType, status, r);
	}
	
	/**
	 * POST new Person "Chuck Norris"
	 * @param mediaType
	 */
	private static void testStep__3_4(String mediaType){
		System.out.println(String.format(testHeader, "3.4"));

		Person chuck = new Person();
		chuck.firstname = "Chuck";
		chuck.lastname = "Norris";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1945);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		chuck.setBirthdate(cal.getTime());
		chuck.measure = new ArrayList<Measurement>();
		chuck.measure.add(new Measurement("weight", "78.9"));
		chuck.measure.add(new Measurement("height", "172"));
		
		Response r = testGeneric("/person", "POST", mediaType, chuck);

		Person responsePerson = r.readEntity(Person.class);
		String status = (responsePerson.id != 0 &&
				(r.getStatus() == 200 || r.getStatus() == 201 || r.getStatus() == 202))? "OK" : "ERROR";

		printResult(4, "POST", "/person", mediaType, status, r);

		createdPerson = responsePerson;
	}
	
	/**
	 * DELETE "Chuck Norris" and verify the resource was deleted
	 * @param mediaType
	 */
	private static void testStep__3_5(String mediaType){
		System.out.println(String.format(testHeader, "3.5"));

		Response r = testGeneric("/person/"+createdPerson.id, "DELETE", mediaType, null);
		
		String status = (r.getStatus() == 204)?"OK":"ERROR";
		printResult(5, "DELETE", "/person/"+createdPerson.id, mediaType, status, r);
		
		r = testGeneric("/person/"+createdPerson.id, "GET", mediaType, null);
		
		status = (r.getStatus() == 404)? "OK" : "ERROR";
		
		printResult(2, "GET", "/person/"+createdPerson.id, mediaType, status, r);
	}
	
	/**
	 * GET valid measure types
	 * @param mediaType
	 */
	private static void testStep__3_6(String mediaType){
		System.out.println(String.format(testHeader, "3.6"));

		Response r = testGeneric("/measureTypes", "GET", mediaType, null);
		
		MeasureTypeList m = null;
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			m = r.readEntity(MeasureTypeList.class);			
		} else if (mediaType.equals(MediaType.APPLICATION_JSON)){
			List<SimpleMeasureType> li= r.readEntity(new GenericType<List<SimpleMeasureType>>() {});
			m = new MeasureTypeList();
			m.setMeasureTypes(new ArrayList<String>());
			for(SimpleMeasureType sMT: li ){
				m.getMeasureTypes().add(sMT.value);
			}
		}
		
		String status = (m.getMeasureTypes().size()>2)? "OK" : "ERROR";
		printResult(9, "GET", "/measureTypes", mediaType, status, r);
		
		measureTypes = m;
	}
	
	/**
	 * For first Person and second Person, get all measure types
	 * @param mediaType
	 */
	private static void testStep__3_7(String mediaType){
		System.out.println(String.format(testHeader, "3.7"));

		ArrayList<Response> responseHist = new ArrayList<Response>();
		String status = "ERROR";
		String foundMeasureType = null;
		for(Person p : new Person[]{firstPerson, lastPerson}){
			for(String measureType : measureTypes.getMeasureTypes()){
				Response r = testGeneric("/person/"+p.id+"/"+measureType, "GET", mediaType, null);
				List<MeasurementWithId> measurements = null;
				if(mediaType.equals(MediaType.APPLICATION_XML)){
					measurements = r.readEntity(HealthProfileHistories.class).getMeasurements();
				} else if (mediaType.equals(MediaType.APPLICATION_JSON)){
					measurements = r.readEntity(new GenericType<List<MeasurementWithId>>() {});
				}
				
				if(measurements!=null && measurements.size() > 0){
						eHealthClient.foundMeasureType = foundMeasureType = measureType;
						eHealthClient.foundMeasureId = measurements.get(0).getMid();
						eHealthClient.foundPersonId = p.id;
				}		
				responseHist.add(r);
			}
		}
		
		if(foundMeasureType != null){
			status = "OK";
		}
		
		int i = 0;
		for(Person p: new Person[]{firstPerson, lastPerson}){
			for(String measureType : measureTypes.getMeasureTypes()){
				printResult(6, "GET", "/person/"+p.id+"/"+measureType, mediaType, status, responseHist.get(i));
				i++;
			}
		}
	}
	
	/**
	 * GET a specific measure
	 * @param mediaType
	 */
	private static void testStep__3_8(String mediaType){
		System.out.println(String.format(testHeader, "3.8"));

		String path = "/person/"+foundPersonId+"/"
				  		+foundMeasureType+"/"
				  		+foundMeasureId;
		Response r = testGeneric(path, "GET", mediaType, null);
		
		String status = (r.getStatus() == 200)? "OK" : "ERROR";
		
		printResult(7, "GET", path, mediaType, status, r);
	}
	
	/**
	 * POST measurement and verify it was created
	 * @param mediaType
	 */
	private static void testStep__3_9(String mediaType){
		System.out.println(String.format(testHeader, "3.9"));

		String status;
		//Calculate initial measurement count
		String measureType = measureTypes.getMeasureTypes().get(0);
		String pathInitial = "/person/"+firstPerson.id+"/"+measureType;
		Response rInitial = testGeneric(pathInitial, "GET", mediaType, null);
		
		List<MeasurementWithId> measurements = null;
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			measurements = rInitial.readEntity(HealthProfileHistories.class).getMeasurements();
		} else if (mediaType.equals(MediaType.APPLICATION_JSON)){
			measurements = rInitial.readEntity(new GenericType<List<MeasurementWithId>>() {});
		}
		int initialCount = measurements.size();
		status = (rInitial.getStatus() == 200)?"OK":"ERROR";
		printResult(6, "GET", pathInitial, mediaType, status, rInitial);

		//Add new measure
		String pathAdd = "/person/"+firstPerson.id+"/"+measureType;
		String newMeasure;
		
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			newMeasure = "<measure>"+
							"<value>72</value>"+
							"<created>2011-12-09</created>"+
							"</measure>";
		} else {
			newMeasure = "{"+
							  "\"value\": \"66.5\","+
							  "\"created\": \"2011-12-09\""+
							"}";
		}
		Response rAdd = testGeneric(pathAdd, "POST", mediaType, newMeasure);
		String ee = rAdd.readEntity(String.class);

		status = (rAdd.getStatus() == 200 || rAdd.getStatus() == 201 || rAdd.getStatus() == 202)?
				 "OK" : "ERROR";
		printResult(8, "POST", pathAdd, mediaType, status, rAdd);
		
		//Check measure count increased
		Response rFinal = testGeneric(pathInitial, "GET", mediaType, null);
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			measurements = rFinal.readEntity(HealthProfileHistories.class).getMeasurements();
		} else if (mediaType.equals(MediaType.APPLICATION_JSON)){
			measurements = rFinal.readEntity(new GenericType<List<MeasurementWithId>>() {});
		}
		int finalCount = measurements.size();
		status = (finalCount == initialCount+1)? "OK":"ERROR";
		printResult(6, "GET", pathInitial, mediaType, status, rFinal);
	}
	
	/**
	 * Generic function for performing a request and getting back the result
	 * @param URIPath Path of request
	 * @param method HTTP method ("GET", "POST", "PUT", or "DELETE")
	 * @param mediaType "application/xml" or "application/json"
	 * @param requestBody Request body to be sent, can be null.
	 * @return
	 */
	private static Response testGeneric(String URIPath,  String method, String mediaType, Object requestBody){
		Response response = null;
		Invocation.Builder builder = service.path(URIPath)
				.request()
				.accept(mediaType)
				.header("Content-Type", mediaType);
		
		Entity<Object> body = null;
		if(requestBody != null){
			if(mediaType.equals(MediaType.APPLICATION_XML)){
				body = Entity.xml(requestBody);
			} else if (mediaType.equals(MediaType.APPLICATION_JSON)){
				body = Entity.json(requestBody);
			}
		}
		
		if(method.equals("GET")){
			response = builder.get();
		} else if(method.equals("POST")){
			response = builder.post(body);
		} else if(method.equals("PUT")){
			response = builder.put(body);
		} else if(method.equals("DELETE")){
			response = builder.delete();
		} else {
			throw new RuntimeException("Unexpected HTTP method: "+method);
		}
		
		response.bufferEntity();
		return response;
	}
	
	/**
	 * Output request's result in pretty format.
	 * @param reqNumber Number of request.
	 * @param reqMethod HTTP method used ("GET", "POST", "PUT" or "DELETE")
	 * @param path The requested URI
	 * @param acceptType The value of the "Accept" header sent, i.e. "application/xml" or "application/json"
	 * @param statusString "OK" or "ERROR"
	 * @param r Response sent by the server
	 */
	private static void printResult(int reqNumber, String reqMethod, String path, String acceptType, String statusString, Response r){
		int statusCode = r.getStatus();
		String result = r.readEntity(String.class);
		String prettyPrint = "";
		if(result != null && result.length() !=0){
			prettyPrint = (r.getMediaType().toString().equals(MediaType.APPLICATION_XML))? prettyXML(result, 5): prettyJSON(result, 5);
		}

		if(reqMethod.equals("GET")){
			System.out.println(String.format(outputFormatGET, reqNumber, reqMethod, path , acceptType, statusString, statusCode, prettyPrint));			
		} else if(reqMethod.equals("PUT") || reqMethod.equals("POST")){
			System.out.println(String.format(outputFormatPUT, reqNumber, reqMethod, path , acceptType, r.getMediaType().toString(), statusString, statusCode, prettyPrint));			
		} else if(reqMethod.equals("DELETE")){
			System.out.println(String.format(outputFormatDELETE, reqNumber, reqMethod, path, statusString, statusCode));			
		}

	}
	
	/**
	 * Given a JSON string, beautify it for printing.
	 * @param jsonString
	 * @param indent
	 * @return
	 */
	private static String prettyJSON(String jsonString, int indent){
		String res;
		
		if(jsonString.length()==0)
			return "";
		
		char c = jsonString.trim().charAt(0);
		if(c == '['){
			JSONArray json = new JSONArray(jsonString);
			res = json.toString(indent);
		} else {
			JSONObject json = new JSONObject(jsonString);
			res = json.toString(indent);
		}
		return res;
	}
	
	/**
	 * Given an XML string, beautify it for printing.
	 * Taken from: http://myshittycode.com/2014/02/10/java-properly-indenting-xml-string/
	 * Credit to: http://stackoverflow.com/users/341508/limc
	 * @param xml
	 * @param indent
	 * @return
	 */
	private static String prettyXML(String xml, int indent) {
	    try {
	        // Turn xml string into a document
	        Document document = DocumentBuilderFactory.newInstance()
	                .newDocumentBuilder()
	                .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

	        // Remove whitespaces outside tags
	        XPath xPath = XPathFactory.newInstance().newXPath();
	        NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
	                                                      document,
	                                                      XPathConstants.NODESET);

	        for (int i = 0; i < nodeList.getLength(); ++i) {
	            Node node = nodeList.item(i);
	            node.getParentNode().removeChild(node);
	        }

	        // Setup pretty print options
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	        // Return pretty print xml string
	        StringWriter stringWriter = new StringWriter();
	        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
	        return stringWriter.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
