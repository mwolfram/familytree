package at.or.wolfram.mate.familytree.common;

import java.nio.charset.Charset;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

public class Tools {

	public static final Charset ENCODING = Charset.forName("UTF-8"); 
	
	public static int extractYear(String dateString) throws IllegalArgumentException {
		DateTimeParser[] parsers = { 
		        DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
		        DateTimeFormat.forPattern("yyyy-MM").getParser(),
		        DateTimeFormat.forPattern("yyyy").getParser()};
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

		return formatter.parseDateTime(dateString).getYear();
	}
	
}
