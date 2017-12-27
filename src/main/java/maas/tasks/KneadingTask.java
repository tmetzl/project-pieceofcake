package maas.tasks;

import org.json.JSONObject;

public class KneadingTask extends Task {

	private String doughId;
	private long kneadingTime;

	public String getDoughId() {
		return doughId;
	}

	public long getKneadingTime() {
		return kneadingTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("dough_id", doughId);
		jsonObject.put("kneading_time", kneadingTime);
		jsonObject.put("release_date", getReleaseDate());
		jsonObject.put("due_date", getDueDate());
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		doughId = jsonObject.getString("dough_id");
		kneadingTime = jsonObject.getLong("kneading_time");
		setReleaseDate(jsonObject.getLong("release_date"));
		setDueDate(jsonObject.getLong("due_date"));
	}

}
