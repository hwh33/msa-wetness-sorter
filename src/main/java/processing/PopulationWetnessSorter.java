package processing;

import census.MetropolitanStatisticalArea;
import climatology.WBANStation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to sort Metropolitan Statistical Areas (MSAs) by population wetness for a
 * given time period. This statistic is defined as the population of an MSA multiplied by the
 * amount of rain received in the time period.
 * 
 * @author Harry Harpham
 */
public class PopulationWetnessSorter {
	
	// A dictionary in which MSA objects are indexed by their associated central urban area
	private HashMap<String, MetropolitanStatisticalArea> msaMap;
	
	private Iterator<WBANStation> wbanStationIterator;
	
	// A mapping of counties to their respective MSAs
	private HashMap<String, MetropolitanStatisticalArea> countyMSAMap;
	
	public PopulationWetnessSorter(Iterator<MetropolitanStatisticalArea> msaIterator,
			Iterator<WBANStation> wbanStationIterator) {
		
		this.msaMap = new HashMap<String, MetropolitanStatisticalArea>();
		while (msaIterator.hasNext()) {
			MetropolitanStatisticalArea currentMSA = msaIterator.next();
			msaMap.put(currentMSA.getCentralUrbanArea(), currentMSA);
		}
		this.wbanStationIterator = wbanStationIterator;
	}
	
	public List<MSAWetnessPair> getSortedMSAList() {
		// TODO: implement me
		return null;
	}

}

class MSAWetnessPair {
	
	MetropolitanStatisticalArea msa;
	float populationWetness;
	
	public MSAWetnessPair(MetropolitanStatisticalArea msa, float populationWetness) {
		this.msa = msa;
		this.populationWetness = populationWetness;
	}
	
}
