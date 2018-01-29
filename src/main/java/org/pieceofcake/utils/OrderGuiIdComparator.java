package org.pieceofcake.utils;

import java.util.Comparator;

import org.pieceofcake.objects.Order;

public class OrderGuiIdComparator implements Comparator<Order>{

	@Override
	public int compare(Order order1, Order order2) {
		String orderGuiId1 = order1.getGuiId();
		String orderGuiId2 = order2.getGuiId();
		
		return orderGuiId1.compareTo(orderGuiId2);
	}
	
	

}
