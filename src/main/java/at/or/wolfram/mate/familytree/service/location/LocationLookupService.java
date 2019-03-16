package at.or.wolfram.mate.familytree.service.location;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import at.or.wolfram.mate.familytree.common.Mapper;
import at.or.wolfram.mate.familytree.common.Tools;
import at.or.wolfram.mate.familytree.model.Coordinates;
import at.or.wolfram.mate.familytree.model.Locations;

public class LocationLookupService {

	private final File cacheFile;
	private Locations locations;
	
	private final OpenStreetMapUtils openStreetMapUtils;
	
	private static final Logger logger = Logger.getLogger(LocationLookupService.class);
	
	public LocationLookupService(String cacheFileName) {
		this.openStreetMapUtils = new OpenStreetMapUtils();
		
		this.cacheFile = new File(cacheFileName);
		if (this.cacheFile.exists()) {
			try {
				readFromCacheFile();
			} catch (IOException e) {
				logger.error("Could not read cache file", e);
			}
		}
		else {
			logger.info("Cache file does not exist. Starting application without locations.");
			this.locations = new Locations();
		}
	}
	
	public Coordinates getCoordinates(String locationName) {
		if (hasValidCoordinatesInCache(locationName)) {
			return getCoordinatesFromCache(locationName);
		}
		
		Coordinates coordinates = getCoordinatesFromOpenStreetMapService(locationName);
		if (coordinates != null && coordinates.areValid()) {
			try {
				addOrUpdateCoordinatesInCache(locationName, coordinates);
			} catch (IOException e) {
				logger.error("Could not write cache file", e);
			}
		}
		
		return coordinates;
	}
	
	private boolean hasValidCoordinatesInCache(String locationName) {
		if (!this.locations.getLocationToCoordinates().containsKey(locationName)) {
			return false;
		}
		
		Coordinates coordinates = getCoordinatesFromCache(locationName);
		return coordinates != null && coordinates.areValid();
	}
	
	private Coordinates getCoordinatesFromCache(String locationName) {
		return this.locations.getLocationToCoordinates().get(locationName);
	}
	
	private Coordinates getCoordinatesFromOpenStreetMapService(String locationName) {
		Map<String, Double> coords = this.openStreetMapUtils.getCoordinates(locationName);
		if (coords == null) {
			return null;
		}
		return new Coordinates(coords.get(OpenStreetMapUtils.LATITUDE), coords.get(OpenStreetMapUtils.LONGITUDE));
	}
	
	private void readFromCacheFile() throws IOException {
		String jsonData = FileUtils.readFileToString(this.cacheFile, Tools.ENCODING);
		this.locations = Mapper.parseJson(jsonData, Locations.class);
	}
	
	private void writeToCacheFile() throws IOException {
		FileUtils.write(this.cacheFile, Mapper.writeJson(this.locations), Tools.ENCODING);
	}
	
	private void addOrUpdateCoordinatesInCache(String locationName, Coordinates coordinates) throws IOException {
		this.locations.getLocationToCoordinates().put(locationName, coordinates);
		writeToCacheFile();
	}
	
}
