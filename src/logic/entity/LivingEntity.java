package logic.entity;

import javafx.geometry.Point2D;
import sharedobject.CellEvolutionManager;
import sharedobject.Collidable;

public abstract class LivingEntity extends Entity implements Collidable{

	public static final double DEFAULT_DAMAGE = 30;
	public static final double IMORTAL_DURATION = 1.5d;
	public static final double MAX_FOOD_HEALTH = 30;
	
	protected double healthPoints = 0.0d;
	protected double maxHealth = 30;
	protected double damageCooldownTimer = 0.0;
	protected double damage = DEFAULT_DAMAGE;
	
	private MicrobeEntity attacker;
	
	public LivingEntity(Point2D position, double maxHealth) {
		super(position);
		this.maxHealth = maxHealth;
		healthPoints = maxHealth;
	}
	
	public double getHealth() {
		return this.healthPoints;
	}
	
	public boolean heal(double amount) {
		if(healthPoints < maxHealth) {
			this.setHealth(healthPoints + amount);
			return true;
		}
		return false;
	}
	
	public void setHealth(double health) {
		if(health > maxHealth)
			health = maxHealth;
		this.healthPoints = health;
	}
	
	public MicrobeEntity getAttacker() {
		return attacker;
	}

	public void setAttacker(MicrobeEntity attacker) {
		this.attacker = attacker;
	}

	public void decreaseHealthPoint(double dmg, MicrobeEntity attacker) {
		if(isImortal()) {
			return;
		}
		//Imortal for specifictime
		healthPoints -= dmg;
		damageCooldownTimer = IMORTAL_DURATION;
//		System.out.println("Taking damage");
		this.attacker = attacker;
		if(this instanceof MicrobeEntity) {
			((MicrobeEntity)this).showHealthbar();
		}
		
		if(healthPoints <= 0.0d) {
			if(this instanceof MicrobeEntity) {
				((MicrobeEntity)this).onDeath();
			}
		}
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	
	public boolean isImortal() {
		return damageCooldownTimer > 0;
	}

	public boolean isAlive() {
		return this.healthPoints > 0;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	@Override
	public void update() {
		super.update();
		damageCooldownTimer -= CellEvolutionManager.getDeltaTime();
		if(damageCooldownTimer < 0)
			damageCooldownTimer = 0;
	}
}
