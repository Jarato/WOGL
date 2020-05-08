package simulation.optimization;

import java.util.ArrayList;
import java.util.HashSet;

import pdf.util.Pair;
import simulation.Plant;
import simulation.PlantGrid;
import simulation.World;
import simulation.creature.Body;
import simulation.creature.Brain;
import simulation.creature.Cadaver;
import simulation.environment.rocks.Rock;
import simulation.environment.rocks.RockSystem;

public class ObjectGrid {
	public static final double BOX_SIZE = 30.0;
	private ObjectCell[][] grid;
	private ViewAbleObjects[][] gridOfViewAbleObjects;
	
	public class ObjectCell {
		public ArrayList<Plant> plants = new ArrayList<Plant>();
		public ArrayList<Body> bodies = new ArrayList<Body>();
		public ArrayList<Cadaver> cadavers = new ArrayList<Cadaver>();
	}
	
	public class ViewAbleObjects {
		public ArrayList<Pair<double[], double[]>> rockLines = new ArrayList<Pair<double[], double[]>>();
		public ArrayList<Plant> plants = new ArrayList<Plant>();
	}
	
	public ObjectGrid(World world) {
		int numOfCells = ((int)(World.SIZE/BOX_SIZE))+1;
		grid = new ObjectCell[numOfCells][numOfCells];
		gridOfViewAbleObjects = new ViewAbleObjects[numOfCells][numOfCells];
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				grid[x][y] = new ObjectCell();
				gridOfViewAbleObjects[x][y] = new ViewAbleObjects();
			}
		}
	}
	
	public ObjectCell getObjectsInRadius(double xMiddle, double yMiddle, double radius) {
		Pair<Integer,Integer> startPosition = calculateGridPosition(xMiddle-radius, yMiddle-radius);
		Pair<Integer,Integer> endPosition = calculateGridPosition(xMiddle+radius, yMiddle+radius);
		ObjectCell res = new ObjectCell();
		HashSet<Pair<double[],double[]>> rlines = new HashSet<Pair<double[], double[]>>();
		for (int x = startPosition.getX(); x < endPosition.getX()+1; x++) {
			for (int y = startPosition.getY(); y < endPosition.getY()+1; y++) {
				res.plants.addAll(grid[x][y].plants);
				res.bodies.addAll(grid[x][y].bodies);
				res.cadavers.addAll(grid[x][y].cadavers);
			}
		}
		return res;
	}
	
	public ViewAbleObjects getViewAbleObjects(Pair<Integer,Integer> gridPos) {
		return gridOfViewAbleObjects[gridPos.getX()][gridPos.getY()];
	}

	public Pair<Integer,Integer> calculateGridPosition(double x, double y) {
		if (x < 0) x = 0;
		else if (x > World.SIZE) x = World.SIZE;
		if (y < 0) y = 0;
		else if (y > World.SIZE) y = World.SIZE; 
		return new Pair<Integer,Integer>((int)(x/BOX_SIZE), (int)(y/BOX_SIZE));
	}
	
	
	public void insertRocksIntoGrid(RockSystem rocksystem) {
		// pre-calculate viewable rocks
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				double radius = Brain.SIGHT_RANGE+Body.MAX_RADIUS+BOX_SIZE/2;
				double[] middlePosition = new double[] {(x+0.5)*BOX_SIZE, (y+0.5)*BOX_SIZE};
				Rock[] rocks = rocksystem.getRocks();
				for (int i = 0; i < rocks.length; i++) {
					Rock rock = rocks[i];
					ArrayList<Pair<double[], double[]>> rocklines = rock.calculateWorldRockLines();
					for (Pair<double[], double[]> line : rocklines) {
						Pair<double[], Double> closestPoint = rocksystem.getClosestPointOnLineTo(middlePosition, line.getX(), line.getY());
						if (closestPoint.getY() < radius) {
							gridOfViewAbleObjects[x][y].rockLines.add(line);
						}
					}
				}	
			}
		}
	}
	
	public void updatePlant(Plant plant, boolean isInserted) {
		// update all the surrounding grid-cells to which the plant was or is visible
		// isInserted==true means, that the plant is a new plant and should be inserted in the surrounding cells
		// isInserted==false means, that the plant should be removed out of the surrounding cells
		double radius = Brain.SIGHT_RANGE+Body.MAX_RADIUS+BOX_SIZE/2 + plant.getRadius();
		double radiusSQ = radius*radius;
		Pair<Integer, Integer> upleft = calculateGridPosition(plant.getXCoordinate()-radius, plant.getYCoordinate()-radius);
		Pair<Integer, Integer> downright = calculateGridPosition(plant.getXCoordinate()+radius, plant.getYCoordinate()+radius);
		for (int x = upleft.getX(); x <= downright.getX(); x++) {
			double xMid = (x+0.5)*BOX_SIZE;
			double xDiff = plant.getXCoordinate()-xMid;
			double xDiffSQ = xDiff*xDiff;
			for (int y = upleft.getY(); y <= downright.getY(); y++) {
				double yMid = (y+0.5)*BOX_SIZE;
				double yDiff = plant.getYCoordinate()-yMid;
				double yDiffSQ = yDiff*yDiff;
				if (xDiffSQ+yDiffSQ < radiusSQ) {
					if (isInserted) {
						gridOfViewAbleObjects[x][y].plants.add(plant);
					} else {
						gridOfViewAbleObjects[x][y].plants.remove(plant);
					}
				}
			}
		}
		
	}
	
	
}
