package logic.entity;

import javafx.geometry.Point2D;
import sharedobject.CircleCollidable;

@Deprecated
public abstract class WeedEntity extends LivingEntity implements CircleCollidable{

	public WeedEntity(Point2D position, double maxHealth) {
		super(position, maxHealth);
	}

}
