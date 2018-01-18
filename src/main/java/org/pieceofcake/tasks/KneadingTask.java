package org.pieceofcake.tasks;

import org.json.JSONObject;

public class KneadingTask extends Task {

	private long kneadingTime;

	public long getKneadingTime() {
		return kneadingTime;
	}

	public void setKneadingTime(long kneadingTime) {
		this.kneadingTime = kneadingTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("kneading_time", kneadingTime);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setKneadingTime(jsonObject.getLong("kneading_time"));
	}

	@Override
	public KneadingTask copy() {
		KneadingTask task = new KneadingTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}

}
