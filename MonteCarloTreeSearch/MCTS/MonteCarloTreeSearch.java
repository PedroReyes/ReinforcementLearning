package MCTS;

import java.util.ArrayList;
import java.util.Random;

import Auxiliar.GameMap;
import Auxiliar.Helper;
import RL.Action;
import RL.RLWorld;
import RL.RLearner;
import RL.RLearner.SELECTION_METHOD;
import RL.State;
import Visualization.TreeDisplay;
import WorldGameComplex.GameWorldSimpleMap;
import WorldGameComplex.GameWorldSimpleMap.ACTIONS;
import processing.core.PApplet;

public class MonteCarloTreeSearch {

	// =====
	// DEBUG
	// =====
	boolean debug = false;
	static boolean displayTree = false;

	// ===========================================================================
	// MODEL (FOR SIMULATION) AND LEARNER (FOR SELECTING ACTIONS WHILE SIMULATING)
	// ===========================================================================
	RLWorld model; // It is used to simulate a game
	RLearner learner; // It is used to established in "model" the current state of the game

	// ======================================================
	// VARIABLES FOR CONSTRUCTING THE MONTE CARLO TREE SEARCH
	// ======================================================
	TreeSearch treeSearch; // the actual tree search
	TreeDisplay treeSearchDisplay; // the object where we display the tree search
	private int temporalId; // To set id for nodes so they are unique based on this id
	Node root; // The root of the tree

	// =======================================================
	// VARIABLES FOR STOPPING THE SEARCHING OF THE BEST ACTION
	// =======================================================
	// Crucial parameters
	public int totalNumberSteps;
	double timeForExecution = 0.2; // Time for execution (n seconds) (not currently working)

	// ===========================================================
	// ALTERNATIVE USING A LIMITED NUMBER OF NEXT STEPS IN 
	// THE SEARCH INSTEAD OF A FULLY SEARCH UNTIL A TERMINAL STATE
	// ===========================================================
	// Possible inclusion (instead of waiting until the end of a game, maybe because it is too long, recollect the average reward
	// for the next "n" actions taken
	public boolean useDepthCharge = false;
	public int simulationDepthCharge = 0;
	public int numberOfStepsSimulated = 0;
	public double acumulatedRewardInSimulation = 0;
	public double C = 1; // this has been set as a constant

	/**
	 * Constructor
	 * @param learner
	 */
	public MonteCarloTreeSearch(RLearner learner) {
		// The world
		if (learner == null) {
			this.model = new GameWorldSimpleMap(null, null, new GameMap());
		} else {
			this.model = new GameWorldSimpleMap(null, null, new GameMap());
			this.model.setWorldState(learner.getStateBefore());
		}

		// The learner
		this.learner = learner;

		// The Monte Carlo Tree Search
		this.treeSearch = new TreeSearch();
	}

	/**
	 * Main method which returns the best possible action or at least try
	 * @return
	 */
	public Integer runMonteCarloTreeSearch() {
		// Timer variables
		double lStartTime = System.nanoTime();
		double lEndTime = lStartTime;
		double diffTime = ((lEndTime - lStartTime) / 1000000000);

		// Auxiliary variable
		Node nodeSelected = null;
		double score;
		int steps = 0;

		// Adding the root/initial state of the MCTS
		temporalId = 0;

		// Creating the model with the current state of the learner
		this.model = new GameWorldSimpleMap(null, null, new GameMap());
		if (learner != null)
			this.model.setWorldState(learner.getStateBefore());

		// Initialize the Monte Carlo Tree Search
		this.treeSearch = new TreeSearch();
		this.root = new Node(this.model.getCurrentState(), temporalId++, null, 0);
		this.treeSearch.addNode(root, null);

		// The display of the MCTS
		if (displayTree) {
			this.treeSearchDisplay = new TreeDisplay();

			String[] a = { "MAIN" };
			PApplet.runSketch(a, this.treeSearchDisplay);
		}

		if (debug) {
			System.out.println(this.model.getCurrentState().getStateAsString());
		}

		while (steps < totalNumberSteps) {
			// SELECT: select a node and expand the tree from that node
			nodeSelected = select(treeSearch.getRoot());

			// EXPANSION: we expand the node selected to the next states where it would go with its legal actions
			expand(nodeSelected);

			// SIMULATION: in this step none node is created in the tree, just going throughout the model until an end state
			score = simulate(nodeSelected);

			// BACKPROGATION the result from the simulation
			backpropagate(nodeSelected, score);

			// Check time
			lEndTime = System.nanoTime();
			diffTime = ((lEndTime - lStartTime) / 1000000000);

			// Number of simulations
			steps++;

			// Debug of the tree
			if (displayTree) {
				treeSearchDisplay.setTreeSearch(treeSearch);
			}

			if (displayTree || false) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// Debug of the tree
		if (displayTree) {
			treeSearchDisplay.setTreeSearch(treeSearch);

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (debug || false) {
			Node currentState = treeSearch.getRoot();
			System.out.println("Cell "
					+ Integer.toString((int) currentState.state.agentPositionX() + 1
							+ GameMap.dimX * (int) currentState.state.agentPositionY())
					+ " > Agent life: " + currentState.state.getLife()[0] + " > Enemy life: "
					+ currentState.state.getLife()[1]);
		}

		Action bestAction = selectBestActionBasedOnScore(treeSearch.getRoot());
		Integer actionOrdinal = bestAction.ordinal();

		if (debug) {
			System.out.println(bestAction.name());
		}

		return actionOrdinal;
	}

	/**
	 * Selection: start from root R and select successive child nodes down to a leaf node L. 
	 * The section below says more about a way of choosing child nodes that lets the game 
	 * tree expand towards most promising moves, which is the essence of Monte Carlo tree search.
	 */
	public Node select(Node node) {

		// The node is selected if no visits were done
		if (node.visits == 0)
			return node;

		// The first child of the node with no visits is selected
		for (int i = 0; i < node.getChildren().size(); i++) {
			Node child = treeSearch.getNodes().get(node.getChildren().get(i));
			if (child.visits == 0)
				return child;
		}

		// Selecting based on the scoring node's children
		if (node.getChildren().size() == 0) {
			return node;
		} else {
			double bestScore = -Double.MAX_VALUE;
			Node result = node;

			for (int i = 0; i < node.getChildren().size(); i++) {
				// Getting the child
				Node child = treeSearch.getNodes().get(node.getChildren().get(i));

				// Assigning the node as the last parent call
				child.lastParent = node;

				// Setting the score of the child
				double newScore = selectFunction(child);

				// Selecting the selected node results
				if (newScore > bestScore) {
					bestScore = newScore;
					result = child;
				}
			}

			return select(result);
		}
	}

	/**
	 * Expansion: unless L ends the game with a win/loss for either player, 
	 * either create one or more child nodes or choose from them node C. 
	 */
	public void expand(Node node) {
		// Legal actions from the state represented by this node
		Action[] actions = model.findLegalActions(node.state);

		// Adding a state to the tree for each legal action
		for (int actionIndex = 0; actionIndex < actions.length; actionIndex++) {
			addNewStateToSearchTree(node, actions[actionIndex]);
		}
	}

	/**
	 * Simulation: play a random playout from node C.
	 *
	 * REMINDER: I have still to implement the pointcuts in a aspects that save the state before starting
	 * the simulation and reset to this saved state after finishing the simulate method !!!
	 */
	public double simulate(Node state) {
		// Operations before executing a simulation
		model.saveState();

		// Set the state
		this.model.setWorldState(state.state);

		// Debug
		if (debug || false) {
			System.out.println("))))))))))))))) State root (simulation mode)" + state.state.getStateAsString());
		}

		// Auxiliary variables for return the result of the simulation
		acumulatedRewardInSimulation = 0;
		numberOfStepsSimulated = 0;
		double score;

		// If we are in a terminal state, there is nothing to simulate
		if (terminalState(state)) {
			score = model.getReward(state.state);

			if (debug) {
				System.out.println("TERMINAL STATE --------------> score = " + score);
			}

			return score;
		}

		// Simulating
		while (!terminalState(state)) {
			// Take the best action (the policy here could be the action-state value policy of a Reinforcement Learning algorithm)
			Action bestAction = selectBestActionForSimulation(state);

			// New state taking the best action
			state = createNextState(state, bestAction, true);

			// Debug
			if (debug) {
				System.out.println(state.getState().getStateAsString());
			}

			if (debug || false) {
				System.out.println(">>>>>>>>>>>>>>> State root (simulation mode)" + state.state.getStateAsString());
			}

			// Simulation based on a depth charge 
			// instead of finishing when achieving a terminal state
			if (useDepthCharge) {
				// Accumulated result of the simulation
				acumulatedRewardInSimulation = acumulatedRewardInSimulation + model.getReward();

				// Number of simulated steps executed 
				numberOfStepsSimulated++;

				// Checking threshold of simulated steps
				if (numberOfStepsSimulated >= simulationDepthCharge) {
					break;
				}
			}
		}

		// Return the result of the simulation
		score = useDepthCharge ? acumulatedRewardInSimulation / numberOfStepsSimulated : model.getReward();

		// Debug
		if (debug || false) {
			//			System.out.println("_________ simulation score = " + score);
			System.out.println("_________ simulation score: " + score + " = " + acumulatedRewardInSimulation + "/"
					+ numberOfStepsSimulated);
			System.out.println("_________ model score:      " + model.getReward());
			//			System.out.println("_________" + score + " = " + acumulatedRewardInSimulation);
		}

		// Operations after executing a simulation
		model.resetToLastSavedState();

		// Return the reward of this end state
		return score;

	}

	/**
	 * Backpropagation: use the result of the playout to update 
	 * information in the nodes on the path from C to R. 
	 */
	public void backpropagate(Node node, double score) {
		// Updating the parents up to the root of the node
		while (node.lastParent != null) {
			// Update current node - Score = 1 is win; Score = 0 is loose
			node.utility = (node.utility * node.visits + score) / (node.visits + 1);
			node.visits++;
			node.resetLabel();

			// debug
			if (debug || false) {
				System.out.println(node.state.getStateAsString() + " >>>>>>> "//
						+ node.utility + ", " //
						+ node.visits + ", " // 
						+ score + ", " // 
						+ (node.lastParent != null ? node.lastParent.visits : "null"));
			}

			// Checking inconsistency
			if (Double.isNaN(node.utility)) {
				try {
					throw new Exception("State exception");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					System.exit(2);
				}
			}

			// Modify next node
			node = node.lastParent;
		}

		if (debug || false) {
			System.out.println("==========================");
		}

		// Updating the root
		if (node.visits == 0) {
			node.visits = 1;
		} else {
			ArrayList<String> children = node.getChildren();
			node.visits = 0;
			for (int i = 0; i < children.size(); i++) {
				node.visits = node.visits + this.treeSearch.getNodes().get(children.get(i)).visits;
			}
		}

		node.resetLabel();
	}

	// ==============================================================================
	// ===============================AUXILIAR METHODS===============================
	// ==============================================================================

	private boolean terminalState(Node state) {
		// Set the model into this state
		model.setWorldState(state.state);

		// We finish the simulation returning the reward
		if (model.endState(state.state)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Given a state this method selects the best action
	 * @param state
	 * @return
	 */
	private Action selectBestActionForSimulation(Node state) {
		// Possible actions from the node we currently are
		Action[] actions = model.findLegalActions(state.state);

		if (actions != null && actions.length != 0) {
			// Choosing randomly the action
			Action bestAction = actions[(new Random()).nextInt(actions.length)];

			// Choosing the action using the learner knowledge
			if (true) {
				int actionOrdinal = selectAction(state.state);

				bestAction = new Action(actionOrdinal, ACTIONS.values()[actionOrdinal].name());
			}

			return bestAction;
		} else {
			// throw new Exception("State exception: there are no actions to be taken.");
			return null;
		}
	}

	/**
	 * Given a state this method selects the best action of the possible already expanded based on 
	 * the score provided by the "selectFunction" function
	 * @param state
	 * @return
	 */
	private Action selectBestActionBasedOnScore(Node node) {
		// Selecting based on the scoring node's children
		if (node.getChildren().size() == 0) {
			return null;
		} else {
			double bestScore = Integer.MIN_VALUE;
			Random r = new Random();
			Node result = treeSearch.getNodes().get(node.getChildren().get(r.nextInt(node.getChildren().size())));

			for (int i = 0; i < node.getChildren().size(); i++) {
				// Getting the child
				Node child = treeSearch.getNodes().get(node.getChildren().get(i));

				// Assigning the node as the last parent call
				child.lastParent = node;

				// Setting the score of the child
				double newScore = selectFunction(child);

				// Selecting the selected node results
				if (newScore > bestScore) {
					bestScore = newScore;
					result = child;
				}
			}

			Action bestAction = result.actionExecutedFromParentState;

			return bestAction;
		}
	}

	/**
	 * Given an state and the set selection method returns the action to be taken
	 * 
	 * @param state
	 * @return
	 */
	public int selectAction(State state) {
		//		System.out.println("Alpha: " + alpha);
		//		System.out.println("Gamma: " + gamma);
		//		System.out.println("Epsilon: " + epsilon);
		// double[] qValues = policy.getQValuesAt(state);
		int selectedAction = -1;
		State aux = new State(learner.getThisWorld().getCurrentState());

		switch (SELECTION_METHOD.values()[learner.actionSelectionMethodForSimulation]) {
		case E_GREEDY: {
			//			System.out.println("great");
			selectedAction = learner.eGreedySelection(state);
			break;
		}
		case E_GREEDY_CHANGING_TEMPORALLY: {
			// If we are changing the value of epsilon throught the time
			// we modified its value here
			if (SELECTION_METHOD.E_GREEDY_CHANGING_TEMPORALLY.ordinal() == learner.actionSelectionMethodForSimulation // <-- if the code is here, this will be always true
					&& learner.totalEpisodes > 1) {
				double episodeIndexOfFixedEpsilon = (learner.totalEpisodes
						- learner.totalEpisodes * RLearner.percentOfTimeForFixedEpsilon);
				if (learner.episodeNumber < episodeIndexOfFixedEpsilon - 2) {
					learner.epsilon = Helper.round(learner.epsilonRange[0]
							+ ((learner.epsilonRange[1] - learner.epsilonRange[0]) * (learner.episodeNumber + 1))
									/ (episodeIndexOfFixedEpsilon - 1),
							3);

					//					System.out.println(learner.epsilon);
				}
			}
			selectedAction = learner.eGreedySelection(state);
			break;
		}
		case UCB: {
			selectedAction = learner.ucbSelection(state);
			break;
		}
		case SOFT_MAX: {
			selectedAction = learner.softMaxSelection(state);
			break;
		}
		case MONTE_CARLO_TREE_SEARCH: {
			//			selectedAction = learner.mctsSelection(state);
			throw new IllegalArgumentException(
					"Error: you can call as selection method in the simulation a MCTS within a MCTS chosen as selection method for the learner");
		}
		default: {
			throw new IllegalArgumentException("Este método de selección no existe.");
		}
		}

		learner.getThisWorld().setWorldState(aux);

		return selectedAction;
	}

	/**
	 * Selection function
	 * @param node
	 * @return
	 */
	private double selectFunction(Node node) {
		// TODO Auto-generated method stub
		// Variables of the selection algorithm
		double vi = node.utility; // node's utility
		double np = node.visits; // node's visits
		double ni = node.lastParent.visits;//treeSearch.getNodes().get(node.lastParent).visits; // parent's visits

		// Calculating the upper confidence bound on trees (UCT)
		//		double upperConfidenceBoundOnTrees = ni == 0 ? 0 : vi + Math.sqrt(Math.log(np) / ni); // UCT algorithm
		double upperConfidenceBoundOnTrees = vi + C * Math.sqrt(Math.log(np) / ni); // UCT algorithm

		if (debug) {
			System.out.println(vi + ", " + np + ", " + ni + " = " + upperConfidenceBoundOnTrees);
		}
		//		if (Double.isNaN(upperConfidenceBoundOnTrees))return Double.MAX_VALUE;

		return upperConfidenceBoundOnTrees;
	}

	/**
	 * Add a new node to the tree given the current node (a node contains a state)
	 * and the given action
	 * @param currentState
	 * @param action
	 */
	private void addNewStateToSearchTree(Node currentState, Action action) {
		// Create a node containing the next state from the current state executing the given action
		Node newNode = createNextState(currentState, action, false);

		// Adding the new node to the tree
		if (!treeSearch.getNodes().containsKey(newNode.getIdentifier())) {
			//			System.out.println("Added connection from " + Arrays.toString(currentState.state.agentPosition()) + " to "
			//					+ Arrays.toString(newNode.state.agentPosition()) + " using action " + ACTIONS.values()[action]);
			treeSearch.addNode(newNode, currentState);
		}
	}

	/**
	 * Creates a new node which store the new state given when applied 
	 * to the current node passed, that stored the current state, the action given.
	 * The node may be simulated or not, that is, it is part of the final tree
	 * @param node
	 * @param action
	 * @return
	 */
	private Node createNextState(Node node, Action action, boolean simulated) {
		// The next state executing the action
		//		model.saveState();
		State newState = new State(model.getNextState(node.state, action.ordinal()));
		double reward = model.getReward();
		//		model.resetToLastSavedState();

		// The new node
		Node newNode = new Node(newState, (simulated ? -1 : temporalId++), action, reward);

		return newNode;
	}

	public TreeSearch getTreeSearch() {
		return treeSearch;
	}

	public void setTreeSearch(TreeSearch treeSearch) {
		this.treeSearch = treeSearch;
	}

}
