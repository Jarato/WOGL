package simulation.world;

import java.util.ArrayList;

public class World {
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
