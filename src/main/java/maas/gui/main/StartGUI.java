package maas.gui.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import maas.main.Scenario;

public class StartGUI extends Application {	
	
	@Override
	public void start(Stage stage) {
		//Start start;
		//start = new Start("random-scenario");
		//MainController controller = new MainController(start.getCustomers());

		try {
			Scenario scenario = Scenario.getInstance();
			scenario.load("random-scenario");
			scenario.start();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/maas/gui/fxml/main.fxml"));
			Parent root;
			root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
