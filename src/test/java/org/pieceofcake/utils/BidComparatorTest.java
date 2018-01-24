package org.pieceofcake.utils;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.pieceofcake.objects.Bid;
import org.pieceofcake.objects.Date;

import jade.core.AID;

public class BidComparatorTest {

	@Test
	public void testBidComparator() {
		List<Bid> bids = new LinkedList<>();

		Bid bid1 = new Bid();
		bid1.setAgentId(new AID("agent1", true));
		bid1.setCompletionTime(new Date(5, 2, 3, 4));
		bids.add(bid1);
		
		Bid bid2 = new Bid();
		bid2.setAgentId(new AID("agent2", true));
		bid2.setCompletionTime(new Date(1, 2, 3, 4));
		bids.add(bid2);
		
		Bid bid3 = new Bid();
		bid3.setAgentId(new AID("agent3", true));
		bid3.setCompletionTime(new Date(2, 2, 3, 4));
		bids.add(bid3);
		
		Collections.sort(bids, new BidCompletionTimeComparator());
		
		assertEquals(new Date(1,2,3,4), bids.get(0).getCompletionTime());
		assertEquals(new Date(2,2,3,4), bids.get(1).getCompletionTime());
		assertEquals(new Date(5,2,3,4), bids.get(2).getCompletionTime());
	}

}
