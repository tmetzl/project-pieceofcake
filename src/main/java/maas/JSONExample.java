package maas;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import org.json.*;

public class JSONExample {

	public static void main(String[] args) {
		System.out.println("Hello");
		try {
			Scanner in = new Scanner(new FileReader("config/scenario.json"));
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
			JSONArray products = scenario.getJSONArray("products");
			for (int i = 0; i < products.length(); i++) {
				// Convert bakery to JSONObject
				JSONObject product = products.getJSONObject(i);
				System.out.println(product);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File not found!");
		}
	}
}
