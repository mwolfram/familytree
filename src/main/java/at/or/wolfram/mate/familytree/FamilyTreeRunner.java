package at.or.wolfram.mate.familytree;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.geojson.GeoJsonObject;

import at.or.wolfram.mate.familytree.common.Mapper;
import at.or.wolfram.mate.familytree.common.Tools;
import at.or.wolfram.mate.familytree.common.geojson.GeoJson;
import at.or.wolfram.mate.familytree.service.familytree.FamilyTreeService;
import at.or.wolfram.mate.familytree.service.location.LocationLookupService;

public class FamilyTreeRunner {

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
		
		File indexHtml = new File("index.html");
		File familyTopHtml = new File("src/main/resources/templates/leaflet/family_top.html");
		File familyBottomHtml = new File("src/main/resources/templates/leaflet/family_bottom.html");
		FileUtils.write(indexHtml, FileUtils.readFileToString(familyTopHtml, Tools.ENCODING), Tools.ENCODING);
		FileUtils.write(indexHtml, "var jsonStr = '" + Mapper.writeJson(geoJson) + "'" + System.lineSeparator(), Tools.ENCODING, true);
		FileUtils.write(indexHtml, FileUtils.readFileToString(familyBottomHtml, Tools.ENCODING), Tools.ENCODING, true);
		System.out.println("Resultfile written.");
	}

}
