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
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import maas.agents.CustomerAgent;
import maas.agents.OrderAgent;
import maas.objects.Bakery;
import maas.objects.Order;
import maas.objects.Product;

public class Start {

	private AgentContainer container;
	private Logger logger;
	private List<CustomerAgent> customers;

	public Start(String scenario) {
		// Create logger
		logger = Logger.getJADELogger(this.getClass().getName());
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		runtime.setCloseVM(true);

		Properties properties = new ExtendedProperties();
		properties.put(Profile.MAIN, true);
		properties.put(Profile.GUI, false);

		Profile profile = new ProfileImpl(properties);

		container = runtime.createMainContainer(profile);
		
		customers = new LinkedList<>();
		
		try {
			loadScenario(scenario);
		} catch (StaleProxyException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
		}		
		
	}
	
	public List<CustomerAgent> getCustomers() {
		return customers;
	}

	public void loadScenario(String filename) throws StaleProxyException {

		try (Scanner in = new Scanner(new FileReader("src/main/config/" + filename + ".json"))) {

			StringBuilder bld = new StringBuilder();
			while (in.hasNext())
				bld.append(in.nextLine());

			String text = bld.toString();

			// Parse scenario as JSONObject
			JSONObject scenario = new JSONObject(text);

			// Step 1: Process bakeries
			JSONArray bakeries = scenario.getJSONArray("bakeries");

			for (int i = 0; i < bakeries.length(); i++) {
				JSONObject bakery = bakeries.getJSONObject(i);
				createBakery(bakery);
			}

			// Step 2: Process the Orders
			JSONArray orders = scenario.getJSONArray("orders");

			// Create a map with the customers as keys and list of orders as
			// values
			Map<String, List<Order>> customerOrderMap = new HashMap<>();
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
					List<Order> customerOrders = new LinkedList<>();
					customerOrders.add(orderObject);
					customerOrderMap.put(customerId, customerOrders);
				}
			}

			// Step 3: Process the customers
			JSONArray jsonCustomers = scenario.getJSONArray("customers");

			for (int i = 0; i < jsonCustomers.length(); i++) {
				// Extract one customer and its Id
				JSONObject customer = jsonCustomers.getJSONObject(i);
				String customerId = customer.getString("guid");

				// Make sure the customer has orders
				if (customerOrderMap.containsKey(customerId)) {
					List<Order> customerOrders = customerOrderMap.get(customerId);
					// Create customer agent

					createCustomer(customer, customerOrders);
				}
			}

		} catch (FileNotFoundException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
		}

	}

	public void createCustomer(JSONObject customer, List<Order> orders) throws StaleProxyException {
		// Get the parameters
		String name = customer.getString("name");
		String guiId = customer.getString("guid");
		int type = customer.getInt("type");
		JSONObject location = customer.getJSONObject("location");
		int locationX = location.getInt("x");
		int locationY = location.getInt("y");
		// Create the agent
		CustomerAgent agent = new CustomerAgent(guiId, type, locationX, locationY, orders);
		customers.add(agent);
		container.acceptNewAgent(name, agent).start();

	}

	public void createBakery(JSONObject jsonBakery) throws StaleProxyException {
		String name = jsonBakery.getString("name");
		String guiId = jsonBakery.getString("guid");
		JSONObject location = jsonBakery.getJSONObject("location");
		int locationX = location.getInt("x");
		int locationY = location.getInt("y");

		Bakery bakery = new Bakery(guiId, name, locationX, locationY);

		JSONArray products = jsonBakery.getJSONArray("products");
		for (int i = 0; i < products.length(); i++) {
			JSONObject jsonProduct = products.getJSONObject(i);
			Product product = new Product(jsonProduct.toString());
			bakery.addProduct(product);
		}

		container.acceptNewAgent(name, new OrderAgent(bakery)).start();
	}

	public static void main(String[] args) {
		new Start("random-scenario");
	}
}
