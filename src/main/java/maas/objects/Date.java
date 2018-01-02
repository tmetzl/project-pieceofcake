package maas.objects;

public class Date {
	
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	public Date(int day, int hour, int minute, int second) {
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
	
	public Date(int day, int hour) {
		this.day = day;
		this.hour = hour;
		this.minute = 0;
		this.second = 0;
	}
	
	@Override
	public String toString() {
		return String.format("%02d:%02d:%02d:%02d", day, hour, minute, second);
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}	

}
