package logic.entity;

import java.util.LinkedList;

import javafx.geometry.Point2D;
import sharedobject.CellEvolutionManager;
import sharedobject.IRenderable;
import sharedobject.Moveable;
import sharedobject.Scaleable;
import utility.Force;

/**
 * @author Zen3515
 *
 */
public abstract class Entity implements IRenderable, Moveable, Scaleable{
	
	//TODO For player velocity become camera speed instead
	
	public static final double DEFAULT_MAXIMUM_SWIM_SPEED = 140;
	public static final double DEFAULT_MAXIMUM_ACCELERATION = 250;
	public static final double MAXIMUM_SPEED = 120;
	public static final double VISCOSSITY_FACTOR = 0.3;
	protected static final double COLLISION_PUSHBACK_FACTOR = 50d;
	
//	protected Entity pinBy = null;
//	protected Entity holdingWith = null;
//	protected Point2D holdingSpot = null;
	
	protected double maxSwimSpeed;
	protected Point2D position;
	protected Point2D acceleration;
	protected Point2D velocity;
	protected double angle;
	protected LinkedList<Force> forces;
	protected double scale = 1;
	
	protected Entity(Point2D position) {
		this.position = position;
		this.velocity = new Point2D(0, 0);
		this.acceleration = new Point2D(0, 0);
		this.forces = new LinkedList<Force>();
		this.maxSwimSpeed = DEFAULT_MAXIMUM_SWIM_SPEED;
	}
	
	public void update() {
		updateAppliedForces();
		setMovementVelocity(acceleration.multiply(CellEvolutionManager.getDeltaTime()));
		move(velocity);
		
//		if(isHolding()) {
//			holdingWith.setPosition(this.position.add(holdingSpot));
//		}
//		System.out.println("Update() new acc = " + this.acceleration.toString() + " , velocity = " + this.velocity.toString());
		//System.out.println("Angle = " + angle);
	}

	public Point2D getPosition() {
		return position;
	}

	/**
	 * Please switch move(velocity) this.position = this.position.add(velocity);
	 * @param position
	 */
	public void setPosition(Point2D position) {
		//TODO use move instead?
		this.position = position;
	}
	
	public void move(Point2D velocity) {
//		if(isPined()) {
//			return;
//		}
		this.position = this.position.add(velocity);
	}
	
	protected void updateAppliedForces() {
		forces.removeIf(e -> (!e.isActive()));
		for(Force f: this.forces) {
			setAcceleration(this.acceleration.add(f.toPoint2D()));
			f.tick(CellEvolutionManager.getDeltaTime());
		}
		deAccelerate();
	}
	
	@Override @Deprecated //NOT WORKING
	public void applyForce(Force force) {
		force.div(getMass());
		this.forces.add(force);
		//updateAppliedForces();//TODO Check if necessary.
	}
	
	/**
	 * there is no tick count for this
	 * @param force
	 */
	public void applyForce(Point2D force) {
		setAcceleration(this.acceleration.add(new Point2D(force.getX()/getMass(), force.getY()/getMass())));
	}
	
	public abstract double getMass();

	@Override 
	public void deAccelerate() {
//		System.out.println("Old acc = " + acceleration);
		if(velocity != Point2D.ZERO) {
			applyForce(this.acceleration.multiply(-VISCOSSITY_FACTOR));
			if(this.acceleration.magnitude() < 3) {
				this.acceleration = Point2D.ZERO;
			}
		}
//		System.out.println("New acc = " + acceleration);
	}
	
	@Override
	public Point2D getVelocity() {
		return velocity;
	}

	@Override
	public void setMovementVelocity(Point2D velocity) {
//		System.out.println("SetMovement = " + velocity);
//		System.out.println("curren acc = " + acceleration);
		if(velocity.magnitude() < 1e-3) {
			velocity = Point2D.ZERO;
		}
		if(velocity.magnitude() > maxSwimSpeed) {
			this.velocity = velocity.normalize().multiply(maxSwimSpeed);
		} else {
			this.velocity = velocity;
		}
	}
	
	@Override
	public void setVelocity(Point2D velocity) {
		if(velocity.magnitude() < 1e-3) {
			velocity = Point2D.ZERO;
		}
		if(velocity.magnitude() > MAXIMUM_SPEED) {
			this.velocity = velocity.normalize().multiply(MAXIMUM_SPEED);
		} else {
			this.velocity = velocity;
		}
	}
	
	@Override
	public Point2D getAcceleration() {
		return acceleration;
	}
	
	/**
	 * Magic force don't care about mass
	 * @param force
	 */
	public void applyMagicForce(Point2D force) {
//		force = new Point2D(force.getX()/getMass(), force.getY()/getMass());
		acceleration = acceleration.add(force);
//		System.out.println("Applied magical force to " + this.getClass().getSimpleName() + ", " + force);
//		System.out.println("new acc = " + acceleration);
	}

	@Override
	public void setAcceleration(Point2D acceleration) {
		if(acceleration.magnitude() > DEFAULT_MAXIMUM_ACCELERATION) {
			this.acceleration = (acceleration.normalize().multiply(DEFAULT_MAXIMUM_ACCELERATION));
		} else {
			this.acceleration = (acceleration);
		}
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
//		if(isPined()) {
//			return;
//		}
		this.angle = angle;
	}
	
	@Override
	public void setScale(double scale) {
		this.scale = scale;
	}

	@Override
	public double getScale() {
		return scale;
	}
	
	protected void pushOther(Entity other, double Factor) {
		Point2D force = getPosition().subtract(other.getPosition()).normalize().multiply(Factor);
//		applyMagicForce(force);
		other.applyForce(force.multiply(-1));
	}
	
	/**
	 * this will be call when body collide with body
	 * @param other
	 */
	protected void pushBoth(Entity other) {
		pushBoth(other, COLLISION_PUSHBACK_FACTOR);
	}
	
	protected void pushBoth(Entity other, double Factor) {
		Point2D force = getPosition().subtract(other.getPosition()).normalize().multiply(Factor);
		applyMagicForce(force);
		other.applyForce(force.multiply(-1));
	}
	
//	public void unPin() {
//		holdingWith.pinBy = null;
//		holdingWith = null;
//	}
//	
//	public void pinTo(Entity other, Point2D holdingSpot) {
//		if(isHolding() || isPined() || other.isPined()) {
//			return; //already pin
//		}
//		holdingWith = other;
//		other.pinBy = this;
//		this.holdingSpot = holdingSpot;
//	}
//	
//	public boolean isHolding() {
//		return holdingWith != null;
//	}
//	
//	public boolean isPined() {
//		return pinBy != null;
//	}
	
}
