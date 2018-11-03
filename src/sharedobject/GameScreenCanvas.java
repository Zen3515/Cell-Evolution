package sharedobject;

import java.util.ArrayList;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;
import logic.entity.Entity;
import logic.entity.MicrobePlayer;
import utility.InputUtility;
import utility.RepeatedBackGround;
import utility.ResizableCanvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;


public class GameScreenCanvas {

	/**
	 * Layer BLURED_DISANCE_BACKGROUND index = 0 is for : blurred distance water and some generated freze MicrobeEntity, 
	 * Layer BACK_PARTICLE_RIPPLE index = 1 is for : particle e.g. dust and ripple and bubble and so on in transprent backgroud entity, 
	 * Layer ENTITY index = 2 is for : entity drawing e.g. food mircobe
	 * Layer FRONT_PARTICLE index = 3 is for : some few blur with bubble
	 * Layer GUI index = 4 is for : GameUI player heath and so on
	 */
	public enum LayerIndex{
		BLURED_DISANCE_BACKGROUND(0, 0.5d), BACK_PARTICLE_RIPPLE(1, 1.0d), ENTITY(2, 1.0d), FRONT_PARTICLE(3, 1.15d), GUI(4, 0.0d);
		
		private final int index;
		private final double speedMultiplyer;
		
		LayerIndex(int index, double speedMultiplyer){
			this.index = index;
			this.speedMultiplyer = speedMultiplyer;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public double getSpeedMultiplyer() {
			return this.speedMultiplyer;
		}
	};
	
	private static Canvas[] canvasLayer;
	private static Paint menuBackground = new LinearGradient(1, 1, 0.5, 0.75, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0, Color.WHITESMOKE), new Stop(1, Color.WHITE)});

	static {
		canvasLayer = new Canvas[5];
		for(int i = 0; i < 5; i++) {
			canvasLayer[i] = new ResizableCanvas(CellEvolutionManager.DEFAULT_GAME_SCREEN_WIDTH, CellEvolutionManager.DEFAULT_GAME_SCREEN_HEIGHT);
		}
		addListerner(canvasLayer[4]);
	}
	
	public static Canvas getCanvasLayer(LayerIndex layer) {
		return canvasLayer[layer.getIndex()];
	}

	public static RepeatedBackGround repeatedWaterBackGround;
	
//	@Deprecated
	public static Canvas[] getCanvasLayer() {
		return canvasLayer;
	}

	public static void addListerner(Canvas cv) {
		cv.widthProperty().addListener(event -> shiftCenter());
		cv.heightProperty().addListener(event -> shiftCenter());
		cv.setOnKeyPressed((KeyEvent event) -> {
			//System.out.println("Push " + event.getCode().getName());
			InputUtility.setKeyPressed(event.getCode(), true);
		});

		cv.setOnKeyReleased((KeyEvent event) -> {
			//System.out.println("released " + event.getCode().getName());
			InputUtility.setKeyPressed(event.getCode(), false);
		});

		cv.setOnMousePressed((MouseEvent event) -> {
			if (event.getButton() == MouseButton.PRIMARY)
				InputUtility.mouseLeftDown();
		});

		cv.setOnMouseReleased((MouseEvent event) -> {
			if (event.getButton() == MouseButton.PRIMARY)
				InputUtility.mouseLeftRelease();
		});

		cv.setOnMouseEntered((MouseEvent event) -> {
			InputUtility.mouseOnScreen = true;
		});

		cv.setOnMouseExited((MouseEvent event) -> {
			InputUtility.mouseOnScreen = false;
		});

		cv.setOnMouseMoved((MouseEvent event) -> {
			if (InputUtility.mouseOnScreen) {
				InputUtility.mouseX = event.getX();
				InputUtility.mouseY = event.getY();
			}
		});

		cv.setOnMouseDragged((MouseEvent event) -> {
			if (InputUtility.mouseOnScreen) {
				InputUtility.mouseX = event.getX();
				InputUtility.mouseY = event.getY();
			}
		});
	}
	
	private static void shiftCenter() {
		CellEvolutionManager.getInstance().resizeQuadTree((int)canvasLayer[4].getWidth(), (int)canvasLayer[4].getHeight());
		Point2D velocity = getCenter().subtract(CellEvolutionManager.getInstance().getEntityList().getPlayer().getPosition());
//		for(Entity en : CellEvolutionManager.getInstance().getEntityList()) {
//			en.move(velocity);
//		}
		CellEvolutionManager.getInstance().getEntityList().shiftBackground(velocity);
		repeatedWaterBackGround.shiftCenter(velocity);
	}
	
	public static Point2D getCenter() {
		return new Point2D(canvasLayer[4].getWidth()/2.0d, canvasLayer[4].getHeight()/2.0d);
	}
	
	public static double getWidth() {
		return canvasLayer[4].getWidth();
	}
	
	public static double getHeight() {
		return canvasLayer[4].getHeight();
	}
	
	public static BoundingBox getVisibleBoundary() {
		double width = canvasLayer[4].getWidth();
		double height = canvasLayer[4].getHeight();
		return new BoundingBox(0, 0, width, height);
	}
	
	public static BoundingBox getLogicalBoundary() {
		double width = canvasLayer[4].getWidth();
		double height = canvasLayer[4].getHeight();
		return new BoundingBox(-(width*0.25), -(height*0.25), width*1.5, height*1.5);
	}
	
	public static void drawGUI() {
		
	}
	
	public static void drawMenu(boolean isActiveButton1, boolean isActiveButton2, boolean isActiveButton3) {
		GraphicsContext gc = canvasLayer[4].getGraphicsContext2D();
		gc.setFill(menuBackground);
		gc.fillRect(0, 0, getWidth(), getHeight());
		
		Point2D center = GameScreenCanvas.getCenter();
		double xButton = center.getX() - 350;
		
		gc.drawImage(CellEvolutionManager.logo, center.getX()-660, 120);
		
		gc.setStroke(Color.web("2FCB1E"));
		gc.setFill(Color.web("008DFF"));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(CellEvolutionManager.MENUFONT);
		
		if(isActiveButton1) {
			gc.setLineWidth(5);
		} else {
			gc.setLineWidth(1);
		}
		gc.strokeRoundRect(xButton, 360, 700, 150, 10, 10);
		gc.fillText("Start Game", center.getX(), 435);
		if(isActiveButton2) {
			gc.setLineWidth(5);
		} else {
			gc.setLineWidth(1);
		}
		gc.strokeRoundRect(xButton, 570, 700, 150, 10, 10);
		gc.fillText("EVOLVE!", center.getX(), 645);
		if(isActiveButton3) {
			gc.setLineWidth(5);
		} else {
			gc.setLineWidth(1);
		}
		gc.strokeRoundRect(xButton, 780, 700, 150, 10, 10);
		gc.fillText("Exit Game", center.getX(), 855);
	}

	private static void clearCanvas() {
		double width = getWidth();
		double height = getHeight();
		canvasLayer[0].getGraphicsContext2D().clearRect(0, 0, width, height);
		canvasLayer[1].getGraphicsContext2D().clearRect(0, 0, width, height);
		canvasLayer[2].getGraphicsContext2D().clearRect(0, 0, width, height);
		canvasLayer[3].getGraphicsContext2D().clearRect(0, 0, width, height);
		canvasLayer[4].getGraphicsContext2D().clearRect(0, 0, width, height);
	}
	
	public static void paintComponent() {
		clearCanvas();
		GraphicsContext gc;
		gc = canvasLayer[0].getGraphicsContext2D();
		repeatedWaterBackGround.draw(gc);
		
//		final GraphicsContext finalGC = canvasLayer[2].getGraphicsContext2D();
		
		final ArrayList<Entity> entitiesList = CellEvolutionManager.getInstance().getEntityList().clone();
		MicrobePlayer player = CellEvolutionManager.getInstance().getEntityList().getPlayer();
		for(int i = 0; i < entitiesList.size(); i++) {
			Entity en = entitiesList.get(i);
			en.draw(gc);
//			gc.setStroke(Color.BLACK);		
//			TestingUtility.drawBoundingBox(gc, ((Collidable) en).getBound());
		}
		if(player.isAlive()) {
			player.draw(gc);
		}
		
	}

}
