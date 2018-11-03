package logic;

import java.util.concurrent.ThreadLocalRandom;

import exception.NoTargetException;
import exception.UnableToRandomExclusivelyException;
import javafx.geometry.Point2D;
import logic.entity.LivingEntity;
import logic.entity.MicrobeEntity;
import sharedobject.CellEvolutionManager;
import sharedobject.GameScreenCanvas;
import sharedobject.Animatable.AnimationState;

public final class MicrobeAI {
	
//	public static void assignAI(MicrobeEntity microbe) {
//		
//	}
	
	private MicrobeEntity microbe;
	private LivingEntity targetMicrobe;
	private AIState myState = AIState.MOVING;
	private double elapsedTimer = 0.0d;
	
//	private static Random  randomer = new Random();
	private static final double MAXIMUM_TIME_PER_STATE = 5d;
	private static final double EVADED_DISTANCE = 800.0d;

	public MicrobeAI(MicrobeEntity microbe) {
		super();
		this.microbe = microbe;
	}
	
	@Deprecated //FOR TESTING ONLY
	public void setAIState(AIState state) {
		myState = state;
	}
	
	public boolean isActive() {
		if(microbe != null) {
			return microbe.isAlive();
		}
		return false;
	}
	
	private void changeState(AIState state) {
		microbe.setAnimationState(AnimationState.IDELING);
		if(myState != state) {
			myState = state;
			elapsedTimer = 0.0d;
		}
	}
	
	private void changeToNewState() {
		if(!GameScreenCanvas.getLogicalBoundary().contains(microbe.getPosition())) {
			Point2D direction = GameScreenCanvas.getCenter().subtract(microbe.getPosition());
			Point2D forward = new Point2D(Math.cos(Math.toRadians(microbe.getAngle()-90)), Math.sin(Math.toRadians(microbe.getAngle()-90)));
			
			double dot = forward.dotProduct(direction);
			double det = ((forward.getX()*direction.getY()) - (forward.getY()*direction.getX()));
			double prefAngle = Math.toDegrees(Math.atan2(det, dot));
//			if(prefAngle < 0) {
//				prefAngle += 360;
//			}
//			System.out.println("Out side better to go back, heading " + prefAngle + ", I'm at " + microbe.getPosition());
			microbe.setAngle(microbe.getAngle() + prefAngle);
//			System.out.println("Out side better to go back, heading " + microbe.getAngle() + ", I'm at " + microbe.getPosition());
			microbe.setAnimationState(AnimationState.FAST_FORWARDING);
			myState = AIState.MOVING;
			elapsedTimer = 0.0d;
			return;
		}		
		AIState newState = randomAIState();
		while(myState == newState) {
			newState = randomAIState();
		}
		changeState(newState);
	}
	
	private void escaping() throws NoTargetException{
		if(targetMicrobe == null) {
			throw new NoTargetException();
		}
		if(microbe.getPosition().distance(targetMicrobe.getPosition()) < EVADED_DISTANCE) {
			Point2D direction = targetMicrobe.getPosition().subtract(microbe.getPosition());
			Point2D forward = new Point2D(Math.cos(Math.toRadians(microbe.getAngle()+90)), Math.sin(Math.toRadians(microbe.getAngle()+90)));
			
			double dot = forward.dotProduct(direction);
			double det = ((forward.getX()*direction.getY()) - (forward.getY()*direction.getX()));
			double prefAngle = Math.toDegrees(Math.atan2(det, dot));
			double delta = Math.abs(prefAngle);
			if(delta > 3) {
				microbe.turn(prefAngle < 0);
			} else {
			}
			microbe.setAnimationState(AnimationState.FORWARDING);
		} else {
			//Successfully evade
			changeToNewState();
		}
	}
	
	private void hunting() throws NoTargetException{
		if(targetMicrobe == null) {
			throw new NoTargetException();
		}
		if(microbe.getHealth() < (microbe.getMaxHealth()/2)) {
			this.changeState(AIState.ESCAPING);
			return;
		}
		if(microbe.getPosition().distance(targetMicrobe.getPosition()) < EVADED_DISTANCE) {
			Point2D direction = targetMicrobe.getPosition().subtract(microbe.getPosition());
			Point2D forward = new Point2D(Math.cos(Math.toRadians(microbe.getAngle()-90)), Math.sin(Math.toRadians(microbe.getAngle()-90)));
			
			double dot = forward.dotProduct(direction);
			double det = ((forward.getX()*direction.getY()) - (forward.getY()*direction.getX()));
			double prefAngle = Math.toDegrees(Math.atan2(det, dot));
			double delta = Math.abs(prefAngle);
			if(delta > 1.5) {
				microbe.turn(prefAngle < 0);
			} else {
				microbe.setTurnRate(0.1);
			}
			microbe.setAnimationState(AnimationState.FAST_FORWARDING);
		} else {
			//It escaped
			changeToNewState();
		}
	}
	
	private void looping() {
		microbe.turn(elapsedTimer % 7 < 2);
		microbe.setAnimationState(AnimationState.FORWARDING);
	}
	
	private void moving() {
		microbe.setAnimationState(AnimationState.FORWARDING);
	}
	
	public void update() {
		if(!microbe.isAlive()) {
			//This will make this bot inactive
			microbe = null;
		}
		if(targetMicrobe != null && !targetMicrobe.isAlive()) {
			targetMicrobe = null;
		}
		MicrobeEntity attacker = microbe.getAttacker();
		if(attacker != null && attacker != targetMicrobe) {
			targetMicrobe = attacker;
			changeState(AIState.ESCAPING);
			microbe.setAttacker(null);
		}
		try {
			switch(myState) {
			case ESCAPING:
				escaping();
				return;
			case HUNTING:
				hunting();
				return;
			case LOOPING:
				looping();
				break;
			case MOVING:
				moving();
				microbe.dampTurnRate();
				break;
			default:
				break;
			}
		} catch (NoTargetException nte) {
			try {
				targetMicrobe = CellEvolutionManager.getInstance().getRandomEntityExclusively(microbe);
			} catch (UnableToRandomExclusivelyException e) {
				while(myState == AIState.ESCAPING || myState == AIState.HUNTING) {
					myState = randomAIState();
				}
			}
		}
		//IF Escaping or Hunting there is no time out
		if(elapsedTimer > MAXIMUM_TIME_PER_STATE) {
			changeToNewState();
		}
		elapsedTimer += CellEvolutionManager.getDeltaTime();
//		System.out.println("AIState = " + myState);
	}
	
	private static AIState randomAIState() {
		int rand = ThreadLocalRandom.current().nextInt(0, 3 + 1);
		if(rand == 0) {
			return AIState.ESCAPING;
		} else if (rand == 1) {
			return AIState.HUNTING;
		} else if (rand == 2) {
			return AIState.LOOPING;
		} else { //rand = 3
			return AIState.MOVING;
		}
	}
	
	public enum AIState{
		MOVING, LOOPING, HUNTING, ESCAPING;
	}

}
