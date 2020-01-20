package simulation.world;

import javafx.scene.paint.Color;
import pdf.simulation.CollisionCircle;

public class Plant extends CollisionCircle {
	public static final Color COLOR = Color.rgb(83, 153, 32);
	public static final double RADIUS = 3.0;
	public static final double EATEN_VALUE = 30;
	public static final int BASE_GROW_TIME = 30000;
	public static final int BASE_DIE_TIME = 30000;
	public static final int GROWTIMER_NO_NEIGHBOR_UPDATE = 5;
	public static final double COLLISION_HARDNESS = 1.0/3.0;
	
	private double dieTimer;

	public double getDieTimer() {
		return dieTimer;
	}

	public void setDieTimer(double setValue) {
		dieTimer = setValue;
	}
	
	public void addDieTimer(double addValue) {
		dieTimer += addValue;
	}
	
	public boolean plantDead() {
		return dieTimer<=0;
	}

	public Plant(double radius, double xPosition, double yPosition) {
		super(radius, xPosition, yPosition);
	}
	
	
}