package simulation.creature;

import java.util.ArrayList;
import java.util.Random;

import analyse.AnalyseStrg;
import pdf.ai.dna.DNA;
import pdf.ai.dna.EvolutionMethods;
import pdf.ai.dna.Evolutionizable;
import pdf.util.Pair;
import pdf.util.UtilMethods;
import simulation.Plant;
import simulation.World;
import simulation.PlantGrid.PlantBox;
import simulation.creature.Brain.InputMask;
import simulation.environment.rocks.Rock;
import simulation.environment.rocks.RockSystem;

public class Creature implements Evolutionizable{
	public static final int SPLIT_TIMER_GOBACK = 2;
	public static final int ACTION_COOLDOWN_BASE = 100;
	public static final double MUTATION_RATE = 0.05;   
	public static final double MUTATION_STRENGTH = 0.1;
	public static final double ENERGY_GAIN_ATTACK = 5;
	//public static final double LIFE_LOSS_NO_ENERGY = 0.2;
	public static final int STARTAGE_OF_DECAY_RADIUS_FACTOR = 130;
	public static final int STARTAGE_OF_DECAY_BASE = 2000;
	
	// FIXED
	private final Body body;
    private final Brain brain = new Brain(Brain.NUMBER_OF_INPUTS, Brain.NUMBER_OF_INTERCELLS, Brain.NUMBER_OF_OUTPUTS);
    private DNA dna;
    private int id;
    private int generation;
    private int parentId;
    // CHANGING WITH AGE
    private double age_digestionDivider;
    private double age_splitTimerDivider;
    private double age_healParam;
    private double age_moveAccDivider;
    private double age_rotateAccDivider;
    private double age_LineColorDivider;
    private int startage_of_decay;
    
    private int age;
    // CHANGING
    private boolean eatingActive;
    private boolean attackingActive;
    private boolean splittingActive;
    private double splitTimer;
    private int eatCooldownTimer;
    private int attackCooldownTimer;
   
    private ArrayList<Integer> childrenId;

    /*		DEATH SYSTEM
    digestion efficiency	down	#
    split timing			up		#
    heal strength			down	#
    dealing dmg				down	
    movement acc			down	#
    angular acc				down	#
    mutation on split		up		
	*/
    
    
    
    public Creature(int newID, double xPosition, double yPosition, Random rnd) {
        this.id = newID;
        this.body = new Body(0, xPosition, yPosition);
        childrenId = new ArrayList<Integer>();
        this.dna = new DNA(getNumberOfNeededGenes());
        this.dna.setRandom(rnd);
        this.dna.setGeneValue(brain.getNumberOfNeededGenes()+6, 0);
        this.dna.setGeneValue(brain.getNumberOfNeededGenes()+5, 0);
        compoundDNA();
        initDecayValues();
        initTimer();
        generation = 0;
        parentId = -1;
    }
    
    public Creature(int newID, DNA newDNA, double xPosition, double yPosition) {
        this.id = newID;
        this.body = new Body(0, xPosition, yPosition);
        childrenId = new ArrayList<Integer>();
        compoundDNA(newDNA);
        initDecayValues();
        initTimer();
        generation = 0;
        parentId = -1;
    }
    
    private void initDecayValues() {
    	age_digestionDivider = 1;
    	age_splitTimerDivider = 1;
    	age_healParam = 1;
    	age_moveAccDivider = 1;
    	age_rotateAccDivider = 1;
    	age_LineColorDivider = 1;
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
    	if (age > startage_of_decay) {
    		if (age%10 == 0) {
    			double overDecayTime = age-startage_of_decay;
        		age_digestionDivider = 1 + overDecayTime/20000.0;
        		age_splitTimerDivider = 1 + overDecayTime/10000.0;
        		age_healParam = 1 + overDecayTime/1000.0;
        		age_moveAccDivider = 1 + overDecayTime/6000.0;
        		age_rotateAccDivider = 1 + overDecayTime/6000.0;
        		age_LineColorDivider = 1 + overDecayTime/15000.0;
    		}
    		body.mulSlowPercent(1 - 1.0/age_moveAccDivider);
    	} 	
    }
    
    public double getLineColorDivider() {
    	return age_LineColorDivider;
    }
    
    public long getAge() {
    	return age;
    }
    
    private void initTimer() {
    	splitTimer = this.body.getSplitTimerBase();
    	attackCooldownTimer = 0;
    	age = 0;
    }
    
    public double getSplitTimer() {
    	return splitTimer;
    }
    
    public int getStartAgeOfDecay() {
    	return startage_of_decay;
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
    		System.out.println("The ID ("+this.id+") has already been set. Creature-id "+newId+" couldn't be set");
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
        startage_of_decay = (int)(Math.pow(body.getRadius(), 2)*STARTAGE_OF_DECAY_RADIUS_FACTOR+STARTAGE_OF_DECAY_BASE);
    }

    public void workBrain(World theWorld) {
    	long timestamp = System.currentTimeMillis();
        workEyes(theWorld);
        brain.getInputMask().stomachPercent = body.getStomach().getX()/body.getStomach().getY();
        brain.getInputMask().lifePercent = body.getLife().getX()/body.getLife().getY();
        brain.getInputMask().movementSpeed = body.getSpeed();
        //set inputs
        brain.applyInputMask();
        long second = System.currentTimeMillis()-timestamp;
		AnalyseStrg.getSpeedAnalyser().addData("Step.InOut/creature.Brain.Input", second);
        //end of input sets.
		timestamp = System.currentTimeMillis();
		brain.calculateNet();
		second = System.currentTimeMillis()-timestamp;
		AnalyseStrg.getSpeedAnalyser().addData("Step.InOut/creature.Brain.Net", second);
    }
    
    public void digest(double foodValue, int foodType) {
    	// foodType == 0 - plant
    	if (foodType == 0) {
    		this.body.changeStomachContent(this.body.getHerbivore_eff()*foodValue/age_digestionDivider);
    	}
    	// foodType == 1 - creature
    	if (foodType == 1) {
    		this.body.changeStomachContent(this.body.getCarnivore_eff()*foodValue/age_digestionDivider);
    	}
    	eatCooldownTimer = ACTION_COOLDOWN_BASE;
    }

    private void workEyes(World theWorld) {
    	InputMask mask = brain.getInputMask();
        //Setting everything to "seeing nothing"
    	mask.resetEyesInput();
        //Seeing plants
    	long timestamp = System.currentTimeMillis();
        PlantBox[][] grid = theWorld.getPlantGrid().getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].getPlant() != null && this.body.inRangeOf(grid[i][j].getPlant(), Brain.SIGHT_RANGE+body.getRadius()+grid[i][j].getPlant().getRadius())) {
                    int whichEye = getViewArea(this.body.angleTo(grid[i][j].getPlant()));
                    if (whichEye >= 0 && whichEye < Brain.NUMBER_OF_SIGHT_AREAS) {
                        double distance = this.body.edgeDistanceTo(grid[i][j].getPlant());
                        if (mask.eyesInput[whichEye].getX() > distance) {
                        	mask.eyesInput[whichEye].set(distance, Plant.COLOR);
                        }
                    }
                }
            }
        }
        long second = System.currentTimeMillis()-timestamp;
		AnalyseStrg.getSpeedAnalyser().addData("Step.InOut/creature.Brain.Input.SeePlants", second);
        //Seeing other creatures
		timestamp = System.currentTimeMillis();
        ArrayList<Creature> creatureList = theWorld.getCreatures();
        for(Creature crt : creatureList) {
            if (crt.id != this.id) {
                if (this.body.inRangeOf(crt.body, Brain.SIGHT_RANGE+this.getBody().getRadius()+crt.getBody().getRadius())) {
                    int whichEye = getViewArea(this.body.angleTo(crt.body));
                    if (whichEye >= 0 && whichEye < Brain.NUMBER_OF_SIGHT_AREAS) {
                        double distance = this.body.edgeDistanceTo(crt.body);
                        if (mask.eyesInput[whichEye].getX() > distance) {
                        	mask.eyesInput[whichEye].set(distance, crt.body.getColor());
                        }
                    }
                }
            }
        }
        second = System.currentTimeMillis()-timestamp;
		AnalyseStrg.getSpeedAnalyser().addData("Step.InOut/creature.Brain.Input.SeeCreatures", second);
        //Seeing cadavers
		timestamp = System.currentTimeMillis();
        ArrayList<Cadaver> cadaverList = theWorld.getCadavers();
        for (Cadaver cad: cadaverList) {
        	if (this.body.inRangeOf(cad, Brain.SIGHT_RANGE+this.getBody().getRadius()+cad.getRadius())) {
                int whichEye = getViewArea(this.body.angleTo(cad));
                if (whichEye >= 0 && whichEye < Brain.NUMBER_OF_SIGHT_AREAS) {
                    double distance = this.body.edgeDistanceTo(cad);
                    if (mask.eyesInput[whichEye].getX() > distance) {
                    	mask.eyesInput[whichEye].set(distance, cad.getColor());
                    }
                }
            }
        }
        second = System.currentTimeMillis()-timestamp;
		AnalyseStrg.getSpeedAnalyser().addData("Step.InOut/creature.Brain.Input.SeeCadavers", second);
        //Seeing walls/rocks
		timestamp = System.currentTimeMillis();
        double angleBase = this.body.getRotationAngle()-(body.getSightAngle()/2.0)+(body.getSightAreaWidth()/2.0); //Base, the middle of the 8
        for (int i = 0; i < brain.getInputMask().eyesInput.length; i++) {
            double angleRadians = Math.toRadians(UtilMethods.rotate360(angleBase+i*body.getSightAreaWidth()));
            Pair<Double,Double> vector = new Pair<Double,Double>(Math.cos(angleRadians), Math.sin(angleRadians));
            //Seeing world-bounds
            double distanceX = Double.MAX_VALUE;
            double distanceY = Double.MAX_VALUE;
            if (vector.getX() > 0) {
                distanceX = (World.SIZE-this.body.getXCoordinate());
            } else if (vector.getX() < 0) {
                distanceX = Math.abs((this.body.getXCoordinate()));
            }
            if (vector.getY() > 0) {
                distanceY = (World.SIZE-this.body.getYCoordinate());
            } else if (vector.getY() < 0) {
                distanceY = Math.abs((this.body.getYCoordinate()));
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
            	distance = Math.sqrt(distance) - body.getRadius();
            }
          
            
            if (mask.eyesInput[i].getX() > distance) {
            	mask.eyesInput[i].set(distance, Rock.COLOR);
            }
            //Seeing rocks
            RockSystem rockSys = theWorld.getRockSystem();
            double[] bodyP = new double[] {body.getXCoordinate(), body.getYCoordinate()};
            double[] visionLine = new double[] {vector.getX(), vector.getY()};
            visionLine = UtilMethods.vectorAddition(bodyP, UtilMethods.vectorSkalar(visionLine, Brain.SIGHT_RANGE+body.getRadius()));
        
            Pair<double[],Double> res = rockSys.getClosestLineIntersec(bodyP, visionLine);
            double rockDist = res.getY()-body.getRadius();
            if (mask.eyesInput[i].getX() > rockDist) {
            	mask.eyesInput[i].set(rockDist, Rock.COLOR);
            }
        }
        second = System.currentTimeMillis()-timestamp;
		AnalyseStrg.getSpeedAnalyser().addData("Step.InOut/creature.Brain.Input.SeeWallsRocks", second);
    }

    private int getViewArea(double angle) {
    	int res = (int) Math.floor(((angle-(body.getRotationAngle()-body.getSightAngle()/2.0) + 360)%360)/body.getSightAreaWidth());
        return res;
    }
    
    private int getCollisionArea(double angle) {
    	int res = (int) Math.floor(((angle-(body.getRotationAngle()-Brain.COLLISION_DETECTION_AREA_ANGLE/2.0) + 360)%360)/Brain.COLLISION_DETECTION_AREA_ANGLE);
    	return res;
    }
    
    public void calculateCollision(Pair<Double,Double> colPoint, double hardness) {
    	int areaIndex = getCollisionArea(body.angleTo(colPoint));
    	InputMask im = brain.getInputMask();
    	im.collision[areaIndex] = Math.max(im.collision[areaIndex], hardness);
    }
    public void workBody(World theWorld) {
    	int[] interpretedOutput = this.brain.interpretOutput();
    	//MOVE
    	double moveAcc = this.body.getSlowedMoveAcc();
    	//System.out.println(moveAcc);
    	this.body.acceleratePercent(body.getMoveBreakValue());
    	switch(interpretedOutput[0]) {
    		case 1: body.accelerateAngle(body.getRotationAngle(), moveAcc);
    			break;
    		case 2:body.accelerateAngle(body.getRotationAngle()+180.0, moveAcc*Body.MOVE_ACC_BACKWARDS_PERC);
    			break;
    	}
    	switch(interpretedOutput[1]) {
		case 1: body.accelerateAngle(body.getRotationAngle()+90.0, moveAcc*Body.MOVE_ACC_SIDEWAYS_PERC);
			break;
		case 2:body.accelerateAngle(body.getRotationAngle()+270.0, moveAcc*Body.MOVE_ACC_SIDEWAYS_PERC);
			break;
    	}
    	//ROTATE
    	this.body.accelerateRotationPercent(Body.ROTATE_BREAK_PERCENT);
    	double rotateAcc = body.getRotationAcceleration()/age_rotateAccDivider;
    	switch(interpretedOutput[2]) {
		case 1:	body.accelerateRotationDirect(-rotateAcc);
			break;
		case 2: body.accelerateRotationDirect(rotateAcc);
			break;
    	}
    	//ACTIONS
    	eatingActive = (interpretedOutput[3] == 1?true:false);
    	attackingActive = (interpretedOutput[4] == 1?true:false);
    	splittingActive = (interpretedOutput[5] == 1?true:false);
    	//ENERGY & LIFE CHANGE
    	body.changeStomachContent(-body.getEnergyLossBase());
    	body.changeLife(-body.getLifeLossBase());
    	if (interpretedOutput[0] > 0) body.changeStomachContent(-body.getEnergyLossAcc());
    	if (interpretedOutput[1] > 0) body.changeStomachContent(-body.getEnergyLossAcc());
    	if (interpretedOutput[2] > 0) body.changeStomachContent(-body.getEnergyLossRot()); 	
    	if (body.getStomach().getX() > 0) { //automatic healing
    		if (body.getLife().getX() < body.getLife().getY()) body.changeStomachContent(-this.body.getEnergyLossHeal());
    		double stomach_percent = this.body.getStomach().getX() / this.body.getStomach().getY();
    		body.changeLife((body.getHealAmountBase()+body.getLifeLossBase())*Math.sqrt(stomach_percent)/(age_healParam*age_healParam));
    		//body.changeLife(this.body.getHealAmountBase()*(Math.sqrt(stomach_percent)/(age_healParam+1)+(age_healParam*stomach_percent - age_healParam)));
    	}
    	body.checkLifeBounds();
    	body.checkStomachBounds();
    	workActionTimer(theWorld);
    }
    
    private void workActionTimer(World theWorld)  {
    	if (eatCooldownTimer > 0) {
    		eatingActive = false;
    		eatCooldownTimer--;
    	}
    	//if (body.getSpeed() > Body.ABLE_TO_EAT_SPEEDTHRESHOLD) eatingActive = false;
    	if (body.getLife().getX() < body.getLife().getY()*0.8) splittingActive = false;
    	if (body.getStomach().getX() < body.getStomach().getY()*0.6) splittingActive = false;
    	if (body.getStomach().getX() == 0) {
    		attackingActive = false;
    	}
    	if (splittingActive) {
    		splitTimer -= 1/age_splitTimerDivider;
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
    		attackCooldownTimer = ACTION_COOLDOWN_BASE;
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