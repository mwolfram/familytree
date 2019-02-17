package at.or.wolfram.mate.familytree.service.familytree;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import at.or.wolfram.mate.familytree.model.LocationAndTime;
import at.or.wolfram.mate.familytree.model.Person;
import at.or.wolfram.mate.familytree.model.Tree;
import at.or.wolfram.mate.familytree.service.location.LocationLookupService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class Crawler {

	private final String baseUrl;
	
	private final LocationLookupService locationLookupService;
	
	private static final Logger logger = Logger.getLogger(Crawler.class);
	
	public Crawler(String baseUrl, LocationLookupService locationLookupService) {
		this.baseUrl = baseUrl;
		this.locationLookupService = locationLookupService;
	}
	
	public Tree execute() {
		List<Person> persons = new ArrayList<Person>();
		Tree tree = new Tree();
		for (int i = 1; i <= 33; i++) {
			logger.info("Crawling index " + i);
			Source source = null;
			while (source == null) {
				try {
					source = new Source(new URL(baseUrl + "index" + i + ".htm"));
					source.fullSequentialParse();
				} catch (MalformedURLException e) {
					logger.info("Retrying index " + i);
				} catch (IOException e) {
					logger.info("Retrying index " + i);
				}
			}
			List<String> links = getPersonLinks(source);
			for (String link : links) {
				Source personSource = null;
				while (personSource == null) {
					try {
						personSource = new Source(new URL(baseUrl + link));
						personSource.fullSequentialParse();
					} catch (MalformedURLException e) {
						logger.info("Retrying person " + link);
					} catch (IOException e) {
						logger.info("Retrying person " + link);
					}
				}
				Person person = getPerson(personSource);
				persons.add(person);
			}
		}
		
		tree.setPersons(persons);
		return tree;
	}
	
	private List<String> getPersonLinks(Source source) {
		List<String> links = new ArrayList<String>();
		for (Element element : source.getAllElements("A HREF")) {
			String link = element.getAttributeValue("HREF");
			if (link.startsWith("per")) {
				links.add(link);
			}
		}
		return links;
	}
	
	private boolean isSpecificRow(Element tr, String identifier) {
		for (Element td : tr.getAllElements("TD")) {
			if (String.valueOf(td.getContent()).contains(identifier)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isPlaceOfBirthRow(Element tr) {
		return isSpecificRow(tr, "Született");
	}
	
	private boolean isPlaceOfDeathRow(Element tr) {
		return isSpecificRow(tr, "Meghalt");
	}
	
	private Person getPerson(Source source) {
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
	        	birth.setCoordinates(locationLookupService.getCoordinates(addressLine));
				person.setBirth(birth);
			}
			if (isPlaceOfDeathRow(tr)) {
				LocationAndTime death = new LocationAndTime();
				death.setTime(tr.getAllElements().get(3).getContent().toString());
				String addressLine = tr.getAllElements().get(4).getContent().toString();
				death.setLocation(addressLine);
				death.setCoordinates(locationLookupService.getCoordinates(addressLine));
				person.setDeath(death);
			}
		}
		return person;
		
	}
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		Source source = new Source(new URL("http://members.chello.at/laszlowolfram/mate.web/per00030.htm"));
		
		for (Element element : source.getAllElements("img")) {
			String alt = element.getAttributeValue("alt");
			String src = element.getAttributeValue("src");
			System.out.println(alt + ": " + src);
		}
		
	}
	
}
