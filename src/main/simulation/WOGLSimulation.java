package main.simulation;

import java.util.Timer;

import javafx.application.Platform;
import main.gui.WorldWindowCtrl;
import main.simulation.world.World;

public class WOGLSimulation {
	private World world;
	private SimulationTask task;
	private Timer timer;
	private WorldWindowCtrl control;
	private boolean running;
	
	public WOGLSimulation(long seed, WorldWindowCtrl windowCtrl) {
		world = new World(seed);
		initialize(windowCtrl);
	}
	
	public WOGLSimulation(WorldWindowCtrl windowCtrl) {
		world = new World();
		initialize(windowCtrl);
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void startSimulation() {
		task = new SimulationTask(world, control);
		timer = new Timer();
		timer.schedule(task, 0,50);
		running = true;
	}
	
	public void stopSimulation() {
		timer.cancel();
		running = false;
	}
	
	private void initialize(WorldWindowCtrl control) {
		this.control = control;
		timer = new Timer();
		running = false;
	}
}