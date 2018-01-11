package org.pieceofcake.objects;

import java.io.Serializable;

import org.json.JSONObject;

public class Order implements Serializable {

	private static final long serialVersionUID = -2137246400449003684L;

	private String guiId;
	private String customerId;
	private Date orderDate;
	private Date dueDate;
	private String[] productIds;
	private int[] productAmounts;

	public Order(JSONObject jsonOrder) {
		// Parse the jsonOrder
		fromJSONObject(jsonOrder);
	}

	public String getGuiId() {
		return guiId;
	}

	public void setGuiId(String guiId) {
		this.guiId = guiId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String[] getProductIds() {
		return productIds;
	}

	public void setProductIds(String[] productIds) {
		this.productIds = productIds;
	}

	public int[] getProductAmounts() {
		return productAmounts;
	}

	public void setProductAmounts(int[] productAmounts) {
		this.productAmounts = productAmounts;
	}

	/**
	 * Create a String representation of the order
	 */
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("Order " + getGuiId() + " from customer " + getCustomerId());
		bld.append("\nOrderDate (in hours): " + getOrderDate());
		bld.append("\nDueDate (in hours): " + getDueDate());
		bld.append("\nProducts: ");

		for (int i = 0; i < this.productIds.length - 1; i++) {
			bld.append("(" + this.productIds[i] + ", " + this.productAmounts[i] + "), ");
		}
		bld.append("(" + this.productIds[this.productIds.length - 1] + ", "
				+ this.productAmounts[this.productIds.length - 1] + ")");
		return bld.toString();
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("guid", getGuiId());
		jsonObject.put("customer_id", getCustomerId());
		jsonObject.put("order_date", getOrderDate().toJSONObject());
		jsonObject.put("delivery_date", getDueDate().toJSONObject());
		JSONObject products = new JSONObject();
		for (int i = 0; i < productIds.length; i++) {
			products.put(productIds[i], productAmounts[i]);
		}
		jsonObject.put("products", products);
		return jsonObject;
	}

	public void fromJSONObject(JSONObject jsonObject) {
		setGuiId(jsonObject.getString("guid"));
		setCustomerId(jsonObject.getString("customer_id"));
		setOrderDate(new Date(jsonObject.getJSONObject("order_date")));
		setDueDate(new Date(jsonObject.getJSONObject("delivery_date")));
		JSONObject products = jsonObject.getJSONObject("products");
		setProductIds(JSONObject.getNames(products));
		int numOfProducts = productIds.length;
		int[] productAmountsFromJSON = new int[numOfProducts];
		for (int i = 0; i < numOfProducts; i++) {
			productAmountsFromJSON[i] = products.getInt(productIds[i]);
		}
		setProductAmounts(productAmountsFromJSON);
	}

}
