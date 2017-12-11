package maas.objects;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class KneadingInfoTest {

	JSONObject jsonDough1;
	JSONObject jsonDough2;

	KneadingInfo dough;
	KneadingInfo dough2;
	
	@Before
	public void setupTest() {
		jsonDough1 = new JSONObject();
		jsonDough1.put("guid", "Bread");
		jsonDough1.put("dough_prep_time", 1);
		jsonDough1.put("resting_time", 9);
		
		jsonDough2 = new JSONObject();
		jsonDough2.put("guid", "Donut");
		jsonDough2.put("dough_prep_time", 3);
		jsonDough2.put("resting_time", 10);
		
		dough = new KneadingInfo();
		dough2 = new KneadingInfo();
	}
	
	@Test
	public void kneadingInfoFromJSONMessageTest() {
		dough.fromJSONMessage(jsonDough1);
		assertEquals(1, dough.getKneadingTime());
		assertEquals(9, dough.getRestingTime());
		assertEquals("Bread", dough.getProductName());
	}

	@Test
	public void kneadinInfoToJSONMessageTest() {
		dough2.setKneadingTime(3);
		dough2.setProductName("Donut");
		dough2.setRestingTime(10);
		String message = dough2.toJSONMessage();
		JSONObject obj = new JSONObject(message);
		assertEquals(jsonDough2.getString("guid"), obj.getString("guid"));
		assertEquals(jsonDough2.getInt("dough_prep_time"), obj.getInt("dough_prep_time"));
		assertEquals(jsonDough2.getInt("resting_time"), obj.getInt("resting_time"));
	}

}
