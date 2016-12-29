package Visualization;

import processing.core.PApplet;

public class MainVisualization {

	public static void main(String[] args) {
		String[] a = { "MAIN" };
		TreeDisplay display = new TreeDisplay();
		PApplet.runSketch(a, display);
		//		PApplet.main(DisplayTree.class.getName());

		if (false) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			display.createMonteCarloTree();
		}
	}

}
