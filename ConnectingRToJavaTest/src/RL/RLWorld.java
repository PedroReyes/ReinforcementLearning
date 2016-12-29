package RL;

import java.awt.Point;

import Auxiliar.GameMap;

public interface RLWorld {

	// ========================
	// METHODS TO RUN THE MODEL
	// ========================
	// Returns the array containing the information about the
	// number of states in each dimension ( [0] - [array.length - 2] )
	// and the number of possible actions ( [array.length - 1] ).
	int[] getDimension();

	State[] getStates();

	Action[] getActions();

	// Returns a new instance of the new state that results
	// from applying the given action to the current state.
	State getNextState(int action);// int[] getNextState(String action);

	// Returns the value for the last reward received from
	// calling the method getNextState( int action ).
	// The reward is given from the world, this does not preclude the agent
	// to define its own internal reward.
	double getReward();

	// Returns true if the given action is a valid action
	// on the current state, false if not.
	boolean validAction(int action);

	// Returns true if current state is absorbing state, false if not.
	boolean endState();

	public boolean endState(State state);

	// Resets the current state to the start position and returns that state.
	State resetState();// int[] resetState();

	// Gets the initial value for the policy.
	double getInitValues();

	// Sets the initial value for the policy.
	void setInitValues(double initValue);

	// For analysis purposes
	Action getOptimalAction();

	// States and actions are proper of the World
	public State[] generateStates(Action[] actions);

	public Action[] generateActions();

	public State[] generateVictoryStates(); // terminal state with happy ending :)

	// This method may be possible to implement or not
	public RLPolicy getOptimalPolicy();

	// Return the end states where the agent will win the game
	public State[] getVictoryStates();

	// Get a general description of the current state
	public String getCurrentGeneralState();

	// ======================
	// METHODS TO RUN THE GUI
	// ======================
	public GameMap getGameMap();

	public double getAgentLife();

	public double getAgentFullLife();

	public double getEnemyLife();

	public double getEnemyFullLife();

	public Point getAgentPosition();

	public State getCurrentState();

	public void setWorldState(State state);

	// =======================
	// METHODS TO RUN THE MCTS
	// =======================
	public Action[] findLegalActions(State state);

	public State getNextState(State state, int action);

	public void saveState();

	public void resetToLastSavedState();

}
