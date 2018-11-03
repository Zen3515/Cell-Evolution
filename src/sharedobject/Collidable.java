package sharedobject;

import javafx.geometry.BoundingBox;

public interface Collidable {

	public BoundingBox getBound();
	
//	public ArrayList<Point2D> getHitBox();
	
//	public void takeCollisionAction();
	
	public void checkCollision(Collidable other);
	
//	public void push(Collidable other);
	
	public HitBoxType getHitBoxType();
	
	public enum HitBoxType{
		POLYGON, CIRCLE;
	}
}
