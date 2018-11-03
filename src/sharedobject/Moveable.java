package sharedobject;

import javafx.geometry.Point2D;
import utility.Force;


public interface Moveable {
//	public static final double DE_ACCElERATING_FACTOR = 0.3d;
//	public enum MovingState{ACCElERATING, ZEROACCElERATIONG, DEACCElERATING, NOTMOVING};
	//public int speed;
	//public Point2D getSpeed();
	public Point2D getAcceleration();
	public void setAcceleration(Point2D acceleration);
	/**
	 * Trying to stop moving.
	 */
	public void deAccelerate();
	public Point2D getVelocity();
	public void setMovementVelocity(Point2D velocity);
	public void setVelocity(Point2D velocity);
	public void applyForce(Force force);
}
