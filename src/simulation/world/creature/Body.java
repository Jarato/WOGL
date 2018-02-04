package simulation.world.creature;

import javafx.scene.paint.Color;
import pdf.ai.dna.DNA;
import pdf.ai.dna.Evolutionizable;
import pdf.simulation.CollisionCircle;
import pdf.util.Pair;
import pdf.util.UtilMethods;

public class Body extends CollisionCircle implements Evolutionizable{
	//CONSTS - GENE
	public static final int NUMBER_OF_GENES = 7;
	//CONSTS - MINMAX
	public static final double MIN_RADIUS = 3.0;
	public static final double MAX_RADIUS = 15.0;
	public static final double SIGHTANGLE_MIN = 40;
	public static final double SIGHTANGLE_MAX = 300;
	public static final double STOMACH_LIFE_MIN_PERCENT = 0.2;
	public static final double VORE_CUTOFF = 0.95;
	public static final double VORE_PURE_EFF = 1.1;
	public static final double OVER_EATING_BUFFER = 0.1;
	public static final double OVER_EATING_DMG = 0.4;
	//CONSTS - MOVEMENT
	public static final double MOVE_BREAK_PERCENT = 0.98;
	public static final double MOVE_ACCELERATION_BASE = 0.01;
	public static final double ROTATE_BREAK_PERCENT = 0.8;
	public static final double ROTATE_ACCELERATION_BASE = 2;
	public static final double SPIKE_LENGTH_PERCENT = 1.5;
	//CONSTS - ACTIONS
	public static final double ABLE_TO_EAT_SPEEDTHRESHOLD = 0.3;
	public static final double SPLIT_TIMER_RADIUS_FACTOR = 8;
	
	public static final double COLLISION_HARDNESS = 2.0/3.0;
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
    //Inactive
    private double moveAcceleration;
    private double rotationAcceleration;
    private double moveBreakValue;
    private double carnivore_eff;
    private double herbivore_eff;
    //ENERGY LOSS
    private double energyLossBase;
    private double energyLossAcc;
	private double energyLossRot;
    private double energyLossAttack;
    private double energyLossHeal;
	//Other
    private double healAmount_base;
    private double slowPercent;
    //ACTIVE
	private double rotationAngle;
    private double rotationVelocity;
    
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
        resetSlowPercent();
    }
    
    public Cadaver createCadaver() {
    	Cadaver ret = new Cadaver(this.radius, this.getXCoordinate(), this.getYCoordinate(), this.radius*this.radius*Cadaver.RADIUS_MASS_MULTIPLIER, color);
    	return ret;
    }
    
    public void setRotationAngle(double rotValue) {
    	rotationAngle = rotValue;
    }
    
    public double calculateAngleToRotation(Pair<Double,Double> toPoint) {
    	return this.angleTo(toPoint) - rotationAngle;
    }
    
    public void mulSlowPercent(double value) {
    	slowPercent = slowPercent*(1-value);
    }
    
    public void resetSlowPercent() {
    	slowPercent = 1.0;
    }
    
    public double getSlowPercent() {
    	return slowPercent;
    }
    
    public double getHealAmountBase() {
    	return healAmount_base;
    }
    
    public double getEnergyLossAttack() {
    	return energyLossAttack;
    }
    
    public double getEnergyLossHeal() {
    	return energyLossHeal;
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
    
    public double getSlowedMoveAcc() {
    	return this.moveAcceleration*slowPercent;
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
    		double diff = stomach.getX()-stomach.getY();
    		double buffer = stomach.getY()*OVER_EATING_BUFFER;
    		if (diff > buffer) {
    			changeLife(-(diff-buffer)*OVER_EATING_DMG);
    		}
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
    
    public double getSpeed() {
    	return UtilMethods.point2DLength(velocity);
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
    
    public double getHerbivore_eff() {
		return herbivore_eff;
	}
    
    public double getCarnivore_eff() {
		return carnivore_eff;
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
        moveBreakValue = 0.895 + this.radius/150.0;
        splitTimerBase = (int)Math.round(this.radius*this.radius * SPLIT_TIMER_RADIUS_FACTOR);
        double stomachLifeValue = this.radius*this.radius*2.5 + 100;
        double baseline = stomachLifeValue/1000.0;
        energyLossBase = baseline*0.006 + 0.0005; // bigger too good: higher 1st - lower second ### smaller too good: lower 1st - higher second
        energyLossAcc = baseline *0.005 + 0.001;
        energyLossRot = baseline *0.002;
        energyLossAttack = baseline * 0.5;
        energyLossHeal = baseline * 0.005;
        healAmount_base = baseline * 0.08+0.002;
        //Stomach/Life-Portion
        double stomachPercent = this.dna.getNormedGene(5, STOMACH_LIFE_MIN_PERCENT, 1-STOMACH_LIFE_MIN_PERCENT);
        this.stomach.setY(stomachPercent*stomachLifeValue);
        this.life.setY((1-stomachPercent)*stomachLifeValue);
        double vore_eff = this.dna.getNormedGene(6, 0.0, 1.0);
        if (vore_eff > VORE_CUTOFF) {
        	this.herbivore_eff = VORE_PURE_EFF;
        	this.carnivore_eff = 0;
        } else if (1-vore_eff > VORE_CUTOFF) {
        	this.herbivore_eff = 0;
        	this.carnivore_eff = VORE_PURE_EFF;
        } else {
        	this.herbivore_eff = vore_eff;
        	this.carnivore_eff = 1-vore_eff;
        }
        
    }

}