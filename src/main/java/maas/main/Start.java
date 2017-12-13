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
import maas.agents.BakeryClockAgent;
import maas.agents.CustomerAgent;
import maas.agents.GPSAgent;
import maas.agents.KneadingAgent;
import maas.agents.KneadingSchedulerAgent;
import maas.agents.OrderAgent;
import maas.agents.StartUpAgent;
import maas.agents.TimerAgent;
import maas.objects.Bakery;
import maas.objects.Order;
import maas.objects.Product;
import maas.streetnetwork.DiGraph;
import maas.streetnetwork.Node;

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
			container.acceptNewAgent("Timer", TimerAgent.getInstance()).start();

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
			
			// Step 4: Process the street network
			JSONObject network = scenario.getJSONObject("street_network");
			createStreetNetwork(network);
			
			// Step 5: Get the duration and set up the startUp agent
			int duration = scenario.getJSONObject("meta").getInt("duration_days");
			container.acceptNewAgent("StartUp", new StartUpAgent(duration)).start();

		} catch (FileNotFoundException e) {
			logger.log(Logger.WARNING, e.getMessage(), e);
		}

	}
	
	public double[] getLocation(JSONObject jsonObject) {
		JSONObject location = jsonObject.getJSONObject("location");
		
		double[] locationXY = new double[2];
		locationXY[0] = location.getDouble("x");
		locationXY[1] = location.getDouble("y");
		
		return locationXY;
	}

	public void createCustomer(JSONObject customer, List<Order> orders) throws StaleProxyException {
		// Get the parameters
		String name = customer.getString("name");
		String guiId = customer.getString("guid");
		int type = customer.getInt("type");
		double[] location = getLocation(customer);
		// Create the agent
		CustomerAgent agent = new CustomerAgent(guiId, type, location[0], location[1], orders);
		customers.add(agent);
		container.acceptNewAgent(name, agent).start();

	}

	public void createBakery(JSONObject jsonBakery) throws StaleProxyException {
		String name = jsonBakery.getString("name");
		String guiId = jsonBakery.getString("guid");
		
		JSONArray kneadingMachines = jsonBakery.getJSONArray("kneading_machines");
		int numberOfKneadingMachines = kneadingMachines.length();
		double[] location = getLocation(jsonBakery);
		Bakery bakery = new Bakery(guiId, name, location[0], location[1]);
		BakeryClockAgent myBakeryClock = new BakeryClockAgent(bakery);

		String[] kneadingAgentNames = new String[numberOfKneadingMachines];
		for (int i = 0; i < numberOfKneadingMachines; i++) {
			JSONObject kneadingMachine = kneadingMachines.getJSONObject(i);
			kneadingAgentNames[i] = kneadingMachine.getString("guid");
		}

		JSONArray products = jsonBakery.getJSONArray("products");
		for (int i = 0; i < products.length(); i++) {
			JSONObject jsonProduct = products.getJSONObject(i);
			Product product = new Product(jsonProduct.toString());
			bakery.addProduct(product);
		}

		for (String kneadingAgentName : kneadingAgentNames) {
			container.acceptNewAgent(kneadingAgentName, new KneadingAgent()).start();
		}
		container.acceptNewAgent(name + "-kneadingScheduler", new KneadingSchedulerAgent(kneadingAgentNames, bakery))
				.start();
		container.acceptNewAgent(name + "-clock", myBakeryClock).start();
		container.acceptNewAgent(name, new OrderAgent(bakery)).start();
	}
	
	public void createStreetNetwork(JSONObject network) throws StaleProxyException {
		DiGraph streetNetwork = new DiGraph();
		JSONArray nodes = network.getJSONArray("nodes");
		
		for (int i=0;i<nodes.length();i++) {
			JSONObject jsonNode = nodes.getJSONObject(i);
			String guid = jsonNode.getString("guid");
			String name = jsonNode.getString("name");
			String type = jsonNode.getString("type");
			String company = jsonNode.getString("company");
			double[] location = getLocation(jsonNode);

			streetNetwork.addNode(new Node(guid, name, type, company, location[0], location[1]));
		}
		
		JSONArray links = network.getJSONArray("links");
		for (int i=0;i<links.length();i++) {
			JSONObject link = links.getJSONObject(i);
			String from = link.getString("source");
			String to = link.getString("target");
			double dist = link.getDouble("dist");
			String edgeGuid = link.getString("guid");
			streetNetwork.addEdge(from, to, dist, edgeGuid);
		}
		
		container.acceptNewAgent("gps-agent", new GPSAgent(streetNetwork)).start();
	}

	public static void main(String[] args) {
		new Start("random-scenario");
	}
}
