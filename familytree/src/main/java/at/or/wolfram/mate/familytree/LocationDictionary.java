package at.or.wolfram.mate.familytree;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDictionary {

	Map<String, LatLon> locationToLatLon;
	
}
