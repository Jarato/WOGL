package gui;

import javafx.scene.canvas.GraphicsContext;
import simulation.world.World;

public class WorldCanvas extends ResizableCanvas {
	//CONSTS
	public static final double Z = 1;
	
	//ATTRIBUTES
	private World world;
	
	public void setWorld(World newWorld) {
		world = newWorld;
	}
	
	@Override
	public void draw() {
		GraphicsContext gc = this.getGraphicsContext2D();
	}

}
