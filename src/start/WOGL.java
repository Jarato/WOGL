package start;
import java.io.IOException;

import gui.GuiControl;
import gui.StatisticWindowCtrl;
import gui.WorldWindowCtrl;
import gui2.ControlWindowCtrl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulation.WOGLSimulation;

public class WOGL extends Application{
	
	private WOGLSimulation simulation;
	private ControlWindowCtrl control;
	private Stage controlStage;
	//private Stage statStage;
	
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
	
	public ControlWindowCtrl getWorldWindowCtrl() {
		return control;
	}

	@Override
	public void start(Stage stage) throws Exception {
		//GuiControl guiControl = new GuiControl();
		controlStage = stage;
		guiInitialize();		
		//initStatWindow();
		//guiControl.setStatStage(statStage);
		//guiControl.setMainStage(stage);
		
		
		//long seed = 1853386138547355417l;
		//simulation = new WOGLSimulation(control);
		//simulation.startSimulation();
		//control.setWOGLSimulation(simulation);
		//control.setGuiControl(guiControl);
	}
	
	/*private void initStatWindow() {
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
	}*/
	
	public void guiInitialize() {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ControlWindow.fxml"));
			Parent root;

			root = (Parent) loader.load();
			control = (ControlWindowCtrl) loader.getController();
			if (control == null) System.out.println("Control-Class is null");
	
			Scene scene = new Scene(root);

			controlStage.setMinWidth(450);
			controlStage.setMinHeight(180);
			controlStage.setMaxWidth(450);
			controlStage.setMaxHeight(180);
			controlStage.setResizable(false);
			controlStage.setTitle("WOGL alpha");
			controlStage.setScene(scene);
			controlStage.show();
			controlStage.setOnCloseRequest(e -> close());
		} catch (IOException e) {
			System.out.println("Fehler beim Laden der fxml-Datei!");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Fehler 2 beim Laden der fxml-Datei!");
			e.printStackTrace();
		}
	}
	
	private void close() {
		//simulation.terminateSimulation();
		controlStage.close();
		//statStage.close();
	}

}