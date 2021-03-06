package org.pieceofcake.gui.controllers;

import java.util.ArrayList;
import java.util.List;

import org.pieceofcake.agents.CustomerAgent;
import org.pieceofcake.gui.utils.ShoppingListEntry;
import org.pieceofcake.objects.Date;
import org.pieceofcake.objects.Location;
import org.pieceofcake.objects.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class CustomerTabController {

	@FXML
	private ComboBox<CustomerAgent> customerChooser;
	@FXML
	private Text customerName;
	@FXML
	private Text customerType;
	@FXML
	private Text customerLocation;
	@FXML
	private ListView<Order> ordersView;
	@FXML
	private TableView<ShoppingListEntry> productTable;
	@FXML
	private TableColumn<ShoppingListEntry, String> productId;
	@FXML
	private TableColumn<ShoppingListEntry, Integer> productAmount;
	@FXML
	private Text orderId;
	@FXML
	private Text orderDate;
	@FXML
	private Text orderDueDate;
	
	public void setCustomers(List<CustomerAgent> customers) {
		productId.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productAmount.setCellValueFactory(new PropertyValueFactory<>("productAmount"));

		ObservableList<CustomerAgent> availableChoices = FXCollections.observableArrayList(customers);

		customerChooser.setItems(availableChoices);
		customerChooser.getSelectionModel().select(0);

		Callback<ListView<CustomerAgent>, ListCell<CustomerAgent>> cellFactoryComboBox = arg -> new ListCell<CustomerAgent>() {
			@Override
			protected void updateItem(CustomerAgent agent, boolean empty) {
				super.updateItem(agent, empty);
				if (agent == null || empty) {
					setGraphic(null);
				} else {
					setText(agent.getGuiId());
				}
			}
		};

		customerChooser.setButtonCell((ListCell<CustomerAgent>) cellFactoryComboBox.call(null));
		customerChooser.setCellFactory(cellFactoryComboBox);
		updateCustomer(customers.get(0));
	}

	public void updateCustomer(CustomerAgent selectedAgent) {
		customerName.setText(selectedAgent.getLocalName());
		customerType.setText(String.valueOf(selectedAgent.getType()));
		Location location = selectedAgent.getLocation();
		double x = location.getX();
		double y = location.getY();
		customerLocation.setText(String.format("(%.2f, %.2f)", x, y));
		updateOrders(selectedAgent);
	}

	public void updateOrders(CustomerAgent selectedAgent) {
		List<Order> orders = selectedAgent.getOrders();
		ordersView.setItems(FXCollections.observableArrayList(orders));

		Callback<ListView<Order>, ListCell<Order>> cellFactoryListView = arg -> new ListCell<Order>() {
			@Override
			protected void updateItem(Order order, boolean empty) {
				super.updateItem(order, empty);
				if (order == null || empty) {
					setGraphic(null);
				} else {
					setText(order.getGuiId());
				}
			}
		};

		ordersView.setCellFactory(cellFactoryListView);
		ordersView.getSelectionModel().selectedItemProperty()
				.addListener((ov, oldOrder, newOrder) -> orderSelected(newOrder));

	}

	@FXML
	public void customerChanged() {
		CustomerAgent customer = customerChooser.getSelectionModel().getSelectedItem();
		updateCustomer(customer);
	}

	public String toDateString(Date date) {
		return String.format("Day %d %02d:%02d:%02d", date.getDay(), date.getHour(), date.getMinute(), date.getSecond());
	}

	public void orderSelected(Order order) {
		if (order != null) {
			orderId.setText(order.getGuiId());
			orderDate.setText(toDateString(order.getOrderDate()));
			orderDueDate.setText(toDateString(order.getDueDate()));
			String[] productIds = order.getProductIds();
			int[] productAmounts = order.getProductAmounts();
			ArrayList<ShoppingListEntry> products = new ArrayList<>();
			for (int i = 0; i < productIds.length; i++) {
				ShoppingListEntry entry = new ShoppingListEntry();
				entry.setProductName(productIds[i]);
				entry.setProductAmount(productAmounts[i]);
				products.add(entry);
			}
			ObservableList<ShoppingListEntry> productList = FXCollections.observableArrayList(products);
			productTable.setItems(productList);

		} else {
			orderId.setText("");
			orderDate.setText("");
			orderDueDate.setText("");
			productTable.setItems(null);
		}
	}

}
