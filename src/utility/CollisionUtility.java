package utility;

import java.util.ArrayList;

import javafx.geometry.Point2D;

public class CollisionUtility {

	private static ArrayList<Point2D> getNorms(ArrayList<Point2D> points) {
		ArrayList<Point2D> normals = new ArrayList<Point2D>();
		for(int i = 0; i < points.size()-1; i++) {
			Point2D temp = points.get(i+1).subtract(points.get(i));
			normals.add(new Point2D(temp.getY(), -temp.getX()));
		}
		Point2D temp = points.get(0).subtract(points.get(points.size()-1));
		normals.add(new Point2D(temp.getY(), -temp.getX()));
		return normals;
	}
	
	/**
	 * 
	 * @param points
	 * @param axis
	 * @return min as first max as second
	 */
	private static Tuple<Double, Double> getMinMax(ArrayList<Point2D> points, Point2D axis){
		double min = points.get(0).dotProduct(axis);
		double max = min;
		for(int i = 1; i < points.size(); i++) {
			double currentProject = points.get(i).dotProduct(axis);
			if(currentProject > max) {
				max = currentProject;
			} else if(currentProject < min) {
				min = currentProject;
			}
		}
		return new Tuple<Double, Double>(min, max);
	}
	
	/**
	 * 
	 * @param poly1
	 * @param origin1 center of entity
	 * @param poly2
	 * @param origin2
	 * @return CollisionResult
	 */
	public static boolean isPolygonCollide(ArrayList<Point2D> poly1, ArrayList<Point2D> poly2) {
		ArrayList<Point2D> norms1 = getNorms(poly1);
		//Point2D[] vectors1 = PrepareVector(poly1, origin1);
		ArrayList<Point2D> norms2 = getNorms(poly2);
		//Point2D[] vectors2 = PrepareVector(poly2, origin2);
		boolean isSeparated = false;
		for(Point2D norm : norms1) {
			Tuple<Double, Double> projection1 = getMinMax(poly1, norm);
			Tuple<Double, Double> projection2 = getMinMax(poly2, norm);
			isSeparated = (projection1.Second < projection2.First) || (projection2.Second < projection1.First);
			if(isSeparated)
				break;
		};
		if(isSeparated) {
			return false;
		}
		for(Point2D norm: norms2) {
			Tuple<Double, Double> projection1 = getMinMax(poly1, norm);
			Tuple<Double, Double> projection2 = getMinMax(poly2, norm);
			isSeparated = (projection1.Second < projection2.First) || (projection2.Second < projection1.First);
			if(isSeparated)
				break;
		}
		return !isSeparated;
	}
	
	public static boolean isPolygonCollideWithCircle(ArrayList<Point2D> poly1, Point2D center, double radius) {
		ArrayList<Point2D> norms1 = getNorms(poly1);
		for(Point2D norm : norms1) {
			norm = norm.normalize();
			Tuple<Double, Double> projection1 = getMinMax(poly1, norm);
			Point2D normRadius = norm.multiply(radius);
			double minCircleProjection = center.subtract(normRadius).dotProduct(norm);
			double maxCircleProjection = center.add(normRadius).dotProduct(norm);
//			System.out.println("minproj = " + minCircleProjection + ", maxProj = " + maxCircleProjection);
			if((projection1.Second < minCircleProjection) || (maxCircleProjection < projection1.First)){
				return false;
			}
		}
		return true;
	}
	
//	/**
//	 * This doesn't work because it use center of polygon f**k it...
//	 * @param poly1Center
//	 * @param poly1
//	 * @param center
//	 * @param radius
//	 * @return
//	 */
//	@Deprecated
//	public static boolean isPolygonCollideWithCircle(Point2D poly1Center, ArrayList<Point2D> poly1, Point2D center, double radius) {
//		Point2D axis = center.subtract(poly1Center).normalize();
//		double maxProject = 0;
//		System.out.println("poly1Center = " + poly1Center + ", CricleCenter = " + center);
//		//Point2D farthest = Point2D.ZERO; //It should not remain zero
//		for(Point2D point : poly1) {
//			System.out.println("tesing point = " + point);
//			double currProject = point.subtract(poly1Center).dotProduct(axis);
//			if(currProject > maxProject) {
//				maxProject = currProject;
//				//farthest = point;
//			}
//		}
//		System.out.println("Max projection = " + maxProject + ", distance = " + (center.distance(poly1Center) - radius));
//		return (center.distance(poly1Center) - radius) <= (maxProject);
//		
//	}
	
	public static boolean isCircleCollide(Point2D center1, double radius1, Point2D center2, double radius2) {
		double distance = center1.distance(center2);
		return distance < radius1 + radius2;
	}

}
