package sharedobject;

import game.ui.EvolveCanvas;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.entity.MicrobePlayer;
import sharedobject.GameScreenCanvas.LayerIndex;
import utility.InputUtility;

public class GameManager {

//	SceneManager
//	CellEvolutionManager
	private static Timeline animationTimeline;
	private static Scene gameScene;
	private static Scene evolveScene;
	private static EvolveCanvas evolvecanvas;
	private static Stage primaryStage;
	private static boolean isGamePause = true;
//	private double fadingAlpha = 0.0d;
//	private static boolean[] activatedButton = {false, false, false};
	
	public static void initialize(Stage primaryStage) {
		GameManager.primaryStage = primaryStage;
		
//		Pane root = new Pane();
//		gameScene = new Scene(root, CellEvolutionManager.DEFAULT_GAME_SCREEN_WIDTH, CellEvolutionManager.DEFAULT_GAME_SCREEN_HEIGHT);
//		root.getChildren().addAll(GameScreenCanvas.getCanvasLayer());
//		for(int i = 0; i < 5; i++) {
//			GameScreenCanvas.getCanvasLayer()[i].widthProperty().bind(root.widthProperty());
//			GameScreenCanvas.getCanvasLayer()[i].heightProperty().bind(root.heightProperty());
//		}
		initializeGameScene();
		initializeEvolveScene();

		primaryStage.setTitle("Bezier Curve: Cell Evolution!");
		primaryStage.setScene(gameScene);
		
		GameScreenCanvas.getCanvasLayer(LayerIndex.GUI).requestFocus();
		primaryStage.show();
		
		animationTimeline = new Timeline(new KeyFrame(Duration.millis(1000.0d / 60.0d), event -> {
			InputUtility.updateInputState();
			if(isGamePause == true) {
				updateMenu();
				return;
			}
			CellEvolutionManager.getInstance().updateWorld();
			Spawner.update();
			GameScreenCanvas.paintComponent();
		}));
		animationTimeline.setCycleCount(Timeline.INDEFINITE);
		animationTimeline.play();
//		ShowMenu();
	}
	
	private static void initializeEvolveScene() {
		GameManager.evolvecanvas = new EvolveCanvas(CellEvolutionManager.getInstance().getPlayer());
		Pane root = new Pane();
		root.getChildren().add(evolvecanvas);
		evolvecanvas.widthProperty().bind(root.widthProperty());
		evolvecanvas.heightProperty().bind(root.heightProperty());
		GameManager.evolveScene = new Scene(root);
	}
	
	private static void initializeGameScene() {
		Pane root = new Pane();
		gameScene = new Scene(root, CellEvolutionManager.DEFAULT_GAME_SCREEN_WIDTH, CellEvolutionManager.DEFAULT_GAME_SCREEN_HEIGHT);
		root.getChildren().addAll(GameScreenCanvas.getCanvasLayer());
		for(int i = 0; i < 5; i++) {
			GameScreenCanvas.getCanvasLayer()[i].widthProperty().bind(root.widthProperty());
			GameScreenCanvas.getCanvasLayer()[i].heightProperty().bind(root.heightProperty());
		}
	}
	
	private static void startGame() {
		isGamePause = false;
		InputUtility.reset();
		if(CellEvolutionManager.getInstance().getEntityList().getPlayer().isAlive() == false) {
			//restart game or new Game
//			CellEvolutionManager.getInstance().getEntityList().setPlayer(Spawner.spawnPlayer());
			MicrobePlayer player = CellEvolutionManager.getInstance().getEntityList().getPlayer();
			player.setHealth(player.getMaxHealth());
		} else {
			//continue game
		}
	}
	
	private static void exitGame() {
		Platform.exit();
	}
	
	private static void updateMenu() {
		if(!InputUtility.mouseOnScreen) {
			return;
		}
		Point2D mousPos = new Point2D(InputUtility.mouseX, InputUtility.mouseY);
//		System.out.println("xPos = " + xPos + ", yPos = " + yPos);
		Point2D center = GameScreenCanvas.getCenter();
		double xButton = center.getX() - 350;
		boolean button1 = new BoundingBox(xButton, 360, 700, 150).contains(mousPos);
		boolean button2 = new BoundingBox(xButton, 570, 700, 150).contains(mousPos);
		boolean button3 = new BoundingBox(xButton, 780, 700, 150).contains(mousPos);
		if(InputUtility.isLeftDown) {
			if(button1) {
				startGame();
				InputUtility.isLeftDown = false;
				InputUtility.updateInputState();
				return;
			} else if(button2) {
				switchToEvolveScreen();
				InputUtility.isLeftDown = false;
				InputUtility.updateInputState();
				return;
			} else if(button3) {
				exitGame();
				return;
			}
		}
		GameScreenCanvas.drawMenu(button1, button2, button3);
	}
	
//	public static void drawFadingMenu() {
//		//TODO change alpha then draw
//	}
	
	private static void switchToEvolveScreen() {
		primaryStage.setScene(evolveScene);
//		evolvecanvas.requestFocus();
		evolvecanvas.paintComponient();
		evolvecanvas.switchTo(CellEvolutionManager.getInstance().getEntityList().getPlayer());
		primaryStage.requestFocus();
	}
	
	public static void switchToMenu() {
		primaryStage.setScene(gameScene);
		primaryStage.requestFocus();
//		primaryStage.show();
	}

	public static boolean isGamePuase() {
		return isGamePause;
	}

	public static void setGamePause(boolean isGamePause) {
//		if(isGamePause) {
//			drawFadingMenu();
//		}
		GameManager.isGamePause = isGamePause;
	}

}
