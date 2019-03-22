package at.or.wolfram.mate.familytree.model;

import lombok.Data;

@Data
public class LocationAndTime {

	private String location;
	private Coordinates coordinates;
	private String time;
	
	public boolean isValid() {
		return 	getTime() != null &&
				getCoordinates() != null &&
				getCoordinates().areValid();
	}
	
}
