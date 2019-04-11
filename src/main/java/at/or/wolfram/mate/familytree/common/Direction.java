package at.or.wolfram.mate.familytree.common;

public enum Direction {
	N, E, S, W;
	
	public static Direction fromString(String directionStr) {
		directionStr = directionStr.replace(":", ""); // XXX Den Graus entfernen!
		return Direction.valueOf(directionStr);
	}
}