package at.or.wolfram.mate.familytree.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Person {

	private String name;
	private Sex sex;
	private String globalImageLink;
	private String globalPageLink;
	private LocationAndTime birth;
	private LocationAndTime death;
	
	@JsonIgnore
	public boolean isBirthDataValid() {
		return getBirth() != null &&
			   getBirth().isValid();
	}
	
	@JsonIgnore
	public boolean isDeathDataValid() {
		return getDeath() != null &&
			   getDeath().isValid();
	}
	
}
