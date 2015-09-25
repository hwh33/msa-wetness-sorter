package climatology;

import java.util.HashMap;
import java.util.List;

public class WBANStation {
	
	private int wbanID;
	private float latitude;
	private float longitude;
	private HashMap<String, List<TimeStampedData>> stationData;
	
	public WBANStation(int wbanID, float latitude, float longitude) {
		this.wbanID = wbanID;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public int getID() {
		return wbanID;
	}
	
	public float getLatitude() {
		return latitude;
	}
	
	public float getLongitude() {
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

}
