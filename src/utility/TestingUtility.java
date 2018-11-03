package utility;

import java.util.ArrayList;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import logic.cellpart.CellPart.CellPartType;
import logic.entity.FoodEntity;
import logic.entity.MicrobeEntity;
import logic.entity.MicrobePlayer;
import logic.entity.FoodEntity.FoodType;

/**
 * @author Zen3515
 *
 */
public class TestingUtility {

	public static MicrobeEntity getSimpleMicrobe2() {
		//System.out.println("Creating SimpleMicrobe2*");
		MicrobeEntity microbe = new MicrobeEntity(new Point2D(500, 500), 100, 50);
		//microbe.setAnimtionTimer(System.nanoTime());
		double[] body = new double[50];
//		double maxWidth = 0;
		for(int i = 0; i < 50; i++) {
			double width = ((45) + (3.2d * i) - (0.064d * i * i));
			if(width < 30)
				width = 30;
//			if(width > maxWidth)
//				maxWidth = width;
			body[i] = width;
		}
		microbe.setCellBody(body);
		microbe.addCellPart(CellPartType.FLAGELLUM, 0, 98, 0, false);
		microbe.addCellPart(CellPartType.FLAGELLUM, 13, 98, 10, true);
		microbe.addCellPart(CellPartType.FLAGELLUM, 30, 98, 20, true);
//		microbe.addCellPart(CellPartType.CARNIVORE_MOUTH, 0, 0, 0, false);
		microbe.addCellPart(CellPartType.EYE, 40, 10, 0, true);
		microbe.addCellPart(CellPartType.CARNIVORE_MOUTH, 30, 0, 30, true);
		//microbe.setWidth(maxWidth*2.0);
		//microbe.setAngle(35);
		return microbe;
	}
	
	public static MicrobeEntity getSimpleMicrobe() {
		//System.out.println("Creating SimpleMicrobe");
		MicrobeEntity microbe = new MicrobeEntity(new Point2D(200, 200), 100, 100);
		
		double[] body = new double[100];
		
//		double maxWidth = 0;
		for(int i = -50; i < 50; i++) {
			double width = ((45) + (3.2d * (i)) - (0.064d * i * i));
			if(width < 30)
				width = 30;
//			if(width > maxWidth)
//				maxWidth = width;
			body[i+50] = width;
		}
		microbe.setCellBody(body);
		microbe.addCellPart(CellPartType.FLAGELLUM, 0, 170, 0, false);
		microbe.addCellPart(CellPartType.FLAGELLUM, 13, 170, 15, true);
		microbe.addCellPart(CellPartType.FLAGELLUM, 30, 170, 30, true);
		microbe.addCellPart(CellPartType.EYE, 27, 10, 0, true);
//		microbe.addCellPart(CellPartType.SPIKE, 15, 0, 45, true, 10, 110);
		microbe.addCellPart(CellPartType.HERBIVORE_MOUTH, 0, 0, 0, false);
		//microbe.setWidth(maxWidth*2.0);
		return microbe;
	}
	
	public static MicrobePlayer getMicrobePlayer() {
		//System.out.println("Creating SimpleMicrobe");
		MicrobePlayer microbe = new MicrobePlayer(new Point2D(500, 500), 100, 100);
		
		ArrayList<Double> bone = new ArrayList<Double>();
		bone.add(70.0d);
		bone.add(0.0d);
		bone.add(175.0d);
		
		microbe.setBone(bone);
		
		double[] body = new double[100];
		
//		double maxWidth = 0;
		for(int i = -50; i < 50; i++) {
			double width = ((45) + (3.2d * (i)) - (0.064d * i * i));
			if(width < 30)
				width = 30;
//			if(width > maxWidth)
//				maxWidth = width;
			body[i+50] = width;
		}
		microbe.setCellBody(body);
		microbe.addCellPart(CellPartType.FLAGELLUM, 0, 190, 0, false);
		microbe.addCellPart(CellPartType.FLAGELLUM, 20, 188, 15, true);
		microbe.addCellPart(CellPartType.FLAGELLUM, 40, 184, 30, true);
//		microbe.addCellPart(CellPartType.SPIKE, 15, 0, 0, true, 10, 120);
		microbe.addCellPart(CellPartType.CARNIVORE_MOUTH, 0, 0, 0, false);
		microbe.addCellPart(CellPartType.HERBIVORE_MOUTH, 85, 150, 90, true);
		microbe.addCellPart(CellPartType.SPIKE, 25, 25, 45, true, 10, 120);
		microbe.addCellPart(CellPartType.EYE, 25, 25, 45, true);
		//microbe.setWidth(maxWidth*2.0);
		return microbe;
	}
	
	public static FoodEntity getFoodEntity(FoodType type, double posX, double posY) {
		return new FoodEntity(new Point2D(posX, posY), 30, type);
	}
	
	public static void drawPoints(GraphicsContext gc, ArrayList<Point2D> points) {
		gc.setFill(Color.AQUA);
		gc.setStroke(Color.AQUA);
		gc.beginPath();
		gc.moveTo(points.get(0).getX(), points.get(0).getY());
		gc.fillOval(points.get(0).getX()-5, points.get(0).getY()-5, 10, 10);
		for(int i = 1; i < points.size(); i++) {
			Point2D point = points.get(i);
			gc.fillOval(point.getX()-5, point.getY()-5, 10, 10);
			gc.lineTo(point.getX(), point.getY());
		}
		gc.closePath();
		gc.stroke();
	}
	
	public static void drawBoundingBox(GraphicsContext gc, BoundingBox bound) {
		gc.strokeRect(bound.getMinX(), bound.getMinY(), bound.getWidth(), bound.getHeight());
	}
}
