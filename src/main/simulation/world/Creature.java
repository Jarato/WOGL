package main.simulation.world;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;
import main.simulation.world.Brain.InputMask;
import main.simulation.world.PlantGrid.PlantBox;
import pdf.ai.dna.DNA;
import pdf.ai.dna.EvolutionMethods;
import pdf.ai.dna.Evolutionizable;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class Creature implements Evolutionizable{
	public static final int SPLIT_TIMER_GOBACK = 1;
	public static final int ATTACK_COOLDOWN_BASE = 100;
	public static final double ATTACK_DMG = 10;
	public static final double MUTATION_RATE = 0.03;
	public static final double MUTATION_STRENGTH = 0.1;
	public static final double ENERGY_GAIN_ATTACK = 10;
	public static final double LIFE_LOSS_NO_ENERGY = 0.2;
	public static final long MAX_AGE = 10000;
	
	private final Body body;
    private final Brain brain = new Brain(Brain.NUMBER_OF_INPUTS, Brain.NUMBER_OF_INTERCELLS, Brain.NUMBER_OF_OUTPUTS);
    private DNA dna;
    private int id;
    private long age;
    private int generation;
    private boolean eatingActive;
    private boolean attackingActive;
    private boolean splittingActive;
    private int splitTimer;
    private int attackCooldownTimer;
    private int parentId;
    private ArrayList<Integer> childrenId;

    public Creature(int newID, double xPosition, double yPosition, Random rnd) {
        this.id = newID;
        this.body = new Body(0, xPosition, yPosition);
        childrenId = new ArrayList<Integer>();
        this.dna = new DNA(getNumberOfNeededGenes());
        this.dna.setRandom(rnd);
        compoundDNA();
        initTimer();
        generation = 0;
        parentId = -1;
    }
    
    public Creature(int newID, DNA newDNA, double xPosition, double yPosition) {
        this.id = newID;
        this.body = new Body(0, xPosition, yPosition);
        childrenId = new ArrayList<Integer>();
        compoundDNA(newDNA);
        initTimer();
        generation = 0;
        parentId = -1;
    }
    
    public ArrayList<Integer> getChildrenIdList() {
    	return childrenId;
    }
    
    public void setParentId(int pId) {
    	parentId = pId;
    }

    public int getParentId() {
    	return parentId;
    }
    
    public int getGeneration() {
    	return generation;
    }
    
    public void setGeneration(int newGen) {
    	generation = newGen;
    }
    
    public void doAge() {
    	age++;
    	if (age > MAX_AGE) {
    		body.getLife().setX(0.0);
    	}
    }
    
    public long getAge() {
    	return age;
    }
    
    private void initTimer() {
    	splitTimer = this.body.getSplitTimerBase();
    	attackCooldownTimer = 0;
    	age = 0;
    }
    
    public int getSplitTimer() {
    	return splitTimer;
    }

    @Override
    public int getNumberOfNeededGenes() {
        return this.brain.getNumberOfNeededGenes()+this.body.getNumberOfNeededGenes();
    }

    @Override
    public DNA getDNA() {
        return this.dna;
    }

    public boolean eats() {
    	return eatingActive;
    }
    
    public void setId(int newId) {
    	if (this.id == -1) {
    		this.id = newId;
    	} else {
    		System.out.println("Die ID ("+this.id+") has already been set. Creature-id "+newId+" couldn't be set");
    	}
    }

    public boolean attacks() {
    	return attackingActive;
    }

    public boolean splits() {
    	return splittingActive;
    }
    
    public int getId() {
    	return id;
    }
    
    public Body getBody() {
    	return body;
    }

    @Override
    public void compoundDNA(DNA newDNA) {
        this.dna = newDNA;
        compoundDNA();
    }
    
    public Brain getBrain() {
    	return brain;
    }

    @Override
    public void compoundDNA() {
        int split= brain.getNumberOfNeededGenes();
        brain.compoundDNA(this.dna.getSequence(0, split));
        body.compoundDNA(this.dna.getSequence(split, body.getNumberOfNeededGenes()));
    }

    public void workBrain(World theWorld) {
        workEyes(theWorld);
        brain.getInputMask().stomachPercent = body.getStomach().getX()/body.getStomach().getY();
        brain.getInputMask().lifePercent = body.getLife().getX()/body.getLife().getY();
        brain.getInputMask().movementSpeed = body.getSpeed();
        //set inputs
        brain.applyInputMask();
        //end of input sets.
        brain.calculateNet();
    }

    private void workEyes(World theWorld) {
    	InputMask mask = brain.getInputMask();
        //Setting everything to "seeing nothing"
    	mask.resetEyesInput();
        //Seeing plants
        PlantBox[][] grid = theWorld.getPlantGrid().getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].getPlant() != null && this.body.inRangeOf(grid[i][j].getPlant(), Brain.SIGHT_RANGE+body.getRadius()+grid[i][j].getPlant().getRadius())) {
                    int whichEye = getViewArea(this.body.angleTo(grid[i][j].getPlant()));
                    if (whichEye >= 0 && whichEye < Brain.NUMBER_OF_SIGHT_AREAS) {
                        double distance = this.body.edgeDistanceTo(grid[i][j].getPlant());
                        if (mask.eyesInputPlant[whichEye].getX() > distance) {
                        	mask.eyesInputPlant[whichEye].set(distance, Plant.COLOR);
                        }
                    }
                }
            }
        }
        //Seeing other creatures
        ArrayList<Creature> creatureList = theWorld.getCreatures();
        for(Creature crt : creatureList) {
            if (crt.id != this.id) {
                if (this.body.inRangeOf(crt.body, Brain.SIGHT_RANGE+this.getBody().getRadius()+crt.getBody().getRadius())) {
                    int whichEye = getViewArea(this.body.angleTo(crt.body));
                    if (whichEye >= 0 && whichEye < Brain.NUMBER_OF_SIGHT_AREAS) {
                        double distance = this.body.edgeDistanceTo(crt.body);
                        if (mask.eyesInputCreature[whichEye].getX() > distance) {
                        	mask.eyesInputCreature[whichEye].set(distance, crt.body.getColor());
                        }
                    }
                }
            }
        }
        //Seeing walls
        double angleBase = this.body.getRotationAngle()-(body.getSightAngle()/2.0)+(body.getSightAreaWidth()/2.0); //Base, the middle of the 8
        for (int i = 0; i < brain.getInputMask().eyesInputWall.length; i++) {
            double angleRadians = Math.toRadians(UtilMethods.rotate360(angleBase+i*body.getSightAreaWidth()));
            Pair<Double,Double> vector = new Pair<Double,Double>(Math.cos(angleRadians), Math.sin(angleRadians));
            double distanceX = Double.MAX_VALUE;
            double distanceY = Double.MAX_VALUE;
            if (vector.getX() > 0) {
                distanceX = (World.SIZE-this.body.getXCoordinate()-this.body.getRadius());
            } else if (vector.getX() < 0) {
                distanceX = Math.abs((this.body.getXCoordinate()-this.body.getRadius()));
            }
            if (vector.getY() > 0) {
                distanceY = (World.SIZE-this.body.getYCoordinate()-this.body.getRadius());
            } else if (vector.getY() < 0) {
                distanceY = Math.abs((this.body.getYCoordinate()-this.body.getRadius()));
            }
            double distance;
            if (vector.getX() == 0) {
                distance = distanceY;
            } else if (vector.getY() == 0) {
                distance = distanceX;
            } else {
            	double tempYDistance = distanceX/vector.getX()*vector.getY();

            	double tempXDistance = distanceY/vector.getY()*vector.getX();
            	distance = Math.min(tempYDistance*tempYDistance+distanceX*distanceX, distanceY*distanceY+tempXDistance*tempXDistance);
            	distance = Math.sqrt(distance);
            }
            if (mask.eyesInputWall[i].getX() > distance) {
            	mask.eyesInputWall[i].set(distance, World.WALL_COLOR);
            }
        }
    }

    private int getViewArea(double angle) {
    	//int res = (int)(Math.floor((angle-(body.getRotationAngle()-body.getSightAngle()/2.0))/body.getSightAreaWidth()+360.0/body.getSightAreaWidth())%(360.0/body.getSightAreaWidth()));
    	int res = (int) Math.floor(((angle-(body.getRotationAngle()-body.getSightAngle()/2.0) + 360)%360)/body.getSightAreaWidth());
    	
    	//int res = (int)(Math.floor((angle-(body.getRotationAngle()-body.getSightAngle()/2.0))/body.getSightAreaWidth()));
    	/*if (res == -1) {
    		System.out.println("angle: "+angle+"\trotation: "+this.body.getRotationAngle());
    		System.out.println("rotation-MAX_ANGLE/2: "+(this.body.getRotationAngle()-Brain.SIGHT_MAXANGLE/2.0));
    		System.out.println("angle - (rotation-MAX_ANGLE/2): "+(angle-(this.body.getRotationAngle()-Brain.SIGHT_MAXANGLE/2.0)));
    		try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}*/
        return res;
    }

    public void workBody(World theWorld) {
    	int[] interpretedOutput = this.brain.interpretOutput();
    	//MOVE
    	double moveAcc = body.getMoveAcceleration();
    	this.body.acceleratePercent(body.getMoveBreakValue());
    	switch(interpretedOutput[0]) {
    		case 1: body.accelerateAngle(body.getRotationAngle(), moveAcc);
    			break;
    		case 2:body.accelerateAngle(body.getRotationAngle()+180.0, moveAcc);
    			break;
    	}
    	switch(interpretedOutput[1]) {
		case 1: body.accelerateAngle(body.getRotationAngle()+90.0, moveAcc);
			break;
		case 2:body.accelerateAngle(body.getRotationAngle()+270.0, moveAcc);
			break;
    	}
    	//ROTATE
    	this.body.accelerateRotationPercent(Body.ROTATE_BREAK_PERCENT);
    	switch(interpretedOutput[2]) {
		case 1:	body.accelerateRotationDirect(-body.getRotationAcceleration());
			break;
		case 2: body.accelerateRotationDirect(body.getRotationAcceleration());
			break;
    	}
    	//ACTIONS
    	eatingActive = (interpretedOutput[3] == 1?true:false);
    	attackingActive = (interpretedOutput[4] == 1?true:false);
    	splittingActive = (interpretedOutput[5] == 1?true:false);
    	//ENERGY & LIFE CHANGE
    	if (body.getStomach().getX() == 0) {
    		body.changeLife(-LIFE_LOSS_NO_ENERGY);
    	}
    	body.changeStomachContent(-body.getEnergyLossBase());
    	if (interpretedOutput[0] > 0) body.changeStomachContent(-body.getEnergyLossAcc());
    	if (interpretedOutput[1] > 0) body.changeStomachContent(-body.getEnergyLossAcc());
    	if (interpretedOutput[2] > 0) body.changeStomachContent(-body.getEnergyLossRot()); 	
    	if (body.getLife().getX() < body.getLife().getY() && body.getStomach().getX() > 0) { //automatic healing
    		body.changeStomachContent(-this.body.getEnergyLossHeal());
    		body.changeLife(this.body.getHealAmount()/(1+UtilMethods.point2DLength(body.getVelocity())));
    	}
    	body.checkLifeBounds();
    	body.checkStomachBounds();
    	workActionTimer(theWorld);
    //if (id == 0) System.out.println(body.getRotationAngle()+"\t"+body.getRotationVelocity());
    }
    
    private void workActionTimer(World theWorld)  {
    	if (body.getSpeed() > Body.ABLE_TO_EAT_SPEEDTHRESHOLD) eatingActive = false;
    	if (body.getLife().getX() < body.getLife().getY()/2.0) splittingActive = false;
    	if (body.getStomach().getX() < body.getStomach().getY()/2.0) splittingActive = false;
    	if (body.getStomach().getX() == 0) {
    		attackingActive = false;
    	}
    	if (splittingActive) {
    		splitTimer--;
    		if (splitTimer < 0) {
    			theWorld.splitCreature(this);
    			splitTimer = this.body.getSplitTimerBase();
    		}
    	} else {
    		splitTimer+=SPLIT_TIMER_GOBACK;
    		if (splitTimer> this.body.getSplitTimerBase()) splitTimer = this.body.getSplitTimerBase();
    	}
    	attackCooldownTimer--;
    	if (attackCooldownTimer > 0) attackingActive = false;
    	if (attackingActive) {
    		attackCooldownTimer = ATTACK_COOLDOWN_BASE;
    		body.changeStomachContent(-this.body.getEnergyLossAttack());
    	}
    }
    
    public Creature split(Random rnd) {
    	DNA nDNA = this.dna.getSequence(0, this.dna.getNumberOfGenes());
    	nDNA = EvolutionMethods.mutate(nDNA,MUTATION_RATE, MUTATION_STRENGTH, rnd);
    	Creature c = new Creature(-1, nDNA, this.body.getXCoordinate(), this.body.getYCoordinate());
    	c.body.getLife().setX(this.body.getLife().getX()/2.0);
    	c.body.getStomach().setX(this.body.getStomach().getX()/2.0);
    	c.body.checkLifeBounds();
    	c.body.checkStomachBounds();
    	c.setGeneration(this.generation+1);
    	this.body.changeLife(-this.body.getLife().getX()/2.0);
    	this.body.changeStomachContent(-this.body.getStomach().getX()/2.0);
    	return c;
    }
    
    public boolean isAlive() {
    	return body.getLife().getX() > 0;
    }

    public void move() {
    	Pair<Double,Double> moveVel = body.getVelocity();
    	body.move(moveVel.getX(), moveVel.getY());
    	body.rotate(body.getRotationVelocity());
    }
    
}