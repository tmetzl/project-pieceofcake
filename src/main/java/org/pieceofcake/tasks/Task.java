package org.pieceofcake.tasks;

import org.json.JSONObject;
import org.pieceofcake.objects.Date;

public abstract class Task {
	
	private Date dueDate;
	private Date releaseDate;
	private String orderId;
	private String productId;
	private int numOfItems;
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public int getNumOfItems() {
		return numOfItems;
	}

	public void setNumOfItems(int numOfItems) {
		this.numOfItems = numOfItems;
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("release_date", getReleaseDate().toJSONObject());
		jsonObject.put("due_date", getDueDate().toJSONObject());
		jsonObject.put("order_id", getOrderId());
		jsonObject.put("num_of_items", getNumOfItems());
		return jsonObject;
	}

	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		Date releaseDateFromJson = new Date();
		releaseDateFromJson.fromJSONObject(jsonObject.getJSONObject("release_date"));
		setReleaseDate(releaseDateFromJson);
		Date dueDateFromJson = new Date();
		dueDateFromJson.fromJSONObject(jsonObject.getJSONObject("due_date"));
		setDueDate(dueDateFromJson);
		setNumOfItems(jsonObject.getInt("num_of_items"));
		setOrderId(jsonObject.getString("order_id"));
	}

}
