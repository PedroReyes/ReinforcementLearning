package Analysis;

import processing.core.PApplet;

public class main {

	public static void main(String[] args) {
		float[] x = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		float[] y = { 2500, 1000, 200, 300, 400, 500, 600, 700, 800, 900, 1000 };
		XYPlot display = new XYPlot(x, y);

		String[] a = { "MAIN" };
		PApplet.runSketch(a, display);
		//		PApplet.main(DisplayTree.class.getName());
	}

}
