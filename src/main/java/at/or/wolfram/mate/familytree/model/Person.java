package at.or.wolfram.mate.familytree.model;

import lombok.Data;

@Data
public class Person {

	private String name;
	private String globalImageLink;
	private String globalPageLink;
	private LocationAndTime birth;
	private LocationAndTime death;
	
}
