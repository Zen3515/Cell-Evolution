package logic.entity;

import javafx.geometry.Point2D;

@Deprecated
public abstract class ParticleEntity extends ObjectEntity {
	
	protected ParticleEntity(Point2D position) {
		super(position);
	}

	protected double lifeSpan;
	
	public enum ParticleType{
		WAVETAIL, SIMPLE_CIRCLE, SIMPLE_BUBBLE;
	}

}
