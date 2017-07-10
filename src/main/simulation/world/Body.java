package main.simulation.world;

import javafx.scene.paint.Color;
import pdf.ai.dna.DNA;
import pdf.ai.dna.Evolutionizable;
import pdf.simulation.CollisionCircle;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class Body extends CollisionCircle implements Evolutionizable{
	//CONSTS
	public static final double MAX_STOMACH = 100;
	public static final double MAX_LIFE = 100;
	public static final int NUMBER_OF_GENES = 4;
	public static final double RADIUS = 7.0;
	public static final double MOVE_BREAK_PERCENT = 0.98;
	public static final double MOVE_ACCELERATION_BASE = 0.01;
	public static final double ROTATE_BREAK_PERCENT = 0.8;
	public static final double ROTATE_ACCELERATION_BASE = 2;
	public static final double SPIKE_LENGTH = 3.0;
	public static final double SIGHTANGLE_MIN = 90;
	public static final double SIGHTANGLE_MAX = 270;
	
	
	
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
    private double rotationAngle;
    private double rotationVelocity;
    private Color color;
    private double sightAngle;
    private double sightAreaWidth;

    //METHODS

    public Body(double radius, double xPosition, double yPosition) {
        super(radius, xPosition, yPosition);
        velocity = new Pair<Double,Double>(0.0,0.0);
        stomach = new Pair<Double,Double>(0.0,MAX_STOMACH);
        life = new Pair<Double,Double>(0.0,MAX_LIFE);
        rotationVelocity = 0;
        rotationAngle = 0;
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

    @Override
    public void compoundDNA() {
        //Color
        double[] rgb = this.dna.normTo(0.0, 1.0);
        this.setColor(new Color(rgb[0], rgb[1], rgb[2], 1.0));   
        sightAngle = this.dna.getNormedGene(3, SIGHTANGLE_MIN, SIGHTANGLE_MAX);
        sightAreaWidth = sightAngle/Brain.NUMBER_OF_SIGHT_AREAS;
    }

}