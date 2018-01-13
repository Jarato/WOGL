package main.simulation;

import javafx.concurrent.Task;
import main.simulation.world.World;

public class FastForwardTask implements Runnable{

	private World world;
	private volatile boolean stop;
	
	public FastForwardTask(World theWorld) {
		world = theWorld;
		stop = false;
	}
	
	public void stopLoop() {
		stop = true;
	}
	
	@Override
	public void run(){
		int loops = 1000;
		while (!stop && loops > 0) {
			try {
				world.step();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loops--;
		}
	}

}