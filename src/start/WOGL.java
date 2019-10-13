package start;
import java.io.IOException;

import gui.GuiControl;
import gui.StatisticWindowCtrl;
import gui.WorldWindowCtrl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulation.WOGLSimulation;

public class WOGL extends Application{
	
	private WOGLSimulation simulation;
	private WorldWindowCtrl control;
	private Stage statStage;
	
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
		
		Application.launch(args);
	}
	
	public WorldWindowCtrl getWorldWindowCtrl() {
		return control;
	}

	@Override
	public void start(Stage stage) throws Exception {
		GuiControl guiControl = new GuiControl();
		guiInitialize(stage);		
		initStatWindow();
		guiControl.setStatStage(statStage);
		guiControl.setMainStage(stage);
		
		
		//long seed = 1853386138547355417l;
		simulation = new WOGLSimulation(control);
		simulation.startSimulation();
		control.setWOGLSimulation(simulation);
		control.setGuiControl(guiControl);
	}
	
	private void initStatWindow() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/fxml/StatisticWindow.fxml"));
		try {
			Parent root = (Parent) loader.load();
			StatisticWindowCtrl control = (StatisticWindowCtrl) loader.getController();
			if (control == null) System.out.println("Statistic Control-Class is null");
			//control.updateCreatureLineChart();
			Scene statScene = new Scene(root);
			statStage = new Stage();
			statStage.setTitle("Statistics");
			statStage.setScene(statScene);
			//statStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void guiInitialize(Stage stage) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/WorldWindow.fxml"));
			Parent root;

			root = (Parent) loader.load();
			control = (WorldWindowCtrl) loader.getController();
			if (control == null) System.out.println("Control-Class is null");
			control.initGuiNodes();
	
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
		} catch (Exception e) {
			System.out.println("Fehler 2 beim Laden der fxml-Datei!");
			e.printStackTrace();
		}
	}
	
	private void close() {
		simulation.terminateSimulation();
		statStage.close();
	}

}