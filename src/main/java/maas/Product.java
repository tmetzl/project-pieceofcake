package maas;

public class Product {
	
	private String id;
	private int doughPrepTime;
	private int doughRestingTime;
	private int itemPrepTime;
	private int breadsPerOven;
	private int bakingTime;
	private int bakingTemp;
	private int coolingRate;
	private int boxingTemp;
	private int breadsPerBox;
	private int productionCost;
	private int salesPrice;
	
	public Product(String jsonProduct) {
		// TODO: Convert jsonProduct to product object
	}
	public String getId() {
		return id;
	}
	public int getDoughPrepTime() {
		return doughPrepTime;
	}
	public int getDoughRestingTime() {
		return doughRestingTime;
	}
	public int getItemPrepTime() {
		return itemPrepTime;
	}
	public int getBreadsPerOven() {
		return breadsPerOven;
	}
	public int getBakingTime() {
		return bakingTime;
	}
	public int getBakingTemp() {
		return bakingTemp;
	}
	public int getCoolingRate() {
		return coolingRate;
	}
	public int getBoxingTemp() {
		return boxingTemp;
	}
	public int getBreadsPerBox() {
		return breadsPerBox;
	}
	public int getProductionCost() {
		return productionCost;
	}
	public int getSalesPrice() {
		return salesPrice;
	}

}
