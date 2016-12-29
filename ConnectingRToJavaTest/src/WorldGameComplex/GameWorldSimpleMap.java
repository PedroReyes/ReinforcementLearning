package WorldGameComplex;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Auxiliar.GameMap;
import RL.Action;
import RL.RLPolicy;
import RL.RLWorld;
import RL.State;

public class GameWorldSimpleMap implements RLWorld {

	// Debugging
	boolean debug = false;

	// AGENT STATS
	double agentFullLife = 10.0;
	double agentLife;

	// ENEMY STATS
	double enemyFullLife = 10.0;
	double enemyLife;
	int stepsForFallingBombs; // each time you removed a 20 percent of enemy life an air attack occurred
	int stepsFallingBombs;
	double bombDamagePerStep; // bombs hurt the agent depending on the agent life and size map so the agent has time enough to get an anti-bomb zone
	double thresholdEnemyLifePercentageForLaunchingAirAttack = 0.8;

	// ENEMY AND AGENT DAMAGE DEPENDING ON EACH LIFE'S
	double agentDamage = 0.1 * enemyFullLife; // positive reward each time we attack the enemy
	double enemyBossDamage = 0.1 * agentFullLife; // negative reward each time the enemy hurts us
	double enemyAmbushDamage; // negative reward each time the enemy hurts us

	// VICTORY STATES
	public State[] victoryStates;

	// ACTIONS AND STATES
	// Use this in case it is suitable to write the state or actions manually, otherwise you will
	// have to go to methods "generateState" or "generateActions" in and do it programmatically in case you can't
	public enum ACTIONS {
		UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN, LEFT, LEFT_UP
	};

	// ESTADOS EXCLUSIVOS, LAS UNICAS COMBINACIONES SON LA VIDA DEL AGENTE CON LOS DIFERENTES ESTADOS
	int stateDimension;

	public enum STATES {
		// FIX STATES REFERRING THE AGENTS LIFE
		VIDA_ALTA, VIDA_BAJA, //

		// EXCLUSIVE STATES (only combinations with the life of the agent)
		//BOMBARDEO_DETECTADO, 
		RECIBIENDO_ATAQUE_AEREO, //
		ZONA_SEGURA, ZONA_REPARACION, ZONA_ATAQUE, ZONA_EMBOSCADA, ZONA_ANTIBOMBARDEOS, //

		// TERMINAL STATES
		AGENTE_DESTRUIDO, ENEMIGO_DESTRUIDO //
	};

	// OPTIMAL POLICY (for analisys purposes)
	RLPolicy optimalPolicy;

	static int[][] policyTrueValues = null;
	// new int[][] {
	// {A, B, C, D, E, TERMINAL_STATE}
	//{ 0, 0, 0, 0, 0, 0 }, // Action - GO_TO_REPAIR_POSITION
	//{ 0, 0, 0, 0, 0, 0 }, // Action - GO_TO_SAFE_POSITION
	//{ 0, 0, 0, 0, 1, 0 } }; // Action - GO_TO_ATTACK_POSITION

	// Random variable for statistical purposes
	Random random;

	// For analysis purposes
	public Action optimalAction;

	// Specific RL attributes
	State currentState;
	State[] states;
	Map<State, Integer> mapStatesToIndex;
	Action[] actions;
	public double initPolicyValues;

	// Auxiliary variable
	int actionExecuted; // action that is executed for the current or last state, depending the perspective we look at it

	// Special attributes of the problem

	// The reward that is giving after an action is being carried out
	double waitingReward; //List<Double> waitingReward;
	int stepOfTheGame;

	// Map
	GameMap gameMap;
	Point agentPosition;

	// =====================================
	// PARAMETERS FOR REWARD CALCULATIONS
	// =====================================
	// Weighs for receiving and producing damage
	//	double weighReceivingDamage = 0.95;
	//	double weighProducingDamage = 1 - weighReceivingDamage;

	// Number of steps that the agent 
	//	double threshold = 0.3;
	//	int thresholdOfBraveness = (int) (agentFullLife * threshold);

	// Number of steps that the agent has been in the current state
	int stepsInCurrentState;

	// Possible cases (solo puede haber una zona activada en cada posicion del mapa)
	STATES[] zones = new STATES[] { STATES.ZONA_SEGURA, STATES.ZONA_REPARACION, STATES.ZONA_ANTIBOMBARDEOS,
			STATES.ZONA_EMBOSCADA, STATES.ZONA_ATAQUE };

	public GameWorldSimpleMap(State[] states, Action[] actions, GameMap gameMap) {
		// SIZE OF THE MAP
		this.gameMap = gameMap;

		// STATES AND ACTIONS ARE OWN OF THE WORLD
		if (actions == null)
			actions = generateActions();

		if (states == null)
			states = generateStates(actions);

		// WINNING STATES (terminal states that result in a winning end)
		victoryStates = generateVictoryStates();

		// SEEDS FOR RANDOM VARIABLES
		int seed = 1000;

		// OPTIMAL POLICY (for analysis purposes)
		optimalPolicy = new RLPolicy(states, actions);

		// GENERAL
		this.states = states;
		this.actions = actions;
		this.random = new Random(seed);

		// THE PROBLEM (giving random values)
		initPolicyValues = 0;
		optimalAction = actions[actions.length - 1];//Actions.Action.tirarPalanca_3;
		resetState();

		// INITIAL LIFE
		agentLife = agentFullLife;
		enemyLife = enemyFullLife;

		//int totalDamageForAirAttack = (int) (0.1 * agentFullLife);
		this.stepsForFallingBombs = this.gameMap.getDimX() * 1;
		this.bombDamagePerStep = (0.1 * agentFullLife) / (this.stepsForFallingBombs);
		this.enemyAmbushDamage = (0.1 * agentFullLife) / (this.gameMap.getDimX() - 2);

		if (debug)
			System.out.println("Optimal action: " + this.optimalAction.name());
	}

	/******* RLWorld interface functions ***********/
	@Override
	public State getNextState(int action) {
		// Just for debuggin purposes this line is
		actionExecuted = action;

		// Calculate reward
		//		calculateRewardUsingLife();
		//		calculateRewardUsingStates();

		// Apply state to agent and enemy lives
		applyStateToAgent();

		// =============================================================
		// UPDATING THE STATE BEFORE
		// =============================================================
		// Carry out action over the map
		Dimension d = getCoords(ACTIONS.values()[action], agentPosition);
		int ax = d.width, ay = d.height;
		if (gameMap.legal(ax, ay) && agentLife > 0 && enemyLife > 0) {
			// move agent
			agentPosition = new Point(ax, ay);
		} else {
			//System.err.println("Illegal action: " + action);
		}

		// Generate new state
		// Variables that compose the states
		agentLife = agentLife <= 0 ? 0 : agentLife;
		int VA = agentLife < agentFullLife / 2 ? 0 : 1; // VIDA_ALTA
		int VB = agentLife < agentFullLife / 2 ? (agentLife <= 0 ? 0 : 1) : 0; // VIDA_BAJA
		int RAA = 0; // RECIBIENDO_ATAQUE_AEREO
		int AD = agentLife <= 0 ? 1 : 0; // AGENTE_DESTRUIDO
		int ED = enemyLife <= 0 ? 1 : 0; // ENEMIGO_DESTRUIDO
		STATES iZoneActivated = gameMap.getMap().get(agentPosition); // zona en la posicion del mapa

		// The air attack is when the enemy goes below 30% of life and it lasts "stepsForFallingBombs" steps
		if (enemyLife <= thresholdEnemyLifePercentageForLaunchingAirAttack * enemyFullLife
				&& stepsFallingBombs < stepsForFallingBombs) {
			//System.out.println("bombing!");
			RAA = 1;
			stepsFallingBombs++;

			// The entire map become a dangerous place except for the anti-air zone
			//			int w = gameMap.getDimX();
			//			int h = gameMap.getDimY();

			if (debug) {
				// The next state
				double[] nextState = generateSingleState(VA, VB, RAA, iZoneActivated, agentPosition, AD, ED);
				String stateName = generateStateName(nextState);

				System.out.println("(Step " + stepsFallingBombs + "/" + stepsForFallingBombs
						+ ") ATAQUE AEREO: AgentLife(" + agentLife + ")" + ", EnemyLife(" + enemyLife + ")"
						+ ", BombDamage(" + bombDamagePerStep + ") > " + stateName);
			}
		} else {
			RAA = 0;

			if (debug) {
				// The next state
				double[] nextState = generateSingleState(VA, VB, RAA, iZoneActivated, agentPosition, AD, ED);
				String stateName = generateStateName(nextState);

				System.out.println("AgentLife(" + agentLife + ")" + ", EnemyLife(" + enemyLife + ")" + ", BombDamage("
						+ bombDamagePerStep + ") > " + stateName + " --> " + waitingReward);
			}
		}

		// Step
		stepOfTheGame++;

		// The next state
		double[] nextState = generateSingleState(VA, VB, RAA, iZoneActivated, agentPosition, AD, ED);
		String stateName = generateStateName(nextState);
		State auxNextState = new State(nextState, getCurrentLifes(), stepsFallingBombs, stepOfTheGame);
		int indexInQTable = mapStatesToIndex.get(auxNextState);
		currentState = new State(nextState, indexInQTable, stateName, getCurrentLifes(), stepsFallingBombs,
				stepOfTheGame);

		// Calculate reward
		//		calculateRewardUsingLife();
		calculateRewardUsingStates();

		return currentState;
	}

	/**
	 * Calculate the reward being in the current state using just state information
	 */
	private void calculateRewardUsingStates() {
		// =============================================================
		// CONSEQUENCES OF THE CURRENT STATE OVER THE AGENT
		// =============================================================
		// Reset the reward
		waitingReward = 0;

		// debug
		boolean debug = false;

		// Reward by zone
		if (currentState.getState()[STATES.ZONA_ATAQUE.ordinal()] == 1) { // ZONA DE ATAQUE
			if (currentState.getShortDescription().contains(STATES.VIDA_ALTA.name()))
				waitingReward = 1;
			else
				waitingReward = -1;

			if (debug)
				System.out.println("Zona de ataque: " + waitingReward);
		} else if (currentState.getState()[STATES.ZONA_REPARACION.ordinal()] == 1) { // ZONA DE REPARACIÓN
			if (currentState.getShortDescription().contains(STATES.VIDA_ALTA.name()))
				waitingReward = -1;
			else
				waitingReward = 1;

			if (debug)
				System.out.println("Zona de reparacion: " + waitingReward);
		} else if (currentState.getState()[STATES.ZONA_EMBOSCADA.ordinal()] == 1) { // ZONA DE EMBOSCADA
			waitingReward = -1;

			if (debug)
				System.out.println("Zona de emboscada: " + waitingReward);
		} else if (currentState.getState()[STATES.ZONA_SEGURA.ordinal()] == 1) {// ZONA SEGURA
			waitingReward = 0;

			if (debug)
				System.out.println("Zona de segura: " + waitingReward);
		} else if (currentState.getState()[STATES.ZONA_ANTIBOMBARDEOS.ordinal()] == 1) { // ZONA ANTIBOMBARDEOS
			waitingReward = 0;

			if (debug)
				System.out.println("Zona de antibombardeos: " + waitingReward);
		}

		// Reward in case of life goes to zero
		if (currentState.getState()[STATES.AGENTE_DESTRUIDO.ordinal()] == 1) {
			waitingReward = -1;

			if (debug)
				System.out.println("Agente destruido: " + waitingReward);
		} else if (currentState.getState()[STATES.ENEMIGO_DESTRUIDO.ordinal()] == 1) {
			waitingReward = 1;

			if (debug)
				System.out.println("Enemigo destruido: " + waitingReward);
		}

		// Reward in case of air attack
		if (currentState.getState()[STATES.RECIBIENDO_ATAQUE_AEREO.ordinal()] == 1) {
			if (currentState.getState()[STATES.ZONA_ANTIBOMBARDEOS.ordinal()] == 1) { // ZONA ANTIBOMBARDEOS
				waitingReward = 1;

				if (debug)
					System.out.println("Bombardeo en zona antibombardeo: " + waitingReward);
			} else {
				waitingReward = -1;

				if (debug)
					System.out.println("Bombardeo en zona no-antibombardeo: " + waitingReward);
			}
		}
	}

	/**
	 * Applied the modification in agent and enemy lives depending on the current state
	 */
	private void applyStateToAgent() {
		// =============================================================
		// CONSEQUENCES OF THE CURRENT STATE OVER THE AGENT
		// =============================================================
		boolean showZone = false;
		if (currentState.getState()[STATES.ZONA_ATAQUE.ordinal()] == 1) { // ZONA DE ATAQUE

			// LIFE STATES
			if (currentState.getShortDescription().contains(STATES.RECIBIENDO_ATAQUE_AEREO.name())) {
				agentLife = agentLife - bombDamagePerStep;
			} else {
				agentLife = agentLife - enemyBossDamage;
				enemyLife = enemyLife - agentDamage;
			}

			if (showZone)
				System.out.println("zona ataque");
		} else if (currentState.getState()[STATES.ZONA_REPARACION.ordinal()] == 1) { // ZONA DE REPARACIÓN
			// LIFE STATES
			agentLife = currentState.getShortDescription().contains(STATES.RECIBIENDO_ATAQUE_AEREO.name())
					? agentLife - bombDamagePerStep : agentFullLife;

			if (showZone)
				System.out.println("zona reparacion");
		} else if (currentState.getState()[STATES.ZONA_EMBOSCADA.ordinal()] == 1) { // ZONA DE EMBOSCADA
			// LIFE STATES
			agentLife = agentLife - enemyAmbushDamage
					- (currentState.getShortDescription().contains(STATES.RECIBIENDO_ATAQUE_AEREO.name())
							? bombDamagePerStep : 0);

			if (showZone)
				System.out.println("zona emboscada");
		} else if (currentState.getState()[STATES.ZONA_SEGURA.ordinal()] == 1) {// ZONA SEGURA
			// LIFE STATES
			if (currentState.getShortDescription().contains(STATES.RECIBIENDO_ATAQUE_AEREO.name())) {
				agentLife = agentLife - bombDamagePerStep;
			}

			if (showZone)
				System.out.println("zona segura");
		} else { // ZONA ANTIBOMBARDEOS
					// There is no cost neither benefit, it is like a ZONA_SEGURA with the difference
					// that in a ZONA_SEGURA if there is an air-attack there will a cost whilst in
					// this zone there will not be
			if (showZone)
				System.out.println("zona antibombardeos");
		}
	}

	private double[] getCurrentLifes() {
		return new double[] { agentLife, enemyLife };
	}

	@Override
	public boolean validAction(int action) {
		Dimension d = getCoords(ACTIONS.values()[action], agentPosition);
		return gameMap.legal(d.width, d.height);
	}

	@Override
	public boolean endState() {
		// TODO Auto-generated method stub
		// The game is finished when the agent or the enemy dies
		boolean endState = currentState.getState()[STATES.AGENTE_DESTRUIDO.ordinal()] == 1
				|| currentState.getState()[STATES.ENEMIGO_DESTRUIDO.ordinal()] == 1;
		return endState;
	}

	@Override
	public boolean endState(State state) {
		// TODO Auto-generated method stub
		// Save state
		saveState();

		setWorldState(state);
		// The game is finished when the agent or the enemy dies
		boolean endState = currentState.getState()[STATES.AGENTE_DESTRUIDO.ordinal()] == 1
				|| currentState.getState()[STATES.ENEMIGO_DESTRUIDO.ordinal()] == 1;

		// Backing up
		resetToLastSavedState();

		return endState;
	}

	@Override
	public State resetState() {
		// TODO Auto-generated method stub
		// Resetting the time there is a air attack
		stepsFallingBombs = 0;

		// Reseting the map
		gameMap.resetMap();

		// Initial agent position is chosen randomly
		agentPosition = new Point((int) Math.random() * gameMap.getDimX(), (int) Math.random() * gameMap.getDimY());

		// Reset agent and enemy lifes
		agentLife = agentFullLife;
		enemyLife = enemyFullLife;

		// Reset initial state
		STATES zoneWhereTheAgentIs = gameMap.getMap().get(agentPosition);
		double[] state = generateSingleState(1, 0, 0, zoneWhereTheAgentIs, agentPosition, 0, 0); // state as array
		//		String name = generateStateName(state); // name of the state
		int index = mapStatesToIndex.get(new State(state, getCurrentLifes(), stepsFallingBombs, stepOfTheGame)); // state index in the Q-table
		currentState = new State(state, index, getCurrentLifes(), stepsFallingBombs, stepOfTheGame);

		// Reset waiting reward
		waitingReward = 0;
		stepOfTheGame = 0;

		return currentState;//currentState.stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public State[] generateStates(Action[] actions) {
		// TODO Auto-generated method stub
		List<State> states = new LinkedList<State>();
		int stateCont = 0;

		// MAP STATE-INDEX
		mapStatesToIndex = new HashMap<State, Integer>();

		// Initial state of the agent will be (0,0)
		agentPosition = new Point(0, 0);

		// Variables that compose the states
		int VA = 1; // VIDA_ALTA
		int VB = 0; // VIDA_BAJA
		int RAA = 0; // RECIBIENDO_ATAQUE_AEREO (state not implemented yet)
		int AD = 0; // AGENTE_DESTRUIDO
		int ED = 0; // ENEMIGO_DESTRUIDO
		int iZoneActivated = 0; // zone where the agent is positioned

		// Composing states
		for (VA = 0; VA < 2; VA++) { // vida alta o no
			for (VB = 0; VB < 2; VB++) { // vida baja o no
				for (RAA = 0; RAA < 2; RAA++) { // bombardeo detectado o no
					for (AD = 0; AD < 2; AD++) { // agente destruido o no
						for (ED = 0; ED < 2; ED++) { // enemigo destruido o no
							// Impossible cases
							if ((VA == 1 && VB == 1) // AGENT LIFE CANNOT BE HIGH AND LOW AT THE SAME TIME
									//|| RAA == 1 // THIS STATE IS NOT IMPLEMENTED YET 
									|| (VA == 1 && AD == 1) // THE AGENT CANNOT BE DESTROYED AND WITH HIGH LIFE AT THE SAME TIME
									|| (VB == 1 && AD == 1) // THE AGENT CANNOT BE DESTROYED AND WITH LOW LIFE AT THE SAME TIME
									|| (VA == 0 && VB == 0 && AD == 0)) { // THE AGENT MUST HAVE HIGHT OR LOW LIFE IF IT IS NOT DEATH 
								// EL AGENTE NO PUEDE TENER LA VIDA ALTA Y BAJA AL MISMO TIEMPO, 
								// PUEDE NO TENER NINGUNA DE LAS DOS LO QUE QUIERE DECIR QUE ESTA MUERTO
								// Impossible cases
							} else {

								for (iZoneActivated = 0; iZoneActivated < zones.length; iZoneActivated++) {
									// ====================================================================================
									// All combination with all possible coordinates
									for (int x = 0; x < gameMap.getDimX(); x++) {
										for (int y = 0; y < gameMap.getDimY(); y++) {
											// Generate state
											double[] auxState = generateSingleState(VA, VB, RAA, zones[iZoneActivated],
													new Point(x, y), AD, ED);

											// Generate name
											String stateName = generateStateName(auxState);

											// Create the state
											State state = new State(auxState, stateCont, stateName, getCurrentLifes(),
													stepsFallingBombs, stepOfTheGame);

											// Insert the state only if it is possible in the current GameMap
											if (// The enemy cannot die in a non-attack zone
											state.getShortDescription().contains(STATES.ENEMIGO_DESTRUIDO.name())
													&& !(gameMap.getMap().get(new Point(x, y)).name()
															.contains(STATES.ZONA_ATAQUE.name()))) {
												// =================
												// IMPOSSIBLE STATE
												// =================
											} else if (// The enemy cannot die in a non-attack zone
											(state.getShortDescription().contains(STATES.ZONA_ANTIBOMBARDEOS.name())
													&& (!(gameMap.getMap().get(new Point(x, y)).name()
															.contains(STATES.ZONA_ANTIBOMBARDEOS.name()))))
													|| (state.getShortDescription()
															.contains(STATES.ZONA_ANTIBOMBARDEOS.name())
															&& state.getShortDescription()
																	.contains(STATES.AGENTE_DESTRUIDO.name()))) {
												// =================
												// IMPOSSIBLE STATE
												// =================
											} else if (// The state contains the name of a possible state zone (for example, ZONA DE RECUPERACION)
											state.getShortDescription()
													.contains(gameMap.getMap().get(new Point(x, y)).name())) {
												// Add the state
												states.add(state); // estado de la zona

												// Mapping between states and index in the table
												mapStatesToIndex.put(state, stateCont);

												// Increment index of states
												stateCont++;
											}
										}
									}
									// ====================================================================================
								}
							}
						}
					}
				}
			}
		}

		// Convert the list to an array of states
		return states.toArray(new State[states.size()]);
	}

	/**
	 * Given the state in form of an array this method returns the name of the state
	 * @param auxState
	 * @return
	 */
	private String generateStateName(double[] auxState) {
		String stateName = "";
		int numberOfStates = STATES.values().length;

		// STATE
		for (int iState = 0; iState < numberOfStates; iState++) {
			String name = STATES.values()[iState].name();
			//			stateName = stateName + (auxState[iState] == 1 ? (iState + 1 == STATES.values().length ? name : name + "")
			//					: (iState + 1 == STATES.values().length ? "" : ""));
			stateName = stateName + (auxState[iState] == 1 ? name + "_" : "");
		}

		// COORDINATES
		stateName = stateName + auxState[numberOfStates + 0];
		stateName = stateName + "_" + auxState[numberOfStates + 1];

		return stateName;
	}

	/**
	 * Generate a state given the parameter of it
	 * @param VA
	 * @param VB
	 * @param RAA
	 * @param zones
	 * @param iZoneActivated
	 * @param AD
	 * @param ED
	 * @return
	 */
	private double[] generateSingleState(int VA, int VB, int RAA, STATES iZoneActivated, Point position, int AD,
			int ED) {
		// State dimension
		// VA,VB,ZA,ZS,ZR,ZE,ZAB,BD, POSICION DEL AGENTE EN X, POSICION DEL AGENTE EN Y
		stateDimension = STATES.values().length + 2;

		// Auxiliar variable to store the state
		double[] auxState = new double[stateDimension];

		// vida
		auxState[STATES.VIDA_ALTA.ordinal()] = VA;
		auxState[STATES.VIDA_BAJA.ordinal()] = VB;

		// deteccion de bombardeo
		auxState[STATES.RECIBIENDO_ATAQUE_AEREO.ordinal()] = RAA;

		// estado final del juego
		auxState[STATES.AGENTE_DESTRUIDO.ordinal()] = AD;
		auxState[STATES.ENEMIGO_DESTRUIDO.ordinal()] = ED;

		// zona en la que nos encontramos
		auxState[iZoneActivated.ordinal()] = 1;

		// posicion del agente
		auxState[stateDimension - 2] = position.getX();
		auxState[stateDimension - 1] = position.getY();

		return auxState;
	}

	@Override
	public Action[] generateActions() {
		// TODO Auto-generated method stub
		Action[] result = new Action[ACTIONS.values().length];
		ACTIONS[] actions = ACTIONS.values();
		for (int i = 0; i < actions.length; i++) {
			result[i] = new Action(i, actions[i].name());
		}

		return result;
	}

	@Override
	public State[] generateVictoryStates() {
		boolean aux = true;
		if (aux) {
			// Victory point
			Point victoryPoint = new Point(gameMap.getDimX() - 1, gameMap.getDimY() - 1);

			// Variables that compose the states
			int VA = 1; // VIDA_ALTA
			int VB = 0; // VIDA_BAJA
			int RAA = 0; // BOMBARDEO_DETECTADO
			int AD = 0; // AGENTE_DESTRUIDO
			int ED = 1; // ENEMIGO_DESTRUIDO
			STATES iZoneActivated = gameMap.getMap().get(victoryPoint);// zona en la posicion del mapa

			// Generate state
			double[] auxState = generateSingleState(VA, VB, RAA, iZoneActivated, victoryPoint, AD, ED);

			// Generate name
			String stateName = generateStateName(auxState);

			// Create the state
			//System.out.println(stateName);
			State state = new State(auxState,
					mapStatesToIndex.get(new State(auxState, getCurrentLifes(), stepsFallingBombs, stepOfTheGame)),
					stateName, getCurrentLifes(), stepsFallingBombs, stepOfTheGame);

			// Victory states
			State[] victoryStates = new State[] { state };

			return victoryStates;
		} else {
			return null;
		}
	}

	// ===========================
	// GETTERS & SETTERS
	// ===========================
	@Override
	public int[] getDimension() {
		// TODO Auto-generated method stub
		return new int[] { states.length, actions.length };
	}

	@Override
	public double getReward() {
		// TODO Auto-generated method stub
		return waitingReward;//waitingReward.stream().mapToDouble(Double::doubleValue).toArray();
	}

	@Override
	public State[] getStates() {
		// TODO Auto-generated method stub
		return states;
	}

	@Override
	public Action[] getActions() {
		// TODO Auto-generated method stub
		return actions;
	}

	public void setStates(State[] states) {
		this.states = states;
	}

	public void setActions(Action[] actions) {
		this.actions = actions;
	}

	@Override
	public Action getOptimalAction() {
		return optimalAction;
	}

	public void setOptimalAction(Action optimalAction) {
		this.optimalAction = optimalAction;
	}

	@Override
	public double getInitValues() {
		// TODO Auto-generated method stub
		return initPolicyValues;
	}

	@Override
	public void setInitValues(double initValue) {
		// TODO Auto-generated method stub
		this.initPolicyValues = initValue;
	}

	@Override
	public RLPolicy getOptimalPolicy() {
		// TODO Auto-generated method stub
		if (policyTrueValues != null) {
			optimalPolicy = null;

			return optimalPolicy;
		} else {
			return null;
		}
	}

	@Override
	public State[] getVictoryStates() {
		// TODO Auto-generated method stub
		return victoryStates;
	}

	@Override
	public String getCurrentGeneralState() {
		// TODO Auto-generated method stub
		return "Agent life: " + agentLife + " & Enemy life: " + enemyLife + "> Current state: " + currentState// (the current state may be more than one state) + STATES.values()[currentState.getState()]
				+ " > Action: " + ACTIONS.values()[actionExecuted] + " > Next state: " // (the next state may be more than one state) + STATES.values()[nextState]
				+ " > reward: " + waitingReward;
	}

	// ===================================
	// AUXILIAR METHODS FOR MAP PURPOSES
	// ===================================
	Dimension getCoords(ACTIONS action, Point coordinates) {
		int ax = coordinates.x, ay = coordinates.y;

		switch (action) {
		case UP:
			ay = ay + 1;
			break;
		case RIGHT_UP:
			ay = ay + 1;
			ax = ax + 1;
			break;
		case RIGHT:
			ax = ax + 1;
			break;
		case RIGHT_DOWN:
			ay = ay - 1;
			ax = ax + 1;
			break;
		case DOWN:
			ay = ay - 1;
			break;
		case LEFT_DOWN:
			ay = ay - 1;
			ax = ax - 1;
			break;
		case LEFT:
			ax = ax - 1;
			break;
		case LEFT_UP:
			ay = ay + 1;
			ax = ax - 1;
			break;
		default:
			throw new IllegalArgumentException("No existe el metodo de selección seleccionado");
		}

		return new Dimension(ax, ay);
	}

	// ===================================
	// AUXILIAR METHODS FOR GUI PURPOSES
	// ===================================
	@Override
	public GameMap getGameMap() {
		return gameMap;
	}

	@Override
	public double getAgentLife() {
		return agentLife;
	}

	@Override
	public double getAgentFullLife() {
		return agentFullLife;
	}

	@Override
	public double getEnemyLife() {
		return enemyLife;
	}

	@Override
	public double getEnemyFullLife() {
		return enemyFullLife;
	}

	@Override
	public Point getAgentPosition() {
		return agentPosition;
	}

	@Override
	public State getCurrentState() {
		return currentState;
	}

	// =========================================
	// AUXILIARY MONTE CARLO TREE SEARCH METHODS
	@Override
	public State getNextState(State state, int action) {
		// saving before changing anything
		saveState();

		// Getting the next state for the temporal current state
		setWorldState(state);
		State nextState = new State(getNextState(action));

		// Getting back the value of the current state to the older one
		resetToLastSavedState();

		return nextState;
	}// =========================================

	@Override
	public void setWorldState(State state) {
		//		stepsFallingBombs = 0;
		waitingReward = 0;
		stepOfTheGame = state.stepOfTheStateInTheGame;
		currentState = new State(state);
		agentLife = state.life[State.LIFE.AGENT_LIFE.ordinal()];
		enemyLife = state.life[State.LIFE.ENEMY_LIFE.ordinal()];
		agentPosition.x = (int) state.agentPositionX();
		agentPosition.y = (int) state.agentPositionY();
	}

	@Override
	public Action[] findLegalActions(State state) {
		// TODO Auto-generated method stub
		// saving before changing anything
		saveState();

		// Getting legal actions for current state
		setWorldState(state);
		List<Action> legalActions = new LinkedList<Action>();
		for (int i = 0; i < actions.length; i++) {
			Action action = actions[i];
			Dimension d = getCoords(ACTIONS.valueOf(action.name()), agentPosition);
			int ax = d.width, ay = d.height;
			if (gameMap.legal(ax, ay) && agentLife > 0 && enemyLife > 0) {
				//				System.out.println("Posicion legal: " + d.toString());
				legalActions.add(action);
			}
		}

		// Getting back the value of the current state to the older one
		resetToLastSavedState();

		return legalActions.toArray(new Action[legalActions.size()]);
	}

	// Saving the local state of the object
	State auxState;
	double auxEnemyLife;
	double auxAgentLife;
	Point auxAgentPosition;
	int auxSteps;
	int auxStepsFallingBombs;

	@Override
	public void saveState() {
		// Saving the local state of the object
		auxState = new State(currentState);
		auxState.life[0] = agentLife;
		auxState.life[1] = enemyLife;
		auxAgentPosition = new Point(agentPosition.x, agentPosition.y);
		auxSteps = stepOfTheGame;
		auxStepsFallingBombs = stepsFallingBombs;
	}

	@Override
	public void resetToLastSavedState() {
		// Getting back the value of the current state to the older one
		currentState = new State(auxState);
		agentLife = auxState.life[0];
		enemyLife = auxState.life[1];
		agentPosition = new Point(auxAgentPosition.x, auxAgentPosition.y);
		stepOfTheGame = auxSteps;
		stepsFallingBombs = auxStepsFallingBombs;
	}

	public Point getAgentPositionInState(State state) {
		// posicion del agente
		return new Point((int) state.getState()[stateDimension - 2], (int) state.getState()[stateDimension - 1]);
	}

}
