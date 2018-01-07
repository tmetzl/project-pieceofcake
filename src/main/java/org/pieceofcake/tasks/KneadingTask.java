package org.pieceofcake.tasks;

import org.json.JSONObject;

public class KneadingTask extends Task {

	private long kneadingTime;
	private long restingTime;

	public long getKneadingTime() {
		return kneadingTime;
	}	

	public void setKneadingTime(long kneadingTime) {
		this.kneadingTime = kneadingTime;
	}

	public long getRestingTime() {
		return restingTime;
	}
	
	public void setRestingTime(long restingTime) {
		this.restingTime = restingTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("kneading_time", kneadingTime);
		jsonObject.put("resting_time", restingTime);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setKneadingTime(jsonObject.getLong("kneading_time"));
		setRestingTime(jsonObject.getLong("resting_time"));
	}

}
