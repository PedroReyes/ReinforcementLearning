package ConnectionRJava;

import Auxiliar.GameMap;
import RL.RLWorld;
import RL.RLearner;
import RL.RLearner.LEARNING_METHOD;
import RL.RLearner.SELECTION_METHOD;
import WorldGameComplex.GameWorldSimpleMap;

public class RLearnerExecutor {

	int sizeMap;
	int numberOfEpisodes;
	RLWorld world;
	RLearner learner;

	public RLearnerExecutor(int sizeMap, int numberOfEpisodes, double initialPolicyValues, double alpha, double gamma,
			double epsilonInicial, double epsilonFinal, String learningMethod, String actionSelectionMethod) {
		// ================================
		// GENERAL PARAMETERS
		// ================================
		this.sizeMap = sizeMap;
		this.numberOfEpisodes = numberOfEpisodes;
		GameMap.usePersonalizedMaps = false;

		// ================================
		// THE WORLD
		// ================================
		GameMap.dimX = sizeMap;
		GameMap.dimY = sizeMap;
		world = new GameWorldSimpleMap(null, null, new GameMap());
		world.setInitValues(initialPolicyValues);

		// ================================
		// THE LEARNER
		// ================================
		learner = new RLearner("", world);
		learner.setAlpha(alpha);
		learner.setGamma(gamma);
		learner.setEpsilonRange(epsilonInicial, epsilonFinal);
		learner.setTotalEpisodes(numberOfEpisodes);
		learner.setTasks(1);
		learner.setC(0.01);
		RLearner.debugProcessOfEpisodes = false;

		// ================================
		// THE LEARNER METHOD
		// ================================
		learner.setLearningMethod(learningMethod);

		// ================================
		// THE SELECTION METHOD
		// ================================
		learner.setActionSelectionMethod(actionSelectionMethod);

	}

	/**
	 * 
	 */
	public void executeLearner() {
		learner.runTrial();

		// The new state in which we carry out an action
		//		newState = learner.getThisWorld().getNextState(learner.selectAction(newState));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//		System.out.println(getResults(2, 5));
		int sizeMap = 3;
		int numberOfEpisodes = 100;
		double initialPolicyValues = 0.0;
		double alpha = 1.0;
		double gamma = 1.0;
		double epsilonInicial = 0.4;
		double epsilonFinal = 0.05;
		RLearnerExecutor rlExecutor = new RLearnerExecutor(sizeMap, numberOfEpisodes, initialPolicyValues, alpha, gamma,
				epsilonInicial, epsilonFinal, LEARNING_METHOD.values()[0].name(), SELECTION_METHOD.values()[0].name());

		rlExecutor.executeLearner();

		//		System.out.println(rlExecutor.toString());
	}

	public RLearner getLearner() {
		return learner;
	}

	public void setLearner(RLearner learner) {
		this.learner = learner;
	}

	public String toString() {
		return this.sizeMap + "x" + this.sizeMap + " map. " //
				+ "\n Number of episodes: " + this.numberOfEpisodes //
				+ "\n Learner method:" //
				+ "\n Action selection method:" //
				+ "\n Alpha: " + this.learner.getAlpha() //
				+ "\n Gamma: " + this.learner.getGamma() //
				+ "\n Epsilon: (" + this.learner.getEpsilonRange()[0] + ", " + this.learner.getEpsilonRange()[1] + ")"
				+ "\n Selection method: " + SELECTION_METHOD.values()[this.learner.getActionSelectionMethod()] //
				+ "\n Learning method: " + LEARNING_METHOD.values()[this.learner.getLearningMethod()] //
				;
	}

}
