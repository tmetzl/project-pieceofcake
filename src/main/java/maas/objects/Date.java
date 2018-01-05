package maas.objects;

import java.io.Serializable;

import org.json.JSONObject;

public class Date implements Serializable {

	private static final long serialVersionUID = 6613519611127982058L;
	
	private int day;
	private int hour;
	private int minute;
	private int second;

	public Date() {

	}

	public Date(int day, int hour, int minute, int second) {
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	@Override
	public String toString() {
		return String.format("%02d:%02d:%02d:%02d", day, hour, minute, second);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Date) {
			Date date = (Date) o;
			return (day == date.getDay() && hour == date.getHour() && minute == date.getMinute()
					&& second == date.getSecond());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public long toSeconds() {
		return second + 60l * (minute + 60l * (hour + 24l * day));
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

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("day", day);
		jsonObject.put("hour", hour);
		jsonObject.put("minute", minute);
		jsonObject.put("second", second);
		return jsonObject;
	}

	public void fromJSONObject(JSONObject jsonObject) {
		day = jsonObject.getInt("day");
		hour = jsonObject.getInt("hour");
		minute = jsonObject.getInt("minute");
		second = jsonObject.getInt("second");
	}

}
