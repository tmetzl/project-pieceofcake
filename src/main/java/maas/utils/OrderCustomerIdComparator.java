package maas.utils;

import java.util.Comparator;

import maas.objects.Order;

public class OrderCustomerIdComparator implements Comparator<Order>{

	@Override
	public int compare(Order order1, Order order2) {
		String id1 = order1.getCustomerId();
		String id2 = order2.getCustomerId();
		
		return id1.compareTo(id2);
	}

}

