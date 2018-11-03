package logic.cellpart;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import logic.entity.MicrobeEntity;

public abstract class CellPart{
	
	protected MicrobeEntity microbe;
	protected ArrayList<CellPartHolder> partList;
	protected Paint color;
	protected static final InnerShadow INNER_SHADOW_EFFECT = new InnerShadow(3, 0, 0, Color.rgb(0, 0, 0, 0.5));
	
	public CellPart(MicrobeEntity microbe) {
		this.microbe = microbe;
		this.partList = new ArrayList<CellPartHolder>();
		this.color = Color.RED;
	}
	
	//public abstract void draw(GraphicsContext gc, double[] xShift);
	
	public abstract CellPartType getCellPartType();

	public void draw(GraphicsContext gc, double[] xShift) {
		
//		System.out.println("Drawing for " + microbe.getPosition());
		
		for(CellPartHolder cellPart : this.partList) {
			
			double pivotX = microbe.getPosition().getX() + cellPart.x + xShift[(int) (cellPart.y/MicrobeEntity.BONEGAP)];
			double pivotY = microbe.getPosition().getY() + cellPart.y;
			
//			System.out.println("pivotx= " + pivotX + ", pivotY = " + pivotY);
			
			if(cellPart.isPaired) { 
				
				//Draw symetrically
				gc.transform(new Affine(new Rotate(cellPart.angle, pivotX, pivotY)));
				draw(gc, pivotX, pivotY);
				gc.transform(new Affine(new Rotate(-cellPart.angle, pivotX, pivotY)));
				
				//Draw second one
				pivotX -= (cellPart.x*2);
				gc.transform(new Affine(new Rotate(-cellPart.angle, pivotX, pivotY)));
				draw(gc, pivotX, pivotY);
				gc.transform(new Affine(new Rotate(cellPart.angle, pivotX, pivotY)));
				
			} else { 
				//Draw only one
				gc.transform(new Affine(new Rotate(cellPart.angle, pivotX, pivotY)));
				draw(gc, pivotX, pivotY);
				gc.transform(new Affine(new Rotate(-cellPart.angle, pivotX, pivotY)));
			}
		}
	}
	
	public abstract void draw(GraphicsContext gc, double posX, double posY);
	
	public abstract void setColor(Color color1, Color color2);

	/**
	 * remove all cell part from the instance
	 */
	public void reset() {
		partList.clear();
	}
	
	public void changeMicrobeOwner(MicrobeEntity microbe) {
		this.microbe = microbe;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<CellPartHolder> getPartList() {
		return (ArrayList<CellPartHolder>) partList.clone();
	}
	
	public void addPart(CellPartHolder part) {
		partList.add(part);
	}
	
	public enum CellPartType{
		SPIKE, CARNIVORE_MOUTH, HERBIVORE_MOUTH, FLAGELLUM, EYE;
	};
	
	public static void drawStill(GraphicsContext gc, CellPartType partType, MicrobeEntity microbe, double x, double y, double angle) {
		gc.transform(new Affine(new Rotate(angle, x, y)));
		microbe.getCellPartFromType(partType).draw(gc, x, y);
		gc.transform(new Affine(new Rotate(-angle, x, y)));
	}
	
}
