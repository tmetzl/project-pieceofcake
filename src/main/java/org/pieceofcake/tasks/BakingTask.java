package org.pieceofcake.tasks;

import org.json.JSONObject;

public class BakingTask extends Task {

	private static final long serialVersionUID = -5071207234146128199L;

	private long bakingTime;
	private long bakingTemperature;
	private int itemPerTray;

	@Override
	public boolean equals(Object o) {
		if (o instanceof BakingTask) {
			BakingTask task = (BakingTask) o;
			return this.bakingTime == task.getBakingTime() && this.bakingTemperature == task.getBakingTemperature()
					&& this.itemPerTray == task.getItemPerTray() && this.checkMutualFields(task);
		}
		return false;
	}

	public int compareTo(BakingTask otherTask) {
		if (equals(otherTask)) {
			return 0;
		}
		return 1;
	}

	@Override
	public int hashCode() {
		return (this.getOrderId() + this.getProductId()).hashCode();
	}

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
		jsonObject.put("item_per_tray", itemPerTray);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setBakingTime(jsonObject.getLong("baking_time"));
		setBakingTemperature(jsonObject.getLong("baking_temp"));
		setItemPerTray(jsonObject.getInt("item_per_tray"));
	}

	@Override
	public BakingTask copy() {
		BakingTask task = new BakingTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}

}
