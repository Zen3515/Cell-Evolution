package logic.entity;

import javafx.geometry.Point2D;
import sharedobject.Collidable;

/**
 * Bubbles can pop()
 * @author Zen3515
 *
 */
@Deprecated
public abstract class BubbleEntity extends ObjectEntity implements Collidable{

	protected BubbleEntity(Point2D position) {
		super(position);
	}
	
}
