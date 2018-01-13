package main.simulation;

import java.util.TimerTask;

import javafx.application.Platform;
import main.gui.WorldCanvas;
import main.gui.WorldWindowCtrl;
import main.simulation.world.World;

public class SimulationTask extends TimerTask{
	public static final int NUMBER_OF_STEPS_PER_DRAW = 2;
	
	
	private World world;
	private WorldWindowCtrl control;
	
	
	
	public SimulationTask(World theWorld, WorldWindowCtrl theControl) {
		world = theWorld;
		control = theControl;
		control.getWorldCanvas().setWorld(world);
	}
	
	@Override
	public void run() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < NUMBER_OF_STEPS_PER_DRAW; i++) {
						world.step();
					}
					
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				control.getWorldCanvas().draw();
				displayUserInterface();
			}
				
		});
	}
	
	private void displayUserInterface() {
		control.displayCreatureCount(world.getNumberOfCreatures());
		control.displayPlantCount(world.getNumberOfPlants());
	}

}