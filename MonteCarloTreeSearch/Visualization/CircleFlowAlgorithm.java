package Visualization;
/**
 * Simmple graph layout system
 * http://processingjs.nihongoresources.com/graphs
 * This code is in the public domain
 */

import processing.core.PApplet;

/**
 * Flow algorithm for drawing nodes in a circle
 */
class CircleFlowAlgorithm implements FlowAlgorithm {
	// draw all nodes in a big circle,
	// without trying to find the best
	// arrangement possible.
	public boolean reflow(PApplet display, DirectedGraph g) {
		float interval = (float) (2 * Math.PI / (float) g.size());
		int cx = display.width / 2;
		int cy = display.height / 2;
		float vl = cx - (2 * g.getNode(0).r1) - 10;
		for (int a = 0; a < g.size(); a++) {
			int[] nc = Universal.rotateCoordinate(vl, 0, (float) a * interval);
			g.getNode(a).x = cx + nc[0];
			g.getNode(a).y = cy + nc[1];
		}
		return true;
	}
}