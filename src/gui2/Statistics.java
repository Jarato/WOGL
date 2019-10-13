package gui2;

import java.io.IOException;

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

		try {
			Thread.sleep(2000);
			series.getData().add(new XYChart.Data(13, 20));
			Thread.sleep(2000);
			series.getData().add(new XYChart.Data(14, 15));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		series.getData().add(new XYChart.Data(1, 23));
		series.getData().add(new XYChart.Data(2, 14));
		series.getData().add(new XYChart.Data(3, 15));
		series.getData().add(new XYChart.Data(4, 24));
		series.getData().add(new XYChart.Data(5, 34));
		series.getData().add(new XYChart.Data(6, 36));
		series.getData().add(new XYChart.Data(7, 22));
		series.getData().add(new XYChart.Data(8, 45));
		series.getData().add(new XYChart.Data(9, 43));
		series.getData().add(new XYChart.Data(10, 17));
		series.getData().add(new XYChart.Data(11, 29));
		series.getData().add(new XYChart.Data(12, 25));

		Scene scene = new Scene(lineChart, 800, 600);
		lineChart.getData().add(series);

		stage.setScene(scene);
		stage.show();
	}
}
