package simulation;

import javafx.scene.paint.Color;
import pdf.simulation.CollisionCircle;
import pdf.util.Pair;
import simulation.optimization.GridAble;

public class Plant extends CollisionCircle implements GridAble{
	public static final Color COLOR = Color.rgb(63, 125, 32);
	public static final double RADIUS = 3.0;
	public static final double EATEN_VALUE = 30;
	public static final int BASE_GROW_TIME = 25000;
	public static final int BASE_DIE_TIME = 30000;
	public static final int GROWTIMER_NO_NEIGHBOR_UPDATE = 3;
	public static final double COLLISION_HARDNESS = 1.0/3.0;
	
	private double dieTimer;
	private Pair<Integer,Integer> gridPosition;

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

	@Override
	public Pair<Integer, Integer> getGridPosition() {
		return gridPosition;
	}

	@Override
	public void setGridPosition(Pair<Integer, Integer> pos) {
		this.gridPosition = pos;
	}
	
	
}