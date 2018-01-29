package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jade.core.AID;

public class BidTest {

	@Test
	public void testBid() {
		AID aid = new AID("agent", true);
		Bid bid = new Bid();
		bid.setAgentId(aid);
		bid.setCompletionTime(new Date(1, 2, 3, 4));

		assertEquals(aid, bid.getAgentId());
		assertEquals(new Date(1, 2, 3, 4), bid.getCompletionTime());
	}

}
