package main.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.input.MouseButton;
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
			if (e.getButton() == MouseButton.PRIMARY) {
				int foundId = wCanvas.getIdOfPosition(e.getX(),e.getY());
				wCanvas.setSelectedId(foundId);
			}
		});
		wCanvas.setOnScroll(e -> {
			wCanvas.zoom(e.getX(), e.getY(), e.getDeltaY());
		});
		wCanvas.setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				wCanvas.drag(e.getX(),e.getY());
			}
		});
		wCanvas.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				wCanvas.setDragStartPos(e.getX(), e.getY());
			}
		});
		
	}

	public WorldCanvas getWorldCanvas() {
		return wCanvas;
	}
}