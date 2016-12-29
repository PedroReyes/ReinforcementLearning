package MCTS;

import java.util.ArrayList;

import Auxiliar.GameMap;
import RL.Action;
import RL.State;

public class Node {

	static int temporalId = 0;
	private String identifier;
	private ArrayList<String> children;
	private ArrayList<String> parents;

	// Label for the tree
	public String label;

	// Particular variables of the node
	public double utility; // utility = wins
	public double visits; // visits = games
	public State state;
	public Node lastParent;
	public Action actionExecutedFromParentState;
	public double rewardAcquired = 0;
	public double UCT = 0;

	// Constructor (just for testing, not connected to the Reinforcement Learning algorithm)
	public Node(String identifier) {
		this.identifier = identifier;
		children = new ArrayList<String>();
		parents = new ArrayList<String>();
		utility = 0;
		visits = 0;
		lastParent = null;
		label = identifier;//"noLabel";
	}

	// Constructor 
	//	public Node(State state) {
	//		this.identifier = state.getStateAsString();//Arrays.toString(state.getState());
	//		this.state = state;
	//		children = new ArrayList<String>();
	//		parents = new ArrayList<String>();
	//		label = //Arrays.toString(state.agentPosition()) 
	//		identifier;
	//		//		"[" + (int) state.agentPositionX() + ", " + (int) state.agentPositionY() + "]" + ", " + (int) state.getLife()[0] + ", " + (int) visits + ", " + (int) utility;
	//	}

	// Constructor (this constructor has parameter connections with the Reinforcement Learning algorithm)
	public Node(State state, int temporalId, Action action, double reward) {
		this.identifier = Integer.toString(temporalId);
		this.state = new State(state);
		utility = 0;
		visits = 0;
		children = new ArrayList<String>();
		parents = new ArrayList<String>();
		lastParent = null;
		actionExecutedFromParentState = action == null ? null : new Action(action.ordinal(), action.name());
		rewardAcquired = reward;
		resetLabel();
		//Arrays.toString(state.agentPosition()) 
		//		identifier;
		//		"[" + (int) state.agentPositionX() + ", " + (int) state.agentPositionY() + "]";
		//		+ ", " + (int) state.getLife()[0] 		+ ", " + (int) visits + ", " + (int) utility;
	}

	public void resetLabel() {
		if (this.lastParent != null)
			this.UCT = this.utility + Math.sqrt(Math.log(this.visits) / this.lastParent.visits);

		label = Integer
				.toString((int) this.state.agentPositionX() + 1 + GameMap.dimX * (int) this.state.agentPositionY())
				+ "\n" + (int) this.utility + "/" + (int) this.visits;

		label = identifier;

		double numberOfDecimales = 100;
		//		String UCT = "";
		//		if (lastParent == null) {
		//			label = "0";
		//		} else {
		//			UCT = lastParent.visits == 0 ? "0"
		//					: Double.toString(Math.round(
		//							(utility + Math.sqrt(Math.log(visits) / (lastParent == null ? visits : lastParent.visits)))
		//									* numberOfDecimales)
		//							/ numberOfDecimales);
		//
		//			label = UCT; // UCT algorithm
		//		}

		if (actionExecutedFromParentState != null)
			label = label + "_" + actionExecutedFromParentState.name();

		label = utility + ", " + visits + (lastParent != null ? ", " + lastParent.visits : "") + UCT;

		if (actionExecutedFromParentState != null) {
			label = "Cell "
					+ Integer.toString(
							(int) this.state.agentPositionX() + 1 + GameMap.dimX * (int) this.state.agentPositionY())
					+ "_" + actionExecutedFromParentState.name() // 
					+ "_UCT=" + UCT + "_Uitlity=" + utility //
					+ "_R=" + rewardAcquired;
		} else {
			label = "Cell " + Integer
					.toString((int) this.state.agentPositionX() + 1 + GameMap.dimX * (int) this.state.agentPositionY());
		}

		label = label + "_Visits=" + visits;
	}

	// Properties
	public String getIdentifier() {
		return identifier;
	}

	public ArrayList<String> getChildren() {
		return children;
	}

	public ArrayList<String> getParents() {
		return parents;
	}

	public State getState() {
		return state;
	}

	// Public interface
	public void addChild(String identifier) {
		//		if (!children.contains(identifier))
		children.add(identifier);
	}

	public void addParent(String identifier) {
		//		if (!parents.contains(identifier))
		parents.add(identifier);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		//		return super.hashCode();
		return identifier.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		//		return super.equals(obj);
		return identifier.equals(obj);
	}

}