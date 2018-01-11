package org.pieceofcake.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pieceofcake.agents.BakeryClockAgent;
import org.pieceofcake.agents.CustomerAgent;
import org.pieceofcake.agents.GPSAgent;
import org.pieceofcake.agents.KneadingAgent;
import org.pieceofcake.agents.KneadingSchedulerAgent;
import org.pieceofcake.agents.OrderAgent;
import org.pieceofcake.agents.StartUpAgent;
import org.pieceofcake.agents.TimerAgent;
import org.pieceofcake.objects.Bakery;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.Product;
import org.pieceofcake.streetnetwork.DiGraph;
import org.pieceofcake.streetnetwork.Node;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

public class Scenario {

	private static Scenario instance;

	private Logger logger;
	private boolean started;
	private JSONObject jsonScenario;
	private DiGraph streetNetwork;

	private Map<String, Agent> tierOneAgents;
	private Map<String, Agent> tierTwoAgents;
	private Map<String, List<Order>> customerOrderMap;
	private List<CustomerAgent> customers;

	private Scenario() {
		started = false;
		logger = Logger.getJADELogger(this.getClass().getName());
		tierOneAgents = new HashMap<>();
		tierTwoAgents = new HashMap<>();
	}

	public static Scenario getInstance() {
		if (instance == null) {
			instance = new Scenario();
		}
		return instance;
	}

	public void load(String filename) {
		if (jsonScenario != null) {
			return;
		}
		try (Scanner in = new Scanner(new FileReader("src/main/config/" + filename + ".json"))) {

			StringBuilder bld = new StringBuilder();
			while (in.hasNext())
				bld.append(in.nextLine());

			String text = bld.toString();

			// Parse scenario as JSONObject
			jsonScenario = new JSONObject(text);

			loadStreetNetwork();
			loadOrders();
			loadCustomers();
			loadBakeries();
		} catch (FileNotFoundException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
		}
	}

	public void start() {
		if (!started) {
			started = true;
			jade.core.Runtime runtime = jade.core.Runtime.instance();
			runtime.setCloseVM(true);

			Properties properties = new ExtendedProperties();
			properties.put(Profile.MAIN, true);
			properties.put(Profile.GUI, false);

			Profile profile = new ProfileImpl(properties);

			AgentContainer container = runtime.createMainContainer(profile);

			try {
				container.acceptNewAgent("Timer", TimerAgent.getInstance()).start();
				int duration = jsonScenario.getJSONObject("meta").getInt("duration_days");
				container.acceptNewAgent("StartUp", new StartUpAgent(duration)).start();

				for (Map.Entry<String, Agent> entry : tierOneAgents.entrySet()) {
					String name = entry.getKey();
					Agent agent = entry.getValue();
					container.acceptNewAgent(name, agent).start();
				}
				for (Map.Entry<String, Agent> entry : tierTwoAgents.entrySet()) {
					String name = entry.getKey();
					Agent agent = entry.getValue();
					container.acceptNewAgent(name, agent).start();
				}

				
			} catch (StaleProxyException s) {
				logger.log(Logger.WARNING, s.getMessage(), s);
			}

		}
	}

	private void loadStreetNetwork() {
		streetNetwork = new DiGraph();
		JSONObject network = jsonScenario.getJSONObject("street_network");
		JSONArray nodes = network.getJSONArray("nodes");

		for (int i = 0; i < nodes.length(); i++) {
			JSONObject jsonNode = nodes.getJSONObject(i);
			String guid = jsonNode.getString("guid");
			String name = jsonNode.getString("name");
			String type = jsonNode.getString("type");
			String company = jsonNode.getString("company");
			Location location = getLocation(jsonNode);
			streetNetwork.addNode(new Node(guid, name, type, company, location));
		}

		JSONArray links = network.getJSONArray("links");
		for (int i = 0; i < links.length(); i++) {
			JSONObject link = links.getJSONObject(i);
			String from = link.getString("source");
			String to = link.getString("target");
			double dist = link.getDouble("dist");
			String edgeGuid = link.getString("guid");
			streetNetwork.addEdge(from, to, dist, edgeGuid);
		}
		tierOneAgents.put("GPS Service", new GPSAgent(streetNetwork));
	}

	private void loadOrders() {
		JSONArray orders = jsonScenario.getJSONArray("orders");
		customerOrderMap = new HashMap<>();
		for (int i = 0; i < orders.length(); i++) {
			// Convert each order to a JSONObject
			JSONObject order = orders.getJSONObject(i);
			// Adding minute and second to comply with date format
			JSONObject orderDate = order.getJSONObject("order_date");
			orderDate.put("minute", 0);
			orderDate.put("second", 0);
			JSONObject dueDate = order.getJSONObject("delivery_date");
			dueDate.put("minute", 0);
			dueDate.put("second", 0);
			Order orderObject = new Order(order);
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
	}

	private void loadCustomers() {
		customers = new ArrayList<>();
		JSONArray jsonCustomers = jsonScenario.getJSONArray("customers");

		for (int i = 0; i < jsonCustomers.length(); i++) {
			// Extract one customer and its Id
			JSONObject customer = jsonCustomers.getJSONObject(i);
			String customerId = customer.getString("guid");

			// Make sure the customer has orders
			if (customerOrderMap.containsKey(customerId)) {
				List<Order> customerOrders = customerOrderMap.get(customerId);
				// Create customer agent

				// Get the parameters
				String name = customer.getString("name");
				String guiId = customer.getString("guid");
				int type = customer.getInt("type");
				Location location = getLocation(customer);
				// Create the agent
				CustomerAgent agent = new CustomerAgent(guiId, type, location, customerOrders);
				customers.add(agent);
				tierTwoAgents.put(name, agent);
			}
		}
	}

	private void loadBakeries() {
		JSONArray bakeries = jsonScenario.getJSONArray("bakeries");

		for (int i = 0; i < bakeries.length(); i++) {
			JSONObject jsonBakery = bakeries.getJSONObject(i);
			String name = jsonBakery.getString("name");
			String guiId = jsonBakery.getString("guid");

			JSONArray kneadingMachines = jsonBakery.getJSONArray("kneading_machines");
			int numberOfKneadingMachines = kneadingMachines.length();
			Location location = getLocation(jsonBakery);
			Bakery bakery = new Bakery(guiId, name, location);
			BakeryClockAgent myBakeryClock = new BakeryClockAgent(bakery);

			String[] kneadingAgentNames = new String[numberOfKneadingMachines];
			for (int j = 0; j < numberOfKneadingMachines; j++) {
				JSONObject kneadingMachine = kneadingMachines.getJSONObject(j);
				kneadingAgentNames[j] = kneadingMachine.getString("guid");
			}

			JSONArray products = jsonBakery.getJSONArray("products");
			for (int j = 0; j < products.length(); j++) {
				JSONObject jsonProduct = products.getJSONObject(j);
				Product product = new Product(jsonProduct.toString());
				bakery.addProduct(product);
			}

			for (String kneadingAgentName : kneadingAgentNames) {
				tierOneAgents.put(kneadingAgentName, new KneadingAgent());
			}
			tierTwoAgents.put(name + "-kneadingScheduler", new KneadingSchedulerAgent(kneadingAgentNames, bakery));
			tierTwoAgents.put(name + "-clock", myBakeryClock);
			tierTwoAgents.put(name, new OrderAgent(bakery));
		}

	}

	private Location getLocation(JSONObject jsonObject) {
		JSONObject location = jsonObject.getJSONObject("location");
		return new Location(location.getDouble("x"), location.getDouble("y"));
	}

	public List<CustomerAgent> getCustomers() {
		return customers;
	}

	public DiGraph getStreetNetwork() {
		return streetNetwork;
	}

}
