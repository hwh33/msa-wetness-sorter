package climatology;

import java.time.LocalDateTime;

public class TimeStampedData {
	
	private LocalDateTime timeStamp;
	private float data;
	
	public TimeStampedData(LocalDateTime timeStamp, float data) {
		this.timeStamp = timeStamp;
		this.data = data;
	}
	
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	
	public float getData() {
		return data;
	}
}
