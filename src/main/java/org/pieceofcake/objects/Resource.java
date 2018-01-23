package org.pieceofcake.objects;

import java.io.Serializable;

import org.json.JSONObject;

public class Resource implements Serializable {
	
	private static final long serialVersionUID = 1734099398446059806L;
	
	private String resourceType;
	private String productId;
	private int amount;
	
	public String getResourceType() {
		return resourceType;
	}
	
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public String getProductId() {
		return productId;
	}
	
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public JSONObject toJSONObject() {
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("resource_type", resourceType);
		jsonRequest.put("product_id", productId);
		jsonRequest.put("amount", amount);		
		return jsonRequest;
	}
	
	public void fromJSONObject(JSONObject jsonRequest) {
		resourceType = jsonRequest.getString("resource_type");
		productId = jsonRequest.getString("product_id");
		amount = jsonRequest.getInt("amount");
	}

}
