package simulation;

import gui.WorldWindowCtrl;
import simulation.world.World;

public class FastForwardTask implements Runnable{

	private World world;
	private FPSTask fpsTask;
	private WorldWindowCtrl control;
	private volatile boolean stopRun = false;
	
	public synchronized void stopRunning() {
		stopRun = true;
	}
	
	public FastForwardTask(World theWorld, WorldWindowCtrl theControl, FPSTask theFPSTask) {
		world = theWorld;
		fpsTask = theFPSTask;
		this.control = theControl;
	}
	
	@Override
	public void run(){
		while(!stopRun) {
			world.step();
			//control.getWorldCanvas().draw();
			fpsTask.addFrame();
		}
		
	}

}