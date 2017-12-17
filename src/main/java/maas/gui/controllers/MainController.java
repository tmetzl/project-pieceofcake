package maas.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import maas.main.Scenario;

public class MainController {
	
	private Scenario scenario;

	@FXML
	private TabPane tabPane;
	@FXML
	private HBox customerTab;
	@FXML
	private CustomerTabController customerTabController;
	
	@FXML
	private void initialize() {
		scenario = Scenario.getInstance();
		customerTabController.setCustomers(scenario.getCustomers());
	}

}
