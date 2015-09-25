package tests;

import climatology.Parser;
import climatology.TimeStampedData;
import climatology.WBANStation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ClimatologyTest extends Test {
	
	public static boolean wbanParserTest() {
		final String WBAN_DATA_FILEPATH = "data/wbanmasterlist.psv";
		final int WBAN_ENTRIES_TO_PRINT = 10;  // make sure the file contains at least this many entries+1
		
		System.out.println("Beginning climatology.wbanParser test");
		
		Iterator<WBANStation> wbanIter;
		try {
			wbanIter = Parser.getWBANIterator(WBAN_DATA_FILEPATH);
		} catch (IOException e) {
			System.out.println("Test failed - IOException caught during parsing.");
			System.out.println("Error message: " + e.getMessage());
			return false;
		}
		
		int i;
		for (i = 0; i < WBAN_ENTRIES_TO_PRINT; i++) {
			WBANStation currWBAN;
			try {
				currWBAN = wbanIter.next();
			} catch(NoSuchElementException e) {
				System.out.println("Test failed - NoSuchElementException caught while iterating"
						+ " through WBANs.");
				System.out.println("Error message: " + e.getMessage());
				return false;
			} catch(IllegalStateException e) {
				System.out.println("Test failed - IllegalStateException caught while iterating"
						+ " through WBANs.");
				System.out.println("Error message: " + e.getMessage());
				System.out.println("Cause: " + e.getCause());
				return false;
			}
			System.out.println("WBAN " + i + ": [" + currWBAN + "]");
		}
		
		WBANStation lastWBAN = null;
		while (wbanIter.hasNext()) {
			try {
				lastWBAN = wbanIter.next();
			} catch (NoSuchElementException e) {
				break;
			}
			i++;
		}
		
		if (lastWBAN != null) {
			System.out.println("Last WBAN " + ": [" + lastWBAN + "]");
		} else {
			System.out.println("Last WBAN null; perhaps some test settings were incorrect");
		}
		
		System.out.println("Total WBANs: " + i);
		
		System.out.println("climatology.wbanParser test complete - passed");
		return true;
	}
	
	public static boolean precipParserTest() {
		final String HOURLY_PRECIP_FILEPATH = "data/QCLCD201505/201505precip.txt";
		
		System.out.println("Beginning climatology.precipParserTest");
		
		HashMap<Integer, ArrayList<TimeStampedData>> precipMap;
		try {
			precipMap = Parser.getHourlyPrecipData(HOURLY_PRECIP_FILEPATH);
		} catch (IOException e) {
			System.out.println("Test failed - IOException caught during parsing.");
			System.out.println("Error message: " + e.getMessage());
			return false;
		}
		
		if (!precipMap.containsKey(103)) {
			System.out.println("Test failed - hash map did not contain expected key.");
			return false;
		}
		
		Iterator<TimeStampedData> wban103DataIter = precipMap.get(103).iterator();
		System.out.println("Time-stamped data for WBAN 103:");
		while (wban103DataIter.hasNext()) {
			TimeStampedData currentTSD = wban103DataIter.next();
			System.out.println("Time stamp: " + currentTSD.getTimeStamp() 
				+ "; data: " + currentTSD.getData());
		}
		
		System.out.println("Total WBANs in hash map: " + precipMap.size());
		
		System.out.println("climatology.precipParser test complete - passed");
		return true;
	}
	
	public boolean run() {
		return wbanParserTest() && precipParserTest();
	}

}
