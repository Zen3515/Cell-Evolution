package logic.cellpart;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import logic.entity.BubbleEntity;
import logic.entity.FoodEntity;
import logic.entity.LivingEntity;
import logic.entity.MicrobeEntity;
import logic.entity.FoodEntity.FoodType;
import sharedobject.CellEvolutionManager;
import sharedobject.CircleCollidable;

public class CarnivoreMouth extends CellPartHitable {

	protected double blockSize = 10d;
	private LivingEntity eating = null;
	private boolean willShowHealthbar = false;
	protected static final double DEFAULT_COOLDOWN_TIMER = 0.3d;
	protected double coolDownTimmer = DEFAULT_COOLDOWN_TIMER;
//	protected InnerShadow innerShadowEffect;

	public CarnivoreMouth(MicrobeEntity microbe) {
		super(microbe);
		animationDuration = 0.25d;
		setColor(Color.AQUA, Color.BLUE);
	}

	@Override
	public ArrayList<Point2D> getHitBox(Affine baseTransformation, double angle, double x, double y) {
		baseTransformation.appendRotation(angle, x, y);
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		result.add(baseTransformation.transform(new Point2D(x - (2.5*blockSize), y)));
		result.add(baseTransformation.transform(new Point2D(x - (2.5*blockSize), y - (6*blockSize))));
		result.add(baseTransformation.transform(new Point2D(x + (2.5*blockSize), y - (6*blockSize))));
		result.add(baseTransformation.transform(new Point2D(x + (2.5*blockSize), y)));
		return result;
	}
	
	public void setColor(Color baseColor, Color innerGlowColor) {
//		this.innerShadowEffect = new InnerShadow(30, innerGlowColor);
		Stop[] stops = new Stop[] { new Stop(0, baseColor), new Stop(1, innerGlowColor)};
		this.color = new RadialGradient(0, 0, 0.5, 0.95, 1, true, CycleMethod.NO_CYCLE, stops);
	}

	@Override
	public CellPartType getCellPartType() {
		return CellPartType.CARNIVORE_MOUTH;
	}

	@Override
	public void draw(GraphicsContext gc, double posX, double posY) {
		double jawRotation = 70;
		
		double animationTime = this.animationTime;
		
		if(animationTime > animationDuration) {
			animationTime = animationDuration;
		}
		
		if(animationTime < animationDuration/2) {
			jawRotation *= (animationTime/animationDuration*2);
		} else {
			jawRotation *= (1 - (animationTime/animationDuration));
		}
//		System.out.println("Jaw rotation = " + jawRotation + ", animationTime = " + animationTime + ", isAnimating " + isAnimating);
		
//		gc.setFill(Color.AQUA);
//		gc.fillRect(posX-3, posY-3, 6, 6);
		
		gc.setEffect(INNER_SHADOW_EFFECT);
		
		gc.transform(new Affine(new Rotate(-jawRotation, posX - blockSize, posY)));
		
		gc.beginPath();
		gc.moveTo(posX - blockSize, posY);
		gc.quadraticCurveTo(posX - (2*blockSize), posY, posX - (3 * blockSize), posY - (2 * blockSize));
		gc.quadraticCurveTo(posX - (5*blockSize), posY - (6*blockSize), posX - blockSize, posY - (7*blockSize));
		gc.lineTo(posX, posY - (7*blockSize));
		gc.lineTo(posX - blockSize, posY - (6*blockSize));
		gc.lineTo(posX, posY - (5*blockSize));
		gc.lineTo(posX - blockSize, posY - (4*blockSize));
		gc.lineTo(posX, posY - (3*blockSize));
		gc.lineTo(posX - blockSize, posY - (2*blockSize));
		gc.quadraticCurveTo(posX, posY, posX - blockSize, posY);
		gc.closePath();
		gc.setFill(color);
		gc.fill();
		
		gc.transform(new Affine(new Rotate(jawRotation, posX - blockSize, posY)));
		
		gc.transform(new Affine(new Rotate(jawRotation, posX + blockSize, posY)));
		
		gc.beginPath();
		gc.moveTo(posX + blockSize, posY);
		gc.quadraticCurveTo(posX + (2*blockSize), posY, posX + (3 * blockSize), posY - (2 * blockSize));
		gc.quadraticCurveTo(posX + (5*blockSize), posY - (6*blockSize), posX + blockSize, posY - (7*blockSize));
		gc.lineTo(posX, posY - (7*blockSize));
		gc.lineTo(posX + blockSize, posY - (6*blockSize));
		gc.lineTo(posX, posY - (5*blockSize));
		gc.lineTo(posX + blockSize, posY - (4*blockSize));
		gc.lineTo(posX, posY - (3*blockSize));
		gc.lineTo(posX + blockSize, posY - (2*blockSize));
		gc.quadraticCurveTo(posX, posY, posX + blockSize, posY);
		gc.closePath();
		gc.setFill(color);
		gc.fill();
		
		gc.transform(new Affine(new Rotate(-jawRotation, posX + blockSize, posY)));
		
		//TODO draw jaw base
		gc.beginPath();
		gc.moveTo(posX - (2*blockSize), posY);
		gc.quadraticCurveTo(posX, posY - (2*blockSize), posX + (2*blockSize), posY);
		gc.bezierCurveTo(posX + (4*blockSize), posY + (2*blockSize), posX + blockSize, posY + blockSize, posX, posY + blockSize);
		gc.bezierCurveTo(posX - blockSize, posY + blockSize, posX - (4*blockSize), posY + (2*blockSize), posX - (2*blockSize), posY);
		gc.closePath();
		gc.setFill(color);
		gc.fill();
		
		gc.setEffect(null);
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
					if(eating instanceof FoodEntity) {
						eating.setHealth(0);
						if(willShowHealthbar)
							microbe.showHealthbar();
					} else if (eating instanceof MicrobeEntity) {
						microbe.attack((MicrobeEntity) eating);
					}
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
	
	public void playConsumeAnimation(LivingEntity food, boolean willShowHealthbar) {
		if(isAnimating == false) {
			this.eating = food;
			this.willShowHealthbar = willShowHealthbar;
			if(eating != null && eating instanceof FoodEntity) {
				CellEvolutionManager.jawEatingSound[(int)(Math.random()+0.5)].play(10);
			} else {
				CellEvolutionManager.jawShut.play(10);
			}
//			if(food != null)
//				microbe.pinTo(food, food.getPosition().subtract(microbe.getPosition()).multiply(0.8));
		}
		isAnimating = true;
	}
	
	@Override
	protected CellPartCollisionResult checkColisionWithCircle(CircleCollidable other) {
		CellPartCollisionResult result = super.checkColisionWithCircle(other);
		if(result == CellPartCollisionResult.COLLIDED_TO_PUSH) {
			if(other instanceof FoodEntity && ((FoodEntity) other).getFoodType() == FoodType.FLESH) {
				return CellPartCollisionResult.COLLIDED_WITH_CONSUMEABLE;
			}
			if(other instanceof BubbleEntity) {
				return CellPartCollisionResult.COLLIDED_WITH_BUBBLE;
			}
		}
		return result;
	}

}
