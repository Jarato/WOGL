package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.gui.WorldCanvas;
import main.gui.WorldWindowCtrl;
import main.simulation.WOGLSimulation;

public class WOGL extends Application{
	
	private WOGLSimulation simulation;
	private WorldWindowCtrl control;
	
	public static void main(String[] args) {
		//angle: 184.98035719800836	rotation: 308.67898113218735
		/*double angle = 0;
		double rotation = 359.99;
		System.out.println(UtilMethods.rotate360(359.99));
		for (int i = 0; i < 361; i++) {
			angle = i;
			int viewArea = (int)(Math.floor((angle-(rotation-Brain.SIGHT_MAXANGLE/2.0))/Brain.SIGHT_AREA_WIDTH+360.0/Brain.SIGHT_AREA_WIDTH)%(360.0/Brain.SIGHT_AREA_WIDTH));
			System.out.println("angle: "+i+" => viewArea: "+viewArea);
		}*/
		
		WOGL.launch(args);
	}
	
	public WorldWindowCtrl getWorldWindowCtrl() {
		return control;
	}

	@Override
	public void start(Stage stage) throws Exception {
		guiInitialize(stage);
		simulation = new WOGLSimulation(control);
		simulation.startSimulation();
		control.setWOGLSimulation(simulation);
	}
	
	public void guiInitialize(Stage stage) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/WorldWindow.fxml"));
			Parent root;

			root = (Parent) loader.load();
			control = (WorldWindowCtrl) loader.getController();
			if (control == null) System.out.println("Control-Class is null");
			WorldCanvas wcanvas = new WorldCanvas();
			Pane pane = control.getWorldCanvasPane();
			control.setWorldCanvas(wcanvas);
			pane.getChildren().add(wcanvas);
			wcanvas.widthProperty().bind(pane.widthProperty());
			wcanvas.heightProperty().bind(pane.heightProperty());
	
			Scene scene = new Scene(root);

			stage.setMinWidth(300);
			stage.setMinHeight(300);
			stage.setTitle("WOGL alpha");
			stage.setScene(scene);
			stage.show();
			stage.setOnCloseRequest(e -> close());
		} catch (IOException e) {
			System.out.println("Fehler beim Laden der fxml-Datei!");
			e.printStackTrace();
		}
	}
	
	private void close() {
		simulation.stopSimulation();
	}

}