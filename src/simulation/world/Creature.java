package simulation.world;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;
import pdf.ai.dna.DNA;
import pdf.ai.dna.Evolutionizable;
import pdf.util.Pair;
import pdf.util.UtilMethods;
import simulation.world.Brain.InputMask;
import simulation.world.PlantGrid.PlantBox;

public class Creature implements Evolutionizable{
	private final Body body;
    private final Brain brain = new Brain(Brain.NUMBER_OF_INPUTS, Brain.NUMBER_OF_INTERCELLS, Brain.NUMBER_OF_OUTPUTS);
    private DNA dna;
    private final int id;
    private boolean eatingActive;
    private boolean attackingActive;
    private boolean splittingActive;

    public Creature(int newID, double xPosition, double yPosition, Random rnd) {
        this.id = newID;
        this.body = new Body(Body.RADIUS, xPosition, yPosition);
        this.dna = new DNA(getNumberOfNeededGenes());
        this.dna.setRandom(rnd);
        compoundDNA();
    }

    public Creature(int newID, DNA newDNA, double xPosition, double yPosition) {
        this.id = newID;
        this.body = new Body(Body.RADIUS, xPosition, yPosition);
        compoundDNA(newDNA);
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
        //set inputs
        brain.applyInputMask();
        //end of input sets.
        brain.calculateNet();
    }

    private void workEyes(World theWorld) {
    	InputMask mask = brain.getInputMask();
        //Setting everything to "seeing nothing"
    	for (int i = 0; i < brain.getInputMask().eyesInputs.length; i++) {
    		mask.eyesInputs[i].set(Brain.SIGHT_RANGE, World.NOTHING_COLOR);
        }
        //Seeing plants
        PlantBox[][] grid = theWorld.getPlantGrid().getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].getPlant() != null && this.body.inRangeOf(grid[i][j].getPlant(), Brain.SIGHT_RANGE)) {
                    int whichEye = getViewArea(this.body.angleTo(grid[i][j].getPlant()));
                    if (whichEye < Brain.NUMBER_OF_SIGHT_AREAS) {
                        double distance = this.body.edgeDistanceTo(grid[i][j].getPlant());
                        if (mask.eyesInputs[whichEye].getX() > distance) {
                        	mask.eyesInputs[whichEye].set(distance, Plant.COLOR);
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
                    if (whichEye < Brain.NUMBER_OF_SIGHT_AREAS) {
                        double distance = this.body.edgeDistanceTo(crt.body);
                        if (mask.eyesInputs[whichEye].getX() > distance) {
                        	mask.eyesInputs[whichEye].set(distance, crt.body.getColor());
                        }
                    }
                }
            }
        }
        //Seeing walls
        double angleBase = this.body.getRotationAngle()-(Brain.SIGHT_MAXANGLE/2.0)+(Brain.SIGHT_AREA_WIDTH/2.0); //Base, the middle of the 8
        for (int i = 0; i < brain.getInputMask().eyesInputs.length; i++) {
            double angleRadians = Math.toRadians(UtilMethods.rotate360(angleBase+i*Brain.SIGHT_AREA_WIDTH));
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
            if (mask.eyesInputs[i].getX() > distance) {
            	mask.eyesInputs[i].set(distance, World.WALL_COLOR);
            }
        }
    }

    private int getViewArea(double angle) {
    	int res = (int)(Math.floor((angle-(this.body.getRotationAngle()-Brain.SIGHT_MAXANGLE/2.0))/Brain.SIGHT_AREA_WIDTH+360.0/Brain.SIGHT_AREA_WIDTH)%(360.0/Brain.SIGHT_AREA_WIDTH));
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
    	this.body.acceleratePercent(Body.MOVE_BREAK_PERCENT);
    	switch(interpretedOutput[0]) {
    		case 1: body.accelerateAngle(body.getRotationAngle(), Body.MOVE_ACCELERATION_BASE);
    			break;
    		case 2:body.accelerateAngle(body.getRotationAngle()+180.0, Body.MOVE_ACCELERATION_BASE);
    			break;
    	}
    	switch(interpretedOutput[1]) {
		case 1: body.accelerateAngle(body.getRotationAngle()+90.0, Body.MOVE_ACCELERATION_BASE);
			break;
		case 2:body.accelerateAngle(body.getRotationAngle()+270.0, Body.MOVE_ACCELERATION_BASE);
			break;
    	}
    	//ROTATE
    	this.body.accelerateRotationPercent(Body.ROTATE_BREAK_PERCENT);
    	switch(interpretedOutput[2]) {
		case 1:	body.accelerateRotationDirect(-Body.ROTATE_ACCELERATION_BASE);
			break;
		case 2: body.accelerateRotationDirect(Body.ROTATE_ACCELERATION_BASE);
			break;
    	}
    	//ACTIONS
    	eatingActive = (interpretedOutput[3] == 1?true:false);
    	attackingActive = (interpretedOutput[4] == 1?true:false);
    	splittingActive = (interpretedOutput[5] == 1?true:false);
    //if (id == 0) System.out.println(body.getRotationAngle()+"\t"+body.getRotationVelocity());
    }
    
    public void move() {
    	Pair<Double,Double> moveVel = body.getVelocity();
    	body.move(moveVel.getX(), moveVel.getY());
    	body.rotate(body.getRotationVelocity());
    }
    
}
