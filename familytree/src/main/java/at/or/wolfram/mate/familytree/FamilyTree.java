package at.or.wolfram.mate.familytree;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class FamilyTree {

	private static final String BASE_URL = "http://members.chello.at/laszlowolfram/mate.web/";
	
	private static LocationDictionary locationDictionary = new LocationDictionary(new HashMap<String, LatLon>());
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		generatePins();
//		crawl();
	}
	
	private static void generatePins() throws JsonParseException, JsonMappingException, IOException {
		String jsonData = FileUtils.readFileToString(new File("dict.json"), Charset.forName("UTF-8"));
		LocationDictionary allPins = Mapper.parseJson(jsonData, LocationDictionary.class);
		for (LatLon loc : allPins.getLocationToLatLon().values()) {
			if (loc == null) continue;
			System.out.println("L.marker(["+loc.getLatitutde()+", "+loc.getLongitude()+"]).addTo(mymap);");
		}
	}
	
	private static void crawl() throws IOException {
		List<Person> persons = new ArrayList<Person>();
		TreeData treeData = new TreeData();
		for (int i = 1; i <= 33; i++) {
			try {
				System.out.println("Index " + i);
				Source source = new Source(new URL(BASE_URL + "index" + i + ".htm"));
				List<String> links = getPersonLinks(source);
				for (String link : links) {
					Person person = getPerson(new Source(new URL(BASE_URL + link)));
					persons.add(person);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		treeData.setPersons(persons);

		// write tree data to file
		FileUtils.write(new File("result.json"), Mapper.writeJson(treeData), Charset.forName("UTF-8"));

		// write location dictionary to file
		FileUtils.write(new File("dict.json"), Mapper.writeJson(locationDictionary), Charset.forName("UTF-8"));
	}
	
	// challenge 1: get all persons
	private static List<String> getPersonLinks(Source source) {
		List<String> links = new ArrayList<String>();
		for (Element element : source.getAllElements("A HREF")) {
			String link = element.getAttributeValue("HREF");
			if (link.startsWith("per")) {
				links.add(link);
			}
		}
		return links;
	}
	
	private static boolean isSpecificRow(Element tr, String identifier) {
		for (Element td : tr.getAllElements("TD")) {
			if (String.valueOf(td.getContent()).contains(identifier)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isPlaceOfBirthRow(Element tr) {
		return isSpecificRow(tr, "Született");
	}
	
	private static boolean isPlaceOfDeathRow(Element tr) {
		return isSpecificRow(tr, "Meghalt");
	}
	
	private static Person getPerson(Source source) {
		Person person = new Person();
		
		for (Element title : source.getAllElements("title")) {
			person.setName(title.getContent().toString());
		}
		
		for (Element tr : source.getAllElements("TR")) {
			if (isPlaceOfBirthRow(tr)) {
				LocationAndTime birth = new LocationAndTime();
				birth.setTime(tr.getAllElements().get(3).getContent().toString());
				String addressLine = tr.getAllElements().get(4).getContent().toString();
				birth.setLocation(addressLine);
				Map<String, Double> coords = OpenStreetMapUtils.getInstance().getCoordinates(addressLine);
		        if (coords != null) {
		        	birth.setLatitude(coords.get("lat"));
		        	birth.setLongitude(coords.get("lon"));
		        	locationDictionary.getLocationToLatLon().put(addressLine, new LatLon(coords.get("lat"), coords.get("lon")));
				}
		        else {
		        	if (addressLine != null && !addressLine.isEmpty() && !addressLine.trim().equals("-")) {
		        		locationDictionary.getLocationToLatLon().put(addressLine, null);
		        	}
		        }
				person.setBirth(birth);
			}
			if (isPlaceOfDeathRow(tr)) {
				LocationAndTime death = new LocationAndTime();
				death.setTime(tr.getAllElements().get(3).getContent().toString());
				String addressLine = tr.getAllElements().get(4).getContent().toString();
				death.setLocation(addressLine);
				Map<String, Double> coords = OpenStreetMapUtils.getInstance().getCoordinates(addressLine);
		        if (coords != null) {
		        	death.setLatitude(coords.get("lat"));
		        	death.setLongitude(coords.get("lon"));
		        	locationDictionary.getLocationToLatLon().put(addressLine, new LatLon(coords.get("lat"), coords.get("lon")));
				}
		        else {
		        	if (addressLine != null && !addressLine.isEmpty() && !addressLine.trim().equals("-")) {
		        		locationDictionary.getLocationToLatLon().put(addressLine, null);
		        	}
		        }
				person.setDeath(death);
			}
		}
		return person;
		
	}
	
}
