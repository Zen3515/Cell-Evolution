package sharedobject;

import java.util.ArrayList;
import java.util.LinkedList;

import exception.UnableToRandomExclusivelyException;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import logic.MicrobeAI;
import logic.entity.Entity;
import logic.entity.LivingEntity;
import logic.entity.MicrobePlayer;
import logic.entity.ObjectEntity;
import sharedobject.Animatable.AnimationState;
import utility.InputUtility;
import utility.QuadTree;
import utility.Rectangle;
import utility.RepeatedBackGround;
import utility.SynchronizedEntityList;

public class CellEvolutionManager {
	
	public static final double DISTANCE_CENTER_TO_PLAYER = 30.0d;
	public static final int DEFAULT_GAME_SCREEN_WIDTH = 1320;
	public static final int DEFAULT_GAME_SCREEN_HEIGHT = 1000;
	public static final double BACKGOUND_RELATIVE_SPEED_MULTIPLIER = 0.053d;
	public static final int MIN_BOT_MICROB_COUNT = 5;
	public static final Font MENUFONT = new Font("Arial Rounded MT Bold", 72);
	
	private static final CellEvolutionManager instance = new CellEvolutionManager();
	private static double deltaTime = 0;
	private static long previousTime = System.nanoTime();
	private QuadTree quadTree = new QuadTree(0, new Rectangle(0, 0, DEFAULT_GAME_SCREEN_WIDTH, DEFAULT_GAME_SCREEN_HEIGHT));

//	private LinkedList<Entity> entities;
	private SynchronizedEntityList entitiesList;
	private LinkedList<MicrobeAI> aiList;
	
	public static Image waterTexture;
	public static Image food_meat;
	public static Image food_plant;
	public static Image bubble1;
	public static Image bubble2;
	public static Image logo;
	public static AudioClip spike_Hit_Body;
	public static AudioClip spike_Draw;
	public static AudioClip[] bouncySound;
	public static AudioClip[] jawEatingSound;
	public static AudioClip jawShut;
	public static AudioClip suckingSound;

	static {
		loadResource();
		previousTime = System.nanoTime();
		GameScreenCanvas.repeatedWaterBackGround = new RepeatedBackGround(waterTexture, 0, 0);
	}

	public CellEvolutionManager() {
		entitiesList = new SynchronizedEntityList(Spawner.spawnPlayer());
		entitiesList.getPlayer().setHealth(0);
		aiList = new LinkedList<>();
	}
	
	public void resizeQuadTree(int width, int heigh) {
		quadTree = new QuadTree(0, new Rectangle(0,0,width,heigh));
	}

	public static CellEvolutionManager getInstance() {
		return instance;
	}

	public static void loadResource() {
		waterTexture = new Image(ClassLoader.getSystemResource("Texture_Water3_2000x2000.jpg").toString());
		spike_Hit_Body = new AudioClip(ClassLoader.getSystemResource("SpikeHitBody.wav").toString());
		spike_Draw = new AudioClip(ClassLoader.getSystemResource("SpikeDraw.wav").toString());
		jawShut = new AudioClip(ClassLoader.getSystemResource("JawShutFullDelay.wav").toString());
		jawEatingSound = new AudioClip[2];
		jawEatingSound[0] = new AudioClip(ClassLoader.getSystemResource("Eating_1.wav").toString());
		jawEatingSound[1] = new AudioClip(ClassLoader.getSystemResource("Eating_2.wav").toString());
		bouncySound = new AudioClip[2];
		bouncySound[0] = new AudioClip(ClassLoader.getSystemResource("Bouncing_1.wav").toString());
		bouncySound[1] = new AudioClip(ClassLoader.getSystemResource("Bouncing_2.wav").toString());
		suckingSound = new AudioClip(ClassLoader.getSystemResource("Sucking_1.wav").toString());
		food_meat = new Image(ClassLoader.getSystemResource("Meat2.png").toString(), 50, 50, true, true);
		food_plant = new Image(ClassLoader.getSystemResource("Plant.png").toString(), 50, 50, true, true);
		bubble1 = new Image(ClassLoader.getSystemResource("Bubble1.png").toString());
		bubble2 = new Image(ClassLoader.getSystemResource("Bubble2.png").toString());
		logo = new Image(ClassLoader.getSystemResource("LOGO.png").toString());
	}

	/**
	 * Do not add player here!!, the one who can all it should be Spawner
	 * @param entity
	 * @param layerIndex
	 */
	public void add(Entity entity) {
//		System.out.println("added " + entity.getClass().getSimpleName());
		entitiesList.add(entity);
	}
	public MicrobePlayer getPlayer() {
		return entitiesList.getPlayer();
	}
	
	public static double getDeltaTime() {
		return CellEvolutionManager.deltaTime;
	}
	
	public LivingEntity getRandomEntityExclusively(Entity exclude) throws UnableToRandomExclusivelyException {
		return entitiesList.randomEntityExclusively(exclude);
	}
	
	private void CheckPlayerInput() {
		MicrobePlayer player = getPlayer();
		if(InputUtility.getKeyPressed(KeyCode.UP)) {
			player.setAnimationState(AnimationState.FAST_FORWARDING);
		} else if(InputUtility.getKeyPressed(KeyCode.DOWN)) {
			player.setAnimationState(AnimationState.BACKWARDING);
		}
		if(InputUtility.getKeyPressed(KeyCode.RIGHT)) {
			if(InputUtility.getKeyPressed(KeyCode.UP) == false) {
				player.setAnimationState(AnimationState.IDELING);
			}
			player.turn(false);
		} else if(InputUtility.getKeyPressed(KeyCode.LEFT)) {
			if(InputUtility.getKeyPressed(KeyCode.UP) == false) {
				player.setAnimationState(AnimationState.IDELING);
			}
			player.turn(true);
		} else {
			player.dampTurnRate();
		}
		if(InputUtility.getKeyPressed(KeyCode.DIGIT1)) {
			player.setAnimationState(AnimationState.ATACKING_SPIKE);
		}
		if(!InputUtility.hasKeyPressed()) {
			player.setAnimationState(AnimationState.IDELING);
		}
	}

	private void updateBackground() {
		Point2D bgSpeed = getPlayer().getVelocity().multiply(-BACKGOUND_RELATIVE_SPEED_MULTIPLIER);
		GameScreenCanvas.repeatedWaterBackGround.update(bgSpeed.getX(), bgSpeed.getY());
	}
	
	public void updateWorld() {
		CellEvolutionManager.deltaTime = (System.nanoTime() - previousTime)*1e-9;
		CellEvolutionManager.previousTime = System.nanoTime();
		CheckPlayerInput();
		MicrobePlayer player = getPlayer();
		player.update();
		updateBackground();
		quadTree.clear();
		quadTree.insert(player);
		entitiesList.checkRemoval();
		for (int i = 0; i < entitiesList.size(); i++) {
			Entity en = entitiesList.get(i);
			if(en instanceof Collidable) {
				quadTree.insert((Collidable) en);
			}
			en.move(player.getVelocity().multiply(-1));
			if(en instanceof LivingEntity) {
				
			} else if(en instanceof ObjectEntity) {
				
			} else {
				throw new RuntimeException("What is this entity ?\nname: " + en.getClass().getSimpleName() + "\nlooked like somthing wrong.");
			}
			en.update();
		}
		
		ArrayList<Collidable> returnObjects = new ArrayList<Collidable>();
		returnObjects = quadTree.retrieve(returnObjects, player);
		checkReturnObject(player, returnObjects);		

		for (int i = 0; i < entitiesList.size(); i++) {
			  Entity checkingentity = entitiesList.get(i);
			  if(!(checkingentity instanceof Collidable)) {
				  continue;
			  }
			  returnObjects.clear();
			  returnObjects = quadTree.retrieve(returnObjects, (Collidable) checkingentity);
			  checkReturnObject((Collidable) checkingentity, returnObjects);
		}
		
		aiList.removeIf(e -> !e.isActive());
		
		for(MicrobeAI bot : aiList) {
			bot.update();
		}
		generateBot();
//		System.out.println("entitycount = " + entitiesList.size());
	}
	
	private void checkReturnObject(Collidable checkingentity, ArrayList<Collidable> returnObjects) {
		final BoundingBox checkingBox = ((Collidable) checkingentity).getBound();
		@SuppressWarnings("unchecked")
		final ArrayList<Collidable> retrived = (ArrayList<Collidable>) returnObjects.clone();
		Thread collisionThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (Collidable returnObject : retrived) {
					  Entity Targetentity = (Entity) returnObject;
					  if(checkingentity == Targetentity) {
//						  System.out.println("don't check collide woth your self, CONTINUED!!!");
						  continue;
					  }
					  BoundingBox otherBox = returnObject.getBound();
					  if( ((otherBox.getMaxX() > checkingBox.getMinX()) && ((otherBox.getMaxY() > checkingBox.getMinY()) || (otherBox.getMinY() < checkingBox.getMaxY()))) || 
						  ((otherBox.getMinX() < checkingBox.getMaxX()) && ((otherBox.getMaxY() > checkingBox.getMinY()) || (otherBox.getMinY() < checkingBox.getMaxY()))) ){
						  ((Collidable) checkingentity).checkCollision((Collidable) Targetentity);
					  }
				  }
			}
		});
		collisionThread.start();
	}
	
	//TODO add entity when game entity density is too low
	private void generateBot() {
//		Spawner.s
		if(this.aiList.size() <= (MIN_BOT_MICROB_COUNT/3)) {
			Spawner.GenerateAIMicrobe(true);;
		}
		if(this.aiList.size() >= MIN_BOT_MICROB_COUNT) {
			return;
		}
//		System.out.println("asked to generateBot " + this.aiList.size() + ", entity size = " + entitiesList.size());
		Spawner.GenerateAIMicrobe(false);
	}
	
	/**
	 * Game Screen canvas should be the one who call this so no public here
	 * @return
	 */
	protected SynchronizedEntityList getEntityList() {
		return entitiesList;
	}

	public LinkedList<MicrobeAI> getAIList(){
		return aiList;
	}
}
