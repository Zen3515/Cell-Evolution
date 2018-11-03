package utility;

public class Rectangle {
	
	public int x;
	public int y;
	public int width;
	public int height;

	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		if(width < 0) {
			this.x += width;
			this.width = -width;
		};
		if(height < 0) {
			this.y += height;
			this.height = -height;
		}
	}
	
	@Override
	public String toString() {
		return "React: [(" + this.x + "," + this.y + "), width = " + this.width + ", height = " + this.height + "]";
	}

}
