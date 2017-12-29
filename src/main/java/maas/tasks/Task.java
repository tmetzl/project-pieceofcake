package maas.tasks;

import org.json.JSONObject;

public abstract class Task {
	
	private int day;
	private long dueDate;
	private long releaseDate;
	private String orderId;
	private String productId;
	
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	public long getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(long releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public abstract JSONObject toJSONObject();

	public abstract void fromJSONObject(JSONObject jsonObject);

}
