package simulation;

import java.util.Timer;

import gui.WorldWindowCtrl;
import javafx.application.Platform;
import simulation.world.World;

public class WOGLSimulation {
	private World world;
	private SimulationTask task;
	private Timer timer;
	
	public WOGLSimulation(long seed, WorldWindowCtrl windowCtrl) {
		world = new World(seed);
		initialize(windowCtrl);
	}
	
	public WOGLSimulation(WorldWindowCtrl windowCtrl) {
		world = new World();
		initialize(windowCtrl);
	}
	
	public void startSimulation() {
		timer.schedule(task, 0,50);
	}
	
	public void stopSimulation() {
		timer.cancel();
	}
	
	private void initialize(WorldWindowCtrl control) {
		task = new SimulationTask(world, control);
		timer = new Timer();
	}
}
