import census.MetropolitanStatisticalArea;
import climatology.WBANStation;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Start {
	
	static final String DATA_DIRECTORY = "data";
	static final String MSA_DATA = DATA_DIRECTORY + File.separator + "CPH-T-5.xls";
	static final String WBAN_DATA = DATA_DIRECTORY + File.separator + "wbanmasterlist.psv";
	static final String MSA_DELINEATION_DATA = DATA_DIRECTORY + File.separator + "msa-delineations.xls";
	static final String PRECIP_DATA = DATA_DIRECTORY + File.separator + "QCLCD201505" + 
			File.separator + "201505precip.txt";
	// April 1, 2010
	static final LocalDateTime CENSUS_TIME_STAMP = LocalDateTime.of(2015, 4, 1, 0, 0);
	
	public static void main(String[] args) {
		
		System.out.println("Loading MSA and WBAN data");
		Iterator<MetropolitanStatisticalArea> msaIter;
		Iterator<WBANStation> wbanIter;
		try {
			msaIter = census.Parser.getMSAIterator(MSA_DATA, CENSUS_TIME_STAMP);
			wbanIter = climatology.Parser.getWBANIterator(WBAN_DATA);
		} catch (IOException e) {
			System.out.println("Problem encountered opening data files: " + e.getMessage());
			System.exit(1);
			return;
		}
		
		HashMap<String, ArrayList<Integer>> msaToFIPSMap = census.Parser.getMSAtoFIPSMap(
				MSA_DELINEATION_DATA);
		
		System.out.println("Connecting MSAs and WBANs");
		HashMap<MetropolitanStatisticalArea, ArrayList<WBANStation>> msaToWBANMap;
		try {
			HashMap<Integer, ArrayList<WBANStation>> fipsToWBANMap = 
					climatology.FCCClient.getFIPSToWBANMap(wbanIter);
			msaToWBANMap = geography.MSAtoWBANMapper.getMSAToWBANMap(
					msaIter, msaToFIPSMap, fipsToWBANMap);
		} catch (IOException e) {
			System.out.println("Problem encountered retrieving data from FCC servers: " 
					+ e.getMessage());
		}
		
		// Now use msaToWBANMap to iterate through keys (MSAs) and sum up precipitation data
		// (stored in WBANs), multiplying by extrapolated population (via MSAs). Should get the
		// proper results.
	}

}
