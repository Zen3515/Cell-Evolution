package logic.entity;

import javafx.geometry.Point2D;
import sharedobject.CellEvolutionManager;
import sharedobject.GameManager;

/**
 * @author Zen3515
 *
 */
public class MicrobePlayer extends MicrobeEntity {
	
	public MicrobePlayer(Point2D position, double healthPoints, int boneLenght) {
		super(position, healthPoints, boneLenght);
	}
	
	@Override
	public void update() {
		if(!isAlive()) {
			return;
		}
		
		//FROM ENTITY
		updateAppliedForces();
		setMovementVelocity(acceleration.multiply(CellEvolutionManager.getDeltaTime()));
//		if(isHolding()) {
//			holdingWith.setPosition(this.position.add(holdingSpot));
//		}
		
		//FROM LIVING ENTITY
		damageCooldownTimer -= CellEvolutionManager.getDeltaTime();
		if(damageCooldownTimer < 0)
			damageCooldownTimer = 0;
		
		//FROM MICROBE ENTITY
		deltaTimeStack += CellEvolutionManager.getDeltaTime();
		if(healthBarTimer > 0) {
			healthBarTimer -= (0.6d*CellEvolutionManager.getDeltaTime());
			if(healthBarTimer < 0) {
				healthBarTimer = 0.0d;
			}
		}
		if(soundCooldownTimer > 0) {
			soundCooldownTimer -= CellEvolutionManager.getDeltaTime();
		}
		if(Math.abs(turningRate) < 0.1d) {
			this.turningRate = 0;
		} else {
			angle += this.turningRate;
			if (angle >= 360) {
				angle -= 360;
			} else if(angle < 0) {
				angle += 360;
			}
		}
		if(flagellumInstance != null)
			flagellumInstance.update();
		if(spikeInstance != null) {
			spikeInstance.update();
		}
		if(carnivoreMouthInstance != null) {
			carnivoreMouthInstance.update();
		}
		if(herbivoreMouthInstance != null) {
			herbivoreMouthInstance.update();
		}
		if(animationstate == AnimationState.FAST_FORWARDING) {
			moveForward(true);
		} else if(animationstate == AnimationState.FORWARDING) {
			moveForward(false);
		} else if(animationstate == AnimationState.BACKWARDING) {
			moveBackward();
		}
		
		
	}
	
	protected void moveBackward() {
		applyForce(new Point2D(Math.cos(Math.toRadians(getAngle()-90))*-swimAcceleration, Math.sin(Math.toRadians(getAngle()-90))*-swimAcceleration));
		
	}
	
	@Override
	public void onDeath() {
		super.onDeath();
		this.velocity = Point2D.ZERO;
		GameManager.setGamePause(true);
	}

}
