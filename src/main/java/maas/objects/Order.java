package maas.objects;

import org.json.JSONObject;

public class Order {

	private String guiId;
	private String customerId;
	private int orderDate;
	private int dueDate;
	private String[] productIds;
	private int[] productAmounts;
	private JSONObject jsonOrder;

	public Order(String jsonOrder) {
		// Parse the jsonOrder
		JSONObject order = new JSONObject(jsonOrder);
		this.jsonOrder = order;
		this.guiId = order.getString("guid");
		this.customerId = order.getString("customer_id");
		JSONObject orderDateJSON = order.getJSONObject("order_date");
		this.orderDate = orderDateJSON.getInt("day") * 24 + orderDateJSON.getInt("hour");
		JSONObject dueDateJSON = order.getJSONObject("delivery_date");
		this.dueDate = dueDateJSON.getInt("day") * 24 + dueDateJSON.getInt("hour");
		// Get the products
		JSONObject products = order.getJSONObject("products");
		this.productIds = JSONObject.getNames(products);
		int numOfProducts = productIds.length;
		this.productAmounts = new int[numOfProducts];
		for (int i = 0; i < numOfProducts; i++) {
			this.productAmounts[i] = products.getInt(this.productIds[i]);
		}

	}

	public String getGuiId() {
		return guiId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public int getOrderDate() {
		return orderDate;
	}

	public int getDueDate() {
		return dueDate;
	}

	public String[] getProductIds() {
		return productIds;
	}

	public int[] getProductAmounts() {
		return productAmounts;
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
	
	/**
	 * Return the JSON representation of the order
	 * @return
	 */
	public String toJSONString() {
		return this.jsonOrder.toString();
	}


}
