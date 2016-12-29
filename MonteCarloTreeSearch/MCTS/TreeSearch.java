package MCTS;

import java.util.ArrayList;
import java.util.HashMap;

public class TreeSearch {

	public final static int ROOT = 0;

	private HashMap<String, Node> nodes;
	private String rootIdentifier;
	//	private TraversalStrategy traversalStrategy;

	// Constructors
	public TreeSearch() {
		nodes = new HashMap<>();
	}

	// Properties
	public HashMap<String, Node> getNodes() {
		return nodes;
	}

	public Node getRoot() {
		return nodes.get(rootIdentifier);
	}

	// Public interface (specific nodes)
	//	public Node addNode(Node newNode) {
	//		if (nodes.isEmpty()) {
	//			System.out.println("Root has been added.");
	//			rootIdentifier = newNode.getIdentifier();
	//		}
	//
	//		return addNodeUsingParentIdentifier(newNode, null);
	//	}

	//	public Node addNode(Node newNode, Node parent) {
	//		nodes.put(newNode.getIdentifier(), newNode);
	//
	//		if (parent != null // 
	//				&& !nodes.get(parent).getChildren().contains(newNode.getIdentifier())// 
	//				&& !newNode.equals(parent)) {
	//			nodes.get(parent).addChild(newNode.getIdentifier());
	//			nodes.get(newNode).addParent(parent.getIdentifier());
	//		}
	//
	//		return newNode;
	//	}

	// Public interface (general nodes, using mostly identifers)
	//	public Node addIdentifier(String identifier) {
	//		if (nodes.isEmpty()) {
	//			rootIdentifier = identifier;
	//		}
	//		return this.addIdentifier(identifier, null);
	//	}

	/**
	 * This method is in case you are just using identifiers for testing purposes
	 * @param identifier
	 * @param parent
	 * @return
	 */
	public Node addIdentifier(String identifier, String parent) {

		// If there is no nodes, then this is the root
		if (nodes.isEmpty()) {
			rootIdentifier = identifier;
		}

		// Creating the node
		Node node = new Node(identifier);

		// Adding to the HashMap
		nodes.put(identifier, node);

		// Adding the parent to the child and vice versa
		if (parent != null // 
				&& !nodes.get(parent).getChildren().contains(node)// 
				&& !identifier.equals(parent)) {
			nodes.get(parent).addChild(identifier);
			nodes.get(identifier).addParent(parent);
		}

		return node;
	}

	/**
	 * This method use the class Node which you can change to contain whatever you need
	 * @param newNode
	 * @param parentNode
	 * @return
	 */
	public Node addNode(Node newNode, Node parentNode) {

		if (parentNode == null || nodes.isEmpty()) {
			// If there is no nodes, then this is the root
			//			if (nodes.isEmpty()) {
			//			System.out.println("Root has been added.");
			rootIdentifier = newNode.getIdentifier();
			//			}

			nodes.clear();

			// Adding new node to the HashMap so we can find it faster
			nodes.put(newNode.getIdentifier(), newNode);
		} else {
			// Adding new node to the HashMap so we can find it faster
			nodes.put(newNode.getIdentifier(), newNode);

			// Auxiliary variable
			String parentId = parentNode.getIdentifier();
			String childId = newNode.getIdentifier();

			//			System.out.println("Child " + childId + " and father " + parentId);
			//			System.out.println(nodes.size());

			// Adding the parent to the child and vice versa
			if (parentNode != null && !nodes.get(parentId).getChildren().contains(newNode)
					&& !childId.equals(parentId)) {
				nodes.get(parentId).addChild(childId);
				nodes.get(childId).addParent(parentId);
			}

			// Setting the last parent of this node
			newNode.lastParent = parentNode;
		}

		return newNode;
	}

	public void display(String identifier) {
		this.display(identifier, ROOT);
	}

	public void display(String identifier, int depth) {
		// The node
		Node node = nodes.get(identifier);

		// Children of the node
		ArrayList<String> children = node.getChildren();

		if (depth == ROOT) {
			System.out.println(node.getIdentifier());
		} else {
			String tabs = String.format("%0" + depth + "d", 0).replace("0", "    "); // 4 spaces
			System.out.println(tabs + node.utility + "/" + node.visits + " > " + node.getIdentifier());
			//			System.out.println(tabs + node.wins + "/" + node.games + " > " + node.getIdentifier());
		}
		depth++;
		for (String child : children) {

			// Recursive call
			this.display(child, depth);
		}
	}
}