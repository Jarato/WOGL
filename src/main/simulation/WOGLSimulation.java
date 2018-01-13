package main.simulation;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import main.gui.WorldWindowCtrl;
import main.simulation.world.World;

public class WOGLSimulation {
	private World world;
	//private ScheduledExecutorService ses;
	private TimerTask task;
	private Timer timer;
	private FastForwardTask ffTask;
	private WorldWindowCtrl control;
	private boolean running;
	private boolean fastForward;
	
	public WOGLSimulation(long seed, WorldWindowCtrl windowCtrl) {
		world = new World(seed);
		world.initilize();
		initialize(windowCtrl);
	}
	
	public WOGLSimulation(WorldWindowCtrl windowCtrl) {
		world = new World();
		world.initilize();
		initialize(windowCtrl);
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void createNewWorld(long seed) {
		stopSimulation();
		world = new World(seed);
		world.initilize();
		startSimulation();
	}
	
	public void createNewWorld() {
		stopSimulation();
		world = new World();
		world.initilize();
		startSimulation();
	}
	
	public void startSimulation() {
		task = new SimulationTask(world, control);
		timer = new Timer();
		timer.schedule(task, 0,30);
		running = true;
	}
	
	public void stopSimulation() {
		timer.cancel();
		running = false;
	}
	
	public void toggleFastForwardSimulation() {
		if (!fastForward) {
			stopSimulation();
			Task task;
			
			ffTask = new FastForwardTask(world);
			ffTask.run();
			fastForward = true;
		} else {
			ffTask.stopLoop();
			try {
				ffTask.join();
				startSimulation();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean getFastForward() {
		return fastForward;
	}
	
	private void initialize(WorldWindowCtrl control) {
		this.control = control;
		timer = new Timer();
		running = false;
		fastForward = false;
	}
}