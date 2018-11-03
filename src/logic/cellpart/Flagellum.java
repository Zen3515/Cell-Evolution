package logic.cellpart;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import logic.entity.MicrobeEntity;
import sharedobject.CellEvolutionManager;

public class Flagellum extends CellPart {

	private double frequency;
	private static final double curvedFactor = 10.0d;
	private static final double boneGap = 2.0d;
	private double[] radius;
	private double k_const;
	private double w_omega;
	private int tail_lenght;
	private boolean isReverse = false;
	private double deltaTimeStack = 0.0;
//	protected Stop[] stops;

	public Flagellum(MicrobeEntity microbe) {
		super(microbe);
		setColor(Color.BLUE, Color.AQUA);
		this.microbe = microbe;
		frequency = 1.0d;
		tail_lenght = 50;
		radius = new double[tail_lenght];
		k_const = 2 * Math.PI / (100 * boneGap);
		isReverse = false;
		updateConstant();
		double size = 10;
		for (int i = 0; i < tail_lenght; i++) {
			radius[i] = size;
			size -= 0.2 * (Math.pow(1.0000001, i));
		}
		//radius[48] = 0;
		//System.out.println("Flagellum initialized");
	}
	
	public void setColor(Color baseColor, Color glowColor) {
		Stop[] stops = new Stop[] { new Stop(0, glowColor), new Stop(1, baseColor)};
//		this.color = new RadialGradient(0, 0, 0.5, 0, 1, true, CycleMethod.NO_CYCLE, stops);
		this.color = new LinearGradient(0.5, 0, 0.5, 0.75, true, CycleMethod.NO_CYCLE, stops);
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
		updateConstant();
	}
	
	public double getFrequency() {
		return frequency;
	}

	public void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}

	private void updateConstant() {
		w_omega = 2 * Math.PI * frequency;
	}

	@Override
	public void draw(GraphicsContext gc, double posX, double posY) {
		gc.setEffect(INNER_SHADOW_EFFECT);
		double dampedhead = 0.0d;
		double[] xShift = new double[tail_lenght]; 
		double y = 0;
		for (int i = 0; i < tail_lenght; i++) {
			y += boneGap;
			double x;
			if(isReverse) {
				x = ((curvedFactor * Math.sin((k_const * y) + (w_omega * deltaTimeStack))) * (Math.pow(1.02, i))) * dampedhead;
			} else {
				x = ((curvedFactor * Math.sin((k_const * y) - (w_omega * deltaTimeStack))) * (Math.pow(1.02, i))) * dampedhead;
			}
			dampedhead += 0.02d;
			xShift[i] = x;
//			gc.setFill(Color.PURPLE);
//			gc.fillOval((x + posX) - radius[i], (y + posY) - radius[i], 2 * radius[i], 2 * radius[i]);
		}
		gc.beginPath();
		y = 0;
		gc.moveTo(posX + xShift[0] + radius[0], posY);
		for(int i = 1; i < tail_lenght; i++) {
			y += boneGap;
			gc.lineTo(posX + xShift[i] + radius[i], y + posY);
		}
		for(int i = tail_lenght-2; i >= 0; i--) {
			y -= boneGap;
			gc.lineTo(posX + xShift[i] - (radius[i]), y + posY);
		}
		gc.arcTo(posX + xShift[0] - radius[0], posY - radius[0], posX + xShift[0], posY - radius[0], radius[0]);
		gc.arcTo(posX + xShift[0] + radius[0], posY - radius[0], posX + xShift[0] + radius[0], posY, radius[0]);
		gc.closePath();
		gc.setFill(color);
		gc.fill();
		gc.setEffect(null);
		
//		gc.setFill(Color.YELLOW);
//		gc.fillRect(posX-2.5, posY-2.5, 5, 5);
	}
	
	public void update() {
		deltaTimeStack += CellEvolutionManager.getDeltaTime();
	}

	@Override
	public CellPartType getCellPartType() {
		return CellPartType.FLAGELLUM;
	}

}


//package logic.cellpart;
//
//import javafx.scene.canvas.GraphicsContext;
//import logic.MicrobeEntity;
//import sharedObject.CellEvolutionManager;
//
//public class Flagellum extends CellPart {
//
//	// TODO make use of state to transit between animation state
//
//	private double frequency;
//	private static final double curvedFactor = 10.0d;
//	private static final double boneGap = 2.0d;
//	private double[] radius;
//	private double k_const;
//	private double w_omega;
//	private int tail_lenght;
//	private boolean isReverse = false;
//	private double deltaTimeStack = 0.0;
//
//	public Flagellum(MicrobeEntity microbe) {
//		super(microbe);
//		this.microbe = microbe;
//		frequency = 1.0d;
//		tail_lenght = 50;
//		radius = new double[tail_lenght];
//		k_const = 2 * Math.PI / (100 * boneGap);
//		isReverse = false;
//		updateConstant();
//		double size = 10;
//		for (int i = 0; i < tail_lenght; i++) {
//			radius[i] = size;
//			size -= 0.2 * (Math.pow(1.0000001, i));
//		}
//		//System.out.println("Flagellum initialized");
//	}
//
//	public void setFrequency(double frequency) {
//		this.frequency = frequency;
//		updateConstant();
//	}
//	
//	public double getFrequency() {
//		return frequency;
//	}
//
//	public void setReverse(boolean isReverse) {
//		this.isReverse = isReverse;
//	}
//
//	private void updateConstant() {
//		w_omega = 2 * Math.PI * frequency;
//	}
//
//	/**
//	 * When draw set stroke and fill color and rotate FIRST!!!!!!!!
//	 */
//	protected void draw(GraphicsContext gc, double posX, double posY) {
//		double dampedhead = 0.0d;
//		for (int i = 1; i < tail_lenght; i++) {
//			double y = ((i * boneGap));
//			double x;
//			if(isReverse) {
//				x = ((curvedFactor * Math.sin((k_const * y) + (w_omega * deltaTimeStack))) * (Math.pow(1.02, i))) * dampedhead;
//			} else {
//				x = ((curvedFactor * Math.sin((k_const * y) - (w_omega * deltaTimeStack))) * (Math.pow(1.02, i))) * dampedhead;
//			}
//			dampedhead += 0.02d;
//			x += posX;
//			y += posY;
//			gc.fillOval(x - radius[i], y - radius[i], 2 * radius[i], 2 * radius[i]);
//			//System.out.println("Drawing bone " + i + " at x= " + x + ", y= " +y + ", time passed =" + timePass);
//		}
//	}
//	
//	public void update() {
//		deltaTimeStack += CellEvolutionManager.getDeltaTime();
//	}
//
//	@Override
//	public CellPartType getCellPartType() {
//		return CellPartType.FLAGELLUM;
//	}
//
//}
