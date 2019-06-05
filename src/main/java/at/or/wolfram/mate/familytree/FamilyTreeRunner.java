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
import at.or.wolfram.mate.familytree.common.ftp.FtpClient;
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
		
		executeIndexJob(familyTreeService, "terkep-wolfram", "Wolfram", "Vágvölgyi");
		executeIndexJob(familyTreeService, "terkep-szajko", "Szajkó");
		executeIndexJob(familyTreeService, "terkep-koermendi", "Körmendi", "Körmendy", "Körmöndi");
		executeIndexJob(familyTreeService, "terkep-niedermay", "Niedermayer");
		executeIndexJob(familyTreeService, "terkep-ipsics", "Ipsics", "Ipsits", "Illésfalvi", "Illésfalvy", "Iglódi");
		executeIndexJob(familyTreeService, "terkep-hannig", "Hannig");
		executeIndexJob(familyTreeService, "terkep-timaffy", "Timaffy");
		
		executeAncestorJob(familyTreeService, "terkep-mate", "per00001.htm");
		executeAncestorJob(familyTreeService, "terkep-david", "per00049.htm");
		executeAncestorJob(familyTreeService, "terkep-csaba", "per00034.htm");
		executeAncestorJob(familyTreeService, "terkep-krisi", "per00096.htm");
		executeAncestorJob(familyTreeService, "terkep-toto", "per00039.htm");
		executeAncestorJob(familyTreeService, "terkep-viki", "per00036.htm");
		
		executeIndexJob(familyTreeService, "terkep"); // mindenki
		
	}
	
	private static void executeAncestorJob(FamilyTreeService familyTreeService, String targetFolder, String rootPersonLink) throws IOException {
		familyTreeService.generateAncestorTree(rootPersonLink);
		GeoJsonObject geoJson = GeoJson.fromTree(familyTreeService.getTree());
		generateIndexHtml(geoJson);
		uploadToFtpServer(targetFolder);
	}
	
	private static void executeIndexJob(FamilyTreeService familyTreeService, String targetFolder, String... whitelist) throws IOException {
		familyTreeService.generateTreeByIndex(whitelist);
		GeoJsonObject geoJson = GeoJson.fromTree(familyTreeService.getTree());
		generateIndexHtml(geoJson);
		uploadToFtpServer(targetFolder);
	}
	
	private static void generateIndexHtml(GeoJsonObject geoJson) throws IOException {
		File indexHtml = new File("index.html");
		File familyTopHtml = new File("src/main/resources/templates/leaflet/family_top.html");
		File familyBottomHtml = new File("src/main/resources/templates/leaflet/family_bottom.html");
		FileUtils.write(indexHtml, FileUtils.readFileToString(familyTopHtml, Tools.ENCODING), Tools.ENCODING);
		FileUtils.write(indexHtml, "var jsonStr = '" + Mapper.writeJson(geoJson) + "'" + System.lineSeparator(), Tools.ENCODING, true);
		FileUtils.write(indexHtml, FileUtils.readFileToString(familyBottomHtml, Tools.ENCODING), Tools.ENCODING, true);
		System.out.println("index.html written.");
	}
	
	private static void uploadToFtpServer(String targetFolder) throws IOException {
		FtpClient ftpClient = new FtpClient("ftp48.world4you.com", 21, "wolfram", "84u37");
		ftpClient.open();
		File file = new File("index.html");
		String remotePath = "/" + targetFolder + "/index.html";
		ftpClient.putFileToPath(file, remotePath);
	}

}
