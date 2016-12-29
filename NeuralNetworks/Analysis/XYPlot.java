package Analysis;
/**
 * Basic1
 * This sketch uses the Graph2D object from the library.
 * It plots a static sin function, and shows how to use 
 * various methods available to alter the graph.
 **/

import java.util.Arrays;
import java.util.Collections;

import org.gicentre.utils.stat.XYChart;

import Auxiliar.Helper;
import processing.core.PApplet;

/**
 *  Equations that are to be plot must be encapsulated into a 
 *  class implementing the IGraph2DCallback interface.
 **/
public class XYPlot extends PApplet {

	public float[] xData, yData;
	public String title = "";// = "Income per person, United Kingdom";
	public String subtitle = "";// = "Gross domestic product measured in inflation-corrected $US";
	public String xLabel = "";// = "xLabel";
	public String yLabel = "";// = "yLabel";

	// Where to set the plot
	public float xOrigin = 15, yOrigin = 15, widthPlot, heightPlot;

	public XYPlot() {
		this.xData = null;
		this.yData = null;
	}

	public XYPlot(float[] x, float[] y) {
		this.xData = x;
		this.yData = y;
	}

	public void settings() {
		size(500, 200);
	}

	// Displays a simple line chart representing a time series.

	XYChart lineChart;

	// Loads data into the chart and customizes its appearance.
	public void setup() {
		textFont(createFont("Arial", 10), 10);

		widthPlot = width;
		heightPlot = height;

		// Both x and y data set here.  
		lineChart = new XYChart(this);

		if (this.xData != null && this.yData != null) {
			lineChart.setData(xData, yData);
		} else {
			//			lineChart.setData(new float[] { 1900, 1910, 1920, 1930, 1940, 1950, 1960, 1970, 1980, 1990, 2000 },
			//					new float[] { 6322, 6489, 6401, 7657, 9649, 9767, 12167, 15154, 18200, 23124, 28645 });
			System.out.println("...");
		}

		// Axis formatting and labels.
		lineChart.showXAxis(true);
		lineChart.showYAxis(true);
		float min = (float) Collections.min(Arrays.asList(Helper.boxFloatArray(yData)));
		float max = (float) Collections.max(Arrays.asList(Helper.boxFloatArray(yData)));
		float realMin = min - (max - min) * 0.05f;
		lineChart.setMinY(realMin);

		//		lineChart.setYFormat("$###,###"); // Monetary value in $US
		//		lineChart.setXFormat("0000"); // Year
		lineChart.setXAxisLabel(xLabel);
		lineChart.setYAxisLabel(yLabel);

		// Symbol colours
		lineChart.setPointColour(color(180, 50, 50, 100));
		lineChart.setPointSize(5);
		lineChart.setLineWidth(2);
	}

	// Draws the chart and a title.
	public void draw() {
		background(255);
		textSize(9);
		lineChart.draw(xOrigin, yOrigin, widthPlot - 30, heightPlot - 30);

		// Draw a title over the top of the chart.
		fill(120);
		textSize(20);
		text(title, 70, 30);
		textSize(11);
		text(subtitle, 70, 45);
	}
}