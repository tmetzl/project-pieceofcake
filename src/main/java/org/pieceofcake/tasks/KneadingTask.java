package org.pieceofcake.tasks;

import org.json.JSONObject;

public class KneadingTask extends Task {

	private static final long serialVersionUID = 1037416424420325389L;

	private long kneadingTime;

	@Override
	public boolean equals(Object o) {
		if (o instanceof KneadingTask) {
			KneadingTask task = (KneadingTask) o;
			return this.kneadingTime == task.getKneadingTime() && this.checkMutualFields(task);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.getOrderId() + this.getProductId()).hashCode();
	}

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
