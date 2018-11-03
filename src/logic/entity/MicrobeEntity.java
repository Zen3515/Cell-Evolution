package logic.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import logic.cellpart.CarnivoreMouth;
import logic.cellpart.CellPart;
import logic.cellpart.CellPartHitable;
import logic.cellpart.CellPart.CellPartType;
import logic.entity.FoodEntity.FoodType;
import sharedobject.Animatable;
import sharedobject.CellEvolutionManager;
import sharedobject.CircleCollidable;
import sharedobject.Collidable;
import sharedobject.Controllable;
import sharedobject.Spawner;
import logic.cellpart.CellPartHolder;
import logic.cellpart.Eye;
import logic.cellpart.Flagellum;
import logic.cellpart.HerbivoreMouth;
import logic.cellpart.Spike;
import utility.CollisionUtility;

/**
 * @author Zen3515
 *
 */
public class MicrobeEntity extends LivingEntity implements Animatable, Controllable{
	
	protected static final double BASE_FREQUENCY = 1.0d;
	public static final double DEFAULT_SCALE = 1.0d;
	public static final double BONEGAP =  2.0d;
	public static final double ATTRIBUTE_SPEED_PER_FLAGELLUM = 20;
	public static final double ATTRIBUTE_DAMAGE_PER_PART = 5.7;
	protected static final double COLLISION_PUSHBACK_FACTOR = 50d;
	protected static final double COLLISION_DAMAGED_PUSHBACK_FACTOR = 2000d;
	protected static final double DEFAULT_HEALTHBAR_TIME = 1.7d;
	protected static final double SOUND_COOLDOWN_TIME = 0.2d;
	protected static final double DEFAULT_SWIM_ACCELERATION = 40.0d;
	protected static final double DEFAULT_RUSH_ACCELERATION = 80.0d;
//	protected static final InnerShadow INNER_SHADOW_EFFECT = new InnerShadow(3, 0, 0, Color.rgb(0, 0, 0, 0.5));

	/**
	 * First call can be null, after add a cell part will no longer null
	 */
	protected Flagellum flagellumInstance = null;
	protected Spike spikeInstance = null;
	protected CarnivoreMouth carnivoreMouthInstance = null;
	protected Eye eyeInstance = null;
	protected HerbivoreMouth herbivoreMouthInstance = null;
	
	protected ArrayList<Double> bone = new ArrayList<>();
	
	protected double healthBarTimer = 0.0d; //this is opacity of health bar
	protected double soundCooldownTimer = 0;
	protected double frequency = BASE_FREQUENCY;
	protected static double curvedFactor = 2.3d;
	protected double k_const;
	protected double w_omega;
	protected double wave_lenght;
	protected double[] cellBody;
	protected double mass = 10.0d;
	protected double deltaTimeStack = 0.0d;
	protected AnimationState animationstate;
	protected double width = 0;
	protected double lenght = 0;
	protected double headExtends = 0;
	protected double maxTurnRate = 1.61;
	protected double turningRate = 0;
	protected double turningStep = 0.23;
	protected ArrayList<Point2D> normalHitbox;
	protected Color strokeColor;
	protected Color bodyColor;
	
	protected double swimAcceleration = DEFAULT_SWIM_ACCELERATION;
	protected double swimRushAcceleration = DEFAULT_RUSH_ACCELERATION;

//Constructor -------------------------------------------------------------------------------------------------
	public MicrobeEntity(Point2D position, double maxHealth, int boneLenght) {
		super(position, maxHealth);
		this.cellBody = new double[boneLenght];
		this.maxHealth = 100;
		animationstate = AnimationState.IDELING;
		updateConstant();
		setColor(Color.AQUA, Color.BLUE);
	}
//Constructor -------------------------------------------------------------------------------------------------
	
//HITBOX ------------------------------------------------------------------------------------------------------
	//TODO as has more path do the calculation here
	protected void calculateNormalHitboxAndCellWidth() {//TODO add check for part too
		double minY = 0;
		double maxY = cellBody.length*BONEGAP;
		double maxX = 0;
		normalHitbox = new ArrayList<Point2D>();
		ArrayList<Point2D> leftSide = new ArrayList<Point2D>();
		boolean isIncreasing = false;
		if(cellBody[0] < cellBody[1])		{
			isIncreasing = true;
		}
		normalHitbox.add(new Point2D(cellBody[0], 0));
		leftSide.add(new Point2D(-cellBody[0], 0));
		maxX = cellBody[0];
		for(int i = 1; i < this.cellBody.length-1; i++) {
			if(maxX < cellBody[i]) {
				maxX = cellBody[i];
			}
			if(cellBody[i] < cellBody[i+1]) {
				if(!isIncreasing) { 
					normalHitbox.add(new Point2D(cellBody[i], (i*BONEGAP)));
					leftSide.add(new Point2D(-cellBody[i], (i*BONEGAP)));
					isIncreasing = true;
				}
			} else if (cellBody[i] > cellBody[i+1]) {
				if(isIncreasing) { //just change
					normalHitbox.add(new Point2D(cellBody[i], (i*BONEGAP)));
					leftSide.add(new Point2D(-cellBody[i], (i*BONEGAP)));
					isIncreasing = false;
				}
			}
		}
		normalHitbox.add(new Point2D(cellBody[cellBody.length-1], (cellBody.length-1) * BONEGAP));
		leftSide.add(new Point2D(-cellBody[cellBody.length-1], (cellBody.length-1) * BONEGAP));
		
		Collections.reverse(leftSide);
		normalHitbox.addAll(leftSide);
		
		//TODO as has more path do the calculation here
		if(spikeInstance != null) {
			for(CellPartHolder part : spikeInstance.getPartList()) {
				for(Point2D hitbox : spikeInstance.getHitBox(new Affine(), part.angle, part.x, part.y)) {
					if(hitbox.getX() > maxX) {
						maxX = hitbox.getX();
					};
					if(hitbox.getY() < minY) {
						minY = hitbox.getY();
					} else if(hitbox.getY() > maxY) {
						maxY = hitbox.getY();
					}
				}
			}
		}
		if(carnivoreMouthInstance != null) {
			for(CellPartHolder part : carnivoreMouthInstance.getPartList()) {
				for(Point2D hitbox : carnivoreMouthInstance.getHitBox(new Affine(), part.angle, part.x, part.y)) {
					if(hitbox.getX() > maxX) {
						maxX = hitbox.getX();
					};
					if(hitbox.getY() < minY) {
						minY = hitbox.getY();
					} else if(hitbox.getY() > maxY) {
						maxY = hitbox.getY();
					}
				}
			}
		}
		if(herbivoreMouthInstance != null) {
			for(CellPartHolder part : herbivoreMouthInstance.getPartList()) {
				for(Point2D hitbox : herbivoreMouthInstance.getHitBox(new Affine(), part.angle, part.x, part.y)) {
					if(hitbox.getX() > maxX) {
						maxX = hitbox.getX();
					};
					if(hitbox.getY() < minY) {
						minY = hitbox.getY();
					} else if(hitbox.getY() > maxY) {
						maxY = hitbox.getY();
					}
				}
			}
		}
		
		this.width = 2.0d*(maxX);
		this.lenght = Math.abs(maxY-minY);
		this.headExtends = Math.abs(minY);
//		System.out.println("Normal hitbox calculated: " + this.normalHitbox);
	}

	@Override
	public BoundingBox getBound() {
		double x = this.position.getX() - (width/2);
		double y = this.position.getY() - ((headExtends));
		Affine affine = this.getAffineTranformation(false);
		Point2D tempPoint = affine.transform(new Point2D(x,y));
		double minX = tempPoint.getX();
		double minY = tempPoint.getY();
		double maxX = tempPoint.getX();
		double maxY = tempPoint.getY();
		tempPoint = affine.transform(new Point2D(x+width, y));
		if(tempPoint.getX() < minX) {
			minX = tempPoint.getX();
		} else if (tempPoint.getX() > maxX) {
			maxX = tempPoint.getX();
		};
		if(tempPoint.getY() < minY) {
			minY = tempPoint.getY();
		} else if (tempPoint.getY() > maxY) {
			maxY = tempPoint.getY();
		};
		tempPoint = affine.transform(new Point2D(x, y+lenght));
		if(tempPoint.getX() < minX) {
			minX = tempPoint.getX();
		} else if (tempPoint.getX() > maxX) {
			maxX = tempPoint.getX();
		};
		if(tempPoint.getY() < minY) {
			minY = tempPoint.getY();
		} else if (tempPoint.getY() > maxY) {
			maxY = tempPoint.getY();
		};
		tempPoint = affine.transform(new Point2D(x+width, y+lenght));
		if(tempPoint.getX() < minX) {
			minX = tempPoint.getX();
		} else if (tempPoint.getX() > maxX) {
			maxX = tempPoint.getX();
		};
		if(tempPoint.getY() < minY) {
			minY = tempPoint.getY();
		} else if (tempPoint.getY() > maxY) {
			maxY = tempPoint.getY();
		};
		return new BoundingBox(minX, minY, maxX-minX, maxY-minY);
	}
	
	@Override
	public HitBoxType getHitBoxType() {
		return HitBoxType.POLYGON;
	}
	
	public ArrayList<Point2D> getBodyHitBox() {
		Affine affine = this.getAffineTranformation(false);		
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		for(Point2D point : this.normalHitbox) {
			result.add(affine.transform(point.add(position)));
		}
		return result;
	}
	
	public void attack(MicrobeEntity other) {
//		System.out.println("normal attack");
		other.decreaseHealthPoint(this.damage, this);
		pushBoth(other, COLLISION_DAMAGED_PUSHBACK_FACTOR);
	}
	
	public void drawAttack(MicrobeEntity other) {
//		System.out.println("draw attack");
//		pushAttack(this);
		pushBoth(other);
	}
	
	public void popAttack(ObjectEntity bubble) {
		
	}
	
	public boolean consumeAttack(FoodEntity other) {
		//other.healthPoints = 0;
		//this.showHealthbar();
		return this.heal(FoodEntity.HEALING_POINT);
	}
	
	/**
	 * this will be call when body collide with body
	 * @param other
	 */
	@Override
	public void pushBoth(Entity other) {
		super.pushBoth(other, COLLISION_PUSHBACK_FACTOR);
		if(soundCooldownTimer <= 0.0d) {
			soundCooldownTimer = SOUND_COOLDOWN_TIME;
			CellEvolutionManager.bouncySound[((int)(Math.random()+0.5))].play(10);
		}
	}

	@Override
	public void checkCollision(Collidable other) {
		//First check if this body collide with other, after that check for cellpart
		if(other instanceof MicrobeEntity) {
			if(CollisionUtility.isPolygonCollide(((MicrobeEntity) other).getBodyHitBox(), getBodyHitBox())) {
				pushBoth((Entity) other);
				return;
			}
		} else { // The other are circle
			if(CollisionUtility.isPolygonCollideWithCircle(getBodyHitBox(), ((CircleCollidable) other).getCenter(), ((CircleCollidable) other).getRadius())){
				pushOther((Entity) other, COLLISION_PUSHBACK_FACTOR);
//				System.out.println("Body Collide with circle");
				return;
			}
		}
		
		//Check cellpart form now on
		if(spikeInstance != null) {
			switch(spikeInstance.isCellPartHit(other)) {
			case COLLIDED_WITH_BODY:
				attack((MicrobeEntity) other);
				spikeInstance.playSpikeHitAnimation();
				return;
			case COLLIDED_WITH_SAMETYPE:
				drawAttack((MicrobeEntity) other);
				spikeInstance.playSpikeDrawAnimation();
				((MicrobeEntity)other).spikeInstance.playSpikeDrawAnimation();
				return;
			case COLLIDED_WITH_BUBBLE:
				popAttack((ObjectEntity) other);
				return;
			case COLLIDED_TO_PUSH:
				pushOther((Entity) other, COLLISION_PUSHBACK_FACTOR);
				break;
			//Other than this is count as not Collide
			default:
				break;
			
			}

		};
		if(carnivoreMouthInstance != null) {
			switch(carnivoreMouthInstance.isCellPartHit(other)) {
			case COLLIDED_TO_PUSH:
				pushOther((Entity) other, COLLISION_PUSHBACK_FACTOR);
				break;
			case COLLIDED_WITH_BODY:
//				attack((MicrobeEntity) other);
				carnivoreMouthInstance.playConsumeAnimation((LivingEntity) other, false);
				break;
			case COLLIDED_WITH_BUBBLE:
				popAttack((ObjectEntity) other);
				break;
			case COLLIDED_WITH_CONSUMEABLE:
				carnivoreMouthInstance.playConsumeAnimation((FoodEntity) other, consumeAttack((FoodEntity) other));
				break;
			case COLLIDED_WITH_SAMETYPE:
				//TODO
				drawAttack((MicrobeEntity) other);
//				carnivoreMouthInstance.playConsumeAnimation();
//				((MicrobeEntity)other).carnivoreMouthInstance.playConsumeAnimation();
				break;
			default:
				break;
			}
		}
		
		if(herbivoreMouthInstance != null) {
			switch(herbivoreMouthInstance.isCellPartHit(other)) {
			case COLLIDED_TO_PUSH:
				pushOther((Entity) other, COLLISION_PUSHBACK_FACTOR);
				break;
			case COLLIDED_WITH_BODY:
				pushOther((Entity) other, COLLISION_PUSHBACK_FACTOR);
				break;
			case COLLIDED_WITH_BUBBLE:
				pushOther((Entity) other, COLLISION_PUSHBACK_FACTOR);
				break;
			case COLLIDED_WITH_CONSUMEABLE:
				herbivoreMouthInstance.playConsumeAnimation((FoodEntity) other, consumeAttack((FoodEntity) other));
				break;
			default:
				break;
			}
		}
		
	}
	
	protected void onDeath() {
		setVelocity(Point2D.ZERO);
		setAcceleration(Point2D.ZERO);
		if(((int)System.currentTimeMillis()) % 2 == 1) {
			Spawner.spawnFood(getBound(), FoodType.FLESH, 4);
		} else {
			Spawner.spawnFood(getBound(), FoodType.PLANT, 4);
		}
	}
//HITBOX ------------------------------------------------------------------------------------------------------
	
//Turning physic ----------------------------------------------------------------------------------------------
	public void setTurnRate(double rate) {
		if(rate > maxTurnRate) {
			rate = maxTurnRate;
		} else if(rate < -maxTurnRate) {
			rate = -maxTurnRate;
		}
		this.turningRate = rate;
	}

	@Override
	public void turn(boolean isLeft) {
		if (isLeft) {
			setTurnRate(this.turningRate - this.turningStep);
		} else {
			setTurnRate(this.turningRate + this.turningStep);
		}
	}

	@Override
	public void dampTurnRate() {
		if(this.turningRate < 0) {
			this.turningRate += 0.055;
		} else {
			this.turningRate -= 0.055;
		}
	}
//Turning physic ----------------------------------------------------------------------------------------------

//Property ----------------------------------------------------------------------------------------------------
//	private double getLenght() {
//		return this.lenght*scale;
//	}
//	
//	private double getWidth() {
//		return this.width*this.scale;
//	}
	
	public int getBodyLenght() {
		return this.cellBody.length;
	}
	
	public ArrayList<Double> getBone() {
		return bone;
	}

	public void setBone(ArrayList<Double> bone) {
		this.bone = bone;
	}

	@Override
	public double getMass() {
		return mass*this.scale;
	}

	@Override
	public void setAnimationState(AnimationState newState) {
		if(this.animationstate != newState) {
			switch(newState) {
			case ATACKING_SPIKE:
//				flagellumInstance.setFrequency(BASE_FREQUENCY);
				spikeInstance.playSpikeHitAnimation();
				break;
			case BACKWARDING:
				flagellumInstance.setFrequency(BASE_FREQUENCY);
				flagellumInstance.setReverse(true);
				break;
			case FAST_FORWARDING:
				flagellumInstance.setFrequency(2.5*BASE_FREQUENCY);
				flagellumInstance.setReverse(false);
				break;
			case FORWARDING:
				flagellumInstance.setFrequency(BASE_FREQUENCY);
				flagellumInstance.setReverse(false);
				break;
			case IDELING:
				flagellumInstance.setFrequency(BASE_FREQUENCY);
				break;
			default:
				break;
			}
			this.animationstate = newState;
			updateConstant();
		}
	}
	
	public void showHealthbar() {
		healthBarTimer = MicrobeEntity.DEFAULT_HEALTHBAR_TIME;
	}
//Property ----------------------------------------------------------------------------------------------------

//Cell part cell body -----------------------------------------------------------------------------------------
	public void setCellBody(double[] cellBody) {
		this.cellBody = cellBody;
		calculateNormalHitboxAndCellWidth();
	}
	
	//TODO When add new part type
	public CellPart getCellPartFromType(CellPartType partType) {
		switch(partType) {
		case FLAGELLUM:
			return flagellumInstance;
		case SPIKE:
			return spikeInstance;
		case CARNIVORE_MOUTH:
			return carnivoreMouthInstance;
		case HERBIVORE_MOUTH:
			return herbivoreMouthInstance;
		case EYE:
			return eyeInstance;
		default:
			return null;
		}
	}

	//TODO add more part type = update here too
	/**
	 * add cell part 
	 * (Spike need width and lenght as argument)
	 * @param partType
	 * @param x
	 * @param y
	 * @param angle
	 * @param isPaired
	 * @throws IllegalArgumentException if argument not match
	 */
	public void addCellPart(CellPartType partType, double x, double y, double angle, boolean isPaired, double... args) {
		CellPart cellpartInstance = getCellPartFromType(partType);
		
		if(cellpartInstance == null) {
			switch(partType) {
			case FLAGELLUM:
				flagellumInstance = new Flagellum(this);
				cellpartInstance = flagellumInstance;
				break;
			case SPIKE:
				if(args.length != 2)
					throw new IllegalArgumentException("Spike need exactly 2 arguments, recieved: " + args.length);
				spikeInstance = new Spike(this, args[0], args[1]); //Thows exception if args not match
				cellpartInstance = spikeInstance;
				break;
			case CARNIVORE_MOUTH:
				carnivoreMouthInstance = new CarnivoreMouth(this);
				cellpartInstance = carnivoreMouthInstance;
				break;
			case HERBIVORE_MOUTH:
				herbivoreMouthInstance = new HerbivoreMouth(this);
				cellpartInstance = herbivoreMouthInstance;
				break;
			case EYE:
				eyeInstance = new Eye(this);
				cellpartInstance = eyeInstance;
				break;
			default:
				return; //TODO
			}
		}
		
		if(partType == CellPartType.FLAGELLUM) {
			angle = -angle;
		}
		
		cellpartInstance.addPart(new CellPartHolder(x, y, angle, isPaired));
		if(cellpartInstance instanceof CellPartHitable) {
			calculateNormalHitboxAndCellWidth();
		}
	}
//Cell part cell body -----------------------------------------------------------------------------------------

//Update logic ------------------------------------------------------------------------------------------------
	private void updateConstant() {
		wave_lenght = (100 * BONEGAP);
		k_const = 2.0d * Math.PI / wave_lenght;
		w_omega = 2.0d * Math.PI * frequency;
	}
	
	@Override
	public void update() {
		if(!isAlive()) {
			return;
		}
		super.update();
		deltaTimeStack += CellEvolutionManager.getDeltaTime();
		if(healthBarTimer > 0) {
			healthBarTimer -= (0.6d*CellEvolutionManager.getDeltaTime());
			if(healthBarTimer < 0) {
				healthBarTimer = 0.0d;
			}
		};
		if(soundCooldownTimer > 0) {
			soundCooldownTimer -= CellEvolutionManager.getDeltaTime();
		}
		if(Math.abs(turningRate) < 0.1d) {
			this.turningRate = 0;
		} else {
			angle += this.turningRate;
			if (angle >= 360) {
				angle -= 360;
			} else if(angle < 0) {
				angle += 360;
			}
		}
		if(flagellumInstance != null) {
			flagellumInstance.update();
		}
		if(spikeInstance != null) {
			spikeInstance.update();
		}
		if(carnivoreMouthInstance != null) {
			carnivoreMouthInstance.update();
		}
		if(herbivoreMouthInstance != null) {
			herbivoreMouthInstance.update();
		}
		if(animationstate == AnimationState.FAST_FORWARDING) {
			moveForward(true);
		} else if(animationstate == AnimationState.FORWARDING) {
			moveForward(false);
		}
		//updateConstant();
	}
	
	public void moveForward(boolean isFast) {
		if(isFast) {
			this.applyForce(new Point2D(Math.cos(Math.toRadians(getAngle()-90))*swimRushAcceleration, Math.sin(Math.toRadians(getAngle()-90))*swimRushAcceleration));
		} else {
			this.applyForce(new Point2D(Math.cos(Math.toRadians(getAngle()-90))*swimAcceleration, Math.sin(Math.toRadians(getAngle()-90))*swimAcceleration));
		}
	}
	
	public void setColor(Color strokeColor, Color bodyBaseColor) {
		this.strokeColor = strokeColor;
//		Stop[] stops = new Stop[] { new Stop(0.5, bodyBaseColor), new Stop(1, bodyGlowColor)};
		this.bodyColor = bodyBaseColor;
//		new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, stops);
	}
//Update logic ------------------------------------------------------------------------------------------------

//Geometry and drawing part -----------------------------------------------------------------------------------
	public Affine getAffineTranformation(boolean isReverse) {
		if(isReverse) {
			Affine affine = new Affine(new Rotate(-this.angle, this.position.getX(), this.position.getY()));
			affine.prependTranslation(0, (cellBody.length/2.0));
			affine.appendScale(1/scale, 1/scale, this.position);
			return affine;
		} else {
			Affine affine = new Affine(new Rotate(this.angle, this.position.getX(), this.position.getY()));
			affine.prependScale(scale, scale, this.position);
			affine.appendTranslation(0, -(cellBody.length/2.0));
			return affine;
		}
	}
	
	/**
	 * Create path for drawing this will not fill the part!
	 * @return cellXShift - use y/boneGap as index
	 */
	private double[] drawBody(GraphicsContext gc) {
		//Drawing main body
		gc.beginPath();
		double yi = position.getY();
		double xi = position.getX();
		gc.moveTo(xi, yi);
		double[] cellXShift = new double[cellBody.length];
		for(int i = 0; i < cellBody.length; i++) {
			yi += BONEGAP;
			double xShift = (curvedFactor * Math.sin((k_const * yi) - (w_omega * deltaTimeStack)));
			cellXShift[i] = xShift;
			gc.lineTo(xi + xShift + cellBody[i], yi);
		}
		for(int i = this.cellBody.length-1; i >= 0; i--) {
			gc.lineTo(xi - cellBody[i] + cellXShift[i], yi);
			yi -= BONEGAP;
		}
		gc.closePath();
		return cellXShift;
	}
	
	protected void drawHealthBar(GraphicsContext gc) {
		if(healthBarTimer <= 0.0d) {
			return;
		}
		double healthBarWidth = 50.0d*scale;
		double x = this.getPosition().getX() - healthBarWidth;
		double y = this.position.getY() - (70.0d*scale);
		
		double opacity = healthBarTimer;
		if(opacity > 1)
			opacity = 1.0d;
		//System.out.println("Drawing health bar");
		gc.setFill(Color.grayRgb(100, opacity*0.7));
		gc.fillRoundRect(x, y, healthBarWidth*2, 8, 7, 7);
		int blue = (int)(healthPoints/maxHealth*255);
		if(blue < 0)
			blue = 0;
		gc.setFill(Color.rgb((255 - blue), blue, 0, opacity));
		gc.fillRoundRect(x, y, healthBarWidth*2*(healthPoints/maxHealth), 8, 7, 7);
		gc.setLineWidth(1);
		gc.setStroke(Color.grayRgb(15, opacity));
		gc.strokeRoundRect(x, y, healthBarWidth*2, 8, 7, 7);
	}
	
	@Override
	public void draw(GraphicsContext gc) {
//		gc.setGlobalAlpha(1.0d);
		gc.transform(this.getAffineTranformation(false));
		
		//Draw the body
		double[] cellXShift = drawBody(gc);
		
		InnerShadow innerglow = new InnerShadow(30, this.strokeColor);
//		innerglow.setInput(INNER_SHADOW_EFFECT);
		innerglow.setChoke(0.5);
		gc.setEffect(innerglow);
		gc.setFill(this.bodyColor);
		gc.fill();
		
		gc.setEffect(null);
		
		//Drawing cell part

		if(spikeInstance != null) {
			spikeInstance.draw(gc, cellXShift);
		}
		if(carnivoreMouthInstance != null) {
			carnivoreMouthInstance.draw(gc, cellXShift);
		}
		if(herbivoreMouthInstance != null) {
			herbivoreMouthInstance.draw(gc, cellXShift);
		}
		
		drawBody(gc);
		//InnerShadow innerglow = new InnerShadow(30, this.strokeColor);
//		innerglow.setInput(INNER_SHADOW_EFFECT);
		//innerglow.setChoke(0.5);
		gc.setEffect(innerglow);
		gc.setFill(this.bodyColor);
		gc.setGlobalAlpha(0.6);
		gc.fill();
		gc.setGlobalAlpha(1.0);
		gc.setEffect(null);
		
		if(flagellumInstance != null) {
			flagellumInstance.draw(gc, cellXShift);
		};
		if(eyeInstance != null) {
			eyeInstance.draw(gc, cellXShift);
		}

		gc.transform(this.getAffineTranformation(true));
		
		drawHealthBar(gc);
	}
	
//Geometry and drawing part -----------------------------------------------------------------------------------

//Static ------------------------------------------------------------------------------------------------------
	
	public static void drawStill(MicrobeEntity entity, GraphicsContext gc) {
//		gc.setGlobalAlpha(1.0d);
		gc.transform(entity.getAffineTranformation(false));
		
		//Draw the body
		gc.beginPath();
		double yi = entity.position.getY();
		double xi = entity.position.getX();
		gc.moveTo(xi, yi);
//		System.out.println("-----Move to " + xi + ", " + yi);
		double[] cellXShift = new double[(int) (entity.cellBody.length*BONEGAP)];
		Arrays.fill(cellXShift, 0);
		for(int i = 0; i < entity.cellBody.length; i++) {
			gc.lineTo(xi + entity.cellBody[i], yi);	
//			System.out.println(i + " line to " + (xi + entity.cellBody[i]) + ", " + yi + "\t width = " + entity.cellBody[i]);
			yi += BONEGAP;
		}
		for(int i = entity.cellBody.length-1; i >= 0; i--) {
			yi -= BONEGAP;
			gc.lineTo(xi - entity.cellBody[i], yi);
//			System.out.println(i + "_2 line to " + (xi - entity.cellBody[i]) + ", " + yi + "\t width = " + entity.cellBody[i]);
		}
		gc.closePath();
		InnerShadow innerglow = new InnerShadow(30, entity.strokeColor);
		innerglow.setChoke(0.5);
		gc.setEffect(innerglow);
		gc.setFill(entity.bodyColor);
		gc.fill();
		
		gc.setEffect(null);
		
		//Drawing cell part
		if(entity.spikeInstance != null) {
			entity.spikeInstance.draw(gc, cellXShift);
		}
		if(entity.carnivoreMouthInstance != null) {
			entity.carnivoreMouthInstance.draw(gc, cellXShift);
		}
		if(entity.herbivoreMouthInstance != null) {
			entity.herbivoreMouthInstance.draw(gc, cellXShift);
		}
		
		//DrawBoy again
		gc.beginPath();
		yi = entity.position.getY();
		xi = entity.position.getX();
		gc.moveTo(xi, yi);
		for(int i = 0; i < entity.cellBody.length; i++) {
			gc.lineTo(xi + entity.cellBody[i], yi);
			yi += BONEGAP;
		}
		for(int i = entity.cellBody.length-1; i >= 0; i--) {
			yi -= BONEGAP;
			gc.lineTo(xi - entity.cellBody[i], yi);
		}
		gc.closePath();
		
		//InnerShadow innerglow = new InnerShadow(30, this.strokeColor);
//		innerglow.setInput(INNER_SHADOW_EFFECT);
		//innerglow.setChoke(0.5);
		gc.setEffect(innerglow);
		gc.setFill(entity.bodyColor);
		gc.setGlobalAlpha(0.6);
		gc.fill();
		gc.setGlobalAlpha(1.0);
		gc.setEffect(null);
		
		if(entity.flagellumInstance != null) {
			entity.flagellumInstance.draw(gc, cellXShift);
		};
		if(entity.eyeInstance != null) {
			entity.eyeInstance.draw(gc, cellXShift);
		}

		gc.transform(entity.getAffineTranformation(true));
	}
	
	/**
	 * Assign instance from lhs to rhs, or if lhs is null will initialize
	 * @param lhs
	 * @param rhs
	 */
	public static void parseCellPartInstance(MicrobeEntity lhs, MicrobeEntity rhs) {
		if(lhs.flagellumInstance != null) {
			rhs.flagellumInstance = lhs.flagellumInstance;
			rhs.flagellumInstance.changeMicrobeOwner(rhs);
		} else {
			rhs.flagellumInstance = new Flagellum(rhs);
		}
		if(lhs.carnivoreMouthInstance != null) {
			rhs.carnivoreMouthInstance = lhs.carnivoreMouthInstance;
			rhs.carnivoreMouthInstance.changeMicrobeOwner(rhs);
		} else {
			rhs.carnivoreMouthInstance = new CarnivoreMouth(rhs);
		}
		if(lhs.herbivoreMouthInstance != null) {
			rhs.herbivoreMouthInstance = lhs.herbivoreMouthInstance;
			rhs.herbivoreMouthInstance.changeMicrobeOwner(rhs);
		} else {
			rhs.herbivoreMouthInstance = new HerbivoreMouth(rhs);
		}
		if(lhs.spikeInstance != null) {
			rhs.spikeInstance = lhs.spikeInstance;
			rhs.spikeInstance.changeMicrobeOwner(rhs);
		} else {
			rhs.spikeInstance = new Spike(rhs);
		}
		if(lhs.eyeInstance != null) {
			rhs.eyeInstance = lhs.eyeInstance;
			rhs.eyeInstance.changeMicrobeOwner(rhs);
		} else {
			rhs.eyeInstance = new Eye(rhs);
		}
	}
	
	/**
	 * Assign instance from lhs to rhs, or if lhs is null will initialize
	 * @param microbe
	 * @param player
	 */
	public static void parseEntity(MicrobeEntity lhs, MicrobeEntity rhs) {
		parseCellPartInstance(lhs, rhs);
		rhs.bodyColor = lhs.bodyColor;
		rhs.bone = lhs.bone;
		rhs.cellBody = lhs.cellBody;
		rhs.damage = lhs.damage;
		rhs.headExtends = lhs.headExtends;
		rhs.healthPoints = lhs.healthPoints;
		rhs.lenght = lhs.lenght;
		rhs.mass = lhs.mass;
		rhs.maxHealth = lhs.maxHealth;
		rhs.maxSwimSpeed = lhs.maxSwimSpeed;
		rhs.maxTurnRate = lhs.maxTurnRate;
		rhs.normalHitbox = lhs.normalHitbox;
		rhs.strokeColor = lhs.strokeColor;
		rhs.swimAcceleration = lhs.swimAcceleration;
		rhs.swimRushAcceleration = lhs.swimRushAcceleration;
		rhs.turningRate = lhs.turningRate;
		rhs.turningStep = lhs.turningStep;
		rhs.width = lhs.width;
	}
	
	private static void updateCellPartPosition(MicrobeEntity microbe, CellPart instance) {
		double maxY = microbe.cellBody.length*BONEGAP;
		for(CellPartHolder part : instance.getPartList()) {
			if(part.y > maxY) {
				part.y = maxY;
			}
			double maxX = microbe.cellBody[(int) (part.y/BONEGAP)];
			if(part.x > maxX) {
				part.x = maxX;
			}
		}
	}
	
	public static void updateCellPartPosition(MicrobeEntity microbe) {
		if(microbe.flagellumInstance != null) {
			updateCellPartPosition(microbe, microbe.flagellumInstance);
		};
		if(microbe.carnivoreMouthInstance != null) {
			updateCellPartPosition(microbe, microbe.carnivoreMouthInstance);
		};
		if(microbe.herbivoreMouthInstance != null) {
			updateCellPartPosition(microbe, microbe.herbivoreMouthInstance);
		};
		if(microbe.spikeInstance != null) {
			updateCellPartPosition(microbe, microbe.spikeInstance);
		};
		if(microbe.eyeInstance != null) {
			updateCellPartPosition(microbe, microbe.eyeInstance);
		};
	}
	
	public static void resetCellPart(MicrobeEntity microbe, CellPartType partType) {
		microbe.getCellPartFromType(partType).reset();
	}
	
	public static void changeBodyColor(MicrobeEntity microbe, Color color1, Color color2) {
		microbe.setColor(color1, color2);
	}
	
	public static void editBone(MicrobeEntity microbe, int boneIndex, double changeAmount) {
		microbe.bone.set(boneIndex, microbe.bone.get(boneIndex) + changeAmount);
	}
	
	public static void changeColor(MicrobeEntity microbe, CellPartType partType, Color color1, Color color2) {
		microbe.getCellPartFromType(partType).setColor(color1, color2);
	}
	
	public static boolean isCellPartInAppropriatePosition(MicrobeEntity microbe, CellPartType partType) {
		for(CellPartHolder part : microbe.getCellPartFromType(partType).getPartList()) {
			if(part.x > microbe.cellBody[(int) (part.y/BONEGAP)]) {
				return false;
			};
			if(part.y > microbe.cellBody.length*BONEGAP) {
				return false;
			};
		}
		return true;
	}
	
	public static boolean isCellPartInAppropriatePosition(MicrobeEntity microbe, double x, double y) {
		if(y < 0) {
			return false;
		}
		if(y > microbe.cellBody.length*BONEGAP) {
//			System.out.println("Too Y = " + y);
			return false;
		};
		if(x > microbe.cellBody[(int) (y/BONEGAP)]) {
//			System.out.println("Too X = " + x);
			return false;
		};
		return true;
	}
	
	public static void calculateAttribute(MicrobeEntity entity) {
		double maxSpeed = ATTRIBUTE_SPEED_PER_FLAGELLUM;
		if(entity.flagellumInstance != null) {
			maxSpeed += entity.flagellumInstance.getPartList().size()*ATTRIBUTE_SPEED_PER_FLAGELLUM;
		};
		double maxDamage = 0;
		if(entity.spikeInstance != null) {
			maxDamage += entity.spikeInstance.getPartList().size()*ATTRIBUTE_DAMAGE_PER_PART;
		};
		if(entity.carnivoreMouthInstance != null) {
			maxDamage += entity.carnivoreMouthInstance.getPartList().size()*ATTRIBUTE_DAMAGE_PER_PART;
		}
		entity.maxSwimSpeed = maxSpeed;
		entity.damage = maxDamage;
	}
		
}