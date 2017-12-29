package maas.tasks;

import org.json.JSONObject;

public class KneadingTask extends Task {

	private long kneadingTime;
	private long restingTime;

	public KneadingTask() {

	}

	public KneadingTask(long kneadingTime, long restingTime, long dueDate, long releaseDate, String orderId, String productId) {
		this.kneadingTime = kneadingTime;
		this.restingTime = restingTime;
		setProductId(productId);
		setDueDate(dueDate);
		setReleaseDate(releaseDate);
		setOrderId(orderId);
	}

	public long getKneadingTime() {
		return kneadingTime;
	}

	public long getRestingTime() {
		return restingTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("kneading_time", kneadingTime);
		jsonObject.put("resting_time", restingTime);
		jsonObject.put("release_date", getReleaseDate());
		jsonObject.put("due_date", getDueDate());
		jsonObject.put("order_id", getOrderId());
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		kneadingTime = jsonObject.getLong("kneading_time");
		restingTime = jsonObject.getLong("resting_time");
		setReleaseDate(jsonObject.getLong("release_date"));
		setDueDate(jsonObject.getLong("due_date"));
		setOrderId(jsonObject.getString("order_id"));
	}

}
