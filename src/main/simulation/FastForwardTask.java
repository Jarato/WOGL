package main.simulation;

import main.simulation.world.World;

public class FastForwardTask implements Runnable{

	private World world;
	private FPSTask fpsTask;
	private volatile boolean stopRun = false;
	
	public synchronized void stopRunning() {
		stopRun = true;
	}
	
	public FastForwardTask(World theWorld, FPSTask theFPSTask) {
		world = theWorld;
		fpsTask = theFPSTask;
	}
	
	@Override
	public void run(){
		while(!stopRun) {
			world.step();
			fpsTask.addFrame();
		}
		
	}

}