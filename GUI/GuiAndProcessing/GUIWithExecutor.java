package GuiAndProcessing;

import Auxiliar.GameMap;
import RL.RLWorld;
import RL.RLearner;
import RL.RLearner.LEARNING_METHOD;
import RL.RLearner.SELECTION_METHOD;
import RL.State;
import WorldGameComplex.GameWorldSimpleMap;
import processing.core.PApplet;

public class GUIWithExecutor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean usarGUI = true;

		// Experiment to be used as resource of knowledge
		//		int numberOfExperiment = 0;
		//		String nameOfExperiment = "/GameWorldSimpleMap_SARSA_UCB_t100.0_ep150.0_lRate1.0_dFactor0.0_cUCB0.01_6x6";
		//		String urlDelExperimento = Output.simulationPathExperimentosGameWorldWithMap[numberOfExperiment];

		// Size of the map
		//		GameMap.dimX = Integer.parseInt(urlDelExperimento.substring(urlDelExperimento.lastIndexOf("_") + 1,
		//				urlDelExperimento.lastIndexOf("x")));
		//		GameMap.dimY = Integer.parseInt(
		//				urlDelExperimento.substring(urlDelExperimento.lastIndexOf("x") + 1, urlDelExperimento.length() - 0));
		GameMap.usePersonalizedMaps = false;

		// World of this map
		RLWorld world = new GameWorldSimpleMap(null, null, new GameMap());

		// Experiment
		int sizeMap = 8;
		int numberOfEpisodes = 10;
		double initialPolicyValues = 0.0;
		double alpha = 1.0;
		double gamma = 0.98;
		double epsilonInicial = 0.05;
		double epsilonFinal = 0.05;
		double cExploratory = 0.01;

		// The learner we are going to execute the game
		RLearner learner = null;
		RLearnerExecutor rlExecutor = new RLearnerExecutor(sizeMap, numberOfEpisodes, initialPolicyValues, alpha, gamma,
				epsilonInicial, epsilonFinal, LEARNING_METHOD.Q_LEARNING.name(),
				SELECTION_METHOD.MONTE_CARLO_TREE_SEARCH.name());

		rlExecutor.executeLearner();

		learner = rlExecutor.getLearner();

		// Set up GUI
		TankWorld gui = null;
		if (usarGUI) {
			gui = new TankWorld(world.getGameMap());
			String[] a = { "MAIN" };
			PApplet.runSketch(a, gui);
		}

		// Initial state
		State newState = learner.getThisWorld().resetState();
		learner.setNewState(newState);

		long delay = 100;
		//		int step = 0;

		// Updating the GUI
		updateGUI(usarGUI, learner, gui);

		// Start the game
		while (!learner.getThisWorld().endState()) {
			// Console
			//			System.out.println(newState);
			//			System.out.println(step++);

			// The new state in which we carry out an action
			newState = learner.getThisWorld().getNextState(learner.selectAction(newState));
			//			newState = learner.getThisWorld().getNextState(learner.greedySelection(newState));

			// Setting the sate
			learner.setNewState(newState);

			// Updating the GUI
			updateGUI(usarGUI, learner, gui);

			// Let the current screen being visualize for a while
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("interrupted.");
			}
		}

		// Updating the GUI
		updateGUI(usarGUI, learner, gui);

		// Console
		//		System.out.println(newState);
	}

	private static void updateGUI(boolean usarGUI, RLearner learner, TankWorld gui) {
		// Changes in the GUI
		if (usarGUI) {
			// Agent life
			double percentageLife = learner.getThisWorld().getAgentLife() * 100
					/ learner.getThisWorld().getAgentFullLife();
			gui.setAgentLife(percentageLife);

			// Enemy life
			percentageLife = learner.getThisWorld().getEnemyLife() * 100 / learner.getThisWorld().getEnemyFullLife();
			gui.setEnemyLife(percentageLife);

			// Agent position
			//			System.out.println(learner.getThisWorld().getAgentPosition());
			gui.setAgentPosition(learner.getThisWorld().getAgentPosition());

			// Map state
			gui.setMapState(learner.getThisWorld().getGameMap().getMap());

			// Current state
			gui.setCurrentState(learner.getThisWorld().getCurrentState());

			// Greedy next states
			gui.setNextStates(learner.getNextStates(3, learner.getNewState()));
		}
	}

}
