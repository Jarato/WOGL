package gui;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import simulation.WOGLSimulation;

public class WorldWindowCtrl extends Control {

	@FXML
	private Pane worldCanvasPane;
	
	@FXML
	private Button startStopButton;
	
	@FXML 
	private Button newWorldButton;
	
	@FXML
	private Label creatureCountLabel;
	
	@FXML
	private Label plantCountLabel;
	
	@FXML
	private Label fpsLabel;
	
	private WorldCanvas wCanvas;
	private WOGLSimulation simulation;

	@FXML
	private void onStartStopButtonClick() {
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
		Alert newWorldAlert = new Alert(AlertType.CONFIRMATION);
		newWorldAlert.setTitle("New World");
		newWorldAlert.setHeaderText("this will destroy the current world");
		newWorldAlert.setContentText("Creating a new world will destroy the current world. The new world will be created with a new random seed.");
		Optional<ButtonType> result = newWorldAlert.showAndWait();
		if (result.get() == ButtonType.OK) {
			simulation.createNewWorld();
			startStopButton.setText("Stop");
		}
	}
	
	@FXML
	private void onFastForwardToggleClick() {
		//System.out.println("Fast Forward");
		simulation.toggleFastForwardSimulation();
	}
	
	public void updateFPSLabel(int numberOfFrames) {
		fpsLabel.setText(numberOfFrames+" fps");
	}
	
	public void updateCreaturePlantCount() {
		displayCreatureCount(simulation.getWorld().getNumberOfCreatures());
		displayPlantCount(simulation.getWorld().getNumberOfPlants());
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
	
	public void displayCreatureCount(int number) {
		creatureCountLabel.setText("creatures: "+number);
	}
	
	public void displayPlantCount(int number) {
		plantCountLabel.setText("plants: "+number);
	}

	public WorldCanvas getWorldCanvas() {
		return wCanvas;
	}
}