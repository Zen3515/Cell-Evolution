package utility;

import javafx.scene.paint.Color;

public class RGBColor {
	
	private int[] rgb = {255, 0, 0};
	private int ptr = 1;
	private boolean isIncreasing = true;
	
	public RGBColor() {
		super();
	}

//	public RGBColor(int r, int g, int b) {
//		rgb[0] = r;
//		rgb[1] = g;
//		rgb[2] = b;
//	}
	
	public void increaseBy(int number) {
		if(isIncreasing) {
			rgb[ptr] += number;
			if(rgb[ptr] >= 255) {
				rgb[ptr] = 255;
				isIncreasing = false;
				ptr -= 1;
				if(ptr < 0) {
					ptr = 2;
				}
			}
		} else {
			rgb[ptr] -= number;
			if(rgb[ptr] <= 0) {
				rgb[ptr] = 0;
				isIncreasing = true;
				ptr -= 1;
				if(ptr < 0) {
					ptr = 2;
				}
			}
		}
	}
	
	public void decreaseBy(int number) {
		if(!isIncreasing) {
			rgb[ptr] += number;
			if(rgb[ptr] >= 255) {
				rgb[ptr] = 255;
				isIncreasing = !isIncreasing;
				ptr -= 1;
				if(ptr < 0) {
					ptr = 2;
				}
			}
		} else {
			rgb[ptr] -= number;
			if(rgb[ptr] <= 0) {
				rgb[ptr] = 0;
				isIncreasing = !isIncreasing;
				ptr -= 1;
				if(ptr < 0) {
					ptr = 2;
				}
			}
		}
	}
	
	public void increase() {
		increaseBy(15);
	}
	
	public void decrease() {
		decreaseBy(15);
	}
	
	public Color getColor() {
		return Color.rgb(rgb[0], rgb[1], rgb[2]);
	}

}
