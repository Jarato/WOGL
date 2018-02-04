package simulation;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;
import gui.WorldWindowCtrl;
import simulation.world.World;

public class SimulationTask implements Runnable{
	public static final int NUMBER_OF_STEPS_PER_DRAW = 1;
	
	
	private World world;
	private WorldWindowCtrl control;
	private FPSTask fpsTask;
	
	
	
	public SimulationTask(World theWorld, WorldWindowCtrl theControl, FPSTask theFPSTask) {
		world = theWorld;
		control = theControl;
		control.getWorldCanvas().setWorld(world);
		fpsTask = theFPSTask;
	}
	
	@Override
	public void run() {
		Callable<Object> ca = new Callable<Object>() {
			@Override
			public Object call() throws Exception{
				for (int i = 0; i < NUMBER_OF_STEPS_PER_DRAW; i++) {
					world.step();
				}
				control.getWorldCanvas().draw();
				fpsTask.addFrame();
				return null;
			}
				
		};
		FutureTask<Object> ft = new FutureTask<Object>(ca);
		Platform.runLater(ft);
			try {
				ft.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}