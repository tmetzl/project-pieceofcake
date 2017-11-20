package maas;

import java.util.Comparator;

public class OrderDateComparator implements Comparator<Order>{

	@Override
	public int compare(Order order1, Order order2) {
		int orderDate1 = order1.getOrderDate();
		int orderDate2 = order2.getOrderDate();	
		
		return orderDate1 - orderDate2;
	}

}
