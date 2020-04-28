package gui;

import java.io.IOException;
import java.util.Random;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Statistics extends Application {
	private static XYChart.Series series = new XYChart.Series<>();;
	private static LineChart<Number, Number> lineChart;

	public static void main(String[] args) {
		Statistics.launch(args);

		
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Line Chart Sample");
		// defining the axes
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Number of Month");
		// creating the chart
		lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		lineChart.setTitle("Stock Monitoring, 2010");
		// defining a series
		series.setName("My portfolio");
		// populating the series with data

		Scene scene = new Scene(lineChart, 800, 600);
		lineChart.getData().add(series);

		stage.setScene(scene);
		stage.show();
		Random rnd = new Random();
		for (int i = 0; i < 1000; i++) {
			series.getData().add(new XYChart.Data(i, rnd.nextDouble()*100));
		}
		
	}
}
