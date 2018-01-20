package main.simulation.world.creature;

import javafx.scene.paint.Color;
import pdf.simulation.CollisionCircle;

public class Cadaver extends CollisionCircle{
	public static final double RADIUS_MASS_MULTIPLIER = 30;
	public static final double MASS_DECAY_PERCENT = 0.001;
	public static final double MASS_DECAY_STATIC = 0.1;
	public static final double SATURATION_DECAY_PER_STEP = 0.01;
	public static final double DIGESTION_VALUE = 20;
	public static final double EATEN_PER_BITE = 30;
	//public static final double MOVEMENT_SLOW_BY_OVERWALK = 0.5;
	
	private double mass;
	private double decay_age;
	private Color initC;
	private Color color;
	
	
	public Cadaver(double radius, double xPosition, double yPosition, double init_mass, Color init_color) {
		super(radius, xPosition, yPosition);
		mass = init_mass;
		initC = init_color;
		color = init_color;
		decay_age = 0;
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean decayed() {
		return mass <= 0;
	}
	
	public void decay() {
		double decay = mass*MASS_DECAY_PERCENT + MASS_DECAY_STATIC;
		mass -= decay;
		decay_age++;
		double color_decay = 1+decay_age*SATURATION_DECAY_PER_STEP;
		color = Color.hsb(initC.getHue(), initC.getSaturation()/color_decay, initC.getBrightness());
	}
	
	public double takeMass(double value) {
		double ret = Math.min(value, mass); // mass = 5 , value = 10          ret = 5
		mass -= value;							//  mass = 15, value = 10     ret = 10
		return ret;
	}

}