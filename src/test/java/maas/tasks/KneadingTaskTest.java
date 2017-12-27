package maas.tasks;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class KneadingTaskTest {

	@Test
	public void testGettersAndSetters() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("dough_id", "Bread");
		jsonObject.put("kneading_time", 10);
		jsonObject.put("release_date", 2);
		jsonObject.put("due_date", 14);

		KneadingTask kneadingTask = new KneadingTask();
		kneadingTask.fromJSONObject(jsonObject);

		assertEquals("Bread", kneadingTask.getDoughId());
		assertEquals(10, kneadingTask.getKneadingTime());
		assertEquals(2, kneadingTask.getReleaseDate());
		assertEquals(14, kneadingTask.getDueDate());

		JSONObject jsonObjectFromKneadingTask = kneadingTask.toJSONObject();

		assertEquals(4, jsonObjectFromKneadingTask.length());
		assertEquals("Bread", jsonObjectFromKneadingTask.get("dough_id"));
		assertEquals(10l, jsonObjectFromKneadingTask.get("kneading_time"));
		assertEquals(2l, jsonObjectFromKneadingTask.get("release_date"));
		assertEquals(14l, jsonObjectFromKneadingTask.get("due_date"));
	}

}
