package maas.objects;

import java.io.Serializable;

import org.json.JSONObject;

public class KneadingInfo implements Serializable {

	private static final long serialVersionUID = -3683289460048995532L;
	private String productName;
	private long kneadingTime;
	private long restingTime;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public long getKneadingTime() {
		return kneadingTime;
	}

	public void setKneadingTime(long kneadingTime) {
		this.kneadingTime = kneadingTime;
	}

	public long getRestingTime() {
		return restingTime;
	}

	public void setRestingTime(long restingTime) {
		this.restingTime = restingTime;
	}

	public String toJSONMessage() {
		JSONObject obj = new JSONObject();
		obj.put("guid", productName);
		obj.put("dough_prep_time", kneadingTime);
		obj.put("resting_time", restingTime);
		return obj.toString();
	}

	public void fromJSONMessage(JSONObject obj) {
		productName = obj.getString("guid");
		kneadingTime = obj.getLong("dough_prep_time");
		restingTime = obj.getLong("resting_time");
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof KneadingInfo) {
			KneadingInfo dough = (KneadingInfo) o;
			return dough.getProductName().equals(productName);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return productName.hashCode();
	}
	
}