package main.simulation.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javafx.scene.paint.Color;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class World {
	//CONSTS
	public static final Color NOTHING_COLOR = Color.WHITE;
	public static final Color WALL_COLOR = Color.GRAY;
	public static final double SIZE = 2000;
	public static final int NUMBER_OF_STARTING_CREATURES = 50;
	public static final int NUMBER_OF_STARTING_PLANTS = 1000;
	//ATTRIBUTES
	private final ArrayList<Creature> creatures;
	private final HashSet<Creature> newCreatures;
	private final PlantGrid plantGrid;
	private final Random randomizer;
	private final long worldSeed;
	private int nextId;
	
	public World() {
		worldSeed = new Random().nextLong();
		newCreatures = new HashSet<Creature>();
		randomizer = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plantGrid = new PlantGrid(randomizer);
	}

	public World(long seed) {
		worldSeed = seed;
		newCreatures = new HashSet<Creature>();
		randomizer = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plantGrid = new PlantGrid(randomizer);
	}
	
	public void initilize() {		
		nextId = 0;
		for (int i = 0; i < NUMBER_OF_STARTING_CREATURES; i++) {
			Pair<Double,Double> rndPos = getRandomWorldPosition(Body.RADIUS);
			Creature c = new Creature(getNextId(),rndPos.getX(), rndPos.getY(), randomizer);
			c.getBody().rotate(randomizer.nextDouble()*360);
			c.getBody().getLife().setX(c.getBody().getLife().getY());
			c.getBody().getStomach().setX(c.getBody().getStomach().getY());
			creatures.add(c);
		}
		for (int i = 0; i < NUMBER_OF_STARTING_PLANTS; i++) {
			Pair<Integer,Integer> rndPos = plantGrid.getRandomGridPosition();
			plantGrid.getGrid()[rndPos.getX()][rndPos.getY()].growPlant();
		}
		System.out.println("Worldseed: "+worldSeed);
	}
	
	public Pair<Double,Double> getRandomWorldPosition(double spacing){
		return new Pair<Double,Double>(randomizer.nextDouble()*(SIZE-2*spacing)+spacing, randomizer.nextDouble()*(SIZE-2*spacing)+spacing);
	}
	
	public long getWorldSeed() {
		return worldSeed;
	}
	
	public PlantGrid getPlantGrid() {
		return this.plantGrid;
	}
	
	public int getNextId() {
		nextId++;
		return nextId-1;
	}

	public ArrayList<Creature> getCreatures(){
		return this.creatures;
	}
	
	public Pair<Integer,Integer> getPlantGridPosition(double x, double y){
		Pair<Integer,Integer> res = new Pair<Integer,Integer>((int) (x/PlantGrid.PLANTBOX_SIZE), (int) (y/PlantGrid.PLANTBOX_SIZE));
		if (res.getX()<0) res.setX(0);
		if (res.getX()>PlantGrid.AXIS_PLANTBOX_AMOUNT-1) res.setX(PlantGrid.AXIS_PLANTBOX_AMOUNT-1);
		if (res.getY()<0) res.setY(0);
		if (res.getY()>PlantGrid.AXIS_PLANTBOX_AMOUNT-1) res.setY(PlantGrid.AXIS_PLANTBOX_AMOUNT-1);
		return res;
	}
	
	public void splitCreature(Creature c) throws IllegalAccessException {
		Creature newC = c.split(randomizer);
		newC.setParentId(c.getId());
		newC.setId(getNextId());
		c.getChildrenIdList().add(newC.getId());
		newCreatures.add(newC);
	}
	
	public void step() throws IllegalAccessException {
		newCreatures.clear();
		//Move
		for (int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			c.move();
			c.getBrain().getInputMask().resetGotHurt();
		}
		//Collision
		for (int i = 0; i < creatures.size(); i++) {
			Body b1 = creatures.get(i).getBody();
			//collision with other creatures
			for (int j = i+1; j < creatures.size(); j++) {
				//if (i != j) {
					Body b2 = creatures.get(j).getBody();
					//collision
					if (b1.inRangeOf(b2, b1.getRadius()+b2.getRadius())) {
						Pair<Double,Double> vector = UtilMethods.point2DSubtraction(b2, b1);
						double length = UtilMethods.point2DLength(vector);
						double factor = ((b1.getRadius()+b2.getRadius())-length)/(2*length);
						vector.set(vector.getX()*factor, vector.getY()*factor);
						b1.move(-vector.getX(), -vector.getY());
						b2.move(vector.getX(), vector.getY());
				//	}
				}
			}
			//collision with walls
			if(b1.getXCoordinate()<b1.getRadius()) {
				b1.setXCoordinate(b1.getRadius());
			} else if (b1.getXCoordinate()>SIZE-b1.getRadius()) {
				b1.setXCoordinate(SIZE-b1.getRadius());
			}
			if(b1.getYCoordinate()<b1.getRadius()) {
				b1.setYCoordinate(b1.getRadius());
			} else if (b1.getYCoordinate()>SIZE-b1.getRadius()) {
				b1.setYCoordinate(SIZE-b1.getRadius());
			}
			//collision with plants
			if (creatures.get(i).eats()) {
				Pair<Integer,Integer> upLeft = getPlantGridPosition(b1.getXCoordinate()-b1.getRadius()-Plant.RADIUS,b1.getYCoordinate()-b1.getRadius()-Plant.RADIUS);
				Pair<Integer,Integer> downRight = getPlantGridPosition(b1.getXCoordinate()+b1.getRadius()+Plant.RADIUS,b1.getYCoordinate()+b1.getRadius()+Plant.RADIUS);
				for (int w = upLeft.getX(); w <= downRight.getX(); w++) {
					for (int h = upLeft.getY(); h <= downRight.getY(); h++) {
						Plant p = plantGrid.getGrid()[w][h].getPlant();
						if (p != null && b1.edgeDistanceTo(p) < 0) {
							plantGrid.getGrid()[w][h].plantEaten();
							b1.changeStomachContent(Plant.EATEN_VALUE);
						}
					}
				}
			}
		}
		//attacking
		for (int i = 0; i < creatures.size(); i++) {
			if (creatures.get(i).attacks()) {
				Body attacker = creatures.get(i).getBody();
				double attackRange = attacker.getRadius()+Body.SPIKE_LENGTH;
				double attackRadians = Math.toRadians(attacker.getRotationAngle());
				Pair<Double,Double> pointOfAttack = new Pair<Double,Double>(attacker.getXCoordinate()+Math.cos(attackRadians)*attackRange, attacker.getYCoordinate()+Math.sin(attackRadians)*attackRange);
				for (int j = 0; j < creatures.size(); j++) {
					if (i != j) {
						Creature attacked = creatures.get(j);
						if (attacked.getBody().inRangeOf(pointOfAttack, attacked.getBody().getRadius())) {
							attacked.getBrain().getInputMask().gotHurt = true;
							attacked.getBody().changeLife(-Creature.ATTACK_DMG);
						};
					}
				}
			}
	
		}
		HashSet<Creature> deadCreatures = new HashSet<Creature>();
		for (int i = 0; i < creatures.size(); i++) {
			if (!creatures.get(i).isAlive()) {
				deadCreatures.add(creatures.get(i));
			}
		}
		creatures.removeAll(deadCreatures);
		plantGrid.calculateGrowth();
		//Input & Output
		for (int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			c.workBrain(this);
			c.workBody(this);
			c.doAge();
			
		}
		creatures.addAll(newCreatures);
	}
}