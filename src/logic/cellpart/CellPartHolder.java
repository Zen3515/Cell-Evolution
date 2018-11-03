package logic.cellpart;

/**
 * Only store only RIGHTSIDE!!!!
 * 
 */
public class CellPartHolder{
	
	public boolean isPaired = true;
	public double x;
	public double y;
	public double angle; //angle from north
//	public double[] arguments;
	
	public CellPartHolder(double x, double y, double angle, boolean isPaired) {
		super();
		if(x < 0) {
			throw new IllegalArgumentException("x position must > 0 as we only store right side");
		}
		this.angle = angle;
		this.x = x;
		this.y = y;
		this.isPaired = isPaired;
//		this.arguments = args;
	}

}
