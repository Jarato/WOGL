package simulation.world;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class World {
	//CONSTS
	public static final Color NOTHING_COLOR = Color.WHITE;
	public static final Color WALL_COLOR = Color.GRAY;
	public static final int SIZE = 500;
	public static final int NUMBER_OF_STARTING_CREATURES = 50;
	//ATTRIBUTES
	private final ArrayList<Creature> creatures;
	private final PlantGrid plants;

	public World() {
		creatures = new ArrayList<Creature>();
		plants = new PlantGrid();
	}

	public PlantGrid getPlantGrid() {
		return this.plants;
	}

	public ArrayList<Creature> getCreatures(){
		return this.creatures;
	}
}
