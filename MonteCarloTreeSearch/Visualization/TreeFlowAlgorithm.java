package Visualization;
/**
 * Simmple graph layout system
 * http://processingjs.nihongoresources.com/graphs
 * This code is in the public domain
 */

import java.util.ArrayList;

import processing.core.PApplet;

/**
 * Flow algorithm for trees - only works for Trees
 */
class TreeFlowAlgorithm implements FlowAlgorithm {

	// tree layout is fairly simpe: segment
	// the screen into as many vertical strips
	// as the tree is deep, then at every level
	// segment a strip in as many horizontal
	// bins as there are nodes at that depth.
	public boolean reflow(PApplet display, DirectedGraph g) {
		if (g instanceof Tree) {
			Tree t = (Tree) g;
			int depth = t.getDepth();
			int vstep = (display.height - 20) / depth;
			int vpos = 30;

			Node first = t.root;
			first.x = display.width / 2;
			first.y = vpos;

			// breadth-first iteration
			ArrayList<Node> children = t.root.getOutgoingLinks();
			while (children.size() > 0) {
				vpos += vstep;
				int cnum = children.size();
				int hstep = (display.width - 20) / cnum;
				int hpos = 10 + (hstep / 2);
				ArrayList<Node> newnodes = new ArrayList<Node>();
				for (Node child : children) {
					child.x = hpos;
					child.y = vpos;
					Universal.addAll(newnodes, child.getOutgoingLinks());
					hpos += hstep;
				}
				children = newnodes;
			}
		}
		return true;
	}
}