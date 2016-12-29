package MCTS;

import RL.RLearner;
import RL.RLearner.LEARNING_METHOD;
import RL.RLearner.SELECTION_METHOD;
import WorldGameComplex.GameWorldSimpleMap.ACTIONS;

public class mainMCTS {

	public static void main(String[] args) {
		RLearner learner = null;

		// With learner
		int sizeMap = 3;
		int numberOfEpisodes = 100;
		double initialPolicyValues = 0.0;
		double alpha = 1.0;
		double gamma = 1.0;
		double epsilonInicial = 0.4;
		double epsilonFinal = 0.05;
		String selectionMethod = SELECTION_METHOD.MONTE_CARLO_TREE_SEARCH.name();
		String learningMethod = LEARNING_METHOD.Q_LEARNING.name();
		RLearnerExecutor rlExecutor = new RLearnerExecutor(sizeMap, numberOfEpisodes, initialPolicyValues, alpha, gamma,
				epsilonInicial, epsilonFinal, 0.01, true, learningMethod, selectionMethod);
		learner = rlExecutor.getLearner();

		// Without learner
		//		learner = null;
		MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(learner);
		int bestAction = mcts.runMonteCarloTreeSearch();//mcts.getTreeSearch().getRoot());
		System.out.println("Best action: " + bestAction + " (" + ACTIONS.values()[bestAction].name() + ")");
	}
}
