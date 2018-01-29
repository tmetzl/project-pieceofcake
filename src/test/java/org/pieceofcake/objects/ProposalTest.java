package org.pieceofcake.objects;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ProposalTest {
	

	private Proposal proposal;
	
	@Before
	public void prepareProposal() {	
		proposal = new Proposal();
		
		List<Date> dates2 = new LinkedList<>();
		dates2.add(new Date(1, 2, 10, 0));
		dates2.add(new Date(1, 3, 00, 0));
		dates2.add(new Date(1, 3, 10, 0));
		dates2.add(new Date(1, 3, 20, 0));
		dates2.add(new Date(1, 4, 00, 0));
		
		proposal.setCompletionTimes(dates2);
		proposal.setOrderId("order-002");
		proposal.setProductId("Pie");
	}
	
	@Test
	public void testJSONMethods() {
		Proposal proposalFromJSON = new Proposal();
		proposalFromJSON.fromJSONObject(proposal.toJSONObject());
		
		assertEquals(proposal.getOrderId(), proposalFromJSON.getOrderId());
		assertEquals(proposal.getProductId(), proposalFromJSON.getProductId());
		
		List<Date> expectedDates = proposal.getCompletionTimes();
		List<Date> datesFromJSON = proposalFromJSON.getCompletionTimes();		
		for (int i=0;i<expectedDates.size();i++) {
			assertEquals(expectedDates.get(i), datesFromJSON.get(i));
		}
	}

}
