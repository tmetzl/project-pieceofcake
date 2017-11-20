package maas.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import maas.agents.CustomerAgent;
import maas.agents.OrderAgent;
import maas.objects.Order;

public class Start {

	private AgentContainer container;

	public Start(String scenario) throws StaleProxyException {
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		runtime.setCloseVM(true);

		Properties properties = new ExtendedProperties();
		properties.put(Profile.MAIN, true);
		properties.put(Profile.GUI, false);

		Profile profile = new ProfileImpl(properties);

		container = runtime.createMainContainer(profile);
		container.acceptNewAgent("baker", new OrderAgent()).start();

		loadScenario(scenario);
	}

	public void loadScenario(String filename) throws StaleProxyException {

		Scanner in;
		try {
			in = new Scanner(new FileReader("config/" + filename + ".json"));
			String text = "";
			while (in.hasNext())
				text = text + in.nextLine();
			in.close();

			// Parse scenario as JSONObject
			JSONObject scenario = new JSONObject(text);

			// Step 1: Process the Orders
			JSONArray orders = scenario.getJSONArray("orders");

			// Create a map with the customers as keys and list of orders as
			// values
			Map<String, List<Order>> customerOrderMap = new HashMap<String, List<Order>>();
			for (int i = 0; i < orders.length(); i++) {
				// Convert each order to a JSONObject
				JSONObject order = orders.getJSONObject(i);
				Order orderObject = new Order(order.toString());
				String customerId = orderObject.getCustomerId();

				// Check whether the customer already has orders
				if (customerOrderMap.containsKey(customerId)) {
					// If the customer already has orders, update its list
					List<Order> customerOrders = customerOrderMap.get(customerId);
					customerOrders.add(orderObject);
					customerOrderMap.put(customerId, customerOrders);
				} else {
					// Else create a new list with this order and put it into
					// the map
					List<Order> customerOrders = new LinkedList<Order>();
					customerOrders.add(orderObject);
					customerOrderMap.put(customerId, customerOrders);
				}
			}

			// Step 2: Process the customers
			JSONArray customers = scenario.getJSONArray("customers");

			for (int i = 0; i < customers.length(); i++) {
				// Extract one customer and its Id
				JSONObject customer = customers.getJSONObject(i);
				String customerId = customer.getString("name");

				// Make sure the customer has orders
				if (customerOrderMap.containsKey(customerId)) {
					List<Order> customerOrders = customerOrderMap.get(customerId);
					// Create customer agent

					createCustomer(customer, customerOrders);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void createCustomer(JSONObject customer, List<Order> orders) throws StaleProxyException {
		// Get the parameters
		String name = customer.getString("name");
		String guiId = customer.getString("guid");
		String type = customer.getString("type");
		JSONObject location = customer.getJSONObject("location");
		int locationX = location.getInt("x");
		int locationY = location.getInt("y");
		// Create the agent
		CustomerAgent agent = new CustomerAgent(guiId, type, locationX, locationY, orders);

		container.acceptNewAgent(name, agent).start();

	}

	public static void main(String[] args) throws StaleProxyException {
		new Start("sample-scenario");
	}
}
