package at.or.wolfram.mate.familytree.service.familytree;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import at.or.wolfram.mate.familytree.common.Tools;
import at.or.wolfram.mate.familytree.model.Coordinates;
import at.or.wolfram.mate.familytree.model.LocationAndTime;
import at.or.wolfram.mate.familytree.model.Person;
import at.or.wolfram.mate.familytree.model.Sex;
import at.or.wolfram.mate.familytree.model.Tree;
import at.or.wolfram.mate.familytree.service.location.LocationLookupService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

public class Crawler {

	private static final String ROOT = "<TABLE border width=100% BGCOLOR=\"#FF0000\">";
	private static final String FATHER = "<TABLE border width=100% BGCOLOR=\"#0080FF\">";
	private static final String MOTHER = "<TABLE border width=100% BGCOLOR=\"#FF8080\">";
	
	private static final String MALE_AVATAR = "ferfi-1.jpg";// TODO do not mix data and view!
	private static final String FEMALE_AVATAR = "no-1.jpg";// TODO do not mix data and view!
	
	private static final String MALE_COLOR = "#0080FF";
	private static final String FEMALE_COLOR = "#FF8080";
	
	private final String baseUrl;
	
	private final LocationLookupService locationLookupService;
	
	private static final Logger logger = Logger.getLogger(Crawler.class);
	
	public Crawler(String baseUrl, LocationLookupService locationLookupService) {
		this.baseUrl = baseUrl;
		this.locationLookupService = locationLookupService;
	}
	
	public Tree crawlIndices() {
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
			List<String> links = getPersonLinksFromIndexPage(source);
			persons.addAll(processPersonPages(links));
		}
		
		tree.setPersons(persons);
		return tree;
	}
	
	public Tree crawlAncestors(String root) {
		List<String> links = getPersonLinksFromAncestors(root);
		List<Person> persons = processPersonPages(links);
		
		Tree tree = new Tree();
		tree.setPersons(persons);
		return tree;
	}
	
	public Tree crawlSelected(String... links) {
		List<Person> persons = processPersonPages(Arrays.asList(links));
		
		Tree tree = new Tree();
		tree.setPersons(persons);
		return tree;
	}
	
	private List<Person> processPersonPages(List<String> links) {
		List<Person> persons = new ArrayList<Person>();
		
		for (String link : links) {
			Source personSource = null;
			String globalPageLink = baseUrl + link;
			while (personSource == null) {
				try {
					personSource = new Source(new URL(globalPageLink));
					personSource.fullSequentialParse();
				} catch (MalformedURLException e) {
					logger.info("Retrying person " + link);
				} catch (IOException e) {
					logger.info("Retrying person " + link);
				}
			}
			Person person = getPerson(personSource);
			String relativeImageLink = getImageLinkFromPersonPage(personSource);
			if (relativeImageLink != null) {
				person.setGlobalImageLink(baseUrl + getImageLinkFromPersonPage(personSource));
			}
			else {
				if (person.getSex() == Sex.MALE) {
					person.setGlobalImageLink(MALE_AVATAR); // TODO do not mix data and view!
				}
				else if (person.getSex() == Sex.FEMALE) {
					person.setGlobalImageLink(FEMALE_AVATAR); // TODO do not mix data and view!
				}
			}
			person.setGlobalPageLink(globalPageLink);
			persons.add(person);
		}
		
		return persons;
	}
	
	private List<String> getPersonLinksFromAncestors(String root) {
		
		Set<String> personLinks = new HashSet<String>();
		Set<String> openSet = new HashSet<String>();
		
		openSet.add(root);
		
		while(!openSet.isEmpty()) {
			
			Iterator<String> it = openSet.iterator();
		    root = it.next();
		    it.remove();
			
			Source personSource = null;
			while (personSource == null) {
				try {
					personSource = new Source(new URL(baseUrl + root));
					personSource.fullSequentialParse();
				} catch (MalformedURLException e) {
					logger.info("Retrying person " + root);
				} catch (IOException e) {
					logger.info("Retrying person " + root);
				}
			}
			
			for (Element element : personSource.getAllElements("A HREF")) {
				String tableElement = element.getParentElement().getParentElement().getParentElement().getParentElement().getParentElement().getStartTag().toString();
				String link = element.getAttributeValue("HREF");
				
				if (ROOT.equals(tableElement)) {
					personLinks.add(link);
				}
				else if (FATHER.equals(tableElement)) {
					personLinks.add(link);
					openSet.add(link);
				}
				else if (MOTHER.equals(tableElement)) {
					personLinks.add(link);
					openSet.add(link);
				}
			}
		}
		
		return new ArrayList<String>(personLinks);
	}
	
	private List<String> getPersonLinksFromIndexPage(Source source) {
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
	
	private Sex getSex(Source source) {
		String sexColorStr = source.getAllElements("TABLE").get(0).getAllElements("TD").get(0).getAttributeValue("BGCOLOR");
		if (MALE_COLOR.equals(sexColorStr)) {
			return Sex.MALE;
		}
		else if (FEMALE_COLOR.equals(sexColorStr)) {
			return Sex.FEMALE;
		}
		else {
			logger.error("Unknown sex color " + sexColorStr);
		}
		
		return null;
	}
	
	private Person getPerson(Source source) {
		Person person = new Person();
		
		for (Element title : source.getAllElements("title")) {
			person.setName(title.getContent().toString());
		}
		
		person.setSex(getSex(source));
		
		for (Element tr : source.getAllElements("TR")) {
			if (isPlaceOfBirthRow(tr)) {
				LocationAndTime birth = tableRowToLocationAndTime(tr);
				person.setBirth(birth);
			}
			if (isPlaceOfDeathRow(tr)) {
				LocationAndTime death = tableRowToLocationAndTime(tr);
				person.setDeath(death);
			}
		}
		return person;
		
	}
	
	private boolean containsHref(Segment segment) {
		return segment.getAllElements("A HREF").size() > 0;
	}
	
	// TODO reuse
	private Source getPage(String relativeLink) {
		Source source = null;
		while (source == null) {
			try {
				source = new Source(new URL(baseUrl + relativeLink));
				source.fullSequentialParse();
			} catch (MalformedURLException e) {
				logger.info("Retrying link " + relativeLink);
			} catch (IOException e) {
				logger.info("Retrying link " + relativeLink);
			}
		}
		return source;
	}
	
	// TODO OO
	private Coordinates coordinatesFromAddressPage(Source addressPage) {
		String locationAndCoordinates = addressPage.getAllElements("TABLE").get(1).getFirstElement("TD").getContent().toString();
		String[] lines = locationAndCoordinates.split("<BR>");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].equals("GPS:") && lines.length >= i + 3) {
				Coordinates coordinates = new Coordinates();
				coordinates.setLatitude(Tools.degreesMinutesSecondsToDecimalCoordinate(lines[i+1]));
				coordinates.setLongitude(Tools.degreesMinutesSecondsToDecimalCoordinate(lines[i+2]));
				return coordinates;
			}
		}
		
		return null;
	}
	
	private LocationAndTime tableRowToLocationAndTime(Element tr) {
		LocationAndTime locationAndTime = new LocationAndTime();
		
		locationAndTime.setTime(tr.getAllElements().get(3).getContent().toString());
		Segment addressLineSegment = tr.getAllElements().get(4).getContent();
		
		if (containsHref(addressLineSegment)) {
			String locationAndTimeLink = addressLineSegment.getAllElements("A HREF").get(0).getAttributeValue("HREF");
			Source addressPage = getPage(locationAndTimeLink);
			String location = addressLineSegment.getAllElements("A HREF").get(0).getContent().toString();
			Coordinates coordinates = coordinatesFromAddressPage(addressPage);
			if (coordinates == null) {
				coordinates = locationLookupService.getCoordinates(location);
			}
			locationAndTime.setCoordinates(coordinates);
		}
		else {
			locationAndTime.setLocation(addressLineSegment.toString());
			locationAndTime.setCoordinates(locationLookupService.getCoordinates(addressLineSegment.toString()));
		}
		
		return locationAndTime;
	}
	
	private String getImageLinkFromPersonPage(Source personPage) {
		String personName = null;
		
		for (Element title : personPage.getAllElements("title")) {
			personName = title.getContent().toString();
		}
		
		for (Element element : personPage.getAllElements("img")) {
			String alt = element.getAttributeValue("alt");
			if (alt != null && alt.equals(personName)) {
				String src = element.getAttributeValue("src");
				if (src != null && !src.isEmpty()) {
					return src;
				}
			}
		}
		
		return null;
	}
	
}
