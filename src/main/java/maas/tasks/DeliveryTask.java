package maas.tasks;

import org.json.JSONObject;

public class DeliveryTask extends Task {

	private int numOfBoxes;
	private String customerId;

	public DeliveryTask() {

	}

	public DeliveryTask(int day, int numOfBoxes, String customerId, long dueDate, long releaseDate, String orderId,
			String productId) {
		this.numOfBoxes = numOfBoxes;
		this.customerId = customerId;
		setProductId(productId);
		setDueDate(dueDate);
		setReleaseDate(releaseDate);
		setOrderId(orderId);
		setDay(day);
	}

	public int getNumOfBoxes() {
		return numOfBoxes;
	}

	public String getCustomerId() {
		return customerId;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("release_date", getReleaseDate());
		jsonObject.put("due_date", getDueDate());
		jsonObject.put("order_id", getOrderId());
		jsonObject.put("num_of_boxes", numOfBoxes);
		jsonObject.put("customer_id", customerId);
		jsonObject.put("day", getDay());
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		setReleaseDate(jsonObject.getLong("release_date"));
		setDueDate(jsonObject.getLong("due_date"));
		setOrderId(jsonObject.getString("order_id"));
		numOfBoxes = jsonObject.getInt("num_of_boxes");
		customerId = jsonObject.getString("customer_id");
		setDay(jsonObject.getInt("day"));
	}

}
