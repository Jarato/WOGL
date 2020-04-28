package simulation.environment.rocks;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

import simulation.environment.OpenSimplexNoise;


public class RockGenerator {
	public static final int WIDTH = 1024;
	public static final int HEIGHT = 1024;
	public static final double FEATURE_SIZE = 80;
	public static final double THRESHOLD = 0.32;
	public static final double MARKTHRESHOLD = 1.0;

	private long seed;
	private OpenSimplexNoise osn;
	
	/*private void main(String[] args)
		throws IOException {
		Random rnd = new Random();
		for (int i = 0; i < 20; i++) {
			long seed = rnd.nextLong();//
			BufferedImage image = generateBinaryNoiseMap(seed, WIDTH, HEIGHT, FEATURE_SIZE, THRESHOLD);
			BufferedImage edge = edgeDetectionOnMap(image);
			BufferedImage edgeCopy = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			edgeCopy.setData(edge.getData());
			//ImageIO.write(image, "png", new File("noiseMap"+FEATURE_SIZE+"_"+THRESHOLD+"_"+seed+".png"));
			//ImageIO.write(edge, "png", new File("edgeMap"+FEATURE_SIZE+"_"+THRESHOLD+"_"+seed+"_edge.png"));
			ArrayList<LinkedList<int[]>> polys = generatePolygons(edge);
			ImageIO.write(markPolygonNodes(edgeCopy, polys), "png", new File("markedMap"+FEATURE_SIZE+"_"+THRESHOLD+"_"+seed+"_edge.png"));
			int k = 1;
			for (LinkedList<int[]> polygon : polys) {
				System.out.println("\nRock "+k);
				k++;
				for (int[] point : polygon) {
					System.out.println(point[0]+", "+point[1]);
				}
			}
		}
	}*/
	
	/*private BufferedImage markPolygonNodes(BufferedImage map, ArrayList<LinkedList<int[]>> polygons) {
	for (LinkedList<int[]> polygon : polygons) {
		for (int[] point : polygon) {
			map.setRGB(point[0], point[1], 0xFF0000);
		}
	}
	return map;
	}*/
	
	public void setSeed(long newSeed) {
		seed = newSeed;
		System.out.println("RockSystem-Seed: "+seed);
		osn = new OpenSimplexNoise(seed);
	}
	
	public long getSeed() {
		return seed;
	}
	
	public ArrayList<LinkedList<int[]>> generateRocks() {
		BufferedImage image = generateBinaryNoiseMap(WIDTH, HEIGHT, FEATURE_SIZE, THRESHOLD);
		BufferedImage edge = edgeDetectionOnMap(image);
		return generatePolygons(edge);
	}
	
	private BufferedImage generateBinaryNoiseMap(int width, int height, double feature_size, double threshold) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < HEIGHT; y++)
		{
			for (int x = 0; x < WIDTH; x++)
			{
				double value = osn.eval(x / feature_size, y / feature_size, 0.0);
				if (value > threshold) {
					image.setRGB(x, y, 0xFFFFFF);
				} else {
					image.setRGB(x, y, 0x000000);
				}
			}
		}
		return image;
	}
	
	private BufferedImage edgeDetectionOnMap(BufferedImage image) {
		BufferedImage res = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		//image.getRGB(-1, -1);
		for (int y = 0; y < image.getHeight(); y++) {
			
			for (int x = 0; x < image.getWidth(); x++) {
				res.setRGB(x, y, 0x000000);
				if (isWhite(x,y,image)) {
					if (x > 0 && !isWhite(x-1, y, image)) res.setRGB(x, y, 0xFFFFFF);
					if (x < image.getWidth()-1 && !isWhite(x+1, y, image)) res.setRGB(x, y, 0xFFFFFF);
					if (y > 0 && !isWhite(x, y-1, image)) res.setRGB(x, y, 0xFFFFFF);
					if (y < image.getHeight()-1 && !isWhite(x, y+1, image)) res.setRGB(x, y, 0xFFFFFF);
				}
			}
		}
		
		return res;
	}
	
	private boolean isWhite(int x, int y, BufferedImage image) {
		return (x>=0 && x<image.getWidth() && y>=0 && y<image.getHeight() && (image.getRGB(x, y) & 0xFF) > 0); 
	}
	
	private ArrayList<LinkedList<int[]>> generatePolygons(BufferedImage edgeMap) {
		ArrayList<LinkedList<int[]>> polygons = new ArrayList<LinkedList<int[]>>();
		for (int y = 0; y < edgeMap.getHeight(); y++) {
			for (int x = 0; x < edgeMap.getWidth(); x++) {
				if (isWhite(x, y, edgeMap)) {
					LinkedList<int[]> edge = edgeCrawler(x,y,edgeMap);
					if (edge.size() > 2) {
						LinkedList<int[]> polygon = cleanPolygon(edge, edgeMap.getWidth(), edgeMap.getHeight());
						//System.out.println("polygon extracted!");
						if (polygon != null) {
							LinkedList<int[]> reducedPolygon = reducePolygon(polygon, MARKTHRESHOLD);
							//System.out.println("polygon reduced!");
							if (reducedPolygon != null) polygons.add(reducedPolygon);
						}
					}
				}
			}
		}
		return polygons;
	}
	
	private double distancePointFromLine(int[] lineStart, int[] lineEnd, int[] point) {
		return Math.abs((lineEnd[1]-lineStart[1])*point[0] - (lineEnd[0]-lineStart[0])*point[1] + lineEnd[0]*lineStart[1] - lineEnd[1]*lineStart[0])/Math.sqrt(Math.pow(lineEnd[1]-lineStart[1], 2)+Math.pow(lineEnd[0]-lineStart[0], 2));
	}
	
	private LinkedList<int[]> reducePolygon(LinkedList<int[]> polygon, double maxDist) {
		if (maxDist < 0.01) return null;
		LinkedList<int[]> reducedPoly = new LinkedList<int[]>();
		int numP = polygon.size();
		boolean stopCondition=false;
		boolean loopedOnce=false;
		int polyStartIndex=-1;
		int first = 0;
		int second = 1;
		int[] firstP; 
		int[] secondP;	
		double maxDiff = 0;
		int maxi = -2;
		while (!stopCondition) {
			firstP = polygon.get(first);
			maxDiff = 0;
			while (maxDiff <= maxDist && first != second) {
				secondP = polygon.get(second);
				int i = (first+1)%numP;
				maxi = i;
				while (i != second && !stopCondition) {
					int[] iPoint = polygon.get(i);
					double diff = distancePointFromLine(firstP, secondP, iPoint);
					if (diff > maxDiff) {
						maxi = i;
						maxDiff = diff;
					}
					i = (i+1)%numP;
				}
				second = (second+1)%numP;
				if (loopedOnce && first != polyStartIndex && second > polyStartIndex) stopCondition=true;
			}
			if (maxDiff > maxDist && !stopCondition) {
				if (first > maxi) loopedOnce = true;
				firstP = polygon.get(maxi);
				first = maxi;
				second = (maxi+1)%numP;
				if (reducedPoly.size() == 0) polyStartIndex = maxi;
				reducedPoly.add(firstP);
				maxi=-2;
			} else if (first == second && reducedPoly.size() < 3) {
				return reducePolygon(polygon, maxDist/2.0);
			}
		}
		return reducedPoly;
	}
	
	private int[] positionFromTo(int[] p1, int[] p2) {
		return new int[] {p1[0]-p2[0], p1[1]-p2[1]};
	}
	
	private int distanceFromTo(int[] p1, int[] p2) {
		return Math.abs(p1[0]-p2[0]) + Math.abs(p1[1]-p2[1]);
	}	
	
	private int[] getNextEdgePointWDir(int[] point, int[] lastPoint, BufferedImage map) {
		//check 8 adjacent pixels
		int[] posFromTo = positionFromTo(lastPoint, point);
		int[] lookAtPoint;
		if (posFromTo[0] == -1 && posFromTo[1] == -1) {
			// 1.
			lookAtPoint = new int[] {point[0]+1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0]  , point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]+1, point[1]  };
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0]-1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]+1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0]-1, point[1]  };
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]  , point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;			
		}
		if (posFromTo[0] == -1 && posFromTo[1] == 0) {
			// 1.
			lookAtPoint = new int[] {point[0]+1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0]+1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]+1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0], point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0], point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0]-1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
		}
		if (posFromTo[0] == -1 && posFromTo[1] == +1) {
			// 1.
			lookAtPoint = new int[] {point[0]+1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0]+1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0], point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0]+1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0]-1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0], point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
		}
		if (posFromTo[0] == 0 && posFromTo[1] == +1) {
			// 1.
			lookAtPoint = new int[] {point[0], point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0]+1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0]+1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0]+1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
		}
		if (posFromTo[0] == +1 && posFromTo[1] == +1) {
			// 1.
			lookAtPoint = new int[] {point[0]-1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0], point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0]+1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0], point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]+1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
		}
		if (posFromTo[0] == +1 && posFromTo[1] == 0) {
			// 1.
			lookAtPoint = new int[] {point[0]-1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0]-1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0], point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0], point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0]+1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]+1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
		}
		if (posFromTo[0] == +1 && posFromTo[1] == -1) {
			// 1.
			lookAtPoint = new int[] {point[0]-1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0], point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0]+1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0]+1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0], point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
		}
		if (posFromTo[0] == 0 && posFromTo[1] == -1) {
			// 1.
			lookAtPoint = new int[] {point[0], point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 2.
			lookAtPoint = new int[] {point[0]+1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]+1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 3.
			lookAtPoint = new int[] {point[0]+1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			// 4.
			lookAtPoint = new int[] {point[0]+1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
			lookAtPoint = new int[] {point[0]-1, point[1]-1};
			if (isWhite(lookAtPoint[0], lookAtPoint[1], map)) return lookAtPoint;
		}
		return null;
	}
	
	private int[] getNextEdgePoint(int[] point, BufferedImage map) {
		//check 8 adjacent pixels
		if (isWhite(point[0]-1, point[1]-1, map)) return new int[] {point[0]-1, point[1]-1};
		if (isWhite(point[0]-1, point[1]  , map)) return new int[] {point[0]-1, point[1]  };
		if (isWhite(point[0]-1, point[1]+1, map)) return new int[] {point[0]-1, point[1]+1};
		if (isWhite(point[0]  , point[1]-1, map)) return new int[] {point[0]  , point[1]-1};
		if (isWhite(point[0]  , point[1]+1, map)) return new int[] {point[0]  , point[1]+1};
		if (isWhite(point[0]+1, point[1]-1, map)) return new int[] {point[0]+1, point[1]-1};
		if (isWhite(point[0]+1, point[1]  , map)) return new int[] {point[0]+1, point[1]  };
		if (isWhite(point[0]+1, point[1]+1, map)) return new int[] {point[0]+1, point[1]+1};
		return null;
	}
		
	private LinkedList<int[]> connectCorners(LinkedList<int[]> list, int mapWidth, int mapHeight) {
		// CONNECT OVER CORNERS
		LinkedList<int[]> connectedList = new LinkedList<int[]>();
		for (int i = 0; i < list.size(); i++) {
			int[] one = list.get(i);
			connectedList.add(one);
			int[] two = list.get((i+1)%list.size());
			if (distanceFromTo(one,two) > 2) {
				int[] diff = positionFromTo(one,two);
				if (diff[0] != 0 && diff[1] != 0) {
					if (one[0] != 0 && one[0] != mapWidth-1 && one[1] != 0 && one[1] != mapHeight-1 || two[0] != 0 && two[0] != mapWidth-1 && two[1] != 0 && two[1] != mapHeight-1) return null;
					/*System.out.println("Need to connect the following two points over the corner:");
					System.out.println(one[0]+", "+one[1]);
					System.out.println(two[0]+", "+two[1]);
					System.out.println("diff");
					System.out.println(diff[0]+", "+diff[1]);*/
					if (Math.abs(diff[0]) == mapWidth-1 || Math.abs(diff[1]) == mapHeight) {
						// OVER TWO CORNERS
						if (one[0] == 0 || one[0] == mapWidth-1) {
							// up and down
							int upDistance = Math.min(one[1], two[1]);
							int downDistance = mapHeight-1-Math.max(one[1], two[1]);
							if (upDistance > downDistance) {
								// connect down
								if (one[0] == 0) {
									// first left then right
									connectedList.add(new int[] {0, mapHeight-1});
									connectedList.add(new int[] {mapWidth-1, mapHeight-1});
								} else {
									// first right then left
									connectedList.add(new int[] {mapWidth-1, mapHeight-1});
									connectedList.add(new int[] {0, mapHeight-1});
								}
							} else {
								// connect up
								if (one[0] == 0) {
									// first left then right
									connectedList.add(new int[] {0, 0});
									connectedList.add(new int[] {mapWidth-1, 0});
								} else {
									// first right then left
									connectedList.add(new int[] {mapWidth-1, 0});
									connectedList.add(new int[] {0, 0});
								}
							}
						} else {
							// left and right
							int leftDistance = Math.min(one[0], two[0]);
							int rightDistance = mapHeight-1-Math.max(one[0], two[0]);
							if (leftDistance > rightDistance) {
								// connect right
								if (one[1] == 0) {
									// first up then down
									connectedList.add(new int[] {mapWidth-1, 0});
									connectedList.add(new int[] {mapWidth-1, mapHeight-1});
								} else {
									// first down then up
									connectedList.add(new int[] {mapWidth-1, mapHeight-1});
									connectedList.add(new int[] {mapWidth-1, 0});
								}
							} else {
								// connect left
								if (one[1] == 0) {
									// first up then down
									connectedList.add(new int[] {0, 0});
									connectedList.add(new int[] {0, mapHeight-1});
								} else {
									// first down then up
									connectedList.add(new int[] {0, mapHeight-1});
									connectedList.add(new int[] {0, 0});
								}
							}
						}
						//System.out.println("over two corners");
					} else {
						// OVER ONE CORNER
						if (one[0] != 0 && one[0] != mapWidth-1 && one[1] != 0 && one[1] != mapHeight-1 || two[0] != 0 && two[0] != mapWidth-1 && two[1] != 0 && two[1] != mapHeight-1) return null;
						
						int[] newPoint = new int[] {-1, -1};
						//System.out.println("over one corner");
						if (one[0] == 0 || one[0] == mapWidth-1) {
							newPoint[0] = one[0];
							newPoint[1] = two[1];
						} else {
							newPoint[0] = two[0];
							newPoint[1] = one[1];
						}
						connectedList.add(newPoint);
					}
				}
			}
		}	
		return connectedList;
	}
		
	private LinkedList<int[]> cleanPolygon(LinkedList<int[]> list, int mapWidth, int mapHeight){
		// SORT
		LinkedList<int[]> sortedList = new LinkedList<int[]>();
		int[] last = list.get(0);
		sortedList.add(last);
		int[] second = null;
		int[] first = list.get(1);
		for (int i = 2; i < list.size(); i++) {
			second = list.get(i);
			if (distanceFromTo(last,second) < distanceFromTo(last,first)) {
				last = second;
			} else {
				last = first;
				first = second;
			}
			sortedList.add(last);
		}
		sortedList.add(first);
		
		return connectCorners(sortedList,mapWidth,mapHeight);
	}
		
	private LinkedList<int[]> edgeCrawler(int startX, int startY, BufferedImage edgeMap){
		LinkedList<int[]> polygon = new LinkedList<int[]>();
		int[] nextEdge = new int[] {startX, startY};
		polygon.add(nextEdge);
		edgeMap.setRGB(nextEdge[0], nextEdge[1], 0x000000);
		int[] previousPoint = nextEdge;
		nextEdge = getNextEdgePoint(nextEdge, edgeMap);
		while (nextEdge != null && (nextEdge[0] != startX || nextEdge[1] != startY)) {
			polygon.addLast(nextEdge);
			edgeMap.setRGB(nextEdge[0], nextEdge[1], 0x000000);
			int[] temp = nextEdge;
			nextEdge = getNextEdgePointWDir(nextEdge, previousPoint, edgeMap);
			previousPoint = temp;
		}
		if (nextEdge == null) {
			//we met a wall
			nextEdge = new int[] {startX, startY};
			previousPoint = nextEdge;
			nextEdge = getNextEdgePoint(nextEdge, edgeMap);
			while (nextEdge != null && (nextEdge[0] != startX || nextEdge[1] != startY)) {
				polygon.addFirst(nextEdge);
				edgeMap.setRGB(nextEdge[0], nextEdge[1], 0x000000);
				int[] temp = nextEdge;
				nextEdge = getNextEdgePointWDir(nextEdge, previousPoint, edgeMap);
				previousPoint = temp;
			}
		}
		
		return polygon;
	}
	
}
