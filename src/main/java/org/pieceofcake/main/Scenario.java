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

import org.pieceofcake.agents.CustomerAgent;
import org.pieceofcake.agents.GPSAgent;
import org.pieceofcake.agents.OrderAgent;
import org.pieceofcake.agents.ProductionAgent;
import org.pieceofcake.agents.StartUpAgent;
import org.pieceofcake.agents.TimerAgent;
import org.pieceofcake.agents.WarehouseAgent;
import org.pieceofcake.interfaces.Machine;
import org.pieceofcake.machines.BakingMachine;
import org.pieceofcake.machines.CoolingMachine;
import org.pieceofcake.machines.ItemPrepMachine;
import org.pieceofcake.machines.KneadingMachine;
import org.pieceofcake.machines.RestingMachine;
import org.pieceofcake.objects.CookBook;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;
import org.pieceofcake.objects.Product;
import org.pieceofcake.streetnetwork.DiGraph;
import org.pieceofcake.streetnetwork.Node;
import org.pieceofcake.tasks.BakingTask;
import org.pieceofcake.tasks.ItemPrepTask;
import org.pieceofcake.tasks.KneadingTask;

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
		Map<String, Location> customerLocations = new HashMap<>();
		JSONArray jsonCustomers = jsonScenario.getJSONArray("customers");

		for (int i = 0; i < jsonCustomers.length(); i++) {
			// Extract one customer and its Id
			JSONObject customer = jsonCustomers.getJSONObject(i);
			customerLocations.put(customer.getString("guid"), getLocation(customer));
		}
		JSONArray orders = jsonScenario.getJSONArray("orders");
		customerOrderMap = new HashMap<>();
		for (int i = 0; i < orders.length(); i++) {
			// Convert each order to a JSONObject
			JSONObject order = orders.getJSONObject(i);
			// Add location to order
			order.put("location", customerLocations.get(order.get("customer_id")).toJSONObject());
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
			Location location = getLocation(jsonBakery);

			loadKneadingMachines(jsonBakery.getJSONArray("kneading_machines"), guiId, location);
			
			loadPrepTables(jsonBakery.getJSONArray("dough_prep_tables"), guiId, location);

			loadOvens(jsonBakery.getJSONArray("ovens"),guiId,location);

			CookBook cookBook = new CookBook();
			JSONArray products = jsonBakery.getJSONArray("products");
			for (int j = 0; j < products.length(); j++) {
				JSONObject jsonProduct = products.getJSONObject(j);
				Product product = new Product(jsonProduct.toString());
				cookBook.addProduct(product);
			}

			tierOneAgents.put(name + "-resting-machine", new ProductionAgent<>(location, new RestingMachine(guiId)));
			tierOneAgents.put(name + "-cooling-machine", new ProductionAgent<>(location, new CoolingMachine(guiId)));

			tierTwoAgents.put(name, new OrderAgent(location, guiId, cookBook));
			tierTwoAgents.put(name + "-warehouse", new WarehouseAgent(location, guiId));
		}

	}
	
	private void loadKneadingMachines(JSONArray kneadingMachines, String guiId, Location location) {
		for (int j = 0; j < kneadingMachines.length(); j++) {
			JSONObject kneadingMachine = kneadingMachines.getJSONObject(j);
			String kneadingAgentName = kneadingMachine.getString("guid");
			Machine<KneadingTask> machine = new KneadingMachine(guiId);
			tierOneAgents.put(kneadingAgentName, new ProductionAgent<>(location, machine));
		}
	}
	
	private void loadPrepTables(JSONArray prepTables, String guiId, Location location) {
		for (int j = 0; j < prepTables.length(); j++) {
			JSONObject prepTable = prepTables.getJSONObject(j);
			String prepTableName = prepTable.getString("guid");
			Machine<ItemPrepTask> machine = new ItemPrepMachine(guiId);
			tierOneAgents.put(prepTableName, new ProductionAgent<>(location, machine));
		}
	}
	
	private void loadOvens(JSONArray ovens, String guiId, Location location) {
		for (int j = 0; j < ovens.length(); j++) {
			JSONObject oven = ovens.getJSONObject(j);
			long coolingRate = oven.getLong("cooling_rate");
			long heatingRate = oven.getLong("heating_rate");
			String ovenName = oven.getString("guid");
			if (coolingRate == 0 || heatingRate == 0) {
				break;
			}
			for (int k = 0; k < 4; k++) {
				Machine<BakingTask> singleOvenSlot = new BakingMachine(guiId, 40, heatingRate, coolingRate);
				tierOneAgents.put(ovenName + "-" + k, new ProductionAgent<>(location, singleOvenSlot));
			}
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
