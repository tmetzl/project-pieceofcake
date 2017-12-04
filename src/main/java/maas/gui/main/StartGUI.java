package maas.gui.main;

import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import maas.main.Start;

public class StartGUI extends Application {	
	
	@Override
	public void start(Stage stage) {
		Start start;
		try {
			start = new Start("random-scenario");
			GUIController controller = new GUIController(start.getCustomers());
			stage.setScene(new Scene(controller,600,400));
			stage.show();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void main(String[] args) {
		launch(args);
	}
}
