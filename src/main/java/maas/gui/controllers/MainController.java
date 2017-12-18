package maas.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import maas.main.Scenario;

public class MainController {
	
	@FXML
	private TabPane tabPane;
	@FXML
	private HBox customerTab;
	@FXML
	private HBox mapTab;
	@FXML
	private CustomerTabController customerTabController;
	@FXML
	private MapTabController mapTabController;
	
	@FXML
	private void initialize() {
		customerTabController.setCustomers(Scenario.getInstance().getCustomers());
		mapTabController.setStreetNetwork(Scenario.getInstance().getStreetNetwork());
	}

}
