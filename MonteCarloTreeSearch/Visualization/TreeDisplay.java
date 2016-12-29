package Visualization;

import java.util.ArrayList;
import java.util.Iterator;

import MCTS.TreeSearch;
import processing.core.PApplet;

public class TreeDisplay extends PApplet {
	/**
	 * Simmple graph layout system
	 * http://processingjs.nihongoresources.com/graphs
	 * This code is in the public domain
	 */
	// Complete tree search
	TreeSearch treeSearch;
	boolean treeSearchIsProvided = false;

	// Auxiliary copy when selected one node 
	TreeSearch selectedTreeSearch;

	// Technically g is of type DirectedGraph, so build a Tree alias:
	DirectedGraph directedGraph = null;
	Tree auxiliaryTree;

	// When changing among trees it is necessary a delay
	int delay = 1000;
	boolean setDelay = false;
	boolean displayingNewTree = false;

	public TreeDisplay() {

	}

	public TreeDisplay(TreeSearch treeSearch) {
		this.treeSearch = treeSearch;

		// If no tree is provided then you can create one executing
		// and pressing keys on the keyboard
		this.treeSearchIsProvided = true;
	}

	public void settings() {
		size(1200, 800);
		//		fullScreen();
	}

	public void setup() {
		// Frame rate
		frameRate(20);

		// Create an initial tree
		//		createMonteCarloTree();
	}

	public void createMonteCarloTree() {
		// TODO Auto-generated method stub
		treeSearch = new TreeSearch();

		treeSearch.addIdentifier("1", null);

		for (int i = 2; i < 6; i++) {
			treeSearch.addIdentifier(String.valueOf(i), "1");
		}

		for (int i = 6; i < 12; i++) {
			treeSearch.addIdentifier(String.valueOf(i), "2");
		}

		for (int i = 6; i < 10; i++) {
			treeSearch.addIdentifier(String.valueOf(i), "3");
		}

		//		treeSearch.addIdentifier(String.valueOf(3), "7");

		resetTree(treeSearch);
	}

	public void draw() {
		// Background
		background(255);

		// Display the tree defined
		if (selectedTreeSearch != null) {
			// Time to check the different between the graphs
			if (setDelay) {
				delay(delay);
				setDelay = false;
			}

			// Displaying a new graph
			displayTree(selectedTreeSearch, true);
		} else {
			// Time to check the different between the graphs
			//			delay(1000);

			// Displaying a new graph
			displayTree(treeSearch, false);
		}

		// Re-flowing the graph
		if (directedGraph != null) {
			// Re-flow of the graph 
			boolean done = directedGraph.reflow();

			// Draw of the re-flowed graph
			directedGraph.draw();
			//			if (!done) {loop();
			//			} else {noLoop();}
		}

		// Checking node selected, if any
		if (mousePressed && !treeSearchIsProvided && false) {
			checkClick();
		}

		noLoop();
	}

	/**
	 * Adding something to the tree just clicking a key
	 */
	public void keyPressed() {
		if (!treeSearchIsProvided && false) {
			String newId = String.valueOf(Integer.valueOf(key));
			if (treeSearch == null) {
				addNewNode(newId, null);
			} else {
				addNewNode(newId, treeSearch.getRoot().getIdentifier());
			}
		}
	}

	/**
	 * Checking the node that has been clicked
	 */
	public void checkClick() {
		if (treeSearch != null && !treeSearch.getNodes().isEmpty() && selectedTreeSearch == null) {
			// Current nodes in the tree
			ArrayList<Node> nodes = auxiliaryTree.nodes;

			// Auxiliary variables
			Node nodeSelected = null;
			double minDistance = Double.MAX_VALUE;
			double distanceBetweenClickAndNode = 0;

			// Checking the node that is closer
			for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
				// Node to be checked
				Node node = (Node) iterator.next();

				// Distance to the node
				distanceBetweenClickAndNode = Universal.distance(node.x, node.y, mouseX, mouseY);

				// Checking if the distance is less
				if (minDistance > distanceBetweenClickAndNode) {
					nodeSelected = node;
					minDistance = distanceBetweenClickAndNode;
				}
			}

			// Checking if the closest node it is at a reasonable distance from the click
			if (Math.abs(mouseX - nodeSelected.x) < nodeSelected.r1 * 2
					&& Math.abs(mouseY - nodeSelected.y) < nodeSelected.r2 * 2) {
				// Drawing the circle selected
				fill(0); // Set fill to white
				ellipse(nodeSelected.x, nodeSelected.y, nodeSelected.r1, nodeSelected.r2); // Draw white ellipse using RADIUS mode

				// Selecting from the current tree, the nodes necessary to reach the node selected
				selectedTreeSearch = null;
				treeSearch.display(treeSearch.getRoot().getIdentifier(), 0);
				selectClickedTree(treeSearch.getNodes().get(nodeSelected.identifier));
				selectedTreeSearch.display(selectedTreeSearch.getRoot().getIdentifier());
				setDelay = true;
				System.out.println("3");
				displayingNewTree = true;
			} else {
				selectedTreeSearch = null;
				System.out.println("1");
			}
			System.out.println("2");
		} else {
			//			if (displayingNewTree) {
			//				displayingNewTree = false;
			//								System.out.println("...");
			//			} else {
			selectedTreeSearch = null;
			System.out.println("shiiit");
			//			}
		}

	}

	/**
	 * When a node is selected, the nodes necessary to reach that node are selected and saved in
	 * selectedTree
	 * @param nodeSelected
	 */
	private void selectClickedTree(MCTS.Node nodeSelected) {
		// Create the selectedTree in case it is not yet
		if (selectedTreeSearch == null) {
			selectedTreeSearch = new TreeSearch();
			selectedTreeSearch.addIdentifier(treeSearch.getRoot().getIdentifier(), null);
		}

		// Adding parents of the node
		ArrayList<String> parents = treeSearch.getNodes().get(nodeSelected.getIdentifier()).getParents();
		for (Iterator<String> iterator = parents.iterator(); iterator.hasNext();) {
			String parentId = (String) iterator.next();

			//			addNewNode(nodeSelected.getIdentifier(), parentId);
			selectedTreeSearch.addIdentifier(nodeSelected.getIdentifier(), parentId);
			selectClickedTree(treeSearch.getNodes().get(parentId));
		}
	}

	/**
	 * Adding a new node to the tree
	 * @param newId
	 * @param idParent
	 */
	private void addNewNode(String newId, String idParent) {
		if (treeSearch == null) {
			// Set up the tree
			treeSearch = new TreeSearch();

			// Add first node
			treeSearch.addIdentifier(newId, null);

			// Reset the tree visualized to only the current root of the treeSearch
			resetTree(treeSearch);
		} else {
			// Add the new element
			//			System.out.println("newId " + newId + " idParent " + idParent);
			treeSearch.addIdentifier(newId, idParent);
		}

		// Send to redraw again the nodes
		redraw();
	}

	private void resetTree(TreeSearch treeSearch) {
		// Setting the tree
		directedGraph = new Tree(this,
				new Node(this, treeSearch.getRoot().getIdentifier(), 0, 0, treeSearch.getRoot().label));

		// Technically g is of type DirectedGraph, so build a Tree alias:
		auxiliaryTree = (Tree) directedGraph;

		// Redraw the tree
		redraw();
	}

	// ================================================================
	// METHODS FOR DISPLAYING A SPECIFIC MONTE CARLO TREE SEARCH
	// ================================================================
	private void displayTree(TreeSearch treeSearch, boolean resetTreeInScreen) {
		if (treeSearch != null && treeSearch.getRoot() != null) {
			// Get the identifier of the root
			String rootIdentifier = treeSearch.getRoot().getIdentifier();

			// Reset the screen with only the root of the current tree
			if (resetTreeInScreen)
				resetTree(treeSearch);

			// Display the tree
			display(treeSearch, rootIdentifier);//, new Node(this, rootIdentifier, 0, 0));
		}
	}

	public void display(TreeSearch treeSearch, String identifier) {
		// Setting the root of the tree
		Node root = new Node(this, identifier, 0, 0, "_2");

		// The node may already exist, in that case may already have some input and output nodes
		if (auxiliaryTree.getNodes().contains(root)) {
			root = auxiliaryTree.getNodes().get(auxiliaryTree.getNodes().indexOf(root));
		}

		// Children of the node
		ArrayList<String> children = treeSearch.getNodes().get(identifier).getChildren();

		// Creating the children
		for (String idChild : children) {

			// Create the new child
			Node newNode = new Node(this, idChild, 0, 0, treeSearch.getNodes().get(idChild).label);

			// The child node may already exist, in that case there is no need to display it again
			if (!auxiliaryTree.getNodes().contains(newNode)) {
				//				System.out.println("Padre: " + identifier + " > " + "Hijo: " + idChild);

				// Join the child to the parent
				auxiliaryTree.addChild(root, newNode);
			}

			// Recursive call
			this.display(treeSearch, idChild);
		}
	}

	// set tree
	public TreeSearch getTreeSearch() {
		return treeSearch;
	}

	public void setTreeSearch(TreeSearch treeSearch) {
		this.treeSearch = treeSearch;
		resetTree(treeSearch);
	}

}
