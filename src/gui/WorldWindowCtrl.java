package gui;

import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import pdf.util.UtilMethods;
import gui.resizable.ResizableCanvas;
import gui.resizable.RessourceCanvas;
import gui.resizable.WorldCanvas;
import simulation.WOGLSimulation;
import simulation.world.creature.Body;
import simulation.world.creature.Creature;

public class WorldWindowCtrl extends Control {

	@FXML
	private Pane worldCanvasPane;
	
	@FXML
	private Button startStopButton;
	
	@FXML 
	private Button newWorldButton;
	
	@FXML
	private AnchorPane selectedAnchor;
	
	@FXML
	private Label fpsLabel;
	
	@FXML
	private ToggleButton followToggle;
	
	@FXML
	private ToggleButton plantViewToggle;
	
	@FXML
	private ToggleButton creatureViewToggle;
	
	@FXML
	private ToggleButton wallViewToggle;
	
	@FXML
	private ToggleButton collisionToggle;
	
	@FXML
	private ListView<String> childrenListView;
	
	@FXML
	private Canvas selectedCanvas;
	
	@FXML
	private Label idLabel;
	
	@FXML
	private Label generationLabel;
	
	@FXML
	private Label ageLabel;
	
	@FXML
	private Label voreEfficiencyLabel;
	
	@FXML
	private Label parentIdLabel;
	
	@FXML
	private Pane ressourceCanvasPane;
	
	private WorldCanvas wCanvas;
	private WOGLSimulation simulation;
	private SelectedCreatureInfo selCreInf;

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
	
	@FXML
	private void onStatisticButtonClick() {
		
	}

	@FXML
	private void onPlantViewToggleClick() {
		updateToggledOutput();
	}
	
	private void updateToggledOutput() {
		selCreInf.setShowPlantView(plantViewToggle.isSelected());
		selCreInf.setShowCreatureView(creatureViewToggle.isSelected());
		selCreInf.setShowWallView(wallViewToggle.isSelected());
		selCreInf.setShowCollision(collisionToggle.isSelected());
	}
	
	@FXML
	private void onCreatureViewToggleClick() {
		updateToggledOutput();
	}
	
	@FXML
	private void onWallViewToggleClick() {
		updateToggledOutput();
	}
	
	@FXML
	private void onCollisionToggleClick() {
		updateToggledOutput();
	}
	
	@FXML
	private void onFollowToggleClick() {
		selCreInf.setFollowSelected(followToggle.isSelected());
	}
	
	public void updateFPSLabel(int numberOfFrames) {
		fpsLabel.setText(numberOfFrames+" fps");
	}
	
	public Pane getWorldCanvasPane() {
		return worldCanvasPane;
	}
	
	public void setWOGLSimulation(WOGLSimulation woglSim) {
		simulation = woglSim;
	}
	
	private void initWorldCanvas() {
		wCanvas.setSelectedCreatureInfo(selCreInf);
		setSelectedPaneVisibility(false);
		wCanvas.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				Creature foundC = wCanvas.getCreatureOnPosition(e.getX(),e.getY());
				boolean cChanged = selCreInf.initInfo(foundC);
				if (foundC != null) {
					setSelectedPaneVisibility(true);
					if (cChanged) resetSelectedPane();
				} else setSelectedPaneVisibility(false);
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
	
	private void initSelectedCreatureInfo() {
		selCreInf = new SelectedCreatureInfo();
		selCreInf.setBodyCanvas(selectedCanvas);
		selCreInf.setwControl(this);
	}
	
	public void initGuiNodes() {
		wCanvas = new WorldCanvas();
		initSelectedCreatureInfo();
		initSelectedOutputPane();
		initWorldCanvas();
		RessourceCanvas rCanvas = new RessourceCanvas();
		rCanvas.setSelectedCreatureInfo(selCreInf);
		ressourceCanvasPane.getChildren().add(rCanvas);
		rCanvas.widthProperty().bind(ressourceCanvasPane.widthProperty());
		rCanvas.heightProperty().bind(ressourceCanvasPane.heightProperty());
		selCreInf.setRessourceCanvas(rCanvas);
		worldCanvasPane.getChildren().add(wCanvas);
		wCanvas.widthProperty().bind(worldCanvasPane.widthProperty());
		wCanvas.heightProperty().bind(worldCanvasPane.heightProperty());
	}
	
	
	public void initSelectedOutputPane() {
		selectedAnchor.managedProperty().bind(selectedAnchor.visibleProperty());
	}
	
	public void setSelectedPaneVisibility(boolean value) {
		selectedAnchor.setVisible(value);
	}
	
	public void updateSelectedPaneInfo() {
		Creature c = selCreInf.getCreature();
		idLabel.setText("id: "+c.getId());
		generationLabel.setText("generation: "+c.getGeneration());
		ageLabel.setText("age: "+c.getAge());
		Body b = c.getBody();
		if (b.getCarnivore_eff() == 0) {
			voreEfficiencyLabel.setText("herbivore");
		} else if (b.getHerbivore_eff() == 0) {
			voreEfficiencyLabel.setText("carnivore");
		} else {
			int herbi = (int)(UtilMethods.roundTo(b.getHerbivore_eff(), 2)*100);
			int carni = (int)(UtilMethods.roundTo(b.getCarnivore_eff(), 2)*100);
			voreEfficiencyLabel.setText("omniv.: "+(b.getCarnivore_eff()>0.5?carni+"% meat":herbi+"% plant"));
		}
		parentIdLabel.setText("parent-id: "+(c.getParentId()==-1?"creator":c.getParentId()));
		ArrayList<Integer> children = c.getChildrenIdList();
		ObservableList<String> items = childrenListView.getItems();
		for (int i = 0; i < children.size(); i++) {
			if (i >= items.size()) {
				items.add(String.valueOf(children.get(i)));
			}
		}
	}
	
	
	public void resetSelectedPane() {
		followToggle.setSelected(false);
		plantViewToggle.setSelected(false);
		creatureViewToggle.setSelected(false);
		wallViewToggle.setSelected(false);
		collisionToggle.setSelected(false);
		childrenListView.getItems().clear();
	}

	public WorldCanvas getWorldCanvas() {
		return wCanvas;
	}
}