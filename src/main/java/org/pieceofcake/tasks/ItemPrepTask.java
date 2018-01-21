package org.pieceofcake.tasks;

import org.json.JSONObject;

public class ItemPrepTask extends Task {

	private static final long serialVersionUID = 4649370483138488377L;
	
	private long itemPrepTime;

	public long getItemPrepTime() {
		return itemPrepTime;
	}

	public void setItemPrepTime(long itemPrepTime) {
		this.itemPrepTime = itemPrepTime;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("item_prep_time", itemPrepTime);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setItemPrepTime(jsonObject.getLong("item_prep_time"));
	}
	
	@Override
	public ItemPrepTask copy() {
		ItemPrepTask task = new ItemPrepTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}
}
