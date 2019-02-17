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
			Feature feature = new Feature();
			
			Coordinates coordinates = getPreferredCoordinatesFromPerson(person);
			if (coordinates == null) {
				logger.error("Failed getting coordinates for person " + person.getName());
				continue;
			}
			Point point = new Point(
					coordinates.getLongitude(), 
					coordinates.getLatitude());
			
			feature.setGeometry(point);
			
			String time = getPreferredTime(person);
			if (time == null) {
				logger.error("Failed getting preferred time for person " + person.getName());
				continue;
			}
			
			try {
				int year = Tools.extractYear(time);
				feature.setProperty("epoch", String.valueOf(year));
			} catch (IllegalArgumentException e) {
				logger.error("Failed extracting year from string " + time);
				continue;
			}
			
			feature.setProperty("name", person.getName());
			
			// TODO hack, rather set link to null if it does not make sense
			if (!"http://members.chello.at/laszlowolfram/mate.web/null".equals(person.getGlobalImageLink()) && person.getGlobalImageLink() != null) {
				feature.setProperty("image", person.getGlobalImageLink());
			}
			
			feature.setProperty("link", person.getGlobalPageLink());

			features.add(feature);
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
	
	private static String getPreferredTime(Person person) {
		if (person.getBirth() != null && person.getBirth().getTime() != null) {
			return person.getBirth().getTime();
		}
		
		if (person.getDeath() != null && person.getDeath().getTime() != null) {
			return person.getDeath().getTime();
		}
		
		return null;
	}
	
	private static Coordinates getPreferredCoordinatesFromPerson(Person person) {
		if (person.getBirth() != null && person.getBirth().getCoordinates() != null && person.getBirth().getCoordinates().areValid()) {
			return person.getBirth().getCoordinates();
		}
		
		if (person.getDeath() != null && person.getDeath().getCoordinates() != null && person.getDeath().getCoordinates().areValid()) {
			return person.getDeath().getCoordinates();
		}
		
		return null;
	}
	
}
