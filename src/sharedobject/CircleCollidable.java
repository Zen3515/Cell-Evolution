package sharedobject;

import javafx.geometry.Point2D;

public interface CircleCollidable extends Collidable{
	
	public Point2D getCenter();
	
	public double getRadius();
	
}
