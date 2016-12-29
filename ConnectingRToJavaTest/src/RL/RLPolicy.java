package RL;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class RLPolicy {
	// Q-Table 
	private RealMatrix qValuesTable;

	// Q-Table dimensions
	int numRows; // number of actions
	int numCols; // number of states
	int numStates, numActions;

	// Auxiliar variables to represent states and actions
	State[] states;
	Action[] actions;

	// Initial values of the Q-Table
	double initialValue;

	/**
	 * Constructor of a Q-Table with as states and actions as indicated
	 * 
	 * @param states
	 * @param actions
	 */
	public RLPolicy(State[] states, Action[] actions) {
		// this.dimSize = dimSize;
		// States and actions
		this.states = states;
		this.actions = actions;

		// Auxiliar variables
		this.numRows = actions.length;
		this.numCols = states.length;
		this.numActions = numRows;// Get number of actions.
		this.numStates = numCols; // Get number of states.

		// The policy
		// Create n-dimensional array with size given in dimSize array.
		qValuesTable = MatrixUtils.createRealMatrix(numRows, numCols);
	}

	/**
	 * Initial values of the Q-Table
	 * 
	 * @param initValue
	 */
	public void initValues(double initValue) {

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				qValuesTable.setEntry(i, j, initValue);
			}
		}

	}

	/**
	 * Given an state, return the maximun q-value
	 * 
	 * @param state
	 * @return
	 */
	public double getMaxQValue(int state) {
		double[] qValues = myQValues(state);
		double maxQ = -Double.MAX_VALUE;

		for (int action = 0; action < qValues.length; action++) {
			if (qValues[action] > maxQ) {
				maxQ = qValues[action];
			}
		}
		return maxQ;
	}

	/**
	 * Given an state, return the action with the maximun q-value
	 * 
	 * @param state
	 * @return
	 */
	public int getActionWithMaxQValue(int state) {
		double[] qValues = myQValues(state);
		double maxQ = -Double.MAX_VALUE;
		int maxQAction = 0;

		for (int action = 0; action < qValues.length; action++) {
			if (qValues[action] > maxQ) {
				maxQ = qValues[action];
				maxQAction = action;
			}
		}

		return maxQAction;
	}

	/**
	 * Given an state return the best action
	 * 
	 * @param state
	 * @return
	 */
	public int getBestAction(int state) {

		double[] qValues = myQValues(state);
		double maxQ = -Double.MAX_VALUE;
		int selectedAction = -1;
		int[] doubleValues = new int[qValues.length];
		int maxDV = 0;

		qValues = myQValues(state);

		for (int action = 0; action < qValues.length; action++) {
			// System.out.println( "STATE: [" + state[0] + "," + state[1] + "]" );
			// System.out.println( "action:qValue, maxQ " + action + ":" + qValues[action] + "," + maxQ );

			if (qValues[action] > maxQ) {
				selectedAction = action;
				maxQ = qValues[action];
				maxDV = 0;
				doubleValues[maxDV] = selectedAction;
			} else if (qValues[action] == maxQ) {
				maxDV++;
				doubleValues[maxDV] = action;
			}
		}

		if (maxDV > 0) {
			// System.out.println( "DOUBLE values, random selection, maxdv =" + maxDV );
			int randomIndex = (int) (Math.random() * (maxDV + 1));
			selectedAction = doubleValues[randomIndex];
		}

		if (selectedAction == -1) {
			// System.out.println("RANDOM Choice !" );
			selectedAction = (int) (Math.random() * qValues.length);
		}

		return selectedAction;

	}

	// ===================
	// AUXILIAR METHODS
	// ===================
	private double[] myQValues(int state) {
		// Get the qValues for every action on this state
		return qValuesTable.getColumn(state);
	}

	public double[] getQValuesAt(int state) {
		// Get the qValues for every action on this state
		return qValuesTable.getColumn(state);
	}

	// ===================
	// GETTERS & SETTERS
	// ===================
	public void setQValue(int state, int action, double newQValue) {
		//System.out.println("Setting state " + state + " with action " + action + " the value " + newQValue);
		qValuesTable.setEntry(action, state, newQValue);
	}

	public double getQValue(int state, int action) {
		return qValuesTable.getEntry(action, state);
	}

	public double getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(double initialValue) {
		this.initialValue = initialValue;
	}

	@Override
	public String toString() {
		String result = "RLPolicy [qValuesTable=\n";

		for (int row = 0; row < qValuesTable.getRowDimension(); row++) {
			result = result + "{";
			for (int column = 0; column < qValuesTable.getColumnDimension(); column++) {
				result = result + getQValue(column, row)
						+ (qValuesTable.getColumnDimension() - 1 == column ? "" : ", ");
			}
			result = result + "}\n";

		}

		result = result + "]";
		return result;
	}

	public RealMatrix getqValuesTable() {
		return qValuesTable;
	}

	public void setqValuesTable(RealMatrix qValuesTable) {
		this.qValuesTable = qValuesTable;
	}

}
