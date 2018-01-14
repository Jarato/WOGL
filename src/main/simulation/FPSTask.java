package main.simulation;

import java.util.TimerTask;

import javafx.application.Platform;
import main.gui.WorldWindowCtrl;

public class FPSTask extends TimerTask{
	private volatile int frames = 0;
	private WorldWindowCtrl control;
	
	public synchronized void addFrame() {
		frames++;
	}
	
	public FPSTask(WorldWindowCtrl TheControl) {
		this.control = TheControl;
	}
	
	@Override
	public void run() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				control.updateFPSLabel(frames);
				frames = 0;
				control.updateCreaturePlantCount();
			}
			
		});
		
	}

}
