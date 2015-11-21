package client;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class eHealthClient {
	
	static WebTarget service;
	
	static String outputFormat = "Request #%d: %s %s Accept: %s Content-type: %s\n"
								 +"=> Result: %s\n"
								 +"=> HTTP Status: %d\n"
								 +"%s\n";
	
	private static Response responseLastRequest;
	private static int validPersonId;
	private static int validMeasureId;
	
	public static void main(String[] args){
		if(args.length < 1){
			System.err.println("Must specify web service endpoint location");
			return;
		}
		
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		service = client.target(args[0]);
		
		//#######################
		//####    STEP 1
		//#######################
		System.out.println(String.format("Working with server: %s", args[0]));
		
		//#######################
		//####    STEP 2
		//#######################
		
		/* * * * * * * * * *
		 * Application/XML
		 * * * * * * * * * */
		
		//R1: Read all people		-> GET /person
		testR1(MediaType.APPLICATION_XML);
		
		//R2: Read specific person	-> GET /person/{id}
		testR2(MediaType.APPLICATION_XML);
		
		//R3: Update person			-> PUT /person/{id} 
		testR3(MediaType.APPLICATION_XML);
		
		//R4: Create person			-> POST /person
		testR4(MediaType.APPLICATION_XML);
		
		//R5: Delete person			-> DELETE /person/{id}
		testR5(MediaType.APPLICATION_XML);
		
		//R6: Read measure history	-> GET /person/{id}/{measureType}
		testR6(MediaType.APPLICATION_XML);
		
		//R7: Read measure value	-> GET /person/{id}/{measureType}/{mid}
		testR7(MediaType.APPLICATION_XML);
		
		//R8: Create measurement	-> POST /person/{id}/{measureType}
		testR8(MediaType.APPLICATION_XML);
		
		//R9: Read measure types	-> GET /measureTypes
		testR9(MediaType.APPLICATION_XML);
		
		/* * * * * * * * * *
		 * Application/JSON
		 * * * * * * * * * */
		
		//R1: Read all people		-> GET /person
		testR1(MediaType.APPLICATION_JSON);
		
		//R2: Read specific person	-> GET /person/{id}
		testR2(MediaType.APPLICATION_JSON);
		
		//R3: Update person			-> PUT /person/{id} 
		testR3(MediaType.APPLICATION_JSON);
		
		//R4: Create person			-> POST /person
		testR4(MediaType.APPLICATION_JSON);
		
		//R5: Delete person			-> DELETE /person/{id}
		testR5(MediaType.APPLICATION_JSON);
		
		//R6: Read measure history	-> GET /person/{id}/{measureType}
		testR6(MediaType.APPLICATION_JSON);
		
		//R7: Read measure value	-> GET /person/{id}/{measureType}/{mid}
		testR7(MediaType.APPLICATION_JSON);
		
		//R8: Create measurement	-> POST /person/{id}/{measureType}
		testR8(MediaType.APPLICATION_JSON);
		
		//R9: Read measure types	-> GET /measureTypes
		testR9(MediaType.APPLICATION_JSON);
	}

	private static void testR1(String mediaType){
		testGeneric(1, "/person", "GET", mediaType, 200, null);

		List<Person> people = responseLastRequest.readEntity(new GenericType<List<Person>>() {});
		validPersonId = people.get(people.size()-1).id;
	}
	
	private static void testR2(String mediaType){
		testGeneric(2, "/person/"+validPersonId, "GET", mediaType, 200, null);
	}
	
	private static void testR3(String mediaType){
		String body;
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			body ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person>"
				+    "<id>56</id>"
				+    "<firstname>Sarai</firstname> "
				+ "<lastname>Cardoso</lastname>"
				 +   "<birthdate>1978-09-02T00:00:00+02:00</birthdate>"
				+"</person>";
		} else {
			body = "{" +
					  "\"id\": 1," +
					  "\"firstname\": \"Sandra\", " +
					  "\"lastname\": \"Cardosinho\"," +
					  "\"birthdate\": 273535200000" +
				  "}";
		}
		testGeneric(3, "/person/"+validPersonId, "PUT", mediaType, 200, body);
	}
	
	private static void testR4(String mediaType){
		String body;
		
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			body = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
						"<person>"+
						"    <firstname>Peter</firstname>"+
						"    <lastname>Smith</lastname>"+
						"    <birthdate>1994-09-02T00:00:00+02:00</birthdate>"+
						"    <healthProfile>"+
						"        <measurement>"+
						"            <measure>height</measure>"+
						"            <value>172</value>>"+
						"        </measurement>"+
						"        <measurement>"+
						"            <measure>weight</measure>"+
						"            <value>58</value>"+
						"        </measurement>"+
						"    </healthProfile>"+
						"</person>";
		} else {
			body = "{"+
				   "  \"firstname\": \"Payel\","+
				   "  \"lastname\": \"Kalabak\","+
				   "  \"birthdate\": 273535200000,"+
				   "  \"healthProfile\": ["+
				   "    {"+
				   "      \"measure\": \"height\","+
				   "      \"value\": \"199\""+
				   "    },"+
				   "    {"+
				   "      \"created\": 1433628000000,"+
				   "      \"measure\": \"weight\","+
				   "      \"value\": \"75\""+
				   "    }"+
				   "  ]"+
				   "}";
		}
		
		testGeneric(4, "/person", "POST", mediaType, 201, body);
	}
	
	private static void testR5(String mediaType){
		String[] pathNewPerson = responseLastRequest.getLocation().getPath().split("/", 0);
		testGeneric(5, "/person/"+pathNewPerson[pathNewPerson.length-1], "DELETE", mediaType, 204, null);
	}

	private static void testR6(String mediaType){
		testGeneric(6, "/person/"+validPersonId+"/weight", "GET", mediaType, 200, null);

		//save for future tests
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			validMeasureId = responseLastRequest.readEntity(MeasureHistory.class).getMeasurements().get(0).mid;
		} else {
			validMeasureId = responseLastRequest.readEntity(new GenericType<List<Measurement>>() {}).get(0).mid;
		}
	}
	
	private static void testR7(String mediaType){
		testGeneric(7, "/person/"+validPersonId+"/weight/"+validMeasureId, "GET", mediaType, 200, null);
	}
	
	private static void testR8(String mediaType){
		String body;
		if(mediaType.equals(MediaType.APPLICATION_XML)){
			body = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
					  "<measure>"+
					  "    <value>82</value>"+
					  "</measure>";
		} else {
			body = "{"+
					  "\"value\": \"66.5\""+
					"}";
		}
		
		testGeneric(8, "/person/"+validPersonId+"/weight", "POST", mediaType, 201, body);
	}
	
	private static void testR9(String mediaType){
		testGeneric(9, "/measureTypes", "GET", mediaType, 200, null);
	}
	
	private static void testGeneric(int reqNumber, String URIPath,  String method, String mediaType, int expectedResponse,
									String requestBody){
		Response response = null;
		Invocation.Builder builder = service.path(URIPath)
				.request()
				.accept(mediaType)
				.header("Content-Type", mediaType);
		
		Entity<String> body = null;
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
			throw new RuntimeException("Unexpected header: "+method);
		}
		
		response.bufferEntity();
		responseLastRequest = response;
		int statusCode = response.getStatus();
		String result = response.readEntity(String.class);
		String  status = (statusCode == expectedResponse)? "OK" : "ERROR";
		String prettyPrint = (mediaType == MediaType.APPLICATION_XML)? prettyXML(result, 5): prettyJSON(result, 5);
		System.out.println(String.format(outputFormat, reqNumber, method, URIPath, mediaType, mediaType, status, statusCode, prettyPrint));
	}
	
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
	
	private static String prettyXML(String xml, int indent) {
		if(xml==null || xml.length()==0)
			return "";
		
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
