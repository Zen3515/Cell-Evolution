package logic.entity;

import exception.InvalidEnumException;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import sharedobject.CellEvolutionManager;
import sharedobject.CircleCollidable;
import sharedobject.Collidable;
import sharedobject.Consumeable;
import utility.CollisionUtility;

public class FoodEntity extends LivingEntity implements Consumeable, CircleCollidable{
	
	protected double radius = 25.0d;
//	protected double overlayAngle = 10.0d;
//	protected double overlayOpacity = 0.0d;
//	protected double opacity = 1.0d;
//	protected static final double opacityIncreasingRate = 0.5d;
//	protected static final double opacityDecreasingRate = 0.7d;
//	private boolean isIncreasing = true;
	protected final FoodType foodType;
	public static final double HEALING_POINT = 15;
//	protected double turnRate;
	
	public FoodEntity(Point2D position) {
		this(position, MAX_FOOD_HEALTH);
	}
	
	public FoodEntity(Point2D position, double maxHealth, FoodType type) {
		super(position, maxHealth);
		this.foodType = type;
		this.damage = 0;
//		randomTurnRate();
		randomAngle();
//		System.out.println("Turn rate = " + this.turnRate);
	}
	
	public void randomAngle() {
		this.angle = Math.random()*360.0d;
	}
	
//	public void randomTurnRate() {
//		turnRate = (Math.random())*10.5;
//	}
	
	public FoodEntity(Point2D position, double maxHealth) {
		this(position, maxHealth, FoodType.FLESH);
	}

	@Override
	public BoundingBox getBound() {
		double radius = this.radius*scale; //scale should be one?
		return new BoundingBox(position.getX() - radius, position.getY() - radius, radius*2, radius*2);
	}

	@Override
	public void checkCollision(Collidable other) {
		switch(other.getHitBoxType()) {
		case CIRCLE:
			if(CollisionUtility.isCircleCollide(this.position, radius, ((CircleCollidable)other).getCenter(), ((CircleCollidable)other).getRadius())) {
				pushBoth((Entity) other);
			}
			return;
		case POLYGON:
			MicrobeEntity mOther = (MicrobeEntity) other; //Can't be something else
			mOther.checkCollision(this);
			break;
		default:
			throw new InvalidEnumException(other.getHitBoxType().name() + " is not allowed");
		}
	}

	@Override
	public void draw(GraphicsContext gc) {
		double x = this.getPosition().getX() - radius;
		double y = this.getPosition().getY() - radius;
		double width = this.radius*2;
		Image foodIMG = null;
		if(this.getFoodType() == FoodType.FLESH) {
			foodIMG = CellEvolutionManager.food_meat;
		} else { //Plant
			foodIMG = CellEvolutionManager.food_plant;
		}

		gc.transform(new Affine(new Rotate(angle, this.getPosition().getX(), this.getPosition().getY())));
		//วาดอันล่าง
//		gc.setGlobalAlpha(opacity);
		gc.drawImage(foodIMG, x, y, width, width);
		
		//วาดอันบน
//		gc.setGlobalAlpha(overlayOpacity);
//		gc.transform(new Affine(new Rotate(overlayAngle, this.getPosition().getX(), this.getPosition().getY())));
//		gc.drawImage(foodIMG, x, y, width, width);
//		gc.setGlobalAlpha(1.0d);
//		gc.transform(new Affine(new Rotate(-overlayAngle, this.getPosition().getX(), this.getPosition().getY())));
		
		gc.transform(new Affine(new Rotate(-angle, this.getPosition().getX(), this.getPosition().getY())));
	}
	
//	@Override
//	public void update() {
//		super.update();
//		
//		if(isIncreasing) {
//			overlayOpacity += opacityIncreasingRate*CellEvolutionManager.getDeltaTime();
//			if(overlayOpacity > 1.0d) {
//				overlayOpacity = 1.0d;
//				this.isIncreasing = false;
//			}
//		} else {
//			opacity -= opacityDecreasingRate*CellEvolutionManager.getDeltaTime();
//			if(opacity < 0.0d) {
//				opacity = 1.0d;
//				overlayOpacity = 0.0d;
//				this.angle = (this.angle + overlayAngle) % 360.0d;
//				this.overlayAngle = (this.overlayAngle + (Math.random()*360.0d)) % 360.0d;
//				this.isIncreasing = true;
//			}
//		}
//	}

	@Override
	public double getMass() {
		return 1;
	}

	@Override
	public HitBoxType getHitBoxType() {
		return HitBoxType.CIRCLE;
	}

	@Override
	public Point2D getCenter() {
		return this.position;
	}

	@Override
	public double getRadius() {
		return this.radius;
	}
	
	public FoodType getFoodType() {
		return this.foodType;
	}
	
	public enum FoodType{
		FLESH, PLANT;
	}
	
}
