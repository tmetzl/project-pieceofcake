package maas.tasks;

import org.json.JSONObject;

public class DeliveryTask extends Task {

	private int numOfBoxes;
	private double locationX;
	private double locationY;

	public int getNumOfBoxes() {
		return numOfBoxes;
	}

	public double getLocationX() {
		return locationX;
	}

	public double getLocationY() {
		return locationY;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("release_date", getReleaseDate());
		jsonObject.put("due_date", getDueDate());
		jsonObject.put("order_id", getOrderId());
		jsonObject.put("num_of_boxes", numOfBoxes);
		jsonObject.put("loc_x", locationX);
		jsonObject.put("loc_y", locationY);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		setReleaseDate(jsonObject.getLong("release_date"));
		setDueDate(jsonObject.getLong("due_date"));
		setOrderId(jsonObject.getString("order_id"));
		numOfBoxes = jsonObject.getInt("num_of_boxes");
		locationX = jsonObject.getDouble("loc_x");
		locationY = jsonObject.getDouble("loc_y");
	}

}
