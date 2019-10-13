package gui;

import javafx.stage.Stage;

public class GuiControl {
	private Stage mainStage;
	private Stage statStage;
	
	public void setStatStage(Stage setStatStage) {
		statStage = setStatStage;
	}
	
	public void setMainStage(Stage setMainStage) {
		mainStage = setMainStage;
	}
	
	public void showStatisticWindow() {
		statStage.show();
	}
	
	public void hideStatisticWindow() {
		statStage.hide();
	}
	
	public boolean isStatisticWindowVisible() {
		return statStage.isShowing();
	}
}
