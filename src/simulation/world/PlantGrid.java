package simulation.world;

import java.util.Random;

import pdf.util.Pair;

public class PlantGrid {
	private final Random rnd;
	
	class PlantBox {
		private final Pair<Double,Double> upperLeftCorner;
		private double growth;
		private Plant plant;

		public PlantBox(double upperLeftX, double upperLeftY) {
			this.upperLeftCorner = new Pair<Double,Double>(upperLeftX, upperLeftY);
		}

		/**
		 * add the growthValue to the inner growth.<br>
		 * if the threshold is reached, a plant will grow and this method will return true, else it will return false
		 * @param growthValue the strength of the growth
		 * @return true, if a new plant grown<br>
		 * false, else
		 */
		public boolean grow(double growthValue){
			this.growth += growthValue;
			if (this.growth > Plant.BASE_GROW_TIME) {
				growPlant();
				this.growth = 0;
				return true;
			} else return false;
		}

		public void plantEaten(){
			this.plant = null;
		}

		public Plant getPlant(){
			return this.plant;
		}
		
		private void removePlant() {
			plant = null;
		}

		public void growPlant() {
			this.plant = new Plant(Plant.RADIUS, upperLeftCorner.getX()+rnd.nextDouble()*PLANTBOX_SIZE, upperLeftCorner.getX()+rnd.nextDouble()*PLANTBOX_SIZE);
		}

	}
	public static final int AXIS_PLANTBOX_AMOUNT = 40;
	public final static double PLANTBOX_SIZE = World.SIZE/AXIS_PLANTBOX_AMOUNT;
	private PlantBox[][] grid;
	private int numberOfLivingPlants;

	public PlantGrid(Random newRnd){
		rnd = newRnd;
		grid = new PlantBox[AXIS_PLANTBOX_AMOUNT][];
		for (int i = 0; i < grid.length; i++){
			grid[i] = new PlantBox[AXIS_PLANTBOX_AMOUNT];
			for (int k = 0; k < grid[i].length; k++) {
				grid[i][k] = new PlantBox(i*PLANTBOX_SIZE, k*PLANTBOX_SIZE);
			}
		}
	}

	public PlantBox[][] getGrid(){
		return grid;
	}
	
	public void removePlant(int x, int y) {
		if (grid[x][y].plant != null) {
			grid[x][y].removePlant();
			numberOfLivingPlants--;
		}
	}

	public void calculateGrowth(){
		//calculating the non-plants
		for (int i = 0; i < grid.length; i++){
			for (int k = 0; i < grid[i].length; k++){
				if (grid[i][k].getPlant() == null){
					if (grid[i][k].grow(numberOfNeighbors(i, k))) numberOfLivingPlants++;
				}
			}
		}
	}
	
	public Pair<Integer,Integer> getRandomGridPosition(){
		return new Pair<Integer,Integer>(rnd.nextInt(AXIS_PLANTBOX_AMOUNT), rnd.nextInt(AXIS_PLANTBOX_AMOUNT));
	}

	private int numberOfNeighbors(int x, int y){
		int nb = 0;
		for (int i = -1; i < 2; i++){
			for (int k = -1; k < 2; k++) {
				if (plantExists(x+i,y+k)) nb += 1;
			}
		}
		return nb;
	}

	private boolean plantExists(int x, int y){
		if (x < 0 || x >= grid.length) return false;
		if (y < 0 || y >= grid[x].length) return false;
		return grid[x][y].getPlant() != null;
	}
}
