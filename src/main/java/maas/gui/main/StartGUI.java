package maas.gui.main;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import maas.main.Start;

public class StartGUI extends Application {	
	
	@Override
	public void start(Stage stage) {
		Start start;
		start = new Start("random-scenario");
		GUIController controller = new GUIController(start.getCustomers());
		stage.setScene(new Scene(controller,600,400));
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
