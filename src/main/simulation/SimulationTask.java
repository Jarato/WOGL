package main.simulation;

import java.util.TimerTask;

import javafx.application.Platform;
import main.gui.WorldCanvas;
import main.gui.WorldWindowCtrl;
import main.simulation.world.World;

public class SimulationTask extends TimerTask{

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
					world.step();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				control.getWorldCanvas().draw();
			}
				
		});
	}

}