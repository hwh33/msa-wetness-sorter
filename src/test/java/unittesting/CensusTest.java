package unittesting;

import census.MetropolitanStatisticalArea;
import census.Parser;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.time.LocalDateTime;

public class CensusTest extends Test {
	
	public static boolean parserTest() {
		final String CENSUS_DATA_FILEPATH = "data/CPH-T-5.xls";
		final LocalDateTime CENSUS_TIME_STAMP = LocalDateTime.of(2010, 4, 1, 0, 0); // April 1, 2010
		final int MSA_ENTRIES_TO_PRINT = 10;  // make sure the file contains at least this many entries+1
		
		System.out.println("Beginning census.Parser test");
		
		Iterator<MetropolitanStatisticalArea> msaIter;
		try {
			msaIter = Parser.getMSAIterator(CENSUS_DATA_FILEPATH, CENSUS_TIME_STAMP);
		} catch(IOException e) {
			System.out.println("Test failed - IOException caught during parsing.");
			System.out.println("Error message: " + e.getMessage());
			return false;
		}
		
		int i;
		for (i = 0; i < MSA_ENTRIES_TO_PRINT; i++) {
			MetropolitanStatisticalArea currMSA;
			try {
				currMSA = msaIter.next();
			} catch(NoSuchElementException e) {
				System.out.println(
						"Test failed - NoSuchElementException caught while iterating through MSAs.");
				System.out.println("Error message: " + e.getMessage());
				return false;
			}
			System.out.println("MSA " + i + ": [" + currMSA + "]");
		}
		
		MetropolitanStatisticalArea lastMSA = null;
		while (msaIter.hasNext()) {
			lastMSA = msaIter.next();
			i++;
		}
		
		if (lastMSA != null) {
			System.out.println("Last MSA " + ": [" + lastMSA + "]");
		} else {
			System.out.println("Last MSA null; perhaps some test settings were incorrect");
		}
		
		System.out.println("Total MSAs: " + i);
		
		System.out.println("census.Parser test complete - passed");
		return true;
	}
	
	public boolean run() {
		return parserTest();
	}

}
