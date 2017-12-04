package maas.gui.main;

import java.io.IOException;
import java.util.List;

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
	
	//private List<CustomerAgent> customers;
	
	public GUIController(List<CustomerAgent> customers) {
		//this.customers = customers;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MaasGUI.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
		this.customerTabController = new CustomerController(customers);
		customerTab.setContent(customerTabController);
		VBox.setVgrow(tabPane, Priority.ALWAYS);
	}

	@FXML
	public void initialize() {


	}

}
