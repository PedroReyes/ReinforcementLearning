package YourTurn;

import java.awt.Point;

import Auxiliar.GameMap;
import RL.Action;
import RL.RLPolicy;
import RL.RLWorld;
import RL.State;

public class EmptyModel implements RLWorld {

	public EmptyModel(State[] states, Action[] actions, GameMap gameMap) {

	}

	@Override
	public int[] getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State[] getStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action[] getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State getNextState(int action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getReward() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getReward(State state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean validAction(int action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean endState() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean endState(State state) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public State resetState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getInitValues() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setInitValues(double initValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public Action getOptimalAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State[] generateStates(Action[] actions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action[] generateActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State[] generateVictoryStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RLPolicy getOptimalPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State[] getVictoryStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentGeneralState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameMap getGameMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAgentLife() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAgentFullLife() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEnemyLife() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEnemyFullLife() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Point getAgentPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action[] findLegalActions(State state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State getNextState(State state, int action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetToLastSavedState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWorldState(State state) {
		// TODO Auto-generated method stub

	}

}
