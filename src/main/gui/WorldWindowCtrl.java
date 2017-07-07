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
	}
	
	public WorldCanvas getWorldCanvas() {
		return wCanvas;
	}
}