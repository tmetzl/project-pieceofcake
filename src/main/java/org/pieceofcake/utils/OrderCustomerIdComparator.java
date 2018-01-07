package org.pieceofcake.utils;

import java.util.Comparator;

import org.pieceofcake.objects.Order;

public class OrderCustomerIdComparator implements Comparator<Order>{

	@Override
	public int compare(Order order1, Order order2) {
		String id1 = order1.getCustomerId();
		String id2 = order2.getCustomerId();
		
		return id1.compareTo(id2);
	}

}

