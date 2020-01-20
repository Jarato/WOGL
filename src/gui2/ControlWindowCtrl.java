package gui2;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;

public class ControlWindowCtrl extends Control{

	@FXML
	private Button runPauseButton;
	
	@FXML
	private ToggleButton fastForwardToggleButton;
	
	@FXML
	private void onNewWorldButtonClick() {
		System.out.println("New World");
	}
	
	@FXML
	private void onRunPauseButtonClick() {
		System.out.println("Run/Pause");
	}

	@FXML
	private void onFastForwardToggleButtonClick() {
		System.out.println("Fast Forward");
	}

	@FXML
	private void onOpenStatisticsButtonClick() {
		System.out.println("Open Statistics");
	}
	
}
