package tests;

import java.util.ArrayList;
import java.util.Iterator;

public class RunTests {
	
	public static void main(String[] args) {
		// test code; delete
//		String locationRegEx = "\"[0-9]+\\*[0-9]+\'[0-9]+\"\"[NS]"
//				+ " [0-9]+\\*[0-9]+\'[0-9]+\"\"[EW]\"";
//		String locTest1 = "\"48*27'52\"\"N 119*31'01\"\"W\"";
//		System.out.println("locTest1: " + locTest1);
//		System.out.println(locTest1.matches(locationRegEx));
//		
//		String[] locTestPair = locTest1.split(" ");
//		String latString = locTestPair[0];
//		latString = latString.replace("\"", "");
//		System.out.println(latString);
//		System.out.println(latString.replaceAll("(^.*)(\\*.*)", "$1"));
//		System.out.println(latString.replaceAll("([0-9]+\\*)([0-9]+)('.*)", "$2"));
//		System.out.println(latString.replaceAll("([0-9*]+)'([0-9]+)([NSEW])", "$2"));
//		System.out.println(latString.replaceAll("([0-9*']+)([NSEW]$)", "$2"));
		
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

}
