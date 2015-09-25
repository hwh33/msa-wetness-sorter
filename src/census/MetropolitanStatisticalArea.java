package census;

import java.time.LocalDateTime;

/**
 * A MetropolitanStatisticalArea (MSA) is a geographical region of relatively high population
 * density used by the U.S. Census Bureau for statistical purposes.
 * A MetropolitanStatisticalArea object represents a single MSA and carries with it the region's
 * population statistics as recorded by the U.S. Census and a time-stamp indicating when those
 * statistics were recorded.
 * 
 * @author Harry Harpham
 */
public class MetropolitanStatisticalArea {

		// This is the urban area by which the MSA is delineated (e.g. 'Austin, TX' or 'Dallas-Fort
		// Worth-Arlington, TX')
		private String centralUrbanArea;
		
		// The date at which the following population statistics were accurate
		private LocalDateTime statsTimeStamp;
		
		// The population as recorded by the U.S. Census data at statsTimeStamp
		private int censusPopulation;
		
		// The average yearly change in population over the decade preceding statsTimeStamp
		private float averageYearlyPopulationChange;
		
		/**
		 * Creates a new MetropolitanStatisticalArea object with the provided information.
		 * 
		 * @param centralUrbanArea					the urban area by which the MSA is delineated
		 * @param statsTimeStamp					the date at which the population statistics
		 * 											were accurate
		 * @param censusPopulation					the population as recorded at statsTimeStamp
		 * @param averageYearlyPopulationChange		the yearly change over the decade preceding
		 * 											statsTimeStamp
		 */
		public MetropolitanStatisticalArea(String centralUrbanArea, LocalDateTime statsTimeStamp, 
				int censusPopulation, float averageYearlyPopulationChange) {
			this.centralUrbanArea = centralUrbanArea;
			this.censusPopulation = censusPopulation;
			this.averageYearlyPopulationChange = averageYearlyPopulationChange;
			this.statsTimeStamp = statsTimeStamp;
		}
		
		/**
		 * Uses the population and average population change of the MSA to extrapolate the
		 * population at the given date. This extrapolation makes the assumption that population
		 * change for the MSA is linear. It is not intended to be accurate for dates far from the
		 * date at which the MSA population statistics were recorded.
		 * 
		 * @param dateToExtrapolateTo	the date at which to population should be extrapolated 
		 * @return						the population the MSA would have at dateToExtrapolateTo
		 * 								assuming linear population change over all time
		 */
		public int extrapolatePopulation(LocalDateTime dateToExtrapolateTo) {
			// TODO: implement me
			return -1;
		}
		
		/**
		 * @return		the urban area by which the MSA is delineated (e.g. 'Austin, TX' or
		 * 				'Dallas-Fort Worth-Arlington, TX')
		 */
		public String getCentralUrbanArea() {
			return centralUrbanArea;
		}
		
		/**
		 * @return 		the date at which the MSA population statistics were recorded	
		 */
		public LocalDateTime getStatsTimeStamp() {
			return statsTimeStamp;
		}
		
		/**
		 * @return		a string representation of the MSA object
		 */
		public String toString() {
			return "Central Urban Area: " + centralUrbanArea + 
					"; Census Population: " + Integer.toString(censusPopulation) + 
					"; Average Yearly Change: " + Float.toString(averageYearlyPopulationChange) +
					"; Stats Time Stamp: " + statsTimeStamp.toString();		
		}
		
}
