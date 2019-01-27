package at.or.wolfram.mate.familytree.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Locations {

	Map<String, Coordinates> locationToCoordinates = new HashMap<String, Coordinates>();
	
}
