package climatology;

import java.time.LocalDateTime;

public class TimeStampedData {
	
	private LocalDateTime timeStamp;
	private double data;
	
	public TimeStampedData(LocalDateTime timeStamp, double data) {
		this.timeStamp = timeStamp;
		this.data = data;
	}
	
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	
	public double getData() {
		return data;
	}
}
