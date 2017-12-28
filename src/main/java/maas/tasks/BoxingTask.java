package maas.tasks;

import org.json.JSONObject;

public class BoxingTask extends Task {

	private long boxingTemperature;
	private int numOfItems;

	public long getBoxingTemperature() {
		return boxingTemperature;
	}

	public int getNumOfItems() {
		return numOfItems;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("order_id", getOrderId());
		jsonObject.put("due_date", getDueDate());
		jsonObject.put("release_date", getReleaseDate());
		jsonObject.put("boxing_temp", boxingTemperature);
		jsonObject.put("num_of_items", numOfItems);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		setOrderId(jsonObject.getString("order_id"));
		setReleaseDate(jsonObject.getLong("release_date"));
		setDueDate(jsonObject.getLong("due_date"));
		boxingTemperature = jsonObject.getLong("boxing_temp");
		numOfItems = jsonObject.getInt("num_of_items");
	}

}
