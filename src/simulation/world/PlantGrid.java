package simulation.world;

import java.util.Random;

import pdf.util.Pair;
import simulation.world.environment.RockSystem;

public class PlantGrid {
	private final Random rnd;
	
	public class PlantBox {
		public static final int DEAD_TIMER_BASE = 5000;
		private final Pair<Double,Double> upperLeftCorner;
		private double growth;
		private int stillDeadTime;
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
		public void growTimer(double growthValue){
			if (growthValue>0) {
				this.growth -= Math.pow(growthValue, 0.9);
			} else {
				this.growth+=Plant.GROWTIMER_NO_NEIGHBOR_UPDATE;
				if (growth > Plant.BASE_GROW_TIME) growth = Plant.BASE_GROW_TIME;
			}	
		}
		
		public int getStillDeadTime() {
			return stillDeadTime;
		}
		
		public boolean checkGrowth() {
			if (this.growth < 0) {
				growPlant();
				
				this.growth = Plant.BASE_GROW_TIME;
				return true;
			} else return false;
		}
		
		public void initTimer() {
			this.stillDeadTime = DEAD_TIMER_BASE;
			this.growth = Plant.BASE_GROW_TIME;
		}
		
		public void lowerStillDeadTimer() {
			stillDeadTime--;
		}

		public void deletePlant(){
			this.plant = null;
			initTimer();
		}

		public Plant getPlant(){
			return this.plant;
		}

		public void growPlant() {
			this.plant = new Plant(Plant.RADIUS, upperLeftCorner.getX()+rnd.nextDouble()*PLANTBOX_SIZE, upperLeftCorner.getY()+rnd.nextDouble()*PLANTBOX_SIZE);
			plant.setDieTimer(Plant.BASE_DIE_TIME);
		}

	}
	public static final int AXIS_PLANTBOX_AMOUNT = 50;
	public final static double PLANTBOX_SIZE = World.SIZE/AXIS_PLANTBOX_AMOUNT;
	private PlantBox[][] grid;
	private int[] noFieldsW;
	private int numberOfLivingPlants;

	public PlantGrid(Random newRnd){
		noFieldsW = new int[9];
		rnd = newRnd;
		grid = new PlantBox[AXIS_PLANTBOX_AMOUNT][];
		for (int i = 0; i < grid.length; i++){
			grid[i] = new PlantBox[AXIS_PLANTBOX_AMOUNT];
			for (int k = 0; k < grid[i].length; k++) {
				grid[i][k] = new PlantBox(i*PLANTBOX_SIZE, k*PLANTBOX_SIZE);
				grid[i][k].initTimer();
			}
		}
	}
	
	public int getNumberOfLivingPlants() {
		return numberOfLivingPlants;
	}
	
	public void initNumberOfLivingPlants() {
		int counter = 0;
		for (int i = 0; i < grid.length; i++){
			for (int k = 0; k < grid[i].length; k++){
				if (grid[i][k].getPlant() != null){
					counter++;
				}
			}
		}
		numberOfLivingPlants = counter;
	}

	public PlantBox[][] getGrid(){
		return grid;
	}
	
	public void removePlant(int x, int y) {
		if (grid[x][y].plant != null) {
			grid[x][y].deletePlant();
			numberOfLivingPlants--;
		}
	}
	
	public int[] getNoFieldsW() {
		return noFieldsW;
	}

	public void calculateGrowth(RockSystem rockSys){
		for (int i = 0; i < 9; i++) {
			noFieldsW[i] = 0;
		}
		//calculating the non-plants
		for (int i = 0; i < grid.length; i++){
			for (int k = 0; k < grid[i].length; k++){
				if (grid[i][k].getPlant() == null){
					if (grid[i][k].getStillDeadTime()>0) {
						grid[i][k].lowerStillDeadTimer();
					} else {
						int neighbors = numberOfNeighbors(i, k);
						noFieldsW[neighbors]++;
						grid[i][k].growTimer(neighbors);
					}
				} else {
					grid[i][k].getPlant().addDieTimer(-1);
				}
			}
		}
		for (int i = 0; i < grid.length; i++){
			for (int k = 0; k < grid[i].length; k++){
				if (grid[i][k].getPlant() == null){
					if (grid[i][k].checkGrowth()) {
						if (rockSys.checkInBounds(grid[i][k].getPlant())) {
							grid[i][k].deletePlant();
						} else {
							numberOfLivingPlants++;
						}
					}
				} else {
					if (grid[i][k].getPlant().plantDead()) {
						grid[i][k].deletePlant();
						numberOfLivingPlants--;
					}
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