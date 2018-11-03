package logic.cellpart;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Affine;
import logic.entity.BubbleEntity;
import logic.entity.FoodEntity;
import logic.entity.MicrobeEntity;
import sharedobject.CellEvolutionManager;
import sharedobject.CircleCollidable;

public class Spike extends CellPartHitable{
	
	private double width, lenght;
	
	protected Stop[] stops;
	protected Color tipColor;

	/**
	 * 
	 * @param width - half width
	 * @param lenght
	 */
	public Spike(MicrobeEntity microbe, double width, double lenght) {
		super(microbe);
		this.width = width;
		this.lenght = lenght;
		setColor(Color.AQUA, Color.BLUE);
	}
	
	public void setColor(Color dipColor, Color tipColor) {
		this.tipColor = tipColor;
		stops = new Stop[] { new Stop(0, dipColor), new Stop(0.095, Color.TRANSPARENT)};
		this.color = new RadialGradient(0, 0, 0.5, 0.95, 8, true, CycleMethod.NO_CYCLE, stops);
	}
	
	public Spike(MicrobeEntity microbe) {
		this(microbe, 10, 160);
	}
	
	@Override
	public ArrayList<Point2D> getHitBox(Affine baseTransformation, double angle, double x, double y){
		baseTransformation.appendRotation(angle, x, y);
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		result.add(baseTransformation.transform(new Point2D(x-width, y)));
		result.add(baseTransformation.transform(new Point2D(x, y-lenght)));
		result.add(baseTransformation.transform(new Point2D(x+width, y)));
		return result;
	}


	@Override
	public void draw(GraphicsContext gc, double x, double y) {
		double extendPart = (0.5*lenght);
		if(animationTime < animationDuration/2) {
			extendPart *= (animationTime/animationDuration*2);
		} else {
			extendPart *= (1 - (animationTime/animationDuration));
		}
		gc.beginPath();
		gc.moveTo(x-width, y);
		gc.quadraticCurveTo(x-width, y-(lenght*0.25d), x, y-lenght-extendPart);
		gc.quadraticCurveTo(x+width, y-(lenght*0.25d), x+width, y);
		gc.arcTo(x + width, y + width, x, y + width, width);
		gc.arcTo(x - width, y + width, x - width, y, width);
//		gc.quadraticCurveTo(x, y+width+width, x-width, y);
		gc.closePath();
		gc.setEffect(INNER_SHADOW_EFFECT);
		gc.setFill(tipColor);
		gc.fill();
		gc.setEffect(null);
		gc.setFill(color);
		gc.fill();
//		gc.setFill(Color.AQUA);
//		gc.fillRect(x-3, y-3, 6, 6);
	}

	public void playSpikeHitAnimation() {
		if(isAnimating == false) {
			CellEvolutionManager.spike_Hit_Body.play(15);
		}
		isAnimating = true;
	}
	
	public void playSpikeDrawAnimation() {
		if(isAnimating == false) {
			CellEvolutionManager.spike_Draw.play(15);
		}
		isAnimating = true;
	}

	@Override
	public CellPartType getCellPartType() {
		return CellPartType.SPIKE;
	}
	
	@Override
	protected CellPartCollisionResult checkColisionWithCircle(CircleCollidable other) {
		CellPartCollisionResult result = super.checkColisionWithCircle(other);
		if(other instanceof FoodEntity) {
			return CellPartCollisionResult.NOT_COLLIDED;
		}
		if(result == CellPartCollisionResult.COLLIDED_TO_PUSH) {
			if(other instanceof BubbleEntity) {
				return CellPartCollisionResult.COLLIDED_WITH_BUBBLE;
			}
		}
		return result;
	}
}
