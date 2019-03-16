package at.or.wolfram.mate.familytree.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {

	private Double latitude;
	private Double longitude;
	
	public boolean areValid() {
		return latitude != null && longitude != null;
	}
	
}
