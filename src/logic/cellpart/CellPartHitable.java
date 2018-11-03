package logic.cellpart;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import logic.entity.MicrobeEntity;
import logic.entity.WeedEntity;
import sharedobject.CellEvolutionManager;
import sharedobject.CircleCollidable;
import sharedobject.Collidable;
import utility.CollisionUtility;

public abstract class CellPartHitable extends CellPart {
	
	protected boolean isAnimating = false;
	protected double animationDuration = 0.35d;
	protected double animationTime = 0.0d;
//	protected CellPartHolder colliding = null;
	
	public CellPartHitable(MicrobeEntity microbe) {
		super(microbe);
	}
	
	public abstract ArrayList<Point2D> getHitBox(Affine baseTransformation, double angle, double x, double y);
	
	protected CellPartCollisionResult checkColisionWithMicrobe(MicrobeEntity other) {
		MicrobeEntity microbeOther = (MicrobeEntity) other;
		if(!checkBodyCollision(microbeOther)) {
			//System.out.println("not Collided");
			return CellPartCollisionResult.NOT_COLLIDED;
		} else {
			//BODY is collide, NOW check if same part hit if it does then we'll cancel damage
			CellPartHitable cpInstance = (CellPartHitable) microbeOther.getCellPartFromType(getCellPartType());
			if(cpInstance == null) {
				return CellPartCollisionResult.COLLIDED_WITH_BODY;
			}
			if(checkPartCollision(cpInstance.partList, microbeOther)) {
				return CellPartCollisionResult.COLLIDED_WITH_SAMETYPE;
			}
			return CellPartCollisionResult.COLLIDED_WITH_BODY;
		}
	}
	
	/**
	 * To be override by mouth, the other just simply push it
	 * @param other
	 * @return
	 */
	protected CellPartCollisionResult checkColisionWithWeedEntity(WeedEntity other) {
		return checkColisionWithCircle((CircleCollidable) other);
	}
	
	protected CellPartCollisionResult checkColisionWithCircle(CircleCollidable other) {
		for(CellPartHolder hitableCellpart : partList) {
			ArrayList<Point2D> partHitbox = getHitBox(microbe.getAffineTranformation(false), hitableCellpart.angle, hitableCellpart.x + microbe.getPosition().getX(), hitableCellpart.y + microbe.getPosition().getY());
			if(CollisionUtility.isPolygonCollideWithCircle(partHitbox, other.getCenter(), other.getRadius())){
				return CellPartCollisionResult.COLLIDED_TO_PUSH;
			}
			if(hitableCellpart.isPaired) {
				partHitbox = getHitBox(microbe.getAffineTranformation(false), -hitableCellpart.angle, -hitableCellpart.x + microbe.getPosition().getX(), hitableCellpart.y + microbe.getPosition().getY());
				if(CollisionUtility.isPolygonCollideWithCircle(partHitbox, other.getCenter(), other.getRadius())){
					return CellPartCollisionResult.COLLIDED_TO_PUSH;
				}
			}
		}
		return CellPartCollisionResult.NOT_COLLIDED;
	}
	
	public CellPartCollisionResult isCellPartHit(Collidable other) {
		if(other instanceof MicrobeEntity) {//POSSIBLE RESULT are NOT_COLLIDE, COLLIDED_WITH_BODY, COLLIDED_WITH_SAMETYPE
			return checkColisionWithMicrobe((MicrobeEntity) other);
			
		} else if (other instanceof WeedEntity) { //the other living entity "Food, weed " 
			//POSSIBLE RESULT are NOT_COLLIDE, COLLIDED_TO_CONSUME
			return checkColisionWithWeedEntity((WeedEntity) other);
		} else if (other instanceof CircleCollidable) {//POSSIBLE RESULT are NOT_COLLIDE, COLLIDED_TO_CONSUME
			return checkColisionWithCircle((CircleCollidable) other);
		}
		return CellPartCollisionResult.NOT_COLLIDED;
	}
	
	private boolean checkPartCollision(ArrayList<CellPartHolder> otherPartList, MicrobeEntity other) {
//		Affine micrboeAffine = microbe.getAffineTranformation(false);
//		Affine otherAffine = other.getAffineTranformation(false);
		for(CellPartHolder hitableCellpart : partList) {
			ArrayList<Point2D> partHitbox = getHitBox(microbe.getAffineTranformation(false), hitableCellpart.angle, hitableCellpart.x + microbe.getPosition().getX(), hitableCellpart.y + microbe.getPosition().getY());
			for(CellPartHolder otherPart : otherPartList) {
				ArrayList<Point2D> otherPartHitbox = getHitBox(other.getAffineTranformation(false), otherPart.angle, otherPart.x + other.getPosition().getX(), otherPart.y + other.getPosition().getY());
				if(CollisionUtility.isPolygonCollide(partHitbox, otherPartHitbox)) {
					return true;
				};
				ArrayList<Point2D> mirrored_partHitbox = getHitBox(microbe.getAffineTranformation(false), -hitableCellpart.angle, -hitableCellpart.x + microbe.getPosition().getX(), hitableCellpart.y + microbe.getPosition().getY());
				if(CollisionUtility.isPolygonCollide(mirrored_partHitbox, otherPartHitbox)) {
					return true;
				};
				//MIRRORED Other
				otherPartHitbox = getHitBox(other.getAffineTranformation(false), -otherPart.angle, -otherPart.x + other.getPosition().getX(), -otherPart.y + other.getPosition().getY());
				if(CollisionUtility.isPolygonCollide(partHitbox, otherPartHitbox)) {
					return true;
				};
				if(CollisionUtility.isPolygonCollide(mirrored_partHitbox, otherPartHitbox)) {
					return true;
				};
			}
		}
		return false;
	}
	
	protected boolean checkBodyCollision(MicrobeEntity other) {
		ArrayList<Point2D> otherBody = other.getBodyHitBox();
		//Affine micrboeAffine = microbe.getAffineTranformation(false);
		for(CellPartHolder hitableCellpart : partList) {
//			Point2D pivot = micrboeAffine.transform(new Point2D(hitableCellpart.x + microbe.getPosition().getX(), hitableCellpart.y + microbe.getPosition().getY()));
			ArrayList<Point2D> partHitbox = getHitBox(microbe.getAffineTranformation(false), hitableCellpart.angle, hitableCellpart.x + microbe.getPosition().getX(), hitableCellpart.y + microbe.getPosition().getY());
			if(CollisionUtility.isPolygonCollide(partHitbox, otherBody)) {
				return true;
			}
			if(hitableCellpart.isPaired) {
//				pivot = micrboeAffine.transform(new Point2D(-hitableCellpart.x + microbe.getPosition().getX(), -hitableCellpart.y + microbe.getPosition().getY()));
				partHitbox = getHitBox(microbe.getAffineTranformation(false), -hitableCellpart.angle, microbe.getPosition().getX() - (hitableCellpart.x), microbe.getPosition().getY() + hitableCellpart.y);
				if(CollisionUtility.isPolygonCollide(partHitbox, otherBody)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void update() {
//		System.out.println("update() " + this.getClass().getSimpleName());
		if(isAnimating) {
//			System.out.println("update()... animating " + this.getClass().getSimpleName());
			if(animationTime < animationDuration) {
				animationTime += CellEvolutionManager.getDeltaTime();
			} else {
				animationTime = 0.0d;
				isAnimating = false;
			}
		}
	}
	
	
	public enum CellPartCollisionResult{
		NOT_COLLIDED, COLLIDED_WITH_BODY, COLLIDED_WITH_SAMETYPE, COLLIDED_WITH_BUBBLE, COLLIDED_WITH_CONSUMEABLE, COLLIDED_TO_PUSH;
	}
}
