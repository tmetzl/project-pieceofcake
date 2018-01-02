package maas.tasks;

import org.json.JSONObject;

import maas.objects.Date;

public abstract class Task {
	
	private Date dueDate;
	private Date releaseDate;
	private String orderId;
	private String productId;
	

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

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", getProductId());
		jsonObject.put("release_date", getReleaseDate().toJSONObject());
		jsonObject.put("due_date", getDueDate().toJSONObject());
		jsonObject.put("order_id", getOrderId());
		return jsonObject;
	}

	public void fromJSONObject(JSONObject jsonObject) {
		setProductId(jsonObject.getString("product_id"));
		Date releaseDate = new Date();
		releaseDate.fromJSONObject(jsonObject.getJSONObject("release_date"));
		setReleaseDate(releaseDate);
		Date dueDate = new Date();
		dueDate.fromJSONObject(jsonObject.getJSONObject("due_date"));
		setDueDate(dueDate);
		setOrderId(jsonObject.getString("order_id"));
	}

}
