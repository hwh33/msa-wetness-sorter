package unit_testing;

import climatology.WBANStation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

// TODO: junit testing
public class RunTests {
	
	public static void main(String[] args) throws IOException {
		ArrayList<Test> allTests = new ArrayList<Test>();
		
		allTests.add(new CensusTest());
		allTests.add(new ClimatologyTest());
		
		Iterator<Test> testIter = allTests.iterator();
		boolean allTestsPassed = true;
		while (testIter.hasNext()) {
			allTestsPassed = allTestsPassed && testIter.next().run();
		}
		
		if (allTestsPassed) {
			System.out.println("Testing complete - all tests passed!");
		} else {
			System.out.println("Testing complete (failed tests)");
		}
	}
	
	private static URL apiRequestFromWBAN(WBANStation wbanStation) throws MalformedURLException {
		return new URL("http://data.fcc.gov/api/block/find?latitude=" + wbanStation.getLatitude()
			+ "&longitude=" + wbanStation.getLongitude() + "&showall=false");
	}

}
