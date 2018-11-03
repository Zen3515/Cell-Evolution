package logic.cellpart;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Affine;
import logic.entity.FoodEntity;
import logic.entity.MicrobeEntity;
import logic.entity.FoodEntity.FoodType;
import sharedobject.CellEvolutionManager;
import sharedobject.CircleCollidable;

public class HerbivoreMouth extends CellPartHitable {
	
	protected double blockSize = 20d;
	private FoodEntity eating = null;
	private boolean willShowHealthbar = false;
	protected static final double DEFAULT_COOLDOWN_TIMER = 0.3d;
	protected double coolDownTimmer = DEFAULT_COOLDOWN_TIMER;

	public HerbivoreMouth(MicrobeEntity microbe) {
		super(microbe);
		animationDuration = 0.7d;
		setColor(Color.AQUA, Color.BLUE);
	}
	
	public void setColor(Color baseColor, Color innerGlowColor) {
		Stop[] stops = new Stop[] { new Stop(0, baseColor), new Stop(1, innerGlowColor)};
		this.color = new RadialGradient(0, 0, 0.5, 0.95, 1, true, CycleMethod.NO_CYCLE, stops);
	}

	@Override
	public ArrayList<Point2D> getHitBox(Affine baseTransformation, double angle, double x, double y) {
		baseTransformation.appendRotation(angle, x, y);
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		result.add(baseTransformation.transform(new Point2D(x - (2*blockSize), y)));
		result.add(baseTransformation.transform(new Point2D(x - (2*blockSize), y - (2*blockSize))));
		result.add(baseTransformation.transform(new Point2D(x + (2*blockSize), y - (2*blockSize))));
		result.add(baseTransformation.transform(new Point2D(x + (2*blockSize), y)));
		return result;
	}

	@Override
	public CellPartType getCellPartType() {
		return CellPartType.HERBIVORE_MOUTH;
	}

	@Override
	public void draw(GraphicsContext gc, double posX, double posY) {
		double animationTime = this.animationTime;
		
		if(animationTime > animationDuration) {
			animationTime = animationDuration;
		}
		
		double yShift = 1.5d*blockSize;
		
		if(animationTime < animationDuration/2) {
			yShift *= (animationTime/animationDuration*2);
		} else {
			yShift *= (1 - (animationTime/animationDuration));
		}
		
		gc.setEffect(INNER_SHADOW_EFFECT);
		gc.beginPath();
		gc.moveTo(posX - blockSize, posY);
		gc.lineTo(posX - (2*blockSize), posY - (2*blockSize));
		gc.quadraticCurveTo(posX, posY - (blockSize) + yShift, posX + (2*blockSize), posY - (2*blockSize));
		gc.lineTo(posX + blockSize, posY);
		gc.quadraticCurveTo(posX, posY + (2*blockSize), posX - blockSize, posY);
		gc.closePath();
		gc.setFill(color);
		gc.fill();
		gc.setEffect(null);
	}
	
	public void playConsumeAnimation(FoodEntity food, boolean willShowHealthbar) {
		if(isAnimating == false) {
			this.eating = food;
			this.willShowHealthbar = willShowHealthbar;
			if(eating != null) {
				CellEvolutionManager.suckingSound.play(10);
			}
		}
		isAnimating = true;
	}
	
	@Override
	public void update() {
//		super.update();
		if(isAnimating) {
//			System.out.println("update()... animating " + this.getClass().getSimpleName());
			if(animationTime < animationDuration) {
				animationTime += CellEvolutionManager.getDeltaTime();
				return;
			} else {
				if(this.eating != null) {
					eating.setHealth(0);
					if(willShowHealthbar)
						microbe.showHealthbar();
					eating = null;
				}
			}
			if(coolDownTimmer > 0.0d) {
				coolDownTimmer -= CellEvolutionManager.getDeltaTime();
			} else {
				animationTime = 0.0d;
				isAnimating = false;
				coolDownTimmer = DEFAULT_COOLDOWN_TIMER;
			}
		}
	}
	
	@Override
	protected CellPartCollisionResult checkColisionWithMicrobe(MicrobeEntity other) {
		MicrobeEntity microbeOther = (MicrobeEntity) other;
		if(!checkBodyCollision(microbeOther)) {
			return CellPartCollisionResult.NOT_COLLIDED;
		} else {
			return CellPartCollisionResult.COLLIDED_TO_PUSH;
		}
	}

	@Override
	protected CellPartCollisionResult checkColisionWithCircle(CircleCollidable other) {
		CellPartCollisionResult result = super.checkColisionWithCircle(other);
		if(result == CellPartCollisionResult.COLLIDED_TO_PUSH) {
			if(other instanceof FoodEntity && ((FoodEntity) other).getFoodType() == FoodType.PLANT) {
				return CellPartCollisionResult.COLLIDED_WITH_CONSUMEABLE;
			}
		}
		return result;
	}

}
