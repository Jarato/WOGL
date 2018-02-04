package simulation.world.environment;

import javafx.scene.paint.Color;
import pdf.util.Pair;
import simulation.world.World;

public class Rock {
	public static final double COLLISION_HARDNESS = 1.0;
	public static final Color COLOR = Color.DIMGREY;
	private Pair<Double,Double> topLeftBound;
	private Pair<Double,Double> bottomRightBound;
	private double[] xPoints;
	private double[] yPoints;
	private double[] world_xPoints;
	private double[] world_yPoints;
	
	public Rock(int numberOfEdges) {
		xPoints = new double[numberOfEdges];
		yPoints = new double[numberOfEdges];
	}
	
	public void setPosition_Size(Pair<Double,Double> topLeft, Pair<Double,Double> bottomRight) {
		topLeftBound = topLeft;
		bottomRightBound = bottomRight;
	}
	
	public Pair<Double,Double> getTopLeftBound(){
		return topLeftBound;
	}
	
	public Pair<Double,Double> getBottomRightBound(){
		return bottomRightBound;
	}
	
	public int getNumberOfPoints() {
		return xPoints.length;
	}
	
	public Pair<double[],double[]> getPoints(){
		return new Pair<double[],double[]>(xPoints,yPoints);
	}
	
	public void calculateWorldPointPosition() {
		world_xPoints = new double[xPoints.length];
		world_yPoints = new double[yPoints.length];
		double x_len = bottomRightBound.getX() - topLeftBound.getX();
		double y_len = bottomRightBound.getY() - topLeftBound.getY();
		for (int i = 0; i < world_xPoints.length; i++) {
			world_xPoints[i] = xPoints[i]*x_len + topLeftBound.getX();
			world_yPoints[i] = yPoints[i]*y_len + topLeftBound.getY();
		}
	}
	
	public Pair<double[],double[]> getWorldPoints(){
		return new Pair<double[],double[]>(world_xPoints,world_yPoints);
	}
}
