package org.pieceofcake.utils;

import java.util.Comparator;

import org.pieceofcake.objects.Order;

public class OrderDueDateComparator implements Comparator<Order>{

	@Override
	public int compare(Order order1, Order order2) {
		return order1.getDueDate().compareTo(order2.getDueDate());
	}
	
}
