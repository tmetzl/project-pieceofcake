package maas.tasks;

import org.json.JSONObject;

public class BakingTask extends Task {

	private long bakingTime;
	private long bakingTemperature;
	private int numOfItems;
	private double coolingTimeFactor;

	public long getBakingTime() {
		return bakingTime;
	}

	public void setBakingTime(long bakingTime) {
		this.bakingTime = bakingTime;
	}

	public long getBakingTemperature() {
		return bakingTemperature;
	}

	public void setBakingTemperature(long bakingTemperature) {
		this.bakingTemperature = bakingTemperature;
	}

	public int getNumOfItems() {
		return numOfItems;
	}

	public void setNumOfItems(int numOfItems) {
		this.numOfItems = numOfItems;
	}

	public double getCoolingTimeFactor() {
		return coolingTimeFactor;
	}

	public void setCoolingTimeFactor(double coolingTimeFactor) {
		this.coolingTimeFactor = coolingTimeFactor;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("baking_time", bakingTime);
		jsonObject.put("baking_temp", bakingTemperature);
		jsonObject.put("num_of_items", numOfItems);
		jsonObject.put("cooling_time_factor", coolingTimeFactor);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setBakingTime(jsonObject.getLong("baking_time"));
		setBakingTemperature(jsonObject.getLong("baking_temp"));
		setNumOfItems(jsonObject.getInt("num_of_items"));
		setCoolingTimeFactor(jsonObject.getDouble("cooling_time_factor"));

	}

}
