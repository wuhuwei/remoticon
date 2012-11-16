package edu.berkeley.remoticon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

public class RoviApiHandler {
	// This is really secure - Justin
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
		String response = "";
		try {
			String line;
			while ((line = in.readLine()) != null) {
				response += line;
			}
			return (JSONObject)JSONValue.parse(response);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public JSONArray getProviders(String zipcode) {
		String urlTemplate = "http://api.rovicorp.com/TVlistings/v9/listings/services/postalcode/%ZIP%/info?locale=en-US&countrycode=US&apikey=%KEY%&sig=%SIG%";
		urlTemplate = urlTemplate.replace("%ZIP%", zipcode);
		urlTemplate = urlTemplate.replace("%KEY%", API_KEY);
		urlTemplate = urlTemplate.replace("%SIG%", signRequest());
		System.out.println(urlTemplate);
		try {
			URL apiURL = new URL(urlTemplate);
			HttpURLConnection connection = (HttpURLConnection) apiURL
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			JSONObject response = readResponse(in);
			JSONObject services = (JSONObject)((JSONObject)response.get("ServicesResult")).get("Services");
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

	public ArrayList<TVGuideEntry> getListings(String serviceID, String startTime) {
		String urlTemplate = "http://api.rovicorp.com/TVlistings/v9/listings/gridschedule/%SERVICEID%/info?locale=en-US&duration=60&inprogress=true&startdate=%START%&apikey=%KEY%&sig=%SIG%";
		urlTemplate = urlTemplate.replace("%SERVICEID%", serviceID);
		urlTemplate = urlTemplate.replace("%START%", startTime);
		urlTemplate = urlTemplate.replace("%KEY%", API_KEY);
		urlTemplate = urlTemplate.replace("%SIG%", signRequest());
		System.out.println(urlTemplate);
		try {
			URL apiURL = new URL(urlTemplate);
			HttpURLConnection connection = (HttpURLConnection) apiURL
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			JSONObject response = readResponse(in);
			JSONArray channels = (JSONArray)((JSONObject)response.get("GridScheduleResult")).get("GridChannels");
			ArrayList<TVGuideEntry> guideEntries = new ArrayList<TVGuideEntry>();
			for(int i = 0; i < channels.size(); i++) {
				JSONObject channel = (JSONObject)channels.get(i);
				Channel c = new Channel();
				c.setAbbr((String)channel.get("CallLetters"));
				c.setFullName((String)channel.get("SourceLongName"));
				c.setId(((Long)channel.get("SourceId")).intValue());
				c.setNumber(Integer.parseInt((String)channel.get("Channel")));
				if(channel.get("Channel") == null) {
					System.out.println("wtfff" + channel.get("SourceLongName"));
				}
				
				JSONArray shows = (JSONArray)channel.get("Airings");
				ArrayList<Program> channelShows = new ArrayList<Program>();
				for(int j = 0; j < shows.size(); j++){
					JSONObject show = (JSONObject)shows.get(j);
					
					Program s = new Program();
					String seriesId = (String)show.get("SeriesId");
					if(seriesId != null) {
						s.setId(Integer.parseInt(seriesId));
					} 
					String time = (String)show.get("AiringTime");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
					Date airingTime = sdf.parse(time);					
					s.setAiringTime(airingTime);
					s.setName((String)show.get("Title"));
					s.setEpisodeTitle((String)show.get("EpisodeTitle"));
					s.setRating((String)show.get("TVRating"));
					s.setCategory((String)show.get("Category"));
					s.setSubcategory((String)show.get("Subcategory"));
					channelShows.add(s);
				}
				
				
				
				
				TVGuideEntry e = new TVGuideEntry(c, channelShows);
				guideEntries.add(e);
			}
			Collections.sort(guideEntries);
			return guideEntries;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Show getShow(String serviceID, String seriesID) {
		String urlTemplate = "http://api.rovicorp.com/TVlistings/v9/listings/programdetails/%%SERVICE%%/%%SERIES%%/info?locale=en-US&copytextformat=PlainText&duration=10080&imagecount=5&include=Program&inprogress=0&pagesize=0&format=json&apikey=%%KEY%%&sig=%%SIG%%";
		urlTemplate = urlTemplate.replace("%%SERVICE%%", serviceID);
		urlTemplate = urlTemplate.replace("%%SERIES%%", seriesID);
		urlTemplate = urlTemplate.replace("%%KEY%%", API_KEY);
		urlTemplate = urlTemplate.replace("%%SIG%%", signRequest());
		System.out.println(urlTemplate);
		try {
			URL apiURL = new URL(urlTemplate);
			HttpURLConnection connection = (HttpURLConnection) apiURL
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			JSONObject response = readResponse(in);
			JSONObject programDetails = (JSONObject)((JSONObject)response.get("ProgramDetailsResult")).get("Program");
			String showDescription = (String)programDetails.get("CopyText");
			
			JSONArray airings = (JSONArray)((JSONObject)((JSONObject)response.get("ProgramDetailsResult")).get("Schedule")).get("Airings");
			
			ArrayList<Airing> nextAirings = new ArrayList<Airing>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			for(int i = 0; i < airings.size() && i < 5; i++) {
				JSONObject airing = (JSONObject)airings.get(i);
				Episode e = new Episode(Integer.parseInt((String)airing.get("ProgramId")), Integer.parseInt(seriesID), (String)airing.get("Title"), (String)airing.get("Copy"));
				Airing a = new Airing(e, sdf.parse((String)airing.get("AiringTime")));
				nextAirings.add(a);
			}
			return new Show(showDescription, nextAirings);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		RoviApiHandler testHandler = new RoviApiHandler();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00'Z'");
    	Date now = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(now);
    	calendar.add(Calendar.MINUTE, -(calendar.get(Calendar.MINUTE) % 30));
    	System.out.println(sdf.format(calendar.getTime()));
		//System.out.println(testHandler.getProviders("94709"));\
		//System.out.println(testHandler.getListings("76550", "2012-11-06T05:00:00Z"));
    	System.out.println(testHandler.getShow("76550", "8128"));

	}
}
