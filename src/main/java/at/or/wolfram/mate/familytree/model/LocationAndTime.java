package at.or.wolfram.mate.familytree.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class LocationAndTime {

	private String location;
	private Coordinates coordinates;
	private String time;
	
	@JsonIgnore
	public boolean isValid() {
		return 	getTime() != null &&
				getCoordinates() != null &&
				getCoordinates().areValid();
	}
	
}
