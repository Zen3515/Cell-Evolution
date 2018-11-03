package utility;

import javafx.geometry.Point2D;

public class Force{

	private double x;
	private double y;
	private double timeLeft;
	
	public Force(double x, double y, double timeLeft) {
		super();
		this.x = x;
		this.y = y;
		this.timeLeft = timeLeft;
	}
	
	public void div(double mass) {
		x /= mass;
		y /= mass;
	}
	
	public void tick(double tickTime) {
		this.timeLeft -= tickTime;
	}
	
	public boolean isActive() {
		return this.timeLeft > 0;
	}
	
	public Point2D toPoint2D() {
		return new Point2D(x, y);
	}

}
