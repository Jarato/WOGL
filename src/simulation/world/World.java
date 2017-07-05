package simulation.world;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;
import pdf.util.Pair;

public class World {
	//CONSTS
	public static final Color NOTHING_COLOR = Color.WHITE;
	public static final Color WALL_COLOR = Color.GRAY;
	public static final double SIZE = 500;
	public static final int NUMBER_OF_STARTING_CREATURES = 50;
	public static final int NUMBER_OF_STARTING_PLANTS = 600;
	//ATTRIBUTES
	private final ArrayList<Creature> creatures;
	private final PlantGrid plantGrid;
	private final Random rnd;
	private final long worldSeed;

	public World() {
		worldSeed = new Random().nextLong();
		rnd = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plantGrid = new PlantGrid(rnd);
		initilize();
	}

	public World(long seed) {
		worldSeed = seed;
		rnd = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plantGrid = new PlantGrid(rnd);
		initilize();
	}
	
	private void initilize() {
		for (int i = 0; i < NUMBER_OF_STARTING_CREATURES; i++) {
			Pair<Double,Double> rndPos = getRandomWorldPosition(Body.RADIUS);
			Creature c = new Creature(i,rndPos.getX(), rndPos.getY(), rnd);
			creatures.add(c);
		}
		for (int i = 0; i < NUMBER_OF_STARTING_PLANTS; i++) {
			Pair<Integer,Integer> rndPos = plantGrid.getRandomGridPosition();
			plantGrid.getGrid()[rndPos.getX()][rndPos.getY()].growPlant();
		}
	}
	
	public Pair<Double,Double> getRandomWorldPosition(double spacing){
		return new Pair<Double,Double>(rnd.nextDouble()*(SIZE-2*spacing)+spacing, rnd.nextDouble()*(SIZE-2*spacing)+spacing);
	}
	
	public long getWorldSeed() {
		return worldSeed;
	}
	
	public PlantGrid getPlantGrid() {
		return this.plantGrid;
	}

	public ArrayList<Creature> getCreatures(){
		return this.creatures;
	}
	
	public void step() {
		for (int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			c.workBrain(this);
			c.workBody(this);
		}
		for (int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			c.move();
			Body b = c.getBody();
			b.setCoordinates(Math.max(b.getXCoordinate(), b.getRadius()), Math.max(b.getYCoordinate(), b.getRadius()));
		}
	}
}
