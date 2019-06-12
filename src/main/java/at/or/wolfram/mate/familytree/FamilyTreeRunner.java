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
	private static final String FTP_SERVER = "ftp48.world4you.com";
	private static final String FAMILY_TREE_CACHE_FILE = "cache/tree.json";
	private static final String LOCATION_LOOKUP_CACHE_FILE = "cache/locations.json";
	
	private String ftpUser;
	private String ftpPassword;
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		FamilyTreeService familyTreeService = new FamilyTreeService(
				BASE_URL, 
				FAMILY_TREE_CACHE_FILE, 
				new LocationLookupService(LOCATION_LOOKUP_CACHE_FILE));
		
		FamilyTreeRunner familyTreeRunner = new FamilyTreeRunner();
		familyTreeRunner.ftpUser = args[0];
		familyTreeRunner.ftpPassword = args[1];
		
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep-wolfram", "Wolfram", "Vágvölgyi");
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep-szajko", "Szajkó");
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep-koermendi", "Körmendi", "Körmendy", "Körmöndi");
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep-niedermay", "Niedermayer");
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep-ipsics", "Ipsics", "Ipsits", "Illésfalvi", "Illésfalvy", "Iglódi");
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep-hannig", "Hannig");
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep-timaffy", "Timaffy");
		
		familyTreeRunner.executeAncestorJob(familyTreeService, "terkep-mate", "per00001.htm");
		familyTreeRunner.executeAncestorJob(familyTreeService, "terkep-david", "per00049.htm");
		familyTreeRunner.executeAncestorJob(familyTreeService, "terkep-csaba", "per00034.htm");
		familyTreeRunner.executeAncestorJob(familyTreeService, "terkep-krisi", "per00096.htm");
		familyTreeRunner.executeAncestorJob(familyTreeService, "terkep-toto", "per00039.htm");
		familyTreeRunner.executeAncestorJob(familyTreeService, "terkep-viki", "per00036.htm");
		
		familyTreeRunner.executeIndexJob(familyTreeService, "terkep"); // mindenki
		
	}
	
	private void executeAncestorJob(FamilyTreeService familyTreeService, String targetFolder, String rootPersonLink) throws IOException {
		familyTreeService.generateAncestorTree(rootPersonLink);
		GeoJsonObject geoJson = GeoJson.fromTree(familyTreeService.getTree());
		generateIndexHtml(geoJson);
		uploadToFtpServer(targetFolder);
	}
	
	private void executeIndexJob(FamilyTreeService familyTreeService, String targetFolder, String... whitelist) throws IOException {
		familyTreeService.generateTreeByIndex(whitelist);
		GeoJsonObject geoJson = GeoJson.fromTree(familyTreeService.getTree());
		generateIndexHtml(geoJson);
		uploadToFtpServer(targetFolder);
	}
	
	private void generateIndexHtml(GeoJsonObject geoJson) throws IOException {
		File indexHtml = new File("index.html");
		File familyTopHtml = new File("src/main/resources/templates/leaflet/family_top.html");
		File familyBottomHtml = new File("src/main/resources/templates/leaflet/family_bottom.html");
		FileUtils.write(indexHtml, FileUtils.readFileToString(familyTopHtml, Tools.ENCODING), Tools.ENCODING);
		FileUtils.write(indexHtml, "var jsonStr = '" + Mapper.writeJson(geoJson) + "'" + System.lineSeparator(), Tools.ENCODING, true);
		FileUtils.write(indexHtml, FileUtils.readFileToString(familyBottomHtml, Tools.ENCODING), Tools.ENCODING, true);
		System.out.println("index.html written.");
	}
	
	private void uploadToFtpServer(String targetFolder) throws IOException {
		FtpClient ftpClient = new FtpClient(FTP_SERVER, 21, ftpUser, ftpPassword);
		ftpClient.open();
		File file = new File("index.html");
		String remotePath = "/" + targetFolder + "/index.html";
		ftpClient.putFileToPath(file, remotePath);
	}

}
