package Visualization;

import java.util.ArrayList;

import processing.core.PApplet;

public class ForceDirectedFlowAlgorithm implements FlowAlgorithm {
	float min_size = 80.0f;
	float elasticity = 200.0f;

	void setElasticity(float e) {
		elasticity = e;
	}

	float repulsion = 4.0f;

	void setRepulsion(float r) {
		repulsion = r;
	}

	// this is actually a simplified force
	// directed algorithm, taking into account
	// only incoming links.
	public boolean reflow(PApplet display, DirectedGraph g) {
		ArrayList<Node> nodes = g.getNodes();
		int reset = 0;
		for (Node n : nodes) {
			ArrayList<Node> incoming = n.getIncomingLinks();
			ArrayList<Node> outgoing = n.getOutgoingLinks();
			// compute the total push force acting on this node
			int dx = 0;
			int dy = 0;
			for (Node ni : incoming) {
				dx += (ni.x - n.x);
				dy += (ni.y - n.y);
			}
			float len = (float) Math.sqrt(dx * dx + dy * dy);
			float angle = Universal.getDirection(dx, dy);
			int[] motion = Universal.rotateCoordinate((float) 0.9 * repulsion, (float) 0.0, angle);
			// move node
			int px = n.x;
			int py = n.y;
			n.x += motion[0];
			n.y += motion[1];
			if (n.x < 0) {
				n.x = 0;
			} else if (n.x > display.width) {
				n.x = display.width;
			}
			if (n.y < 0) {
				n.y = 0;
			} else if (n.y > display.height) {
				n.y = display.height;
			}
			// undo repositioning if elasticity is violated
			float shortest = n.getShortestLinkLength();
			if (shortest < min_size || shortest > elasticity * 2) {
				reset++;
				n.x = px;
				n.y = py;
			}
		}
		return reset == nodes.size();
	}
}