package application;

import javafx.application.Application;
import javafx.stage.Stage;
import sharedobject.GameManager;

public class Main extends Application {

//	private GameManager gameManager;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			GameManager.initialize(primaryStage);
			
			//TODO Remove this line
//			Spawner.spawnAIMicrobe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
