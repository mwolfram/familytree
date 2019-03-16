package at.or.wolfram.mate.familytree;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.geojson.GeoJsonObject;

import at.or.wolfram.mate.familytree.common.Mapper;
import at.or.wolfram.mate.familytree.common.geojson.GeoJson;
import at.or.wolfram.mate.familytree.service.familytree.FamilyTreeService;
import at.or.wolfram.mate.familytree.service.location.LocationLookupService;

public class FamilyTree {

	private static final String BASE_URL = "http://members.chello.at/laszlowolfram/mate.web/";
	private static final String FAMILY_TREE_CACHE_FILE = "cache/tree.json";
	private static final String LOCATION_LOOKUP_CACHE_FILE = "cache/locations.json";
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		FamilyTreeService familyTreeService = new FamilyTreeService(
				BASE_URL, 
				FAMILY_TREE_CACHE_FILE, 
				new LocationLookupService(LOCATION_LOOKUP_CACHE_FILE));
		
		GeoJsonObject geoJson = GeoJson.fromTree(familyTreeService.getTree());
		System.out.println("---");
		System.out.println(Mapper.writeJson(geoJson));
		System.out.println("---");
	}
	
//	private static void generatePersonPins() throws IOException {
//		String jsonData = FileUtils.readFileToString(new File("result.json"), Charset.forName("UTF-8"));
//		Tree allPins = Mapper.parseJson(jsonData, Tree.class);
//		
//		for (Person p : allPins.getPersons()) {
//			if (p.getBirth() != null) {
//				
//			}
//			if (p.getDeath() != null) {
//				
//			}
//		}
//	}
//	
//	private static void generateOnePin(Double latitude, Double longitude) {
//		FileUtils.writeStringToFile(new File("pins.txt"), "L.marker(["+loc.getLatitutde()+", "+loc.getLongitude()+"]).addTo(mymap);" + System.lineSeparator(), Charset.forName("UTF-8"), true);
//	}
//	
//	private static void generatePins() throws JsonParseException, JsonMappingException, IOException {
//		String jsonData = FileUtils.readFileToString(new File("dict.json"), Charset.forName("UTF-8"));
//		Locations allPins = Mapper.parseJson(jsonData, Locations.class);
//		
//		for (Coordinates loc : allPins.getLocationToLatLon().values()) {
//			if (loc == null) continue;
//			if (loc.getLatitutde() == null || loc.getLongitude() == null) continue;
//			System.out.println("L.marker(["+loc.getLatitutde()+", "+loc.getLongitude()+"]).addTo(mymap);");
//			FileUtils.writeStringToFile(new File("pins.txt"), "L.marker(["+loc.getLatitutde()+", "+loc.getLongitude()+"]).addTo(mymap);" + System.lineSeparator(), Charset.forName("UTF-8"), true);
//		}
//	}
	
}
