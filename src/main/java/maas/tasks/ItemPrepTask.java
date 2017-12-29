package maas.tasks;

import org.json.JSONObject;

public class ItemPrepTask extends Task {

	private int numOfItems;
	private long itemPrepTime;

	public ItemPrepTask() {

	}

	public ItemPrepTask(int numOfItems, long itemPrepTime, long dueDate, long releaseDate, String orderId,
			String productId) {
		this.numOfItems = numOfItems;
		this.itemPrepTime = itemPrepTime;
		setProductId(productId);
		setDueDate(dueDate);
		setReleaseDate(releaseDate);
		setOrderId(orderId);
	}

	public int getNumOfItems() {
		return numOfItems;
	}

	public long getItemPrepTime() {
		return itemPrepTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("num_of_items", numOfItems);
		jsonObject.put("item_prep_time", itemPrepTime);
		jsonObject.put("order_id", getOrderId());
		jsonObject.put("due_date", getDueDate());
		jsonObject.put("release_date", getReleaseDate());
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		numOfItems = jsonObject.getInt("num_of_items");
		itemPrepTime = jsonObject.getLong("item_prep_time");
		setDueDate(jsonObject.getLong("due_date"));
		setReleaseDate(jsonObject.getLong("release_date"));
		setOrderId(jsonObject.getString("order_id"));

	}

}
