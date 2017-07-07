package simulation.world;

import javafx.scene.paint.Color;
import pdf.simulation.CollisionCircle;

public class Plant extends CollisionCircle {
	public static final Color COLOR = Color.rgb(83, 153, 32);
	public static final double RADIUS = 3.0;
	public static final double EATEN_VALUE = 50;
	public static final int BASE_GROW_TIME = 1000;

	public Plant(double radius, double xPosition, double yPosition) {
		super(radius, xPosition, yPosition);

	}
}
