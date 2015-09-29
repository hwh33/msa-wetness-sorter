package climatology;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FCCClient {
	
	public static HashMap<Integer, ArrayList<WBANStation>> getFIPSToWBANMap(
			Iterator<WBANStation> wbanIter) throws IOException {
		HashMap<Integer, ArrayList<WBANStation>> fipsToWBANMap = 
				new HashMap<Integer, ArrayList<WBANStation>>();
		while (wbanIter.hasNext()) {
			WBANStation currentWBAN = wbanIter.next();
			int fipsCode = getFIPSFromWBAN(currentWBAN);
			if (!fipsToWBANMap.containsKey(fipsCode)) {
				fipsToWBANMap.put(fipsCode, new ArrayList<WBANStation>());
			}
			
			fipsToWBANMap.get(fipsCode).add(currentWBAN);
		}
		return fipsToWBANMap;
	}

	private static int getFIPSFromWBAN(WBANStation wbanStation) throws IOException {
		URL apiRequest = apiRequestFromWBAN(wbanStation);
		HttpURLConnection connection = (HttpURLConnection)apiRequest.openConnection();
		connection.setRequestMethod("GET");
		
		InputStream inStream = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		StringBuilder response = new StringBuilder();
		String currentLine = reader.readLine();
		while(currentLine != null) {
			response.append(currentLine + '\n');
			currentLine = reader.readLine();
		}
		reader.close();
		
		return fipsCodeFromAPIResponse(response.toString());
	}
	
	private static URL apiRequestFromWBAN(WBANStation wbanStation) throws MalformedURLException {
		return new URL("http://data.fcc.gov/api/block/find?latitude=" + wbanStation.getLatitude()
			+ "&longitude=" + wbanStation.getLongitude() + "&showall=false");
	}
	
	private static int fipsCodeFromAPIResponse(String apiResponse) {
		JSONObject apiResponseJSON = new JSONObject(apiResponse);
		String fipsString = apiResponseJSON.getJSONObject("County").getString("FIPS Code");
		return Integer.parseInt(fipsString);
	}
	
}
