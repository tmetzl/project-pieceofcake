package maas;

public class Order {
	
	private String guiId;
	private String customerId;
	private int orderDate;
	private int dueDate;
	private String[] productIds;
	private int[] productAmounts;
	
	public Order(String jsonOrder) {
		// TODO: Fill the fields with the jsonOrder
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

}
