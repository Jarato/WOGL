package simulation;

import java.util.TimerTask;

import gui.WorldCanvas;
import gui.WorldWindowCtrl;
import javafx.application.Platform;
import simulation.world.World;

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
				world.step();
				control.getWorldCanvas().draw();
			}
				
		});
	}

}
