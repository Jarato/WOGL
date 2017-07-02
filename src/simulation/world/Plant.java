package simulation.world;

import javafx.scene.paint.Color;
import pdf.simulation.CollisionCircle;
import simulation.Consts;

public class Plant extends CollisionCircle {


	public Plant(double radius, double xPosition, double yPosition) {
		super(radius, xPosition, yPosition);

	}

	public Color getColor() {
		return Consts.PLANT.COLOR;
	}

}
