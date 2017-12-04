package maas.gui.main;

import java.io.IOException;
import java.util.List;

import jade.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import maas.gui.agents.CustomerController;
import maas.agents.CustomerAgent;

public class GUIController extends VBox {

	@FXML
	private TabPane tabPane;
	@FXML
	private Tab customerTab;
	
	private CustomerController customerTabController;
	
	public GUIController(List<CustomerAgent> customers) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MaasGUI.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			Logger logger = Logger.getJADELogger("GUIController");
			logger.log(Logger.WARNING, "Error loading main FXML", e);
		}

		
		this.customerTabController = new CustomerController(customers);
		customerTab.setContent(customerTabController);
		VBox.setVgrow(tabPane, Priority.ALWAYS);
	}

}
