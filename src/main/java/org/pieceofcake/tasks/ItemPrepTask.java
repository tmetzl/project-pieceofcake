package org.pieceofcake.tasks;

import org.json.JSONObject;

public class ItemPrepTask extends Task {

	private int numOfItems;
	private long itemPrepTime;

	public int getNumOfItems() {
		return numOfItems;
	}

	public void setNumOfItems(int numOfItems) {
		this.numOfItems = numOfItems;
	}

	public long getItemPrepTime() {
		return itemPrepTime;
	}

	public void setItemPrepTime(long itemPrepTime) {
		this.itemPrepTime = itemPrepTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("num_of_items", numOfItems);
		jsonObject.put("item_prep_time", itemPrepTime);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setNumOfItems(jsonObject.getInt("num_of_items"));
		setItemPrepTime(jsonObject.getLong("item_prep_time"));
	}
}
