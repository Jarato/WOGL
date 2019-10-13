package simulation.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javafx.scene.paint.Color;
import pdf.util.Pair;
import pdf.util.UtilMethods;
import simulation.world.creature.Body;
import simulation.world.creature.Cadaver;
import simulation.world.creature.Creature;
import simulation.world.environment.Rock;
import simulation.world.environment.RockSystem;
import statistic.Datapack.DATATYPE;
import statistic.Statistic;
import statistic.StatisticManager;

public class World {
	//CONSTS
	public static final Color NOTHING_COLOR = Color.WHITE;
	public static final double SIZE = 3000;
	public static final int NUMBER_OF_STARTING_CREATURES = 100;
	public static final int NUMBER_OF_STARTING_PLANTS = 1500;
	public static final int STEP_PER_STAT_UPDATE = 100;
	//ATTRIBUTES
	private final RockSystem rockSys;
	private final ArrayList<Creature> creatures;
	private final HashSet<Creature> newCreatures;
	private final ArrayList<Cadaver> cadavers;
	private final PlantGrid plantGrid;
	private final Random randomizer;
	private final long worldSeed;
	private final Statistic creature_count_stat;
	private final Statistic plant_count_stat;
	private int nextId;
	private int maxGen;
	private long step;
	
	public World() {
		worldSeed = new Random().nextLong();
		newCreatures = new HashSet<Creature>();
		randomizer = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plantGrid = new PlantGrid(randomizer);
		cadavers = new ArrayList<Cadaver>();
		rockSys = new RockSystem(randomizer);
		maxGen = 0;
		step = 0;
		creature_count_stat = StatisticManager.createStatistic("creature count");
		plant_count_stat = StatisticManager.createStatistic("plant count");
		creature_count_stat.addDataByMethod(this, "getNumberOfCreatures", DATATYPE.INTEGER);
		plant_count_stat.addDataByMethod(plantGrid, "getNumberOfLivingPlants", DATATYPE.INTEGER);
	}

	public World(long seed) {
		worldSeed = seed;
		newCreatures = new HashSet<Creature>();
		randomizer = new Random(worldSeed);
		creatures = new ArrayList<Creature>();
		plantGrid = new PlantGrid(randomizer);
		cadavers = new ArrayList<Cadaver>();
		rockSys = new RockSystem(randomizer);
		maxGen = 0;
		step = 0;
		creature_count_stat = StatisticManager.createStatistic("creature count");
		plant_count_stat = StatisticManager.createStatistic("plant count");
		creature_count_stat.addDataByMethod(this, "getNumberOfCreatures", DATATYPE.INTEGER);
		plant_count_stat.addDataByMethod(plantGrid, "getNumberOfLivingPlants", DATATYPE.INTEGER);
	}
	
	public void initialize() {		
		nextId = 0;
		rockSys.createTestRock();
		for (int i = 0; i < NUMBER_OF_STARTING_CREATURES; i++) {
			Pair<Double,Double> rndPos = getRandomWorldPosition(Body.MAX_RADIUS);
			Creature c = new Creature(getNextId(),rndPos.getX(), rndPos.getY(), randomizer);
			c.getBody().rotate(randomizer.nextDouble()*360);
			c.getBody().getLife().setX(c.getBody().getLife().getY());
			c.getBody().getStomach().setX(c.getBody().getStomach().getY());
			creatures.add(c);
			if (i == 0) {
				System.out.println("genes: "+c.getNumberOfNeededGenes()+"\tbrain: "+c.getBrain().getNumberOfNeededGenes()+"\tbody: "+c.getBody().getNumberOfNeededGenes());
			}
		}
		for (int i = 0; i < NUMBER_OF_STARTING_PLANTS; i++) {
			Pair<Integer,Integer> rndPos = plantGrid.getRandomGridPosition();
			plantGrid.getGrid()[rndPos.getX()][rndPos.getY()].growPlant();
			Plant p = plantGrid.getGrid()[rndPos.getX()][rndPos.getY()].getPlant();
			if (rockSys.checkInBounds(p)) {
				plantGrid.getGrid()[rndPos.getX()][rndPos.getY()].deletePlant();
			} else {
				plantGrid.getGrid()[rndPos.getX()][rndPos.getY()].getPlant().setDieTimer(randomizer.nextInt((int)(Plant.BASE_DIE_TIME*0.5))+Plant.BASE_DIE_TIME*0.5);
			}	
		}
		plantGrid.initNumberOfLivingPlants();
		System.out.println("Worldseed: "+worldSeed);
	}
	
	public void gatherWorldStepStats() {
		creature_count_stat.observe();
		plant_count_stat.observe();
	}
	
	public RockSystem getRockSystem() {
		return rockSys;
	}
	
	public ArrayList<Cadaver> getCadavers(){
		return cadavers;
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
	
	public int getNumberOfCreatures() {
		if (creatures == null) return 0;
		return creatures.size();
	}
	
	public int getNumberOfPlants() {
		if (plantGrid == null) return 0;
		return plantGrid.getNumberOfLivingPlants();
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
	
	public void splitCreature(Creature c) {
		Creature newC = c.split(randomizer);
		newC.setParentId(c.getId());
		newC.setId(getNextId());
		c.getChildrenIdList().add(newC.getId());
		if (maxGen < newC.getGeneration()) {
			maxGen = newC.getGeneration();
			System.out.println("Step\t"+step+"\tGeneration\t"+maxGen);
		}
		newCreatures.add(newC);
	}
	
	public Creature getCreatureById(int id) {
		for(int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			if (c.getId() == id) return c;
		}
		return null;
	}
	
	
	
	public synchronized void step() {
		newCreatures.clear();
		//Move
		for (int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			c.move();
			c.getBrain().getInputMask().reset_Col_GotHurt();
			c.getBody().resetSlowPercent();
			c.doAge();
		}
		//Collision
		for (int i = 0; i < creatures.size(); i++) {
			Creature c1 = creatures.get(i);
			Body b1 = c1.getBody();
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
						c1.calculateCollision(b2, Body.COLLISION_HARDNESS);
						creatures.get(j).calculateCollision(b1, Body.COLLISION_HARDNESS);
				//	}
				}
			}
			//collision with cadavers
			for (int j = 0; j < cadavers.size(); j++) {
				Cadaver cad = cadavers.get(j);
				if (b1.inRangeOf(cad, b1.getRadius()+cad.getRadius())) {
					if (creatures.get(i).eats()) {
						creatures.get(i).digest(Cadaver.DIGESTION_VALUE, 1);
						cad.takeMass(Cadaver.EATEN_PER_BITE);
					}
					b1.mulSlowPercent(cad.calculateSlow(b1.getRadius()));
					c1.calculateCollision(cad, Cadaver.COLLISION_HARDNESS);
				}
			}
		
			//collision with world-bounds
			if(b1.getXCoordinate()<b1.getRadius()) { // left side
				b1.setXCoordinate(b1.getRadius());
				c1.calculateCollision(new Pair<Double,Double>(0.0,b1.getYCoordinate()), Rock.COLLISION_HARDNESS);
			} else if (b1.getXCoordinate()>SIZE-b1.getRadius()) { // right side
				b1.setXCoordinate(SIZE-b1.getRadius());
				c1.calculateCollision(new Pair<Double,Double>(World.SIZE,b1.getYCoordinate()), Rock.COLLISION_HARDNESS);
			}
			if(b1.getYCoordinate()<b1.getRadius()) { // top side
				b1.setYCoordinate(b1.getRadius());
				c1.calculateCollision(new Pair<Double,Double>(b1.getXCoordinate(),0.0), Rock.COLLISION_HARDNESS);
			} else if (b1.getYCoordinate()>SIZE-b1.getRadius()) { // bottom side
				b1.setYCoordinate(SIZE-b1.getRadius());
				c1.calculateCollision(new Pair<Double,Double>(b1.getXCoordinate(),World.SIZE), Rock.COLLISION_HARDNESS);
			}
			// ROCK SYSTEM
			Pair<double[], Double> res = rockSys.getClosestPointTo(b1);
			double[] cp = res.getX();
			double shiftDist = b1.getRadius()-res.getY();
			double[] vecCon = new double[] { cp[0] - b1.getXCoordinate(), cp[1] - b1.getYCoordinate() };
			//check for inbounds of rocks
			if (rockSys.checkInBounds(b1)) {
				double[] shiftVec = UtilMethods.vectorSkalar(vecCon, (b1.getRadius()+res.getY())/res.getY());
				b1.setCoordinates(b1.getXCoordinate() + shiftVec[0], b1.getYCoordinate() + shiftVec[1]);
			} else if (res.getY() < b1.getRadius()) { //collision with rocks
				double[] shiftVec = UtilMethods.vectorSkalar(vecCon, shiftDist/res.getY());
				b1.setCoordinates(b1.getXCoordinate() - shiftVec[0], b1.getYCoordinate() - shiftVec[1]);
				c1.calculateCollision(new Pair<Double,Double>(cp[0],cp[1]), Rock.COLLISION_HARDNESS);
			}
			
			
			//collision with plants
			Pair<Integer,Integer> upLeft = getPlantGridPosition(b1.getXCoordinate()-b1.getRadius()-Plant.RADIUS,b1.getYCoordinate()-b1.getRadius()-Plant.RADIUS);
			Pair<Integer,Integer> downRight = getPlantGridPosition(b1.getXCoordinate()+b1.getRadius()+Plant.RADIUS,b1.getYCoordinate()+b1.getRadius()+Plant.RADIUS);
			for (int w = upLeft.getX(); w <= downRight.getX(); w++) {
				for (int h = upLeft.getY(); h <= downRight.getY(); h++) {
					Plant p = plantGrid.getGrid()[w][h].getPlant();
					if (p != null && b1.edgeDistanceTo(p) < 0) {
						c1.calculateCollision(p, Plant.COLLISION_HARDNESS);	
						if (creatures.get(i).eats()) {
							plantGrid.removePlant(w,h);
							creatures.get(i).digest(Plant.EATEN_VALUE, 0);
						}		
					}
				}
			}
		}
		//attacking
		for (int i = 0; i < creatures.size(); i++) {
			if (creatures.get(i).attacks()) {
				Body attackerB = creatures.get(i).getBody();
				double attackRange = attackerB.getRadius()*Body.SPIKE_LENGTH_PERCENT;
				double attackRadians = Math.toRadians(attackerB.getRotationAngle());
				Pair<Double,Double> spikeVector = new Pair<Double,Double>(Math.cos(attackRadians)*attackRange, Math.sin(attackRadians)*attackRange);
				double spikeSquaredLength = UtilMethods.point2DScalarProduct(spikeVector, spikeVector);
				for (int j = 0; j < creatures.size(); j++) {
					if (i != j) {
						Creature attacked = creatures.get(j);
						Pair<Double,Double> attackedVector = UtilMethods.point2DSubtraction(attacked.getBody(), attackerB);
						double projectionScalar = UtilMethods.point2DScalarProduct(spikeVector, attackedVector)/spikeSquaredLength;
						if (projectionScalar >= 0) {
							if (projectionScalar > 1) projectionScalar = 1;
							double scalar = projectionScalar;
							Pair<Double,Double> closestPointOnSpike = new Pair<Double,Double> (spikeVector.getX()*scalar, spikeVector.getY()*scalar);
							double distance = UtilMethods.point2DLength(UtilMethods.point2DSubtraction(attackedVector, closestPointOnSpike));
							if (distance < attacked.getBody().getRadius()) {
								attacked.getBrain().getInputMask().gotHurt = true;
								attacked.getBody().changeLife(-attackerB.getAttackDmg());
								creatures.get(i).digest(Creature.ENERGY_GAIN_ATTACK, 1);
							}
						}
					}
				}
			}
	
		}
		HashSet<Cadaver> decayedCadavers = new HashSet<Cadaver>();
		//Cadavers decaying
		for (Cadaver cad: cadavers) {
			cad.decay();
			if (cad.decayed()) {
				decayedCadavers.add(cad);
			}
		}
		cadavers.removeAll(decayedCadavers);
		//Dead creature calculations
		HashSet<Creature> deadCreatures = new HashSet<Creature>();
		for (int i = 0; i < creatures.size(); i++) {
			if (!creatures.get(i).isAlive()) {
				deadCreatures.add(creatures.get(i));
				cadavers.add(creatures.get(i).getBody().createCadaver());
			}
		}
		creatures.removeAll(deadCreatures);
		plantGrid.calculateGrowth(rockSys);
		//Input & Output
		for (int i = 0; i < creatures.size(); i++) {
			Creature c = creatures.get(i);
			c.workBrain(this);
			c.workBody(this);
		}
		creatures.addAll(newCreatures);
		step++;
		if (step%STEP_PER_STAT_UPDATE == 0) {
			creature_count_stat.observe();
			plant_count_stat.observe();
		}
	}
	
	
}