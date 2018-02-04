package simulation;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import gui.WorldWindowCtrl;
import javafx.application.Platform;
import javafx.concurrent.Task;
import simulation.world.World;
import statistic.StatisticManager;

public class WOGLSimulation {
	private World world;
	private ScheduledExecutorService ses;
	private Thread ffThread;
	private Runnable simTask;
	private FastForwardTask ffTask;
	private WorldWindowCtrl control;
	private boolean running;
	private boolean fastForward;
	private Timer fpsTimer;
	private FPSTask fpsTask;
	
	public WOGLSimulation(long seed, WorldWindowCtrl windowCtrl) {
		world = new World(seed);
		world.initialize();
		initialize(windowCtrl);
		control.getWorldCanvas().initZoomDragValues();
	}

	public World getWorld() {
		return world;
	}
	
	public boolean isFastForward() {
		return fastForward;
	}
	
	public WOGLSimulation(WorldWindowCtrl windowCtrl) {
		world = new World();
		world.initialize();
		initialize(windowCtrl);
		control.getWorldCanvas().initZoomDragValues();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void createNewWorld(long seed) {
		stopSimulation();
		world = new World(seed);
		world.initialize();
		control.getWorldCanvas().initZoomDragValues();
		startSimulation();
	}
	
	public void createNewWorld() {
		stopSimulation();
		world = new World();
		world.initialize();
		control.getWorldCanvas().initZoomDragValues();
		startSimulation();
	}
	
	public void startSimulation() {
		if (!fastForward) {
			simTask = new SimulationTask(world, control, fpsTask);
			ses.scheduleAtFixedRate(simTask, 0, 10, TimeUnit.MILLISECONDS);
		}
		running = true;
	}
	
	public void stopSimulation() {
		if (!fastForward) {
			stopExeService();
		}
		running = false;
	}
	
	public void terminateSimulation() {
		ses.shutdown();
		ffTask.stopRunning();
		fpsTimer.cancel();
	}
	
	private void stopExeService() {
		ses.shutdown();
		try {
			ses.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ses = Executors.newScheduledThreadPool(1);
	}
	
	public void toggleFastForwardSimulation() {
		if (!fastForward) {
			stopExeService();
			ffTask = new FastForwardTask(world, control, fpsTask);
			ffThread = new Thread(ffTask);
			ffThread.start();
			fastForward = true;
		} else {
			ffTask.stopRunning();
			try {
				
				ffThread.join();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			control.getWorldCanvas().draw();
			fastForward = false;
			if (running) {
				startSimulation();
			}
			
		}
	}
	
	public boolean getFastForward() {
		return fastForward;
	}
	
	private void initialize(WorldWindowCtrl control) {
		fpsTask = new FPSTask(control);
		fpsTimer = new Timer();
		fpsTimer.scheduleAtFixedRate(fpsTask, 0, 1000);
		ffTask = new FastForwardTask(world, control, fpsTask);
		this.control = control;
		ses = Executors.newScheduledThreadPool(1);
		running = false;
		fastForward = false;
	}
}