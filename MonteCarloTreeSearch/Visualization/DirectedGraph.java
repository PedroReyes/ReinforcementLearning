package Visualization;

import java.util.ArrayList;

import processing.core.PApplet;

public class DirectedGraph {

	ArrayList<Node> nodes = new ArrayList<Node>();
	FlowAlgorithm flower = new CircleFlowAlgorithm();
	PApplet display;

	void setFlowAlgorithm(FlowAlgorithm f) {
		flower = f;
	}

	void addNode(Node node) {
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
	}

	int size() {
		return nodes.size();
	}

	boolean linkNodes(Node n1, Node n2) {
		if (nodes.contains(n1) && nodes.contains(n2)) {
			n1.addOutgoingLink(n2);
			n2.addIncomingLink(n1);
			return true;
		}
		return false;
	}

	Node getNode(int index) {
		return nodes.get(index);
	}

	ArrayList<Node> getNodes() {
		return nodes;
	}

	ArrayList<Node> getRoots() {
		ArrayList<Node> roots = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.getIncomingLinksCount() == 0) {
				roots.add(n);
			}
		}
		return roots;
	}

	ArrayList<Node> getLeaves() {
		ArrayList<Node> leaves = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.getOutgoingLinksCount() == 0) {
				leaves.add(n);
			}
		}
		return leaves;
	}

	// the method most people will care about
	boolean reflow() {
		return flower.reflow(display, this);
	}

	// this does nothing other than tell nodes to draw themselves.
	public void draw() {
		for (Node n : nodes) {
			n.draw();
		}
	}
}
