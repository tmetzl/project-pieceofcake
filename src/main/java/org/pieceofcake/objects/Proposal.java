package org.pieceofcake.objects;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Proposal {
	
	private String orderId;
	private String productId;
	private List<Date> completionTimes;
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public List<Date> getCompletionTimes() {
		return completionTimes;
	}

	public void setCompletionTimes(List<Date> completionTimes) {
		this.completionTimes = completionTimes;
	}
	
	public JSONObject toJSONObject() {
		JSONObject jsonProposal = new JSONObject();
		jsonProposal.put("order_id", orderId);
		jsonProposal.put("product_id", productId);
		JSONArray dates = new JSONArray(completionTimes);
		jsonProposal.put("completion_times", dates);
		return jsonProposal;
	}
	
	public void fromJSONObject(JSONObject jsonProposal) {
		orderId = jsonProposal.getString("order_id");
		productId = jsonProposal.getString("product_id");
		JSONArray dates = jsonProposal.getJSONArray("completion_times");
		completionTimes = new LinkedList<>();
		for (int i=0;i<dates.length();i++) {
			Date date = new Date();
			date.fromJSONObject(dates.getJSONObject(i));
			completionTimes.add(date);
		}
		Collections.sort(completionTimes);
	}
	
}
