package main.simulation.world;

import javafx.scene.paint.Color;
import pdf.ai.dna.DNA;
import pdf.ai.dna.Evolutionizable;
import pdf.simulation.CollisionCircle;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class Body extends CollisionCircle implements Evolutionizable{
	//CONSTS
	//public static final double MAX_STOMACH = 100;
	//public static final double MAX_LIFE = 100;
	public static final int NUMBER_OF_GENES = 6;
	//public static final double RADIUS = 7.0;
	public static final double MIN_RADIUS = 3.0;
	public static final double MAX_RADIUS = 15.0;
	public static final double MOVE_BREAK_PERCENT = 0.98;
	public static final double MOVE_ACCELERATION_BASE = 0.01;
	public static final double ROTATE_BREAK_PERCENT = 0.8;
	public static final double ROTATE_ACCELERATION_BASE = 2;
	public static final double SPIKE_LENGTH = 3.0;
	public static final double SIGHTANGLE_MIN = 60;
	public static final double SIGHTANGLE_MAX = 300;
	
	
	
	//ATTRIBUTES

    private DNA dna;
    private final Pair<Double,Double> velocity;
    /**
     * X current value, Y maximum value
     */
    private final Pair<Double,Double> stomach;
    /**
     * X current value, Y maximum value
     */
    private final Pair<Double,Double> life;
    /**
     * 0 - 360
     */
    private double moveAcceleration;
    private double moveBreakValue;
    private double energyLossBase;
    private double energyLossAcc;
	private double energyLossRot;
    private double rotationAngle;
    private double rotationVelocity;
    private double rotationAcceleration;
    private int splitTimerBase;
    private Color color;
    private double sightAngle;
    private double sightAreaWidth;

    //METHODS

    public Body(double radius, double xPosition, double yPosition) {
        super(radius, xPosition, yPosition);
        velocity = new Pair<Double,Double>(0.0,0.0);
        stomach = new Pair<Double,Double>(0.0,0.0);
        life = new Pair<Double,Double>(0.0,0.0);
        rotationVelocity = 0;
        rotationAngle = 0;
    }
    
    public double getEnergyLossAcc() {
		return energyLossAcc;
	}

	public double getEnergyLossRot() {
		return energyLossRot;
	}
    
    public double getMoveAcceleration() {
    	return moveAcceleration;
    }
    
    public double getEnergyLossBase() {
    	return energyLossBase;
    }
    
    public double getMoveBreakValue() {
    	return moveBreakValue;
    }
    
    public Pair<Double,Double> getLife() {
    	return this.life;
    }
    
    public double getSightAngle() {
    	return sightAngle;
    }
    
    public double getSightAreaWidth() {
    	return sightAreaWidth;
    }

    public Pair<Double,Double> getStomach() {
    	return this.stomach;
    }

    public Pair<Double,Double> getVelocity() {
    	return this.velocity;
    }
    
    public double getRotationVelocity() {
    	return rotationVelocity;
    }
    
    public void rotate(double rVelocity) {
    	this.rotationAngle = UtilMethods.rotate360(rotationAngle+rVelocity);
    }

    public void accelerateRotationDirect(double angleAcc) {
    	this.rotationVelocity += angleAcc;
    }
    
    public void accelerateRotationPercent(double anglePercent) {
    	this.rotationVelocity *= anglePercent;
    }

    public void acceleratePercent(double accPercent) {
    	velocity.set(velocity.getX()*accPercent, velocity.getY()*accPercent);
    }

    public void accelerateDirect(double accX, double accY) {
    	velocity.set(velocity.getX()+accX, velocity.getY()+accY);
    }

    public void accelerateAngle(double angle, double length) {
    	double radians = Math.toRadians(angle);
		velocity.set(velocity.getX()+length*Math.cos(radians), velocity.getY()+length*Math.sin(radians));
    }
    
    public double getRotationAcceleration(){
    	return rotationAcceleration;
    }

    public void changeStomachContent(double value) {
    	stomach.setX(stomach.getX()+value);	
    }

    public void changeLife(double value) {
    	life.setX(life.getX()+value);
    }
    
    public void checkStomachBounds() {
    	if (stomach.getX() < 0.0) {
    		stomach.setX(0.0);
    	} else if(stomach.getX() > stomach.getY()) {
    		stomach.setX(stomach.getY());
    	}
    }
    
    public void checkLifeBounds() {
    	if (life.getX() < 0.0) {
    		life.setX(0.0);
    	} else if(life.getX() > life.getY()) {
    		life.setX(life.getY());
    	}
    }

    public double getRotationAngle() {
    	return rotationAngle;
    }

    @Override
    public int getNumberOfNeededGenes() {
        return NUMBER_OF_GENES;
    }

    @Override
    public DNA getDNA() {
        return this.dna;
    }

    @Override
    public void compoundDNA(DNA newDNA) {
    	if (newDNA.getNumberOfGenes() != this.getNumberOfNeededGenes()) throw new IllegalArgumentException("number of genes in the given DNA-Object does not match the number of needed genes of this creature");
        this.dna = newDNA;
        compoundDNA();
    }

    public void setColor(Color newColor){
        this.color = newColor;
    }

    public Color getColor() {
        return color;
    }
    
    public int getSplitTimerBase() {
    	return splitTimerBase;
    }

    @Override
    public void compoundDNA() {
        //Color
        this.setColor(new Color(dna.getNormedGene(0, 0.0,1.0), dna.getNormedGene(1, 0.0,1.0), dna.getNormedGene(2, 0.0,1.0), 1.0)); 
        //SightAngle
        sightAngle = this.dna.getNormedGene(3, SIGHTANGLE_MIN, SIGHTANGLE_MAX);
        sightAreaWidth = sightAngle/Brain.NUMBER_OF_SIGHT_AREAS;
        //Radius
        setRadius(this.dna.getNormedGene(4, MIN_RADIUS, MAX_RADIUS));
        double t = (this.radius/3.0)*(this.radius/3.0); 
        rotationAcceleration = 5.0/t;
        moveAcceleration = 0.1/t;//0.09/t+0.0004;
        moveBreakValue = 0.92 + this.radius/200.0;
        splitTimerBase = (int)Math.round(this.radius * 50);
        double stomachLifeValue = this.radius*40;
        energyLossBase = stomachLifeValue*0.00001+0.01;
        energyLossAcc = stomachLifeValue *0.00003;
        energyLossRot = stomachLifeValue *0.00001;
        //Stomach/Life-Portion
        double stomachPercent = this.dna.getNormedGene(5, 0.25, 0.75);
        this.stomach.setY(stomachPercent*stomachLifeValue);
        this.life.setY((1-stomachPercent)*stomachLifeValue);
    }

}