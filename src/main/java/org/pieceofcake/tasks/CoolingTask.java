package org.pieceofcake.tasks;

import org.json.JSONObject;

public class CoolingTask extends Task {

	private static final long serialVersionUID = 8050976247731177039L;

	private long bakingTemperature;
	private double coolingTimeFactor;

	@Override
	public boolean equals(Object o) {
		if (o instanceof CoolingTask) {
			CoolingTask task = (CoolingTask) o;
			return this.bakingTemperature == task.getBakingTemperature()
					&& this.coolingTimeFactor == task.getCoolingTimeFactor() && this.checkMutualFields(task);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.getOrderId() + this.getProductId()).hashCode();
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

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("baking_temp", bakingTemperature);
		jsonObject.put("cooling_time_factor", coolingTimeFactor);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setBakingTemperature(jsonObject.getLong("baking_temp"));
		setCoolingTimeFactor(jsonObject.getDouble("cooling_time_factor"));
	}

	@Override
	public CoolingTask copy() {
		CoolingTask task = new CoolingTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}

}
