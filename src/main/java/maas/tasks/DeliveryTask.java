package maas.tasks;

import org.json.JSONObject;

import maas.objects.Location;

public class DeliveryTask extends Task {

	private int itemPerBox;
	private Location location;
	private int numOfItems;

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

	public int getNumOfItems() {
		return numOfItems;
	}

	public void setNumOfItems(int numOfItems) {
		this.numOfItems = numOfItems;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject jsonObject = super.toJSONObject();
		jsonObject.put("item_per_box", itemPerBox);
		jsonObject.put("location", location.toJSONObject());
		jsonObject.put("num_of_items", numOfItems);
		return jsonObject;
	}

	@Override
	public void fromJSONObject(JSONObject jsonObject) {
		super.fromJSONObject(jsonObject);
		setItemPerBox(jsonObject.getInt("item_per_box"));
		setNumOfItems(jsonObject.getInt("num_of_items"));
		Location location = new Location();
		location.fromJSONObject(jsonObject.getJSONObject("location"));
		setLocation(location);
	}
}
