package org.pieceofcake.tasks;

import org.json.JSONObject;

public class BakingTask extends Task {

	private long bakingTime;
	private long bakingTemperature;
	private double coolingTimeFactor;
	private int itemPerTray;

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

	public double getCoolingTimeFactor() {
		return coolingTimeFactor;
	}

	public void setCoolingTimeFactor(double coolingTimeFactor) {
		this.coolingTimeFactor = coolingTimeFactor;
	}

	public int getItemPerTray() {
		return itemPerTray;
	}

	public void setItemPerTray(int itemPerTray) {
		this.itemPerTray = itemPerTray;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("baking_time", bakingTime);
		jsonObject.put("baking_temp", bakingTemperature);
		jsonObject.put("cooling_time_factor", coolingTimeFactor);
		jsonObject.put("item_per_tray", itemPerTray);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setBakingTime(jsonObject.getLong("baking_time"));
		setBakingTemperature(jsonObject.getLong("baking_temp"));
		setCoolingTimeFactor(jsonObject.getDouble("cooling_time_factor"));
		setItemPerTray(jsonObject.getInt("item_per_tray"));
	}

	@Override
	public BakingTask copy() {
		BakingTask task = new BakingTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}

}
