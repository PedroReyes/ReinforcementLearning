package Visualization;

import processing.core.PApplet;

/**
 * Simmple graph layout system
 * http://processingjs.nihongoresources.com/graphs
 * This code is in the public domain
 */

// this is the interface for graph reflowing algorithms
interface FlowAlgorithm {
	// returns "true" if done, or "false" if not done
	boolean reflow(PApplet display, DirectedGraph g);
}