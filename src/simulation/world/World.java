package simulation.world;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;

public class World {
	//CONSTS
	public static final Color NOTHING_COLOR = Color.WHITE;
	public static final Color WALL_COLOR = Color.GRAY;
	public static final double SIZE = 500;
	public static final int NUMBER_OF_STARTING_CREATURES = 50;
	//ATTRIBUTES
	private final ArrayList<Creature> creatures;
	private final PlantGrid plants;
	private final Random rnd;
	private final long worldSeed;

	public World() {
		worldSeed = new Random().nextLong();
		rnd = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plants = new PlantGrid();
	}

	public World(long seed) {
		worldSeed = seed;
		rnd = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plants = new PlantGrid();
	}
	
	private void initilize() {
		
	}
	
	public long getWorldSeed() {
		return worldSeed;
	}
	
	public PlantGrid getPlantGrid() {
		return this.plants;
	}

	public ArrayList<Creature> getCreatures(){
		return this.creatures;
	}
}
