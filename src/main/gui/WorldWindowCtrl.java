package main.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import main.simulation.WOGLSimulation;

public class WorldWindowCtrl extends Control {

	@FXML
	private Pane worldCanvasPane;
	
	@FXML
	private Button startStopButton;
	
	@FXML 
	private Button newWorldButton;
	
	private WorldCanvas wCanvas;
	private WOGLSimulation simulation;

	@FXML
	private void onStartStopButtonClick() {
		System.out.println("Start stop");
		if (simulation.isRunning()) {
			simulation.stopSimulation();
			startStopButton.setText("Start");
		} else {
			simulation.startSimulation();
			startStopButton.setText("Stop");
		}
		
	}
	
	@FXML
	private void onNewWorldButtonClick() {
		System.out.println("new World!");
	}
	
	public Pane getWorldCanvasPane() {
		return worldCanvasPane;
	}
	
	public void setWOGLSimulation(WOGLSimulation woglSim) {
		simulation = woglSim;
	}
	
	public void setWorldCanvas(WorldCanvas theWorldCanvas) {
		wCanvas = theWorldCanvas;
		wCanvas.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				int foundId = wCanvas.getIdOfPosition(e.getX(),e.getY());
				wCanvas.setSelectedId(foundId);
				if (!simulation.isRunning()) wCanvas.draw();
			}
		});
		wCanvas.setOnScroll(e -> {
			wCanvas.zoom(e.getX(), e.getY(), e.getDeltaY());
			if (!simulation.isRunning()) wCanvas.draw();
		});
		wCanvas.setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				wCanvas.drag(e.getX(),e.getY());
				if (!simulation.isRunning()) wCanvas.draw();
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