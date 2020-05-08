package simulation.environment.rocks;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import pdf.util.Pair;
import simulation.environment.EarCut;

public class Rock {
	public static final double COLLISION_HARDNESS = 1.0;
	public static final Color COLOR = Color.rgb(36, 36, 35);;
	private Pair<Double,Double> topLeftBound;
	private Pair<Double,Double> bottomRightBound;
	private double[] xPoints;
	private double[] yPoints;
	private double[] world_xPoints;
	private double[] world_yPoints;
	private double[] xypoints;
	private List<Integer> triangleIndices;
	
	public Rock(int numberOfEdges) {
		xPoints = new double[numberOfEdges];
		yPoints = new double[numberOfEdges];
	}
	
	public void setPosition_Size(Pair<Double,Double> topLeft, Pair<Double,Double> bottomRight) {
		topLeftBound = topLeft;
		bottomRightBound = bottomRight;
	}
	
	public List<Integer> getTriangleIndices(){
		return triangleIndices;
	}
	
	public double[] getTrianglePoints() {
		return xypoints;
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
	
	public ArrayList<Pair<double[],double[]>> calculateWorldRockLines() {
		ArrayList<Pair<double[], double[]>> res = new ArrayList<Pair<double[], double[]>>();
		int N = xPoints.length;
		for (int i = 0; i < N; i++) {
			double[] first = new double[] {world_xPoints[i], world_yPoints[i]};
			double[] second = new double[] {world_xPoints[(i+1)%N], world_yPoints[(i+1)%N]};
			res.add(new Pair<double[], double[]>(first, second));
		}
		return res;
	}
	
	public void calculateWorldPointPosition() {
		world_xPoints = new double[xPoints.length];
		world_yPoints = new double[yPoints.length];
		double x_len = bottomRightBound.getX() - topLeftBound.getX();
		double y_len = bottomRightBound.getY() - topLeftBound.getY();
		xypoints = new double[world_xPoints.length*2];
		for (int i = 0; i < world_xPoints.length; i++) {
			world_xPoints[i] = xPoints[i]*x_len + topLeftBound.getX();
			world_yPoints[i] = yPoints[i]*y_len + topLeftBound.getY();
			xypoints[i*2] = world_xPoints[i];
			xypoints[i*2+1] = world_yPoints[i];
		}
		triangleIndices = EarCut.earcut(xypoints);
	}
	
	public Pair<double[],double[]> getWorldPoints(){
		return new Pair<double[],double[]>(world_xPoints,world_yPoints);
	}
}
