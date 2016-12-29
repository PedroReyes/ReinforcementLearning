package RL;

import java.util.Arrays;

import WorldGameComplex.GameWorldSimpleMap.STATES;

public class State {

	// Specific propetrties of the problem

	// General properties
	private int ordinalState;
	private String shortDescription;
	private double[] state; // feature 1, feature 2, etc...
	public double[] life; // 0: agent life; 1: enemy life
	public int stepsFallingBombs;
	public int stepOfTheStateInTheGame;

	public enum LIFE {
		AGENT_LIFE, ENEMY_LIFE
	};

	public State(double[] state, double[] life, int stepsFallingBombs, int stepOfTheStateInTheGame) {
		this.stepOfTheStateInTheGame = stepOfTheStateInTheGame;

		this.life = new double[2]; // auxiliary variable
		System.arraycopy(life, 0, this.life, 0, life.length);

		this.stepsFallingBombs = stepsFallingBombs;

		this.state = state;
		this.ordinalState = -1;
		this.shortDescription = "";
	}

	public State(double[] state, int ordinalState, double[] life, int stepsFallingBombs, int stepOfTheStateInTheGame) {
		this.stepOfTheStateInTheGame = stepOfTheStateInTheGame;

		this.life = new double[2]; // auxiliary variable
		System.arraycopy(life, 0, this.life, 0, life.length);

		this.stepsFallingBombs = stepsFallingBombs;

		this.state = state;
		this.ordinalState = ordinalState;
		this.shortDescription = "";
	}

	public State(double[] state, int ordinal, String shortDescription, double[] life, int stepsFallingBombs,
			int stepOfTheStateInTheGame) {
		this.stepOfTheStateInTheGame = stepOfTheStateInTheGame;

		this.life = new double[2]; // auxiliary variable
		System.arraycopy(life, 0, this.life, 0, life.length);

		this.stepsFallingBombs = stepsFallingBombs;

		this.state = state;
		this.ordinalState = ordinal;
		this.shortDescription = shortDescription;

	}

	public State(State copy) {
		// Step of this particular state while playing
		this.stepOfTheStateInTheGame = copy.stepOfTheStateInTheGame;

		// Intrinsic variables of a state
		this.life = new double[2]; // auxiliary variable
		System.arraycopy(copy.life, 0, this.life, 0, copy.life.length);

		// Features of a state
		this.state = new double[copy.state.length];
		System.arraycopy(copy.state, 0, this.state, 0, copy.state.length);

		this.ordinalState = copy.ordinalState;
		this.shortDescription = copy.shortDescription;
	}

	// ===========================
	// GETTERS & SETTERS
	// ===========================
	@Override
	public String toString() {
		return "State [ordinalState=" + ordinalState + ", shortDescription=" + shortDescription + ", state="
				+ Arrays.toString(state) + "]";
	}

	public double[] getLife() {
		return life;
	}

	// vida
	public boolean vidaAlta() {
		return state[STATES.VIDA_ALTA.ordinal()] == 1;
	}

	public boolean vidaBaja() {
		return state[STATES.VIDA_BAJA.ordinal()] == 1;
	}

	// deteccion de bombardeo
	public boolean recibiendoBombardeo() {
		return state[STATES.RECIBIENDO_ATAQUE_AEREO.ordinal()] == 1;
	}

	// 
	//	state[STATES.RECIBIENDO_ATAQUE_AEREO.ordinal()]=RAA;
	//
	// estado final del juego
	public boolean agenteDestruido() {
		return state[STATES.AGENTE_DESTRUIDO.ordinal()] == 1;
	}

	public boolean enemigoDestruido() {
		return state[STATES.ENEMIGO_DESTRUIDO.ordinal()] == 1;
	}

	// zona en la que nos encontramos
	public boolean enZonaAtaque() {
		return state[STATES.ZONA_ATAQUE.ordinal()] == 1;
	}

	public boolean enZonaSegura() {
		return state[STATES.ZONA_SEGURA.ordinal()] == 1;
	}

	public boolean enZonaAntibombardeos() {
		return state[STATES.ZONA_ANTIBOMBARDEOS.ordinal()] == 1;
	}

	public boolean enZonaEmboscada() {
		return state[STATES.ZONA_EMBOSCADA.ordinal()] == 1;
	}

	public boolean enZonaReparacion() {
		return state[STATES.ZONA_REPARACION.ordinal()] == 1;
	}

	// posicion del agente
	public double[] agentPosition() {
		return new double[] { agentPositionX(), agentPositionY() };
	}

	public double agentPositionX() {
		return state[state.length - 2];
	}

	public double agentPositionY() {
		return state[state.length - 1];
	}

	// ==============================
	public int getOrdinalState() {
		return ordinalState;
	}

	public double[] getState() {
		return state;
	}

	public String getStateAsString() {
		return "step " + stepOfTheStateInTheGame + " > " + // 
				"Agent life: " + life[LIFE.AGENT_LIFE.ordinal()] + ", Enemy life: " + life[LIFE.ENEMY_LIFE.ordinal()]
				+ ", Agent position(" + agentPositionX() + ", " + agentPositionY() + ")" + ", "
				+ Arrays.toString(getState());
	}

	public void setState(double[] state) {
		this.state = state;
	}

	public void setOrdinalState(int ordinalState) {
		this.ordinalState = ordinalState;
	}

	public void setState(int state) {
		this.ordinalState = state;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(state);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (!Arrays.equals(state, other.state))
			return false;
		return true;
	}

}
