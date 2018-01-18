package org.pieceofcake.tasks;

import org.json.JSONObject;

public class BoxingTask extends Task {

	private int itemPerBox;

	public int getItemPerBox() {
		return itemPerBox;
	}

	public void setItemPerBox(int itemPerBox) {
		this.itemPerBox = itemPerBox;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("item_per_box", itemPerBox);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setItemPerBox(jsonObject.getInt("item_per_box"));
	}

	@Override
	public BoxingTask copy() {
		BoxingTask task = new BoxingTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}

}
