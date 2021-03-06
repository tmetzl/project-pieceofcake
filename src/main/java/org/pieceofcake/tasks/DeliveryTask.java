package org.pieceofcake.tasks;

import org.json.JSONObject;
import org.pieceofcake.objects.Location;

public class DeliveryTask extends Task {

	private static final long serialVersionUID = 1951480863418113600L;

	private int itemPerBox;
	private Location location;

	@Override
	public boolean equals(Object o) {
		if (o instanceof DeliveryTask) {
			DeliveryTask task = (DeliveryTask) o;
			return this.itemPerBox == task.getItemPerBox() && this.location.equals(task.getLocation())
					&& this.checkMutualFields(task);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.getOrderId() + this.getProductId()).hashCode();
	}

	public int getItemPerBox() {
		return itemPerBox;
	}

	public void setItemPerBox(int itemPerBox) {
		this.itemPerBox = itemPerBox;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("item_per_box", itemPerBox);
		jsonObject.put("location", location.toJSONObject());
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setItemPerBox(jsonObject.getInt("item_per_box"));
		Location locationFromJson = new Location();
		locationFromJson.fromJSONObject(jsonObject.getJSONObject("location"));
		setLocation(locationFromJson);
	}

	@Override
	public DeliveryTask copy() {
		DeliveryTask task = new DeliveryTask();
		task.fromJSONObject(this.toJSONObject());
		return task;
	}
}
