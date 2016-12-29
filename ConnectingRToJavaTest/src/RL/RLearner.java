package RL;

import java.util.Arrays;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import Auxiliar.Helper;

public class RLearner {

	public static boolean debugProcessOfEpisodes = false;

	// =========================
	// CONSTANT
	// =========================
	// Prefix file for naming policy files
	public static String prefixForNamingPolicyFile = "QValues_";

	// =========================
	// SIMULATION NAME
	// =========================
	public String simulationName;

	// ===================================
	// MAIN VARIABLES
	// ===================================
	RLWorld thisWorld;
	RLPolicy policy;

	// ===================================
	// IMPROVEMENT OVER TOOLS
	// ===================================
	public boolean useStateParallelization;

	// ===================================
	// LEARNER PARAMETERS
	// ===================================
	int learningMethod; // how to learn?
	private int actionSelectionMethod; // how to select the action?
	private int totalTasks; // how long do I learn? a task is divided in episodes (a task is when we reset the policy to initial values)
	private int totalEpisodes; // how long do I learn? an episode is divided in steps (an episode is the number of times the game is executed)

	// ===================================
	// LEARNER METHODS
	// ===================================
	// Learning types
	public static enum LEARNING_METHOD {
		//, SAMPLE_AVERAGE, SARSA, Q_LAMBDA // Good parms were lambda=0.05, gamma=0.1, alpha=0.01, epsilon=0.1
		Q_LEARNING // Q'(s,a)=Q(s,a)+alpha(s,a)*(R+gamma*max(Q'(s,a)-Q(s,a))
		, Q_LEARNING_LAMBDA // >NO USAR<, ¿Por qué? al ser un metodo off-policy cada vez qeu se coge una accion aleatoria esta rompe la traza de elegibilidad
		// lo que supone que el algoritmo no tome la ventaja que supone usar estas trazas dado que, además, normalmente suele ser comun
		// que en las primeras iteraciones se realicen mucha exploracion por lo que la mejora del algoritmo es solo un poco mas rapido
		//		, GRADIENT_BANDIT // This method is possible if there is only one state, it is based on preferences (it is used with soft-max selection in Sutton & Barto's book)
		// H'(A) = H(A) + alpha*(R-avR)*(1-pi(A)), where A is the action selected
		// H'(a) = H(a) + alpha*(R-avR)*(1-pi(a)), where a is the rest of actions
		// where pi(a)=Pr{A=a}=[e^H(a)]/[Sumatory(e^H(b))] is the probability of taking action a
		, SARSA // (state, action, reward, state action)
		, SARSA_LAMBDA // se supone que es mejor algoritmo qeu el SARSA normal dado que se aprovecha de la ventaja que supone usar trazas de elegibilidad (elegibility traces)
	};

	// ===================================
	// LEARNER PARAMETERS
	// ===================================
	public static enum LEARNING_PARAMETER {
		// GLOBAL PARAMETERS
		totalTasks, totalEpisodes,

		// LEARNING PARAMETERS
		alphaLearningRate, gammaDiscountFactor, lambdaSteps, //

		// SELECTION PARAMETERS
		epsilonSelection, c_UCBSelection, temperatureSoftmaxSelection, baselineUsedSoftmaxSelection//
	}

	// q_learning
	private double alpha; // q-learning: The learning rate determines to what extent the newly acquired information will override the old information.
	// For deterministic environments alpha=1, and for stochastic environments alpha=0.1.
	// gradientBandit-learning: same that q-learning
	// sarsa: same that q-learning
	private double gamma; // q-learning: The discount factor determines the importance of future rewards.
	// A factor of 0 will make the agent "myopic" (or short-sighted) by only considering current rewards, 
	// while a factor approaching 1 will make it strive for a long-term high reward.
	// sarsa: same that q-learning

	// Elegibility traces for TD methods like QLearning or SARSA 
	double lambda; // sarsa: this parameter is used to control how far we take into account the estimations, that is, somehow, how many steps we use for the estimation (this attribute is used for TD(lambda) methods).
	// For TD(lambda) methods (=0 then we have a 1-step TD backup, = 1 then we have a Monte Carlo backup)
	private RealMatrix elegibilityTrace; // elegibility trace makes reference to TD(lambda) methods

	// ===================================
	// SELECTION METHODS
	// ===================================
	// Action selection types
	public static enum SELECTION_METHOD {
		E_GREEDY // normal eGreedy selection
		, E_GREEDY_CHANGING_TEMPORALLY // if you choose this option, you have to set up independently the range of epsilon
		, UCB // Upper-Confident-Bound action selection
		, SOFT_MAX // Selection is based on action preferences. Action preferences are determined to a soft-max distribution (i.e., Gibbs or Boltzmann distribution)
	};

	// e_greedy
	public double epsilon; // for e-greedy selection

	// e_greedy temporally
	private double[] epsilonRange; // [initial value, final value]
	public static double percentOfTimeForFixedEpsilon = 0.333; // if you put 0.1, and you set 100 episodes, then adds 10 episodes to the 100
	// that will have the same epsilon

	// ucb
	private double cExploratoryParameter; // degree of exploration (this number must be greater than 0 to have some effect in the formula)
	private int timesSelected[][]; // number of times an action is selected

	// softmax
	private double softmaxTemperature; // (This paramater is represented with the symbol "tao")
	// High temperatures cause the actions to be all (nearly) equiprobable. 
	// Low temperatures cause a greater difference in selection probability for actions that differ in their value estimates. 
	// In the limit as temperature tends to zero, softmax action selection becomes the same as greedy action selection
	private double averageReward; // reward baseline term with which the reward is compared. 
	// If the reward is higher than the baseline, then the probability of taking At in the future is increased, 
	// and if the reward is below baseline, then probability is decreased. 
	// The non-selected actions move in the opposite direction.
	private boolean baselineUsed; // this boolean variable refers to the used or not of the averageReward variable of the softmax funciton
									// it seems better to use it, so default value will be true

	// ===================================
	// AUXILIAR VARIABLES
	// ===================================
	public State stateBefore;
	//public int state;
	private State newState;
	public int actionSelected;
	private double reward;
	private int taskNumber;
	private int episodeNumber; // which episode we are in
	private int timeStep; // which step time in the episode we are in

	public RLearner() {

	}

	/**
	 * Set the world, policy and default parameters of the RLeaner. To change this default parameters you will have to do it manually using setter methods.
	 * 
	 * @param world
	 */
	public RLearner(String simulationName, RLWorld world) {
		// =========================
		// CONSTANT
		// =========================
		//random = new Random(123);

		// =========================
		// SIMULATION NAME
		// =========================
		this.simulationName = !simulationName.contains(".csv") ? simulationName + ".csv" : simulationName;

		// =========================
		// THE WORLD AND THE POLICY
		// =========================
		// Getting the world from the invoking method.
		thisWorld = world;

		// Creating new policy with dimensions to suit the world.
		policy = new RLPolicy(thisWorld.getStates(), thisWorld.getActions());// dimSize);

		// Initializing the policy with the initial values defined by the world.
		policy.initValues(thisWorld.getInitValues());

		// =========================
		// EXPERIMENT'S PARAMETERS
		// =========================
		// Default number of task (a task would be like create again the RLeaner
		// but with the difference the information is stored in a file)
		totalTasks = 2000;

		// Default number of episodes
		totalEpisodes = 1000;

		// ===================================
		// LEARNER METHODS
		// ===================================
		// default learning method
		learningMethod = LEARNING_METHOD.Q_LEARNING.ordinal();// Q_LEARNING; Q_LAMBDA; SARSA; etc

		// q_learning
		setAlpha(1); // for q-learning method (=1 for deterministic environments)
		setGamma(0.1); // for q-learning method (=0 if only considering current rewards)
		setLambda(0.8); // for TD(lambda) methods (=0 then we have a 1-step TD backup, = 1 then we have a Monte Carlo backup)
		resetElegibilityTrace(); // elegibility trace makes reference to TD(lambda) methods

		// ===================================
		// SELECTION METHODS
		// ===================================
		// default selection method
		actionSelectionMethod = SELECTION_METHOD.E_GREEDY.ordinal();

		// e_greedy
		epsilon = 0.1; // for e-greedy selection

		// e_greedy_temporally
		epsilonRange = new double[] { epsilon, 0 };

		// ucb
		cExploratoryParameter = 1;
		timesSelected = new int[thisWorld.getActions().length][thisWorld.getStates().length];

		// soft-max
		softmaxTemperature = 1; // For high temperatures (\tau\to \infty), all actions have nearly the same probability
		// For a low temperature (\tau\to 0^+), the probability of the action with the highest expected reward tends to 1.
		averageReward = 0; // reward baseline term with which the reward is compared. 
		// If the reward is higher than the baseline, then the probability of taking At in the future is increased, 
		// and if the reward is below baseline, then probability is decreased. 
		// The non-selected actions move in the opposite direction.
	}

	// ============================
	// MAIN METHODS
	// ============================
	private void runningTrial() {

	}

	/**
	 * Execute one trial that is made of as epochs as it was
	 */
	public void runTrial() {
		// Running trial (this method is just for AspectJ deficiencies in writing regular expressions)
		runningTrial();

		// Start the trial (composed by several tasks)
		for (taskNumber = 0; taskNumber < totalTasks; taskNumber++) {
			// Reset the policy for the next task
			newPolicy();

			for (episodeNumber = 0; episodeNumber < totalEpisodes; episodeNumber++) {
				// Run epoch (Start the episode (composed by several episode steps))
				runEpoch();
			}
		}

		// End of trial
		trialCompleted();
	}

	/**
	 * Execute one epoch of the RLearner. It executes the RL algorithm until it reaches an end state
	 * 
	 * @param i
	 */
	public void runEpoch() {

		// Reset state to start position defined by the world.
		stateBefore = thisWorld.resetState();
		elegibilityTrace = resetElegibilityTrace();
		//int[] currentState = thisWorld.getStates()[thisWorld.resetState()].getState();

		switch (LEARNING_METHOD.values()[learningMethod]) {
		case Q_LEARNING:
		case Q_LEARNING_LAMBDA: {
			// Learning method
			qLearning();
			break;
		}
		case SARSA:
		case SARSA_LAMBDA: {
			// Learning method
			sarsaLearning();
			break;
		}
			//		case GRADIENT_BANDIT: {
			//			// This learning method is made to be used just for environments with one single state
			//			if (thisWorld.getStates().length > 1) {
			//				String title = "Learning method: " + LEARNING_METHOD.GRADIENT_BANDIT.name();
			//				String message = "There must be one single state with this learning method";
			//				Helper.showMessageDialog(title, message);
			//				System.exit(0);
			//			} else {
			//				// Learning method
			//				banditLearning();
			//			}
			//			break;
			//		}
		default:
			break;
		} // switch

	} // runEpoch

	/**
	 * Given an state and the set selection method returns the action to be taken
	 * 
	 * @param state
	 * @return
	 */
	public int selectAction(State state) {
		// double[] qValues = policy.getQValuesAt(state);
		int selectedAction = -1;

		switch (SELECTION_METHOD.values()[actionSelectionMethod]) {
		case E_GREEDY: {
			selectedAction = eGreedySelection(state);
			break;
		}
		case E_GREEDY_CHANGING_TEMPORALLY: {
			// If we are changing the value of epsilon throught the time
			// we modified its value here
			if (SELECTION_METHOD.E_GREEDY_CHANGING_TEMPORALLY.ordinal() == getActionSelectionMethod() // <-- if the code is here, this will be always true
					&& totalEpisodes > 1) {

				double episodeIndexOfFixedEpsilon = (totalEpisodes - totalEpisodes * percentOfTimeForFixedEpsilon);
				if (episodeNumber < episodeIndexOfFixedEpsilon - 2) {
					epsilon = Helper.round(epsilonRange[0] + ((epsilonRange[1] - epsilonRange[0]) * (episodeNumber + 1))
							/ (episodeIndexOfFixedEpsilon - 1), 3);
				}
			}

			selectedAction = eGreedySelection(state);
			break;
		}
		case UCB: {
			selectedAction = ucbSelection(state);
			break;
		}
		case SOFT_MAX: {
			selectedAction = softMaxSelection(state);
			break;
		}
		default: {
			throw new IllegalArgumentException("Este método de selección no existe.");
		}
		}
		return selectedAction;
	}

	/**
	 * It carries out the basic process in reinforcement learning algorithm, that is, given the curretn state, execute an action, and receive the new state and
	 * reward.
	 */
	private void reinforcementLearningBaseProcess() {
		//stateBefore = state;
		actionSelected = selectAction(stateBefore);
		newState = thisWorld.getNextState(actionSelected);
		reward = getWorldReward();
	}

	private double getWorldReward() {
		reward = thisWorld.getReward();//[0];

		return reward;
	}

	/**
	 * This method is used just for knowing from the aspects classes when the learner has finished all the tasks
	 */
	private void trialCompleted() {
		// TODO Auto-generated method stub
	}

	/**
	 * This method is used just for knowing from the aspects classes when the learner has finished an episode
	 */
	private void recordCurrentStateOfRLearner() {
		// TODO Auto-generated method stub

	}

	// ============================
	// LEARNING METHODS
	// ============================
	/**
	 * (ACTION Q-VALUES) Use the off-policy Q-learning learning method to update the q-values
	 */
	private void qLearning() {
		int newActionSelected;
		double this_Q;
		double max_Q;
		double new_Q;
		timeStep = 0;

		// A bandit problem is that which there is only a single state
		boolean multiarmProblem = thisWorld.getStates().length == 1 ? true : false;

		while (!thisWorld.endState() || multiarmProblem) {
			// Execute the natural process of [state -> action-> (newState, reward)]
			reinforcementLearningBaseProcess();

			// Execute the process relative to the specific learning method
			this_Q = policy.getQValue(stateBefore.getOrdinalState(), actionSelected);
			max_Q = policy.getMaxQValue(newState.getOrdinalState());

			if (getLearningMethod() == LEARNING_METHOD.Q_LEARNING.ordinal()) {
				// Calculate new Value for Q
				new_Q = this_Q + alpha * (reward + (gamma * max_Q) - this_Q);
				//System.out.println("State(" + RandomWalkWorld.STATES.values()[stateBefore] + "); " + "Action("+ RandomWalkWorld.ACTIONS.values()[actionSelected] + "); " + "Q-value(" + new_Q + ")");
				policy.setQValue(stateBefore.getOrdinalState(), actionSelected, new_Q);
			} else if (getLearningMethod() == LEARNING_METHOD.Q_LEARNING_LAMBDA.ordinal()) {
				// A'
				newActionSelected = selectAction(newState);

				// A*
				int optimalAction = policy.getActionWithMaxQValue(newState.getOrdinalState());

				// Calculating estimates
				double delta = (reward + gamma * max_Q - this_Q);

				// Updating elegibility trace
				elegibilityTrace.setEntry(actionSelected, stateBefore.getOrdinalState(),
						(1 - alpha) * elegibilityTrace.getEntry(actionSelected, stateBefore.getOrdinalState()) + 1);

				// Updating elegibility traces based on elegibility traces
				for (int iAction = 0; iAction < thisWorld.getActions().length; iAction++) {
					for (int jState = 0; jState < thisWorld.getStates().length; jState++) {
						// Updating the qValues using elegibility trace values
						// Updating the qValues using elegibility trace values
						policy.setQValue(jState, iAction, policy.getQValue(jState, iAction)
								+ alpha * delta * elegibilityTrace.getEntry(iAction, jState));

						// Updating the elegibility traces
						if (optimalAction == newActionSelected)
							elegibilityTrace.setEntry(iAction, jState,
									gamma * lambda * elegibilityTrace.getEntry(iAction, jState));
						else
							elegibilityTrace.setEntry(iAction, jState, 0);
					}
				}
			}

			// Update analysis record
			recordCurrentStateOfRLearner();

			// Set state to the new state.
			stateBefore = newState;

			// If a bandit problem, this sentence force the while to act like an if
			multiarmProblem = false;

			// Update the stepTime on this epoch
			timeStep++;
		}
	}

	/**
	 * (ACTION PREFERENCES) This is an special method learning for a reinforcement learning environemtn in which
	 * there is only one single state and many other actions. This should be used with
	 * a softmax function
	 */
	//	private void banditLearning() {
	//
	//		// Auxiliar parameters
	//		double new_Q;
	//		timeStep = 0;
	//
	//		if (episodeNumber == 0) {
	//			averageReward = 0;
	//		}
	//
	//		// A bandit problem is that which there is only a single state
	//		boolean multiarmProblem = thisWorld.getStates().length == 1 ? true : false;
	//
	//		while (!thisWorld.endState() || multiarmProblem) {
	//			// Execute the natural process of [state -> action-> (newState, reward)]
	//			reinforcementLearningBaseProcess();
	//
	//			// Probabilities of the actions
	//			double[] qValues = policy.getQValuesAt(stateBefore.getOrdinalState());
	//			double[] prob = actionProbabilities(qValues);
	//
	//			// Updating average reward
	//			if (baselineUsed) {
	//				averageReward = episodeNumber == 0 ? reward
	//						: ((averageReward * episodeNumber) + reward) / (episodeNumber + 1);
	//			} else {
	//				averageReward = 0;
	//			}
	//
	//			// Calculate the new Preferences (not value) for Q of the actions
	//			for (int i = 0; i < thisWorld.getActions().length; i++) {
	//				if (i == actionSelected) {
	//					// Calculate new Preference (not value) for Q for the action selected
	//					new_Q = policy.getQValue(stateBefore.getOrdinalState(), i)
	//							+ alpha * (reward - averageReward) * (1 - prob[i]);
	//					policy.setQValue(stateBefore.getOrdinalState(), i, new_Q);
	//				} else {
	//					// Calculate new Preference (not value) for Q for the action not selected
	//					new_Q = policy.getQValue(stateBefore.getOrdinalState(), i)
	//							- alpha * (reward - averageReward) * (prob[i]);
	//					policy.setQValue(stateBefore.getOrdinalState(), i, new_Q);
	//				}
	//			}
	//
	//			// Update analysis record
	//			recordCurrentStateOfRLearner();
	//
	//			// Set state to the new state.
	//			stateBefore = newState;
	//
	//			// If a bandit problem, this sentence force the while to act like an if
	//			multiarmProblem = false;
	//
	//			// Update the stepTime on this epoch
	//			timeStep++;
	//		}
	//	}

	/**
	 * Use the on-policy SARSA (State.Action.Reward.State.Action) learning method to update the q-values
	 */
	private void sarsaLearning() {
		int newActionSelected;
		double this_Q;
		double next_Q;
		double new_Q;

		// reset time step of the new episode
		timeStep = 0;

		// initial action
		actionSelected = selectAction(stateBefore);

		// A bandit problem is that which there is only a single state
		boolean multiarmProblem = thisWorld.getStates().length == 1 ? true : false;

		while (!thisWorld.endState() || multiarmProblem) {
			// State with the action selected
			newState = thisWorld.getNextState(actionSelected);//[0];

			// Reward obtained by action commited in the state
			reward = getWorldReward();

			// Action for the state
			newActionSelected = selectAction(newState);

			// Temporal-Difference part
			this_Q = policy.getQValue(stateBefore.getOrdinalState(), actionSelected);
			next_Q = policy.getQValue(newState.getOrdinalState(), newActionSelected);

			if (getLearningMethod() == LEARNING_METHOD.SARSA.ordinal()) {
				// Calculating estimates
				new_Q = this_Q + alpha * (reward + gamma * next_Q - this_Q); // multiply here for eligibility trace of the pair <state, action>

				// Setting estimates
				policy.setQValue(stateBefore.getOrdinalState(), actionSelected, new_Q);
			} else if (getLearningMethod() == LEARNING_METHOD.SARSA_LAMBDA.ordinal()) {
				// Calculating estimates
				double delta = (reward + gamma * next_Q - this_Q);

				// Updating elegibility trace
				elegibilityTrace.setEntry(actionSelected, stateBefore.getOrdinalState(),
						(1 - alpha) * elegibilityTrace.getEntry(actionSelected, stateBefore.getOrdinalState()) + 1);

				// Updating elegibility traces based on elegibility traces
				for (int iAction = 0; iAction < thisWorld.getActions().length; iAction++) {
					for (int jState = 0; jState < thisWorld.getStates().length; jState++) {
						// Updating the qValues using elegibility trace values
						policy.setQValue(jState, iAction, policy.getQValue(jState, iAction)
								+ alpha * delta * elegibilityTrace.getEntry(iAction, jState));

						// Updating the elegibility traces
						elegibilityTrace.setEntry(iAction, jState,
								gamma * lambda * elegibilityTrace.getEntry(iAction, jState));
					}
				}
			} else {
				throw new IllegalArgumentException("The learning method you are executing is not yet implemented here");
			}

			// Update analysis record
			recordCurrentStateOfRLearner();

			// Set state to the new state and action to the new action.
			stateBefore = newState;
			actionSelected = newActionSelected;

			// If a bandit problem, this sentence force the while to act like an if
			multiarmProblem = false;

			// Update the stepTime on this epoch
			timeStep++;
		}
	}

	// ============================
	// SELECTION METHODS
	// ============================
	public int greedySelection(State state) {
		int selectedAction = -1;
		// random = false;
		double[] qValues = policy.getQValuesAt(state.getOrdinalState());
		double maxQ = -Double.MAX_VALUE;
		int[] doubleValues = new int[qValues.length];
		int maxDV = 0;

		// Exploit
		for (int action = 0; action < qValues.length; action++) {
			if (thisWorld.validAction(action)) {
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
		}

		if (maxDV > 0) {
			//int randomIndex = (int) (random.nextDouble() * (maxDV + 1));
			int randomIndex = (int) (Math.random() * (maxDV + 1));

			selectedAction = doubleValues[randomIndex];
		}

		// Select random action if all qValues == 0 or exploring...
		if (selectedAction == -1) {
			//selectedAction = (int) (random.nextDouble() * qValues.length);
			selectedAction = (int) (Math.random() * qValues.length);
		}

		// Choose new action if not valid.
		while (!thisWorld.validAction(selectedAction)) {
			//selectedAction = (int) (random.nextDouble() * qValues.length);
			selectedAction = (int) (Math.random() * qValues.length);
		}

		return selectedAction;
	}

	/**
	 * Using the epsilon parameter we decided whether to act in a exploratory way or not depending on
	 * a random parameter
	 * 
	 * @param state
	 * @return
	 */
	private int eGreedySelection(State state) {
		int selectedAction = -1;
		// random = false;
		double[] qValues = policy.getQValuesAt(state.getOrdinalState());
		double maxQ = -Double.MAX_VALUE;
		int[] doubleValues = new int[qValues.length];
		int maxDV = 0;

		// Explore
		//if (random.nextDouble() < epsilon) {
		if (Math.random() < epsilon) {
			selectedAction = -1;
			// random = true;
		} else {
			// Exploit
			for (int action = 0; action < qValues.length; action++) {
				if (thisWorld.validAction(action)) {
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
			}

			if (maxDV > 0) {
				//int randomIndex = (int) (random.nextDouble() * (maxDV + 1));
				int randomIndex = (int) (Math.random() * (maxDV + 1));

				selectedAction = doubleValues[randomIndex];
			}
		}

		// Select random action if all qValues == 0 or exploring...
		if (selectedAction == -1) {
			//selectedAction = (int) (random.nextDouble() * qValues.length);
			selectedAction = (int) (Math.random() * qValues.length);
		}

		// Choose new action if not valid.
		while (!thisWorld.validAction(selectedAction)) {
			//selectedAction = (int) (random.nextDouble() * qValues.length);
			selectedAction = (int) (Math.random() * qValues.length);
		}

		return selectedAction;
	}

	/**
	 * Using the Upper-Confident-Bound action selection formula we select the best action
	 * 
	 * @param state2
	 * @return
	 */
	private int ucbSelection(State state) {
		// TODO Auto-generated method stub
		int selectedAction = 0;
		// random = false;
		double[] qValues = policy.getQValuesAt(state.getOrdinalState());
		int ordinalState = state.getOrdinalState();
		double maxQ = -100000;//-Double.MAX_VALUE;
		int[] doubleValues = new int[qValues.length];

		int maxDV = 0;
		double qActionValue = 0;

		// Exploit with UCB
		for (int action = 0; action < qValues.length; action++) {
			// Only taking into account actions that are legals
			if (thisWorld.validAction(action)) {
				// Getting action value using UCB formula			
				qActionValue = timesSelected[action][ordinalState] == 0 ? Double.MAX_VALUE
						: qValues[action] + cExploratoryParameter
								* (Math.sqrt((Math.log(timeStep) / timesSelected[action][ordinalState])));

				// Checking max Q value
				if (qActionValue > maxQ) {
					selectedAction = action;
					maxQ = qActionValue;
					maxDV = 0;
					doubleValues[maxDV] = selectedAction;
				} else if (qActionValue == maxQ) {
					maxDV++;
					doubleValues[maxDV] = action;
				}
			}
		}

		if (maxDV > 0) {
			int randomIndex = (int) (Math.random() * (maxDV + 1));
			selectedAction = doubleValues[randomIndex];
		}

		timesSelected[selectedAction][ordinalState]++;

		return selectedAction;
	}

	/**
	 * Gradient selection, this selection is only possible if there is only one single state
	 * @return
	 */
	private int softMaxSelection(State state) {
		int selectedAction = 0;
		double[] qValues = policy.getQValuesAt(state.getOrdinalState());
		double[] prob = actionProbabilities(qValues);

		// Once we know the probabilities of each action, we choose one
		int action;
		boolean valid = false;
		double rndValue;

		double offset; // = previous cumulative probability

		while (!valid) {
			rndValue = Math.random();
			offset = 0;

			for (action = 0; action < qValues.length; action++) {
				// Roulette wheel 
				if (rndValue > offset && rndValue < offset + prob[action]) {
					selectedAction = action;
				}

				// Cumulative Probability Distribution (CDF)
				offset += prob[action];
			}

			// The last index should be always 1.0 or close to it
			System.out.println("Last index value; " + offset);

			if (thisWorld.validAction(selectedAction))
				valid = true;
		}

		return selectedAction;

	}

	// ============================
	// AUXILIAR METHODS
	// ============================
	/**
	 * Given the q-values for an state this method returns the probabilities of selected
	 * each action using the soft-max function.
	 *  
	 * @param qValues
	 * @return
	 */
	private double[] actionProbabilities(double[] qValues) {
		int action;
		double prob[] = new double[qValues.length];
		double sumProb = 0;

		// Calculating the denominator of the formulae
		for (action = 0; action < qValues.length; action++) {
			// Numerator
			prob[action] = Math.exp(qValues[action] / softmaxTemperature);

			// Denominator
			sumProb += prob[action];
		}

		// Calculating probability of taking each action
		for (action = 0; action < qValues.length; action++)
			prob[action] = prob[action] / sumProb;

		return prob;
	}

	/**
	 * AK: let us clear the policy
	 * @return
	 */
	public RLPolicy newPolicy() {
		// Create the policy again
		policy = new RLPolicy(thisWorld.getStates(), thisWorld.getActions());

		// Initializing the policy with the initial values defined by the world.
		policy.initValues(thisWorld.getInitValues());

		// Reseting the world
		thisWorld.resetState();

		// Resetting the eligibility traces
		resetElegibilityTrace();

		// Initializing the epsilon value
		epsilon = epsilonRange[0];

		return policy;
	}

	/**
	 * Get the RMSE from the current policy compared with the true values stored in the world object
	 * @return
	 */
	public double getRootMeanSquareErrorFromCurrentPolicy() {
		RLPolicy optimalPolicy = thisWorld.getOptimalPolicy();
		RLPolicy estimatePolicy = policy;
		double[] differencesBetweenEstimatesAndRealValues = new double[policy.numRows * policy.numCols];

		// Lets normalize the estimate policy so we can compare it with the optimal policy that is also normalized

		// Getting the max value and the minimun value
		//		double max = Double.MIN_VALUE;
		//		double min = Double.MAX_VALUE;
		//		for (int row = 0; row < matrixOfEstimatePolicy.getRowDimension(); row++) {
		//			for (int column = 0; column < matrixOfEstimatePolicy.getColumnDimension(); column++) {
		//				max = Double.max(max, matrixOfEstimatePolicy.getEntry(row, column));
		//				min = Double.min(min, matrixOfEstimatePolicy.getEntry(row, column));
		//			}
		//		}
		//		matrixOfEstimatePolicy.scalarMultiply(1 / max);

		// It is only possible to check the RMS error if we know which is the optimal policy
		if (thisWorld.getOptimalPolicy() != null) {
			// Calculating the differences between the estimates and the real values of the policy
			for (int iAction = 0; iAction < estimatePolicy.numRows; iAction++) {
				for (int jState = 0; jState < estimatePolicy.numCols; jState++) {
					// Index of the difference in the array
					int index = iAction * (estimatePolicy.numCols) + jState;

					// Calculating the difference
					differencesBetweenEstimatesAndRealValues[index] = estimatePolicy.getQValue(jState, iAction)
							- optimalPolicy.getQValue(jState, iAction);
				}
			}

			// Calculating the Root Mean Square Error (RMSE)
			return Helper.round(Helper.rmse(differencesBetweenEstimatesAndRealValues), 5);
		} else {
			return -1;
		}
	}

	private RealMatrix resetElegibilityTrace() {
		elegibilityTrace = MatrixUtils.createRealMatrix(thisWorld.getActions().length, thisWorld.getStates().length);
		return elegibilityTrace;
	}

	// ============================
	// TO STRING
	// ============================

	// ============================
	// GETTERS & SETTERS
	// ============================
	public RLPolicy getPolicy() {
		return policy;
	}

	@Override
	public String toString() {
		return "RLearner [simulationName=" + simulationName + ", thisWorld=" + thisWorld + ", \npolicy=" + policy
				+ ", \nuseStateParallelization=" + useStateParallelization + ", learningMethod=" + learningMethod
				+ ", actionSelectionMethod=" + actionSelectionMethod + ", totalTasks=" + totalTasks + ", totalEpisodes="
				+ totalEpisodes + ", \nalpha=" + alpha + ", gamma=" + gamma + ", lambda=" + lambda
				+ ", elegibilityTrace=" + elegibilityTrace + ", \nepsilon=" + epsilon + ", epsilonRange="
				+ Arrays.toString(epsilonRange) + ", cExploratoryParameter=" + cExploratoryParameter
				+ ", timesSelected=" + Arrays.toString(timesSelected) + ", temperature=" + softmaxTemperature
				+ ", averageReward=" + averageReward + ", baselineUsed=" + baselineUsed + ", stateBefore=" + stateBefore
				+ ", newState=" + newState + ", actionSelected=" + actionSelected + ", reward=" + reward
				+ ", taskNumber=" + taskNumber + ", episodeNumber=" + episodeNumber + ", timeStep=" + timeStep + "]";
	}

	public void setPolicy(RLPolicy policy) {
		this.policy = policy;
	}

	public void setAlpha(double a) {
		if (a >= 0 && a <= 1)
			alpha = a;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public double getGamma() {
		return gamma;
	}

	public void setEpsilon(double e) {
		if (e >= 0 && e <= 1) {
			epsilon = e;

			// e_greedy_temporally
			epsilonRange = new double[] { epsilon, 0 };
		} else
			throw new IllegalArgumentException("Epsilon value in e-greedy selection method must be among 0 and 1");
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setTotalEpisodes(int e) {
		if (e > 0)
			totalEpisodes = e;
	}

	public int getTotalEpisodes() {
		return totalEpisodes;
	}

	public void setActionSelectionMethod(String as) {
		for (int i = 0; i < SELECTION_METHOD.values().length; i++) {
			if (SELECTION_METHOD.values()[i].name().equals(as)) {
				System.out.println("SELECTION METHOD SELECTED: " + SELECTION_METHOD.values()[i]);
				setActionSelectionMethod(SELECTION_METHOD.values()[i]);

				break;
			}
		}
	}

	public void setActionSelectionMethod(SELECTION_METHOD as) {
		switch (as) {
		case E_GREEDY_CHANGING_TEMPORALLY:
			actionSelectionMethod = SELECTION_METHOD.E_GREEDY_CHANGING_TEMPORALLY.ordinal();
			break;
		case E_GREEDY:
			actionSelectionMethod = SELECTION_METHOD.E_GREEDY.ordinal();
			epsilonRange = new double[] { epsilon, epsilon };
			break;
		case UCB:
			actionSelectionMethod = SELECTION_METHOD.UCB.ordinal();
			break;
		case SOFT_MAX:
			actionSelectionMethod = SELECTION_METHOD.SOFT_MAX.ordinal();
			break;
		default:
			throw new IllegalArgumentException("No existe el metodo de selección seleccionado");
		}
	}

	public int getActionSelectionMethod() {
		return actionSelectionMethod;
	}

	public void setLearningMethod(String lm) {
		for (int i = 0; i < LEARNING_METHOD.values().length; i++) {
			if (LEARNING_METHOD.values()[i].name().equals(lm)) {
				System.out.println("LEARNING METHOD SELECTED: " + LEARNING_METHOD.values()[i]);
				setLearningMethod(LEARNING_METHOD.values()[i]);

				break;
			}
		}
	}

	public void setLearningMethod(LEARNING_METHOD lm) {
		switch (lm) {
		case Q_LEARNING: {
			learningMethod = LEARNING_METHOD.Q_LEARNING.ordinal();
			break;
		}
		case Q_LEARNING_LAMBDA: {
			learningMethod = LEARNING_METHOD.Q_LEARNING_LAMBDA.ordinal();
			break;
		}
			//		case GRADIENT_BANDIT: {
			//			learningMethod = LEARNING_METHOD.GRADIENT_BANDIT.ordinal();
			//			setActionSelectionMethod(SELECTION_METHOD.SOFT_MAX);
			//			break;
			//		}
		case SARSA: {
			learningMethod = LEARNING_METHOD.SARSA.ordinal();
			break;
		}
		case SARSA_LAMBDA: {
			learningMethod = LEARNING_METHOD.SARSA_LAMBDA.ordinal();
			break;
		}
		default: {
			//learningMethod = LEARNING_METHOD.Q_LEARNING.ordinal();
			throw new IllegalArgumentException("No existe el metodo de aprendizaje seleccionado");
		}
		}
	}

	public int getLearningMethod() {
		return learningMethod;
	}

	public State getStateBefore() {
		return stateBefore;
	}

	public int getAction() {
		return actionSelected;
	}

	public double getReward() {
		return reward;
	}

	public int getTimeStep() {
		return timeStep;
	}

	public int getEpisodeNumber() {
		return episodeNumber;
	}

	public RLWorld getThisWorld() {
		return thisWorld;
	}

	public String getSimulationName() {
		return simulationName;
	}

	public int getTotalTasks() {
		return totalTasks;
	}

	public void setTasks(int tasks) {
		this.totalTasks = tasks;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public void setEpsilonRange(double initialValue, double finalValue) {
		epsilonRange = new double[] { initialValue, finalValue };
		epsilon = initialValue;
	}

	public double getC() {
		return cExploratoryParameter;
	}

	public void setC(double c) {
		this.cExploratoryParameter = c;
	}

	public double getTemperature() {
		return softmaxTemperature;
	}

	public void setTemperature(double temperature) {
		this.softmaxTemperature = temperature;
	}

	public double getSoftmaxTemperature() {
		return softmaxTemperature;
	}

	public boolean isBaselineUsed() {
		return baselineUsed;
	}

	public void setBaselineUsed(boolean baselineUsed) {
		this.baselineUsed = baselineUsed;
	}

	public double[] getEpsilonRange() {
		return epsilonRange;
	}

	public void setEpsilonRange(double[] epsilonRange) {
		this.epsilonRange = epsilonRange;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public State getNewState() {
		return newState;
	}

	public void setNewState(State newstate) {
		this.newState = newstate;
	}

	public void setEpisodeNumber(int episodeNumber) {
		this.episodeNumber = episodeNumber;
	}

	public String[] getLearningMethods() {
		LEARNING_METHOD[] aux = LEARNING_METHOD.values();
		String[] result = new String[aux.length];

		for (int i = 0; i < aux.length; i++) {
			result[i] = aux[i].name();
		}

		return result;
	}

	public String[] getSelectionMethods() {
		SELECTION_METHOD[] aux = SELECTION_METHOD.values();
		String[] result = new String[aux.length];

		for (int i = 0; i < aux.length; i++) {
			result[i] = aux[i].name();
		}

		return result;
	}

	public State[] getNextStates(int numberOfStates, State newState) {
		if (newState != null) {
			State[] nextStates = new State[numberOfStates];

			// Back up of the real state
			State backupNewState = new State(newState);
			State auxState = new State(newState);

			// Next states
			for (int i = 0; i < nextStates.length; i++) {
				// Next state
				nextStates[i] = this.getThisWorld().getNextState(this.greedySelection(auxState));

				// Set up next state
				auxState = nextStates[i];
			}

			// Backing up to real state
			// Getting the next state for the temporal current state
			this.getThisWorld().setWorldState(backupNewState);

			return nextStates;
		} else {
			return null;
		}
	}

	public double[] getNextGreedyAgentPositions(int numberOfStates, State newState) {
		if (newState != null) {
			double[] nextStates = new double[numberOfStates * 2];

			// Back up of the real state
			State backupNewState = new State(newState);
			State auxState = new State(newState);

			// Next states
			for (int i = 0; i < nextStates.length; i = i + 2) {
				// Next state
				State aux = this.getThisWorld().getNextState(this.greedySelection(auxState));
				nextStates[i] = aux.agentPositionX();
				nextStates[i + 1] = aux.agentPositionY();

				// Set up next state
				auxState = aux;
			}

			// Backing up to real state
			// Getting the next state for the temporal current state
			this.getThisWorld().setWorldState(backupNewState);

			return nextStates;
		} else {
			return null;
		}
	}

}
