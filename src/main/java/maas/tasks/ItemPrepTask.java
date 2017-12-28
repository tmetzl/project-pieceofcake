package maas.tasks;

import org.json.JSONObject;

public class ItemPrepTask extends Task{
	
	private String productId;
	// maybe get this from order information
	private int numOfItems;
	private long itemPrepTime;

	public int getNumOfItems() {
		return numOfItems;
	}

	public long getItemPrepTime() {
		return itemPrepTime;
	}

	public String getProductId() {
		return productId;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", productId);
		jsonObject.put("num_of_items", numOfItems);
		jsonObject.put("item_prep_time", itemPrepTime);
		jsonObject.put("order_id", getOrderId());
		jsonObject.put("due_date", getDueDate());
		jsonObject.put("release_date", getReleaseDate());
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		productId = jsonObject.getString("product_id");
		numOfItems = jsonObject.getInt("num_of_items");
		itemPrepTime = jsonObject.getLong("item_prep_time");
		setDueDate(jsonObject.getLong("due_date"));
		setReleaseDate(jsonObject.getLong("release_date"));
		setOrderId(jsonObject.getString("order_id"));
		
	}

}
