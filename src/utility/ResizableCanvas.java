package utility;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {

	public ResizableCanvas() {
		// TODO Auto-generated constructor stub
	}

	public ResizableCanvas(double width, double height) {
		super(width, height);
		this.setVisible(true);
		//TODO is it need to redraw instantaneously ?
		//widthProperty().addListener(evt -> GameScreenCanvas.paintComponent(0.0d));
        //heightProperty().addListener(evt -> GameScreenCanvas.paintComponent(0.0d));
        
	}
	
	@Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

}
