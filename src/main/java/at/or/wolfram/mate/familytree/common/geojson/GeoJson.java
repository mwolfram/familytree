package at.or.wolfram.mate.familytree.common.geojson;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.Point;

import at.or.wolfram.mate.familytree.common.Tools;
import at.or.wolfram.mate.familytree.model.Coordinates;
import at.or.wolfram.mate.familytree.model.Person;
import at.or.wolfram.mate.familytree.model.Tree;

public class GeoJson {

	private static final Logger logger = Logger.getLogger(GeoJson.class);
	
	private GeoJson() {
	}
	
	public static GeoJsonObject fromTree(Tree tree) {
		FeatureCollection features = new FeatureCollection();
		
		for (Person person : tree.getPersons()) {
			Feature birthFeature = createBirthFeature(person);
			Feature deathFeature = createDeathFeature(person);
			
			if (birthFeature != null) {
				features.add(birthFeature);
			}
			
			if (deathFeature != null) {
				features.add(deathFeature);
			}
		}
		
		List<Feature> featuresList = features.getFeatures();
		Collections.sort(featuresList, new Comparator<Feature>() {

			public int compare(Feature o1, Feature o2) {
				int t1 = Integer.parseInt((String)(o1.getProperty("epoch")));
				int t2 = Integer.parseInt((String)(o2.getProperty("epoch")));
				if (t1 < t2) {
					return -1;
				}
				else if (t1 > t2) {
					return 1;
				}
				else {
					return 0;
				}
			}
			
		});
		
		features.setFeatures(featuresList);
		
		return features;
	}
	
	private static Feature createBirthFeature(Person person) {
		Feature feature = new Feature();
		
		if (!person.isBirthDataValid()) {
			return null;
		}
		
		Coordinates coordinates = person.getBirth().getCoordinates();
		Point point = new Point(
				coordinates.getLongitude(), 
				coordinates.getLatitude());
		
		feature.setGeometry(point);
		
		String time = person.getBirth().getTime();
		
		try {
			int year = Tools.extractYear(time);
			feature.setProperty("epoch", String.valueOf(year));
		} catch (IllegalArgumentException e) {
			logger.error("Failed extracting year from string " + time);
			return null;
		}
		
		feature.setProperty("name", person.getName());
		
		if (person.getGlobalImageLink() != null) {
			feature.setProperty("image", person.getGlobalImageLink());
		}
		
		feature.setProperty("link", person.getGlobalPageLink());
		feature.setProperty("shadow", "red.png"); // TODO do not mix data and view!
		feature.setProperty("event", "Született"); // TODO do not mix data and view!

		return feature;
	}
	
	private static Feature createDeathFeature(Person person) {
		Feature feature = new Feature();
		
		if (!person.isDeathDataValid()) {
			return null;
		}

		Coordinates coordinates = person.getDeath().getCoordinates();
		Point point = new Point(
				coordinates.getLongitude(), 
				coordinates.getLatitude());
		
		feature.setGeometry(point);
		
		String time = person.getDeath().getTime();
		
		try {
			int year = Tools.extractYear(time);
			feature.setProperty("epoch", String.valueOf(year));
		} catch (IllegalArgumentException e) {
			logger.error("Failed extracting year from string " + time);
			return null;
		}
		
		feature.setProperty("name", person.getName());
		
		if (person.getGlobalImageLink() != null) {
			feature.setProperty("image", person.getGlobalImageLink());
		}
		
		feature.setProperty("link", person.getGlobalPageLink());
		feature.setProperty("shadow", "black.png"); // TODO do not mix data and view!
		feature.setProperty("event", "Meghalt"); // TODO do not mix data and view!

		return feature;
	}
	
}
