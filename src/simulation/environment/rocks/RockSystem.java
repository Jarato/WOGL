package simulation.environment.rocks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import pdf.simulation.Point2D;
import pdf.util.Pair;
import pdf.util.UtilMethods;
import simulation.World;
import simulation.environment.EarCut;

public class RockSystem {
	private RockGenerator rockgenerator;
	private Rock[] rocks;
	
	public RockSystem(long seed) {
		rockgenerator = new RockGenerator();
		rockgenerator.setSeed(seed);
	}
	
	public void generateRocks() {
		ArrayList<LinkedList<int[]>> polyRocks = rockgenerator.generateRocks();
		rocks = new Rock[polyRocks.size()];
		int k = 0;
		for (LinkedList<int[]> poly : polyRocks) {
			rocks[k] = new Rock(poly.size());
			double[] x = rocks[k].getPoints().getX();
			double[] y = rocks[k].getPoints().getY();
			double[] xy = new double[poly.size()*2];
			int i = 0;
			for (int[] point : poly) {
				x[i] = ((double)point[0])/(RockGenerator.WIDTH-1);
				y[i] = ((double)point[1])/(RockGenerator.HEIGHT-1);
				xy[i*2] = point[0];
				xy[i*2+1] = point[1];
				i++;
			}
			rocks[k].setPosition_Size(new Pair<Double,Double>(0.0,0.0), new Pair<Double,Double>(World.SIZE, World.SIZE));
			rocks[k].calculateWorldPointPosition();
			k++;
		}
		
		/*
		rocks[0] = new Rock(4);
		double[] x = rocks[0].getPoints().getX();
		double[] y = rocks[0].getPoints().getY();
		x[0] = 0.35;
		y[0] = 0.0;
		x[1] = 0.65;
		y[1] = 0.0;
		x[2] = 0.7;
		y[2] = 1.0;
		x[3] = 0.3;
		y[3] = 1.0;
		rocks[0].setPosition_Size(new Pair<Double,Double>(1200.0,0.0), new Pair<Double,Double>(1800.0,1485.0));
		rocks[0].calculateWorldPointPosition();
		rocks[1] = new Rock(4);
		x = rocks[1].getPoints().getX();
		y = rocks[1].getPoints().getY();
		x[0] = 0.3;
		y[0] = 0.0;
		x[1] = 0.7;
		y[1] = 0.0;
		x[2] = 0.65;
		y[2] = 1.0;
		x[3] = 0.35;
		y[3] = 1.0;
		rocks[1].setPosition_Size(new Pair<Double,Double>(1200.0,1515.0), new Pair<Double,Double>(1800.0,3000.0));
		rocks[1].calculateWorldPointPosition();
		*/
		
		
	}

	public Rock[] getRocks() {
		return rocks;
	}
	
	public boolean checkInBounds(Point2D object) {
		double[] vecObj = new double[] {object.getXCoordinate(), object.getYCoordinate()};
		boolean res = false;
		for(int i = 0; i < rocks.length; i++) {
			Rock rock = rocks[i];
			double[] xWP = rock.getWorldPoints().getX();
			double[] yWP = rock.getWorldPoints().getY();
			int N = rock.getNumberOfPoints();
			for (int n = 0; n < N; n++) {
				double[] vecFirst = new double[] {xWP[n],yWP[n]};
				double[] vecSecond = new double[] {xWP[(n+1)%N], yWP[(n+1)%N]};
				if ( ((vecFirst[1]>vecObj[1]) != (vecSecond[1]>vecObj[1])) &&
					     (vecObj[0] < (vecSecond[0]-vecFirst[0]) * (vecObj[1]-vecFirst[1]) / (vecSecond[1]-vecFirst[1]) + vecFirst[0]) ) res = !res;
			}
		}
		
		return res;
	}
	
	public Pair<double[], Double> getClosestPointTo(Point2D object) {
		double[] vecObj = new double[] {object.getXCoordinate(), object.getYCoordinate()};
		double[] closestPoint = new double[2];
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < rocks.length; i++) {
			Rock rock = rocks[i];
			double[] xWP = rock.getWorldPoints().getX();
			double[] yWP = rock.getWorldPoints().getY();
			int N = rock.getNumberOfPoints();
			for (int n = 0; n < N; n++) {
				double[] vecFirst = new double[] {xWP[n],yWP[n]};
				double[] vecSecond = new double[] {xWP[(n+1)%N], yWP[(n+1)%N]};
				double[] vecFTS = new double[] {vecSecond[0]-vecFirst[0], vecSecond[1] - vecFirst[1]};
				double[] vecFTO = new double[] {vecObj[0]-vecFirst[0], vecObj[1]-vecFirst[1]};
				double vecFTS_len_sq = vecFTS[0]*vecFTS[0] + vecFTS[1]*vecFTS[1];
				double dotP = vecFTS[0]*vecFTO[0] + vecFTS[1]*vecFTO[1];
				double res = dotP/vecFTS_len_sq;
				double[] currentPoint = new double[2];
				if (res < 0) {
					currentPoint = vecFirst;
				} else if (res > 1) {
					currentPoint = vecSecond;
				} else {
					currentPoint[0] = vecFirst[0] + res * vecFTS[0];
					currentPoint[1] = vecFirst[1] + res * vecFTS[1];
				}
				double dist = UtilMethods.vectorLength(UtilMethods.vectorSubtraction(vecObj, currentPoint));
				if (dist < minDist) {
					closestPoint = currentPoint;
					minDist = dist;
				}
			}
		}
		return new Pair<double[],Double>(closestPoint, minDist);
	}
	
	public Pair<double[], Double> getClosestPointOnLineTo(double[] objectPos, double[] lineStart, double[] lineEnd) {
		double[] vecObj = objectPos;
		double[] vecFirst = lineStart;
		double[] vecSecond = lineEnd;
		double[] vecFTS = new double[] {vecSecond[0]-vecFirst[0], vecSecond[1] - vecFirst[1]};
		double[] vecFTO = new double[] {vecObj[0]-vecFirst[0], vecObj[1]-vecFirst[1]};
		double vecFTS_len_sq = vecFTS[0]*vecFTS[0] + vecFTS[1]*vecFTS[1];
		double dotP = vecFTS[0]*vecFTO[0] + vecFTS[1]*vecFTO[1];
		double res = dotP/vecFTS_len_sq;
		double[] currentPoint = new double[2];
		if (res < 0) {
			currentPoint = vecFirst;
		} else if (res > 1) {
			currentPoint = vecSecond;
		} else {
			currentPoint[0] = vecFirst[0] + res * vecFTS[0];
			currentPoint[1] = vecFirst[1] + res * vecFTS[1];
		}
		double dist = UtilMethods.vectorLength(UtilMethods.vectorSubtraction(vecObj, currentPoint));
		return new Pair<double[],Double>(currentPoint, dist);
	}
	
	private double[] intersection(double[] p0, double[] p1, double[] p2, double[] p3) {
		    double[] s10 = new double[] {p1[0] - p0[0], p1[1] - p0[1]};
		    double[] s32 = new double[] {p3[0] - p2[0], p3[1] - p2[1]};
		    double det = s10[0] * s32[1] - s32[0] * s10[1];
		    if (det == 0) return null;
		    
		    boolean det_pos = det > 0;

		    double[] s02 = new double[] {p0[0] - p2[0], p0[1] - p2[1]};

		    double s_det = s10[0] * s02[1] - s10[1] * s02[0];

		    if ((s_det < 0) == det_pos) return null;

		    double t_det = s32[0] * s02[1] - s32[1] * s02[0];

		    if ((t_det < 0) == det_pos) return null;

		    if (((s_det > det) == det_pos) || ((t_det > det) == det_pos)) return null;

		    double t = t_det/det;
		    double[] isp = new double[] { p0[0] + (t * s10[0]), p0[1] + (t * s10[1])};
		    
		    return isp;
	}
	
	public Pair<double[], Double> getClosestLineIntersec(double[] firstP, double[] secondP){
		double min_dist = Double.MAX_VALUE;
		double[] min_dist_pos = new double[2];
		
		for (int i = 0; i < rocks.length; i++) {
			Rock rock = rocks[i];
			double[] xWP = rock.getWorldPoints().getX();
			double[] yWP = rock.getWorldPoints().getY();
			int N = rock.getNumberOfPoints();
			for (int n = 0; n < N; n++) {
				double[] vecBase = new double[] {xWP[n],yWP[n]};
				double[] vecTo = new double[] {xWP[(n+1)%N], yWP[(n+1)%N]};
				
				double[] res = intersection(firstP, secondP, vecBase, vecTo);// intersectionpoint
				if (res != null) {
					double distance = UtilMethods.vectorLength(UtilMethods.vectorSubtraction(res, firstP));
					if (distance < min_dist) {
						min_dist = distance;
						min_dist_pos = res;
					}
				}
			}
		}
		return new Pair<double[], Double>(min_dist_pos, min_dist);
	}
	
	public Pair<Pair<double[],double[]>, Double> getClosestIntersectingRockSection(double[] firstP, double[] secondP) {
		double min_dist = Double.MAX_VALUE;
		Pair<double[],double[]> closestRockSection = null;
		
		for (int i = 0; i < rocks.length; i++) {
			Rock rock = rocks[i];
			double[] xWP = rock.getWorldPoints().getX();
			double[] yWP = rock.getWorldPoints().getY();
			int N = rock.getNumberOfPoints();
			for (int n = 0; n < N; n++) {
				double[] vecBase = new double[] {xWP[n],yWP[n]};
				double[] vecTo = new double[] {xWP[(n+1)%N], yWP[(n+1)%N]};
				
				double[] res = intersection(firstP, secondP, vecBase, vecTo);// intersectionpoint
				if (res != null) {
					double distance = UtilMethods.vectorLength(UtilMethods.vectorSubtraction(res, firstP));
					if (distance < min_dist) {
						min_dist = distance;
						closestRockSection = new Pair<double[], double[]>(vecBase, vecTo);
					}
				}
			}
		}
		return new Pair<Pair<double[], double[]>, Double>(closestRockSection, min_dist);
	}
	
	public Pair<double[], Double> getClosestLineIntersecToLines(double[] firstP, double[] secondP, ArrayList<Pair<double[], double[]>> toLines){
		double min_dist = Double.MAX_VALUE;
		double[] min_dist_pos = new double[2];
		for (Pair<double[], double[]> line : toLines) {
			double[] res = intersection(firstP, secondP, line.getX(), line.getY());// intersectionpoint
			if (res != null) {
				double distance = UtilMethods.vectorLength(UtilMethods.vectorSubtraction(res, firstP));
				if (distance < min_dist) {
					min_dist = distance;
					min_dist_pos = res;
				}
			}
		}
		return new Pair<double[], Double>(min_dist_pos, min_dist);
	}
}
