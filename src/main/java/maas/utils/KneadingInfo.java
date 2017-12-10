package maas.utils;

import java.io.Serializable;

public class KneadingInfo implements Serializable {

	private static final long serialVersionUID = -3683289460048995532L;
	private String productName;
	private long kneadingTime;

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
}