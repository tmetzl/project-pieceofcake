package org.pieceofcake.tasks;

import org.json.JSONObject;

public class ItemPrepTask extends Task {

	private static final long serialVersionUID = 4649370483138488377L;

	private long itemPrepTime;

	@Override
	public boolean equals(Object o) {
		if (o instanceof ItemPrepTask) {
			ItemPrepTask task = (ItemPrepTask) o;
			return this.itemPrepTime == task.getItemPrepTime() && this.checkMutualFields(task);
		}
		return false;
	}

	public int compareTo(ItemPrepTask otherTask) {
		if (equals(otherTask)) {
			return 0;
		}
		return 1;
	}

	@Override
	public int hashCode() {
		return (this.getOrderId() + this.getProductId()).hashCode();
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
