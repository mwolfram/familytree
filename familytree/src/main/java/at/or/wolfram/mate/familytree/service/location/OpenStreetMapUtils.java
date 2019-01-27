package at.or.wolfram.mate.familytree.service.location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class OpenStreetMapUtils {

    private static final Logger logger = Logger.getLogger(OpenStreetMapUtils.class);

    protected static final String LATITUDE = "lat";
    protected static final String LONGITUDE = "lon";
    
    public OpenStreetMapUtils() {
    }

    private String getRequest(String url) throws Exception {

        final URL obj = new URL(url);
        final HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        if (con.getResponseCode() != 200) {
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public Map<String, Double> getCoordinates(String address) {
        Map<String, Double> res;
        StringBuffer query;
        String[] split = address.split(" ");
        String queryResult = null;

        query = new StringBuffer();
        res = new HashMap<String, Double>();

        query.append("https://nominatim.openstreetmap.org/search?q=");

        if (split.length == 0) {
            return null;
        }

        for (int i = 0; i < split.length; i++) {
            query.append(split[i]);
            if (i < (split.length - 1)) {
                query.append("+");
            }
        }
        query.append("&format=json&addressdetails=1");

        logger.debug("Query:" + query);

        try {
            queryResult = getRequest(query.toString());
        } catch (Exception e) {
            logger.error("Error when trying to get data with the following query " + query);
        }

        if (queryResult == null) {
            return null;
        }

        Object obj = JSONValue.parse(queryResult);
        logger.debug("obj=" + obj);

        if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            if (array.size() > 0) {
                JSONObject jsonObject = (JSONObject) array.get(0);

                String lon = (String) jsonObject.get(LONGITUDE);
                String lat = (String) jsonObject.get(LATITUDE);
                logger.debug(LONGITUDE + "=" + lon);
                logger.debug(LATITUDE + "=" + lat);
                res.put(LONGITUDE, Double.parseDouble(lon));
                res.put(LATITUDE, Double.parseDouble(lat));

            }
        }

        return res;
    }
}

