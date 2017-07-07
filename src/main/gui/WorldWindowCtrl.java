package main.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;

public class WorldWindowCtrl extends Control {

	@FXML
	private Pane worldCanvasPane;
	
	private WorldCanvas wCanvas;

	public Pane getWorldCanvasPane() {
		return worldCanvasPane;
	}
	
	public void setWorldCanvas(WorldCanvas theWorldCanvas) {
		wCanvas = theWorldCanvas;
		wCanvas.setOnMouseClicked(e -> {
			int foundId = wCanvas.getIdOfPosition(e.getX(),e.getY());
			wCanvas.setSelectedId(foundId);
			
			/*System.out.println("screen: ("+e.getScreenX()+","+e.getScreenY()+")");
			System.out.println("scene: ("+e.getSceneX()+","+e.getSceneY()+")");
			System.out.println("nil: ("+e.getX()+","+e.getY()+")");*/
		});
	}

	public WorldCanvas getWorldCanvas() {
		return wCanvas;
	}
}