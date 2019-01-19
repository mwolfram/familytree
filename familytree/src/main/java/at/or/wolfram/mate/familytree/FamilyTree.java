package at.or.wolfram.mate.familytree;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class FamilyTree {

	private static final String BASE_URL = "http://members.chello.at/laszlowolfram/mate.web/";
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		List<Person> persons = new ArrayList<Person>();
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
		
		for (Person person : persons) {
			System.out.println(person);
		}
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
				birth.setLocation(tr.getAllElements().get(4).getContent().toString());
				person.setBirth(birth);
			}
			if (isPlaceOfDeathRow(tr)) {
				LocationAndTime death = new LocationAndTime();
				death.setTime(tr.getAllElements().get(3).getContent().toString());
				death.setLocation(tr.getAllElements().get(4).getContent().toString());
				person.setDeath(death);
			}
		}
		return person;
		
	}
	
}
