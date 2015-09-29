package geography;

import census.MetropolitanStatisticalArea;
import climatology.WBANStation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MSAtoWBANMapper {
	
	public static HashMap<MetropolitanStatisticalArea, ArrayList<WBANStation>> getMSAToWBANMap(
			Iterator<MetropolitanStatisticalArea> msaIter,
			HashMap<String, ArrayList<Integer>> msaToFipsMap,
			HashMap<Integer, ArrayList<WBANStation>> fipsToWBANMap) {
		
		 
		HashMap<MetropolitanStatisticalArea, ArrayList<WBANStation>> msaToWBANMap = 
				new HashMap<MetropolitanStatisticalArea, ArrayList<WBANStation>>();
		while (msaIter.hasNext()) {
			MetropolitanStatisticalArea currentMSA = msaIter.next();
			ArrayList<Integer> fipsCodes = msaToFipsMap.get(currentMSA.getCentralUrbanArea());
			//Iterator<Integer> fipsCodesIter = fipsCodes.iterator();
			ArrayList<WBANStation> wbanList = new ArrayList<WBANStation>();
			//while (fipsCodesIter.hasNext()) {
			for (int currentFipsCode : fipsCodes) {
				//int currentFipsCode = fipsCodesIter.next();
				ArrayList<WBANStation> currentWBANS = fipsToWBANMap.get(currentFipsCode);
				wbanList.addAll(currentWBANS);
			}
			msaToWBANMap.put(msaIter.next(), wbanList);
		}
		
		return msaToWBANMap;
	}

}
