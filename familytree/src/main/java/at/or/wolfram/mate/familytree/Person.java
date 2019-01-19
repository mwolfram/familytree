package at.or.wolfram.mate.familytree;

import lombok.Data;

@Data
public class Person {

	private String name;
	private LocationAndTime birth;
	private LocationAndTime death;
	
}
