package at.or.wolfram.mate.familytree.common;

import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

public class Tools {

	public static final Charset ENCODING = Charset.forName("UTF-8"); 
	
	private static final Logger logger = Logger.getLogger(Tools.class);
	
	public static int extractYear(String dateString) throws IllegalArgumentException {
		DateTimeParser[] parsers = { 
		        DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
		        DateTimeFormat.forPattern("yyyy-MM").getParser(),
		        DateTimeFormat.forPattern("yyyy").getParser()};
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

		return formatter.parseDateTime(dateString).getYear();
	}
	
	public static Double parseDoubleWithGermanLocale(String doubleString) {
		return Double.valueOf(doubleString.replace(",", "."));
	}
	
	public static Double degreesMinutesSecondsToDecimalCoordinate(String dms) {
		System.out.println("GPS coordinate from [" + dms + "]");
		Direction direction = Direction.fromString(dms.split(" ")[0].trim());
		
		String degreesStr = dms.split(" ")[1].split("°")[0].trim();
		double degrees = Double.valueOf(degreesStr);

		String decimalsStr = dms.split("°")[1].trim();
		String minutesStr = decimalsStr.split("'")[0].trim();
		String secondsStr = decimalsStr.split("'")[1].trim();

		double minutes = parseDoubleWithGermanLocale(minutesStr);
		double seconds = parseDoubleWithGermanLocale(secondsStr.replace("\"", ""));
		
		System.out.println("parsed: " + direction + " " + degrees + "°" + minutes + "' " + seconds + "\"");
		
		double sign = (direction == Direction.S || direction == Direction.W) ? -1.0 : 1.0;
		double decimalPart = ((minutes * 60) + (seconds * 3600)) / 3600.0;
		return sign * (degrees + decimalPart);
		
		// https://stackoverflow.com/questions/8263959/how-to-convert-between-degrees-minutes-seconds-to-decimal-coordinates
	}
	
	public static Double degreesMinutesSecondsToDecimalCoordinates(Direction direction, int degrees, int minutes, double seconds) {
		return 0.0;
	}
	
}
