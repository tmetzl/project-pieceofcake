package maas.tasks;

import org.json.JSONObject;

public class BakingTask extends Task {

	private long bakingTime;
	private long bakingTemperature;
	private int numOfItems;

	public int getNumOfItems() {
		return numOfItems;
	}

	public long getBakingTemperature() {
		return bakingTemperature;
	}

	public long getBakingTime() {
		return bakingTime;
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

	}

}
