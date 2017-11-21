package maas.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.json.*;

import jade.util.Logger;
import maas.objects.Order;
import maas.objects.Product;

public class JSONExample {

	public static void main(String[] args) {
		System.out.println("Hello");
		Scanner in = null;
		try {
			in = new Scanner(new FileReader("config/sample-scenario.json"));
			String text = "";
			while (in.hasNext())
				text = text + in.nextLine();
			// System.out.println(text);
			JSONObject scenario = new JSONObject(text);
			//System.out.println(scenario.get("bakeries"));

			// Convert the bakeries into a JSONArray
			JSONArray bakeries = scenario.getJSONArray("bakeries");
			for (int i = 0; i < bakeries.length(); i++) {
				// Convert bakery to JSONObject
				JSONObject bakery = bakeries.getJSONObject(i);
				JSONArray products = bakery.getJSONArray("products");
				for (int j=0; j < products.length(); j++ ) {
					JSONObject product = products.getJSONObject(j);
					//System.out.println(product);
					Product prod = new Product(product.toString());
					System.out.println(prod);
					System.out.println("\n----");
				}
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
			List<Order> listOfOrders = new LinkedList<Order>();
			for (int i = 0; i < orders.length(); i++) {
				// Convert bakery to JSONObject
				JSONObject order = orders.getJSONObject(i);
				System.out.println(order);
				Order orderObject = new Order(order.toString());
				listOfOrders.add(orderObject);
				System.out.println(orderObject);
			}
			
			System.out.println(listOfOrders);
			

		} catch (FileNotFoundException e) {
			Logger logger = Logger.getJADELogger("JSONExample");
			logger.log(Logger.WARNING, e.getMessage(), e);	
		} finally {
			in.close();
		}
	}
}
