package logic.cellpart;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import logic.entity.MicrobeEntity;

public class Eye extends CellPart {

	protected Color irisColor;
	protected Color fleshColor;
	protected Color eyeHoleColor;
	protected double eyeRadius;
	protected static final InnerShadow INNER_SHADOW_EFFECT = new InnerShadow(3, 0, 0, Color.rgb(0, 0, 0, 0.5));
	
	public Eye(MicrobeEntity microbe) {
		super(microbe);
		eyeRadius = 22d;
		setColor(Color.DEEPSKYBLUE, Color.WHITE, Color.BLACK);
	}
	
	
	public double getEyeRadius() {
		return eyeRadius;
	}


	public void setEyeRadius(double eyeRadius) {
		this.eyeRadius = eyeRadius;
	}

	public void setColor(Color irisColor, Color fleshColor, Color eyeHoleColor) {
		this.irisColor = irisColor;
		this.fleshColor = fleshColor;
		this.eyeHoleColor = eyeHoleColor;
	}

	@Override
	public CellPartType getCellPartType() {
		return CellPartType.EYE;
	}

	@Override
	public void draw(GraphicsContext gc, double posX, double posY) {
		gc.setEffect(INNER_SHADOW_EFFECT);
		gc.setFill(this.fleshColor);
		gc.fillOval(posX - eyeRadius, posY - eyeRadius, (2*eyeRadius), (2*eyeRadius));
		gc.setEffect(null);
		
		double tempEyeRadius = eyeRadius/2.0d;
		gc.setFill(irisColor);
		gc.fillOval(posX - tempEyeRadius, posY - tempEyeRadius, (2*tempEyeRadius), (2*tempEyeRadius));
		
		tempEyeRadius = eyeRadius/5.0d;
		gc.setFill(eyeHoleColor);
		gc.fillOval(posX - tempEyeRadius, posY - tempEyeRadius, (2*tempEyeRadius), (2*tempEyeRadius));
	}


	@Override
	public void setColor(Color color1, Color color2) {
		irisColor = color1;
		//eyeHoleColor = color2;
	}
	

}
