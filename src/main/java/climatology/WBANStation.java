package climatology;

import java.util.HashMap;
import java.util.List;

public class WBANStation {
	
	private int wbanID;
	private double latitude;
	private double longitude;
	private HashMap<String, List<TimeStampedData>> stationData;
	
	public WBANStation(int wbanID, double latitude, double longitude) {
		this.wbanID = wbanID;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public int getID() {
		return wbanID;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void inputStationData(String dataName, List<TimeStampedData> dataList) {
		stationData.put(dataName, dataList);
	}
	
	public List<TimeStampedData> getStationData(String dataName) {
		return stationData.get(dataName);
	}
	
	public void deleteStationData(String dataName) {
		stationData.remove(dataName);
	}
	
	public String toString() {
		String stringToReturn =  "WBAN-ID: " + Integer.toString(wbanID)
				+ "; latitude: " + Double.toString(latitude)
				+ "; longitude: " + Double.toString(longitude);
		if (stationData != null) {
			stringToReturn = stringToReturn + "; Station Data: " + stationData.toString();
		}
		return stringToReturn;
	}

}
