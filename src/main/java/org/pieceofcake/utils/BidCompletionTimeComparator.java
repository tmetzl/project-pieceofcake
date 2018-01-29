package org.pieceofcake.utils;

import java.util.Comparator;

import org.pieceofcake.objects.Bid;

public class BidCompletionTimeComparator implements Comparator<Bid> {

	@Override
	public int compare(Bid bid1, Bid bid2) {
		return bid1.getCompletionTime().compareTo(bid2.getCompletionTime());
	}

}
