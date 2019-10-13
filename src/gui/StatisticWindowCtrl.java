package gui;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Control;

public class StatisticWindowCtrl extends Control {
	
	@FXML
	private LineChart creatureLineChart;
	
	@FXML
	private LineChart plantLineChart;
	
	@FXML
	private LineChart crePlaLineChart;
	
	
	public void updateCreatureLineChart() {
		creatureLineChart.getData().add(new XYChart.Data(2,2));
	}
}
