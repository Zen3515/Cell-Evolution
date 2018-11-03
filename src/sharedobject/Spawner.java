package sharedobject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import logic.MicrobeAI;
import logic.entity.FoodEntity;
import logic.entity.FoodEntity.FoodType;
import logic.entity.MicrobeEntity;
import logic.entity.MicrobePlayer;
import utility.TestingUtility;

public class Spawner {

	public static final double DEFAULT_SPAWN_COLLDOWN_TIME = 1.4d;
	private static double spawnCooldownTimer = DEFAULT_SPAWN_COLLDOWN_TIME;
	private static ArrayList<Thread> runningThread = new ArrayList<>();
	
//	private static Entity spawn(Entity entity, boolean isByPassCooldown) {
//		if(isByPassCooldown) {
////			new Thread();
//			return null;
//		}
//		if(isInCooldown()) {
//			//DO not spawn as still in cooldown
//			return null;
//		}
////		spawningQueque.push(e);
//		spawnCooldownTimer = DEFAULT_SPAWN_COLLDOWN_TIME;
//	}
	
	public static MicrobePlayer spawnPlayer() {
		MicrobePlayer player = TestingUtility.getMicrobePlayer();
		player.setPosition(GameScreenCanvas.getCenter());
		return player;
	}
	
	public static boolean GenerateAIMicrobe(boolean isBypassCoolDown) {
		if(!isBypassCoolDown && isInCooldown()) {
			return false;//Cooling down
		}
		BoundingBox visibleBox = GameScreenCanvas.getVisibleBoundary();
		BoundingBox logicalBox = GameScreenCanvas.getLogicalBoundary();
		int x = ThreadLocalRandom.current().nextInt((int)logicalBox.getMinX(), (int)logicalBox.getMaxX() + 1);
		int y;
		
		if(x > visibleBox.getMinX() && x < visibleBox.getMaxX()) {
			if(x % 2 == 0) {
				y = ThreadLocalRandom.current().nextInt((int)logicalBox.getMinY(), (int)visibleBox.getMinY());
			} else {
				y = ThreadLocalRandom.current().nextInt((int)visibleBox.getMaxY(), (int)logicalBox.getMaxY());
			}
		} else {
			y = ThreadLocalRandom.current().nextInt((int)logicalBox.getMinY(), (int)logicalBox.getMaxY());
		}
		
		Point2D positon = new Point2D(x,y);
		Thread spawingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				spawnAIMicrobe(positon);
			}
			
		});
		spawnCooldownTimer = DEFAULT_SPAWN_COLLDOWN_TIME;
		spawingThread.start();
		runningThread.add(spawingThread);
		return true;
	}
	
	public static boolean isInCooldown() {
		return spawnCooldownTimer > 0;
	}
	
	public static synchronized void spawnAIMicrobe(Point2D position) {
		//TODO randomly create microbe
		MicrobeEntity en;
		if(((int)System.currentTimeMillis()) % 2 == 0) {
			en = TestingUtility.getSimpleMicrobe2();
		} else {
			en = TestingUtility.getSimpleMicrobe();
		}
		en.setPosition(position);
		MicrobeAI ai = new MicrobeAI(en);
		
		//for test only
		//REMOVE THIS
		//REMOVETHIS
//		ai.setAIState(AIState.MOVING);
		
		final List<MicrobeAI> aiList = CellEvolutionManager.getInstance().getAIList();
		synchronized(aiList) {
			aiList.add(ai);
		}
		CellEvolutionManager.getInstance().getEntityList().add(en);
	}
	
	public static void spawnMicrobe() {
		
	}
	
	public static void spawnFood(BoundingBox bound, FoodType type, int amount) {
		double boundWidth = bound.getWidth();
		double boundHeight = bound.getHeight();
		for(int i = 0; i < amount; i++) {
			spawnFood(type, bound.getMinX() + (Math.random()*(boundWidth/4)), bound.getMinY() + (Math.random()*(boundHeight/4)));
		}
	}

	public static void spawnFood(FoodType type) {
		double screenWidth = GameScreenCanvas.getWidth();
		double screenHeight = GameScreenCanvas.getHeight();
		spawnFood(type, Math.random()*(screenWidth/4), Math.random()*(screenHeight/4));
	}
	
	public static void spawnFood(FoodType type, double x, double y) {
		FoodEntity food = new FoodEntity(new Point2D(x, y), FoodEntity.MAX_FOOD_HEALTH, type);
		CellEvolutionManager.getInstance().add(food);
	}
	
	public static void update() {
		if(spawnCooldownTimer > 0) {
			spawnCooldownTimer -= CellEvolutionManager.getDeltaTime();
		}
	}

}
