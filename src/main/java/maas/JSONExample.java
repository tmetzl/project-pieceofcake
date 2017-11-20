package maas;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import org.json.*;

public class JSONExample {

	public static void main(String[] args) {
		System.out.println("Hello");
		try {
			Scanner in = new Scanner(new FileReader("config/sample-scenario.json"));
			String text = "";
			while (in.hasNext())
				text = text + in.nextLine();
			// System.out.println(text);
			JSONObject scenario = new JSONObject(text);
			System.out.println(scenario.get("bakeries"));

			// Convert the bakeries into a JSONArray
			JSONArray bakeries = scenario.getJSONArray("bakeries");
			for (int i = 0; i < bakeries.length(); i++) {
				// Convert bakery to JSONObject
				JSONObject bakery = bakeries.getJSONObject(i);
				System.out.println(bakery);
			}

			// Convert the products into a JSONArray
			JSONArray customers = scenario.getJSONArray("customers");
			for (int i = 0; i < customers.length(); i++) {
				// Convert bakery to JSONObject
				JSONObject customer = customers.getJSONObject(i);
				System.out.println(customer);
			}
			// Convert the orders into a JSONArray
			JSONArray orders = scenario.getJSONArray("orders");
			for (int i = 0; i < orders.length(); i++) {
				// Convert bakery to JSONObject
				JSONObject order = orders.getJSONObject(i);
				System.out.println(order);
				Order orderObject = new Order(order.toString());
				System.out.println(orderObject);
			}
			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File not found!");
		}
	}
}
