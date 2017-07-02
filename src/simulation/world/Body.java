package simulation.world;

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
    private Color color;

    //METHODS

    public Body(double radius, double xPosition, double yPosition) {
        super(radius, xPosition, yPosition);
        velocity = new Pair<Double,Double>(0.0,0.0);
        stomach = new Pair<Double,Double>(0.0,MAX_STOMACH);
        life = new Pair<Double,Double>(0.0,MAX_LIFE);
    }

    public Pair<Double,Double> getLife() {
    	return this.life;
    }

    public Pair<Double,Double> getStomach() {
    	return this.stomach;
    }

    public Pair<Double,Double> getVelocity() {
    	return this.velocity;
    }

    public void rotate(double angle) {
    	this.rotationAngle = UtilMethods.rotate360(rotationAngle+angle);
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

    public boolean isAlive() {
    	return this.life.getX() > 0;
    }

    public boolean isStarving() {
    	return this.stomach.getX() == 0;
    }

    public void changeStomachContent(double value) {
    	double newValue = this.stomach.getX()+value;
    	if (newValue < 0.0) {
    		this.stomach.setX(0.0);
    	} else if(newValue > this.stomach.getY()) {
    		this.stomach.setX(this.stomach.getY());
    	} else this.stomach.setX(newValue);
    }

    public void changeLife(double value) {
    	double newValue = this.life.getX()+value;
    	if (newValue < 0.0) {
    		this.life.setX(0.0);
    	} else if(newValue > this.life.getY()) {
    		this.life.setX(this.life.getY());
    	} else this.life.setX(newValue);
    }

    public double getRotationAngle() {
    	return this.rotationAngle;
    }

    @Override
    public int getNumberOfNeededGenes() {
        return Consts.CREATURE.BODY.NUMBER_OF_BODYGENES;
    }

    @Override
    public DNA getDNA() {
        return this.dna;
    }

    @Override
    public void compoundDNA(DNA newDNA) {
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
    	//Size
        setRadius(this.dna.getNormedGene(0, Consts.CREATURE.BODY.SIZE_MINIMUM, Consts.CREATURE.BODY.SIZE_MAXIMUM));
        double maxValue = this.dna.getNormedGene(0, Consts.CREATURE.BODY.STOMACHSIZE_MINIMUM, Consts.CREATURE.BODY.STOMACHSIZE_MAXIMUM);
        stomach.set(maxValue, maxValue);
        life.set(maxValue, maxValue);
        //Color
        double[] rgb = this.dna.getSequence(1, 3).normTo(0.0, 1.0);
        this.setColor(new Color(rgb[0], rgb[1], rgb[2], 1.0));
        //utilization efficiency
        double effValue = this.dna.getNormedGene(4, 0.0, 1.0);
        if (effValue > Consts.CREATURE.BODY.EFFICIENCY_THRESHOLD) {
        	this.efficiency.set(0.0, Consts.CREATURE.BODY.VORE_EFFICIENCY);
        } else if (1.0-effValue > Consts.CREATURE.BODY.EFFICIENCY_THRESHOLD){
        	this.efficiency.set(Consts.CREATURE.BODY.VORE_EFFICIENCY, 0.0);
        } else {
        	this.efficiency.set(1-effValue, effValue);
        }
        //
    }

}
