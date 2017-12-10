package maas.gui.utils;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ShoppingListEntry {
	
	private SimpleStringProperty productName;
	private SimpleIntegerProperty productAmount;
	
	public void setProductName(String name) {
		productNameProperty().set(name);
	}

	public String getProductName() {
		return productName.get();
	}
	
	public SimpleStringProperty productNameProperty() {
		if (productName == null) {
			productName = new SimpleStringProperty(this, "productName");
		}
		return productName;
	}
	
	public void setProductAmount(int amount) {
		productAmountProperty().set(amount);
	}

	public Integer getProductAmount() {
		return productAmount.get();
	}
	
	public SimpleIntegerProperty productAmountProperty() {
		if (productAmount == null) {
			productAmount = new SimpleIntegerProperty(this, "productAmount");
		}
		return productAmount;
	}


}
