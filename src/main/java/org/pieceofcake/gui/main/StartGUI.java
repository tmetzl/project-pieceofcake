package org.pieceofcake.gui.main;

import java.io.IOException;

import org.pieceofcake.main.Scenario;

import jade.util.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class StartGUI extends Application {	
	
	@Override
	public void start(Stage stage) {
		try {
			Scenario scenario = Scenario.getInstance();
			scenario.load("random-scenario");
			scenario.start();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/org/pieceofcake/gui/fxml/main.fxml"));
			Parent root;
			root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			Logger logger = Logger.getJADELogger(this.getClass().getName());
			logger.log(Logger.WARNING, e.getMessage(), e);
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
