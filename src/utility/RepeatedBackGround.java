package utility;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import sharedobject.CellEvolutionManager;
import sharedobject.GameScreenCanvas;

public class RepeatedBackGround {

	private double xPosition;
	private double yPosition;
	private final double width; 
	private final double height;

	public RepeatedBackGround(Image image, double x, double y) {
		setPostion(x, y);
		width = CellEvolutionManager.waterTexture.getWidth();
		height = CellEvolutionManager.waterTexture.getHeight();
//		System.out.println("RepeatedBackGround: w = " + width + ", h = " + height);
	}
	
	public void update(double x, double y) {
		moveBy(x, y);
	}

	public void draw(GraphicsContext gc) {
		double y = yPosition;
		while (y < GameScreenCanvas.getHeight()) {
			double x = xPosition;
			while (x < GameScreenCanvas.getWidth()) {
				gc.drawImage(CellEvolutionManager.waterTexture, x, y);
//				gc.strokeRect(x, y, width, height);
				x += width;
			}
			y += height;
		}

	}

	public void shiftCenter(Point2D velocity) {
		update(velocity.getX(), velocity.getY());
	}

	private void moveToPreferPosition() {
		if (xPosition > 0) {
			while (xPosition > 0) {
				xPosition -= width;
			}
		} else {
			while (xPosition + width < 0) {
				xPosition += width;
			}
		};
		if (yPosition > 0) {
			while (yPosition > 0) {
				yPosition -= height;
			}
		} else {
			while (yPosition + height < 0) {
				yPosition += height;
			}
		};
	}

	public void setPostion(double x, double y) {
		this.xPosition = x;
		this.yPosition = y;
		moveToPreferPosition();
	}

	public void moveBy(double x, double y) {
		setPostion(xPosition + x, yPosition + y);
	}

}




//package utility;
//
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//
//import com.jhlabs.image.GrayscaleFilter;
//import com.jhlabs.image.SwimFilter;
//import com.jhlabs.image.WaterFilter;
//import com.sun.javafx.iio.ImageStorage.ImageType;
//
//import javafx.embed.swing.SwingFXUtils;
//import javafx.geometry.Point2D;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.image.Image;
//import javafx.scene.image.WritableImage;
//import sharedObject.CellEvolutionManager;
//
//public class RepeatedBackGround {
//
////	private Image image;
//	private double xPosition;
//	private double yPosition;
//	SwimFilter underWater;
////	BufferedImage bufferImage;
//	private final double width; 
//	private final double height;
//	private double time = 0.0d;
//
//	public RepeatedBackGround(Image image, double x, double y) {
////		this.image = image;
//		setPostion(x, y);
//		underWater = new SwimFilter();
//		underWater.setTurbulence(1);
//		width = CellEvolutionManager.waterTexture.getWidth();
//		height = CellEvolutionManager.waterTexture.getHeight();
//		System.out.println("RepeatedBackGround: w = " + width + ", h = " + height);
//	}
//	
//	public void update(double x, double y) {
//		moveBy(x, y);
//		time += CellEvolutionManager.getDeltaTime();
//		underWater.setTime((float) time);
////		Graphics graphic = bufferImage.getGraphics();
////		y = yPosition;
////		while (y < GameScreenCanvas.getHeight()) {
////			x = xPosition;
////			while (x < GameScreenCanvas.getWidth()) {
////				graphic.drawImage(CellEvolutionManager.waterTexture2, (int) Math.round(x), (int)Math.round(y), null);
////				x += width;
////			}
////			y += height;
////		}
//	}
//
//	public void draw(GraphicsContext gc) {
//		double y = yPosition;
//		while (y < GameScreenCanvas.getHeight()) {
//			double x = xPosition;
//			while (x < GameScreenCanvas.getWidth()) {
//				gc.drawImage(CellEvolutionManager.waterTexture, x, y);
////				gc.strokeRect(x, y, height, image.getHeight());
//				x += width;
//			}
//			y += height;
//		}
//		
//		//gc.clearRect(0, 0, GameScreenCanvas.getWidth(), GameScreenCanvas.getHeight());
//		
//		WritableImage wi = new WritableImage((int)gc.getCanvas().getWidth(), (int)gc.getCanvas().getHeight());
//		gc.getCanvas().snapshot(null, wi);
////		
//		BufferedImage bi = SwingFXUtils.fromFXImage((Image)wi, null);
//		//		
////		BufferedImage outputBI = underWater.createCompatibleDestImage(bi, null);
//		BufferedImage outputBI = new BufferedImage((int)GameScreenCanvas.getWidth(), (int)GameScreenCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
////		outputBI = new GrayscaleFilter().filter(bi, outputBI);
////		
//		SwingFXUtils.toFXImage(bi, (WritableImage)wi);
////		
//		gc.drawImage(wi, 0, 0);
//
//	}
//
//	public void shiftCenter(Point2D velocity) {
//		//setPostion(xPosition + velocity.getX(), yPosition + velocity.getY());
////		bufferImage = new BufferedImage((int)GameScreenCanvas.getWidth(), (int)GameScreenCanvas.getHeight(), BufferedImage.TYPE_INT_RGB);
//		update(velocity.getX(), velocity.getY());
//		// moveToPreferPosition();
//	}
//
//	private void moveToPreferPosition() {
//		if (xPosition > 0) {
//			while (xPosition > 0) {
//				xPosition -= width;
//			}
//		} else {
//			while (xPosition + width < 0) {
//				xPosition += width;
//			}
//		};
//		if (yPosition > 0) {
//			while (yPosition > 0) {
//				yPosition -= height;
//			}
//		} else {
//			while (yPosition + height < 0) {
//				yPosition += height;
//			}
//		};
//	}
//
//	public void setPostion(double x, double y) {
//		this.xPosition = x;
//		this.yPosition = y;
//		moveToPreferPosition();
//	}
//
//	public void moveBy(double x, double y) {
//		setPostion(xPosition + x, yPosition + y);
//	}
//
//}
