package maas.tasks;

import org.json.JSONObject;

public abstract class Task {

	private long dueDate;
	private long releaseDate;
	private String orderId;

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
