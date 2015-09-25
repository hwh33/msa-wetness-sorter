package climatology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * A parser designed to read climatology data stored in a file.
 * 
 * @author Harry Harpham
 */
public class Parser {
	
	/**
	 * Parses WBAN Station data in the file located at the input file path to return an iterator
	 * of WBANStation objects representing each WBAN Station in the file.
	 * 
	 * @param wbanDataFilepath	the path to the file containing the WBAN Station data; this file
	 * 							should be a text file of pipe-separated-values (.psv)
	 * @return					an iterator over WBANStation objects representing each of the
	 * 							WBAN Stations in the input file
	 * @throws IOException		if the input file cannot be found or if the data in the file cannot
	 * 							be read
	 * @throws IllegalArgumentException		if the input file is badly formatted
	 */
	public static Iterator<WBANStation> getWBANIterator(String wbanDataFilepath) 
			throws IOException {
		BufferedReader wbanDataFileReader;
		wbanDataFileReader = new BufferedReader(new FileReader(wbanDataFilepath));
		
		final String WBAN_ID_COL_HEADER = "\"WBAN_ID\"";
		final String LOCATION_COL_HEADER = "\"LOCATION\"";
		
		// The first row of the file tells us which data is in each column. We are interested in
		// the columns titled "WBAN_ID" and "LOCATION"
		String firstLine = wbanDataFileReader.readLine();
		String[] colHeaders = firstLine.split("\\|");
		int wbanIDIndex = -1;
		int locationIndex = -1;
		for (int i = 0; i < colHeaders.length; i++) {
			if (colHeaders[i].equals(WBAN_ID_COL_HEADER)) {
				wbanIDIndex = i;
			}
			if (colHeaders[i].equals(LOCATION_COL_HEADER)) {
				locationIndex = i;
			}
		}
		if (wbanIDIndex == -1 || locationIndex == -1) {
			wbanDataFileReader.close();
			throw new IllegalArgumentException();
		}
		
		Iterator<WBANStation> newWBANIterator = new WBANIterator(wbanDataFileReader, wbanIDIndex,
				locationIndex);
		
//		wbanDataFileReader.close();
		return newWBANIterator;
	}
	
	public static HashMap<Integer, ArrayList<TimeStampedData>> getHourlyPrecipData(
			String hourlyPrecipDataFilepath, Function<LocalDateTime, Boolean> exclusionCondition) 
			throws IOException {
		
		final int WBAN_ID_INDEX = 0;
		final int YMD_INDEX = 1;
		final int HOUR_INDEX = 2;
		final int PRECIP_INDEX = 3;
		
		BufferedReader hourlyPrecipFileReader;
		hourlyPrecipFileReader = new BufferedReader(new FileReader(hourlyPrecipDataFilepath));
		
		// We want to advance to the second line before we begin processing.
		String currentLine = hourlyPrecipFileReader.readLine();
		currentLine = hourlyPrecipFileReader.readLine();
		
		HashMap<Integer, ArrayList<TimeStampedData>> newHourlyPrecipMap = 
				new HashMap<Integer, ArrayList<TimeStampedData>>();
		
		while (currentLine != null) {
			String[] cols = currentLine.split(",");
			String wbanIDString = cols[WBAN_ID_INDEX];
			String yearMonthDayString = cols[YMD_INDEX];
			String hourString = cols[HOUR_INDEX];
			String precipString = cols[PRECIP_INDEX];
			
			// We do not add empty entries to the map. Skip these.
			if (precipString.equals(" ") || precipString.contains("T")) {
				currentLine = hourlyPrecipFileReader.readLine();
				continue;
			}
			
			int wbanID;
			LocalDateTime timeStamp;
			double precipitation;
			try {
				wbanID = Integer.parseInt(wbanIDString);
				int year = Integer.parseInt(yearMonthDayString.substring(0, 4));
				int month = Integer.parseInt(yearMonthDayString.substring(4, 6));
				int day = Integer.parseInt(yearMonthDayString.substring(6));
				int hour = Integer.parseInt(hourString) - 1;  // we need to zero-index the hour
				int minute = 0;
				timeStamp = LocalDateTime.of(year, month, day, hour, minute);
				precipitation = Double.parseDouble(precipString);
			} catch (NumberFormatException e) {
				hourlyPrecipFileReader.close();
				throw new IllegalArgumentException("Malformed data in input file");
			}
			
			// If the timeStamp meets the condition, then we need to exclude this from our map.
			if (exclusionCondition.apply(timeStamp)) {
				continue;
			}
			
			if (!newHourlyPrecipMap.containsKey(wbanID)) {
				newHourlyPrecipMap.put(wbanID, new ArrayList<TimeStampedData>());
			}
			
			newHourlyPrecipMap.get(wbanID).add(new TimeStampedData(timeStamp, precipitation));
			
			currentLine = hourlyPrecipFileReader.readLine();
		}
		
		hourlyPrecipFileReader.close();
		return newHourlyPrecipMap;
	}

}

class WBANIterator implements Iterator<WBANStation> {
	private BufferedReader wbanDataFileReader;
	private int wbanIDIndex;
	private int locationIndex;
	
	// After instantiation, this value will only be null again when wbanDataFileReader has read all
	// lines in the file.
	private String nextLine;
	
	public WBANIterator(BufferedReader wbanDataFileReader, int wbanIDIndex, int locationIndex) 
			throws IOException {
		this.wbanDataFileReader = wbanDataFileReader;
		this.wbanIDIndex = wbanIDIndex;
		this.locationIndex = locationIndex;
		this.nextLine = this.wbanDataFileReader.readLine();
	}
	
	public boolean hasNext() {
		// The final lines of the list do not represent real WBANs. They begin with this prefix.
		final String END_OF_LIST_PREFIX = "\"999\"";
		
		return !nextLine.startsWith(END_OF_LIST_PREFIX) && nextLine != null;
	}
	
	public WBANStation next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		}
		
		String currentLine = nextLine;
		try {
			nextLine = wbanDataFileReader.readLine();
		} catch (IOException e) {
			throw new IllegalStateException("Iterator's backing file no longer accessible", e);
		}
		
		// If two pipe characters are side by side (as is the case for an empty field), then the
		// line will not get split properly into an array of fields. So we temporarily insert the %
		// character as a placeholder for empty fields.
		currentLine = currentLine.replace("|", "%|");
		String[] fields = currentLine.split("\\|");
		
		int wbanID;
		double latitude;
		double longitude;
		try {
			wbanID = parseWBANID(fields[wbanIDIndex]);
			double[] locationPair = new double[2];
			try {
				locationPair = parseLocationString(fields[locationIndex]);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException();
			}
			latitude = locationPair[0];
			longitude = locationPair[1];
		} catch (IllegalArgumentException e) {
			// throw new IllegalStateException("Illegal formatting in iterator's backing file", e);
			
			// If we come across a WBAN whose data is ill-formed, we will simply move on to the
			// next one. We make an exception if we are on the last line because we would rather
			// not surprise the caller with a NoSuchElementException.
			if (nextLine == null) {
				throw new IllegalStateException(
						"Illegal formatting in iterator's backing file", e);
			}
			return this.next();
		}
		
		return new WBANStation(wbanID, latitude, longitude);
	}
	
	private int parseWBANID(String wbanIDString) throws IllegalArgumentException {
		// Remove the placeholder character as well as the leading and trailing quotes characters.
		wbanIDString = wbanIDString.replace("%", "");
		wbanIDString = wbanIDString.replace("\"", "");
		
		int wbanID;
		try {
			wbanID = Integer.parseInt(wbanIDString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Malformed WBAN ID: " + wbanIDString);
		}
		
		return wbanID;
	}
	
	private double[] parseLocationString(String locationString) throws IllegalArgumentException {
		// Remove the placeholder character.
		locationString = locationString.replace("%", "");
		
		// There are two formats for the location string:
		// [format 1] "<decimal value>, <decimal longitude>"
		// [format 2] "degrees*minutes'seconds""hemisphere degrees*minutes'seconds""hemisphere"
		
		// We check for format 1 by looking for a comma.
		if (locationString.contains(",")) {
			// Remove the leading and trailing quote characters and separate by the comma.
			locationString = locationString.replace("\"", "");
			String[] locationStringPair = locationString.split(",");
			if (locationStringPair.length != 2) {
				throw new IllegalArgumentException("Malformed location: " + locationString);
			}
			
			double[] locationPair = new double[2];
			try {
				locationPair[0] = Double.parseDouble(locationStringPair[0]);
				locationPair[1] = Double.parseDouble(locationStringPair[1]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Malformed location: " + locationString);
			}
			
			return locationPair;
		} else {
			// The string locationRegEx is a regular expression matching format 2 for location strings.
			// If our string does not match this reg-ex at this point, then it must be malformed.
			String locationRegEx = "\"[0-9]+\\*[0-9]+\'[0-9]+\"\"[NS]"
					+ " [0-9]+\\*[0-9]+\'[0-9]+\"\"[EW]\"";
			if (!locationString.matches(locationRegEx)) {
				throw new IllegalArgumentException("Malformed location: " + locationString);
			}
			
			// We can separate into latitude and longitude by the space character.
			String[] locationStringPair = locationString.split(" ");
			if (locationStringPair.length != 2) {
				throw new IllegalArgumentException("Malformed location: " + locationString);
			}
			
			double[] locationPair = new double[2];
			for (int i = 0; i < locationStringPair.length; i++) {
				String dmsString = locationStringPair[i];
				dmsString = dmsString.replaceAll("\"", "");
				// We use regular expressions to extract the components of the DMS coordinate.
				String degreesString = dmsString.replaceAll("(^.*)(\\*.*)", "$1");
				String minutesString = dmsString.replaceAll("([0-9]+\\*)([0-9]+)('.*)", "$2");
				String secondsString = dmsString.replaceAll("([0-9*]+)'([0-9]+)([NSEW])", "$2");
				String directionString = dmsString.replaceAll("([0-9*']+)([NSEW]$)", "$2");
				
				int degrees;
				int minutes;
				int seconds;
				try {
					degrees = Integer.parseInt(degreesString);
					minutes = Integer.parseInt(minutesString);
					seconds = Integer.parseInt(secondsString);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Malformed location: " + locationString);
				}
				locationPair[i] = decimalFromDMS(degrees, minutes, seconds, directionString);
			}
			
			return locationPair;
		}
	}
	
	// Given a latitude or longitude in DMS (Degrees, Minutes, Seconds), returns the decimal
	// coordinate.
	private double decimalFromDMS(int degrees, int minutes, int seconds, String direction) {
		final int SECONDS_PER_DEGREE = 3600;
		
		double totalSeconds = minutes * 60 + seconds;
		double totalSecondsContributionToDegrees = totalSeconds / SECONDS_PER_DEGREE;
		double totalDegrees = degrees + totalSecondsContributionToDegrees;
		
		if (direction.equals("S") || direction.equals("W")) {
			totalDegrees = totalDegrees * -1;
		}
		
		return totalDegrees;
	}
	
}
