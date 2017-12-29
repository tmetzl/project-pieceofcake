package maas.tasks;

import org.json.JSONObject;

public class BakingTask extends Task {

	private long bakingTime;
	private long bakingTemperature;
	private int numOfItems;
	private double coolingTimeFactor;

	public BakingTask() {

	}

	public BakingTask(long bakingTime, long bakingTemperature, int numOfItems, double coolingTimeFactor, long dueDate,
			long releaseDate, String orderId, String productId) {
		this.bakingTemperature = bakingTemperature;
		this.bakingTime = bakingTime;
		this.numOfItems = numOfItems;
		this.coolingTimeFactor = coolingTimeFactor;
		setProductId(productId);
		setDueDate(dueDate);
		setReleaseDate(releaseDate);
		setOrderId(orderId);
	}

	public int getNumOfItems() {
		return numOfItems;
	}

	public long getBakingTemperature() {
		return bakingTemperature;
	}

	public long getBakingTime() {
		return bakingTime;
	}

	public double getCoolingTimeFactor() {
		return coolingTimeFactor;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("order_id", getOrderId());
		jsonObject.put("due_date", getDueDate());
		jsonObject.put("release_date", getReleaseDate());
		jsonObject.put("baking_time", bakingTime);
		jsonObject.put("baking_temp", bakingTemperature);
		jsonObject.put("num_of_items", numOfItems);
		jsonObject.put("cooling_time_factor", coolingTimeFactor);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		setOrderId(jsonObject.getString("order_id"));
		setReleaseDate(jsonObject.getLong("release_date"));
		setDueDate(jsonObject.getLong("due_date"));
		bakingTime = jsonObject.getLong("baking_time");
		bakingTemperature = jsonObject.getLong("baking_temp");
		numOfItems = jsonObject.getInt("num_of_items");
		coolingTimeFactor = jsonObject.getDouble("cooling_time_factor");
	}

}
