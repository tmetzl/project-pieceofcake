package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import maas.objects.Date;

public class KneadingTaskTest {

	@Test
	public void testGettersAndSetters() {
		Date releaseDate = new Date(0, 3, 1, 0);
		Date dueDate = new Date(1, 2, 3, 4);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("product_id", "Bread");
		jsonObject.put("kneading_time", 10);
		jsonObject.put("resting_time", 2);
		jsonObject.put("release_date", releaseDate.toJSONObject());
		jsonObject.put("due_date", dueDate.toJSONObject());
		jsonObject.put("order_id", "order-001");

		KneadingTask kneadingTask = new KneadingTask();
		kneadingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", kneadingTask.getProductId());
		assertEquals(10, kneadingTask.getKneadingTime());
		assertEquals(2, kneadingTask.getRestingTime());
		assertEquals(releaseDate, kneadingTask.getReleaseDate());
		assertEquals(dueDate, kneadingTask.getDueDate());
		assertEquals("order-001", kneadingTask.getOrderId());

		JSONObject jsonObjectFromKneadingTask = kneadingTask.toJSONObject();
		KneadingTask anotherKneadingTask = new KneadingTask();
		anotherKneadingTask.fromJSONObject(jsonObjectFromKneadingTask);

		assertEquals("Bread", anotherKneadingTask.getProductId());
		assertEquals(10, anotherKneadingTask.getKneadingTime());
		assertEquals(2, anotherKneadingTask.getRestingTime());
		assertEquals(releaseDate, anotherKneadingTask.getReleaseDate());
		assertEquals(dueDate, anotherKneadingTask.getDueDate());
		assertEquals("order-001", anotherKneadingTask.getOrderId());
	}
}
