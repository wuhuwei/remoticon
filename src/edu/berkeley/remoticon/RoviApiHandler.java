package edu.berkeley.remoticon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RoviApiHandler {
	private final String USERNAME = "weitogo";
	private final String PASSWORD = "h3lloworld";
	private final String API_KEY = "6gckp829sp22xcn3puqqsbrc";
	private final String SECRET = "ZpKXWqxauS";

	private String signRequest() {
		long unixTime = System.currentTimeMillis() / 1000L;
		String concatString = API_KEY + SECRET + Long.toString(unixTime);
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(concatString.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONObject readResponse(BufferedReader in) {
		String response;
		try {
			response = "";
			String line;
			while ((line = in.readLine()) != null) {
				response += line;
			}
			return (JSONObject)JSONValue.parse(response);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public JSONArray getProviders(String zipcode) {
		String urlTemplate = "http://api.rovicorp.com/TVlistings/v9/listings/services/postalcode/%ZIP%/info?locale=en-US&countrycode=US&apikey=%KEY%&sig=%SIG%";
		urlTemplate = urlTemplate.replace("%ZIP%", zipcode);
		urlTemplate = urlTemplate.replace("%KEY%", API_KEY);
		urlTemplate = urlTemplate.replace("%SIG%", signRequest());

		try {
			URL apiURL = new URL(urlTemplate);
			HttpURLConnection connection = (HttpURLConnection) apiURL
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			JSONObject response = readResponse(in);
			JSONObject services = (JSONObject)((JSONObject) response.get("ServicesResult")).get("Services");
			return (JSONArray)services.get("Service");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		/*
		 * try {
		 * 
		 * 
		 * SOAPMessage message = MessageFactory.newInstance().createMessage();
		 * SOAPPart soapPart = message.getSOAPPart(); SOAPEnvelope soapEnvelope
		 * = soapPart.getEnvelope();
		 * soapEnvelope.addAttribute(soapEnvelope.createName("xmlns:typ"),
		 * "http://api.rovicorp.com/v9/common/types");
		 * soapEnvelope.addAttribute(soapEnvelope.createName("xmlns:lis"),
		 * "http://api.rovicorp.com/v9/listings");
		 * 
		 * SOAPHeader soapHeader = soapEnvelope.getHeader();
		 * 
		 * SOAPHeaderElement authHeader =
		 * soapHeader.addHeaderElement(soapEnvelope.createName("AuthHeader",
		 * "typ", "http://api.rovicorp.com/v9/common/types")); SOAPElement
		 * usernameElement = authHeader.addChildElement("UserName", "typ");
		 * usernameElement.setTextContent(USERNAME); SOAPElement passwordElement
		 * = authHeader.addChildElement("Password", "typ");
		 * passwordElement.setTextContent(PASSWORD);
		 * 
		 * SOAPBody soapBody = soapEnvelope.getBody(); SOAPElement
		 * servicesElement =
		 * soapBody.addBodyElement(soapEnvelope.createName("GetServices", "lis",
		 * "http://api.rovicorp.com/v9/listings")); SOAPElement requestElement =
		 * servicesElement.addChildElement("Request", "lis");
		 * requestElement.addChildElement("Locale",
		 * "lis").setTextContent("en-US"); SOAPElement searchElement =
		 * requestElement.addChildElement("ServiceSearch", "lis");
		 * searchElement.addChildElement("PostalCode",
		 * "lis").setTextContent("90210");
		 * searchElement.addChildElement("CountryCode",
		 * "lis").setTextContent("US");
		 * 
		 * 
		 * 
		 * ByteArrayOutputStream output = new ByteArrayOutputStream();
		 * message.writeTo(output); String xmlString = new
		 * String(output.toByteArray());
		 * 
		 * URL url = new
		 * URL("http://api.rovicorp.com/v9/listingsservice.asmx?apikey=" +
		 * API_KEY); HttpURLConnection connection = (HttpURLConnection)
		 * url.openConnection(); connection.setRequestProperty("Content-Length",
		 * String.valueOf(xmlString.length()));
		 * connection.setRequestProperty("Content-Type", "text/xml");
		 * connection.setRequestProperty("Connection", "Close");
		 * connection.setRequestProperty("SoapAction", "");
		 * connection.setDoOutput(true);
		 * 
		 * OutputStreamWriter wr = new
		 * OutputStreamWriter(connection.getOutputStream());
		 * wr.write(xmlString); wr.flush();
		 * 
		 * BufferedReader rd = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream()));
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

	public JSONArray getListings(String serviceID) {
		String urlTemplate = "http://api.rovicorp.com/TVlistings/v9/listings/linearschedule/%SERVICEID%/info?locale=en-US&oneairingpersourceid=true&apikey=%KEY%&sig=%SIG%";
		urlTemplate = urlTemplate.replace("%SERVICEID%", serviceID);
		urlTemplate = urlTemplate.replace("%KEY%", API_KEY);
		urlTemplate = urlTemplate.replace("%SIG%", signRequest());

		try {
			URL apiURL = new URL(urlTemplate);
			HttpURLConnection connection = (HttpURLConnection) apiURL
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			JSONObject response = readResponse(in);
			JSONObject schedule = (JSONObject)((JSONObject) response.get("LinearScheduleResult")).get("Schedule");
			return (JSONArray)schedule.get("Airings");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		RoviApiHandler testHandler = new RoviApiHandler();
		testHandler.getProviders("94709");
		System.out.println(testHandler.getListings("360861"));

	}
}
