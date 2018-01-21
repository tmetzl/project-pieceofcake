package org.pieceofcake.tasks;

import org.json.JSONObject;

public class RestingTask extends Task {

	private static final long serialVersionUID = 5746383628733720536L;
	
	private long restingTime;

	public long getRestingTime() {
		return restingTime;
	}

	public void setRestingTime(long restingTime) {
		this.restingTime = restingTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("resting_time", restingTime);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setRestingTime(jsonObject.getLong("resting_time"));
	}

	@Override
	public RestingTask copy() {
		RestingTask task = new RestingTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}

}
