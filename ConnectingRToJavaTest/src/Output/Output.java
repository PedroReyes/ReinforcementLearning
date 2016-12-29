package Output;

import Analysis.RCodeGenerator;

public class Output {

	// Maximun decimals stored
	public static int maxDecimales = 5;

	// Oficial separator
	public static String separator = ";";

	// ===============================================================
	// ORGINAL PATH FOR ALL SAVED FILES
	// ===============================================================
	public static String originalPath = !RCodeGenerator.isOperativeSystemWindows()
			? "/Users/pedro/Google Drive/Desarrollo/BasicReinforcementLearningAlgorithm"
			: "C:/Users/papa/Dropbox/Desarrollo/BasicReinforcementLearningAlgorithm";

	// ===============================================================
	// PATH FOR CHARTING WITH R SCRIPTS
	// ===============================================================
	public static String rScriptForCharting = originalPath + "/src/Analysis/RLGraficar.R";
	public static String rScriptForSetUpRserve = originalPath + "/src/Analysis/SetUpRserve.R";

	// ===============================================================
	// TYPE OF CHARTS THAT MAY BE PLOTTED
	// ===============================================================
	public static enum typeOfCharts {
		xEpisode_yWinProbability, // Description: measure the probability of winning a game with the policy acquired in the current episode
		xEpisode_yOptimalAction, // Description: average of [percentage of optimal actions chosen in an episode] of each episode per task.
		xEpisode_yPercentageOfActions, // Description: histogram of the average of the [percentage of each action chosen over an episode] of each episode per task.
		xEpisode_yAverageReward, // Description: average of [average reward of each step in an episode] of each episode per task.
		xEpisode_yAverageSteps, // Description: average of all the tasks of steps needed in an episode
		xEpisode_yAverageStepsWhenWin, // Description: average of all the tasks of steps needed in an lost episode
		xEpisode_yAverageStepsWhenLoose, // Description: average of all the tasks of steps needed in an win episode
		histogramOfQvalues, // Description: histogram of the QValues obtained in the last episode of the last task
		policyPerformance // Description: 
	};

	// ========================================================================
	// EXPERIMENTS PATH AND DESCRIPTIONS OF THE GAME WORLD WITH AN EXPLICIT MAP
	// ========================================================================
	public static String[] simulationPathExperimentosGameWorldWithMap = new String[] {
			originalPath + "/Experimentos/GameProblemWithMap/qLearningNewRewardFunction_3x3" // 0
			, originalPath + "/Experimentos/GameProblemWithMap/1_Qlearning_eGreedyMap_8x8" // 1
			, originalPath + "/Experimentos/GameProblemWithMap/2_SARSA_eGreedyMap_3x3" // 2
			, originalPath + "/Experimentos/GameProblemWithMap/3_Qlearning_eGreedyMap_6x6" // 3
			, originalPath + "/Experimentos/GameProblemWithMap/4_SARSA_eGreedyMap_6x6" // 4
			, originalPath + "/Experimentos/GameProblemWithMap/5_Qlearning_eGreedyMap_12x12" // 5
			, originalPath + "/Experimentos/GameProblemWithMap/6_SARSA_eGreedyMap_12x12" // 6
			, originalPath + "/Experimentos/GameProblemWithMap/7_Qlearning_eGreedyMap_24x24" // 7
			, originalPath + "/Experimentos/GameProblemWithMap/8_SARSA_eGreedyMap_24x24" // 8
	};

	public static String[] simulationDescriptionExperimentosGameWorldWithMap = new String[] {
			// Experimento 0
			"Ejemplo con unas pocas iteraciones para probar que el programa en R funciona\n"
					+ "y tambien para poder ver si todo se ejecuta con normalidad.",

			// 1_Best parameters for Q-learning and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando Q-learning y eGreedy con una mapa de 3x3",

			// 2_Best parameters for SARSA and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando SARSA y eGreedy con una mapa de 3x3",

			// 3_Best parameters for Q-learning and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando Q-learning y eGreedy con una mapa de 6x6",

			// 4_Best parameters for SARSA and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando SARSA y eGreedy con una mapa de 6x6",

			// 5_Best parameters for Q-learning and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando Q-learning y eGreedy con una mapa de 12x12",

			// 6_Best parameters for SARSA and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando SARSA y eGreedy con una mapa de 12x12",

			// 7_Best parameters for Q-learning and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando Q-learning y eGreedy con una mapa de 24x24",

			// 8_Best parameters for SARSA and eGreedy
			"En este experiment se buscan los mejores valores de los parámetros para el problema usando SARSA y eGreedy con una mapa de 24x24" };

	// ===============================================================
	// EXPERIMENTS PATH AND DESCRIPTIONS OF THE GAME WORLD
	// ===============================================================
	public static String[] simulationPathExperimentosGameWorld = new String[] {
			originalPath + "/Experimentos/GameProblem/0_Experimento0" // 0
			, originalPath + "/Experimentos/GameProblem/1_ExclusiveStates" // 1
	};

	public static String[] simulationDescriptionExperimentosGameWorld = new String[] {
			// Experimento 0
			"Ejemplo con unas pocas iteraciones para probar que el programa en R funciona\n"
					+ "y tambien para poder ver si todo se ejecuta con normalidad.",

			// ExclusiveStates
			"Los estados primitivos son exclusivos entre sí. Si tenemos n estados primitivos, estos n estados no se combinan entre ellos. "
					+ "La unica excepción en este caso es la vida del agente cuya vida puede ser alta o baja y estos dos estados sí son "
					+ "combinados con los n estados primitivos." };

	// ===============================================================
	// EXPERIMENTS PATH AND DESCRIPTIONS OF THE RANDOM WALK WORLD
	// ===============================================================
	public static String[] simulationPathExperimentosRandomWalkWorld = new String[] {
			originalPath + "/Experimentos/RandomWalkProblem/0_Experimento0" // 0
			, originalPath + "/Experimentos/RandomWalkProblem/1_ExperimentoTraces" // 1
	};

	public static String[] simulationDescriptionExperimentosRandomWalkWorld = new String[] {
			// Experimento 0
			"Ejemplo con unas pocas iteraciones para probar que el programa en R funciona\n"
					+ "y tambien para poder ver si todo se ejecuta con normalidad.",
			// Traces
			"Libro de Sutton y Barto: trazas de elegibilidad." };

	// ===============================================================
	// EXPERIMENTS PATH AND DESCRIPTIONS OF THE MULTIARM BANDIT WORLD
	// ===============================================================
	public static String[] simulationPathExperimentosMultiarmBanditWorld = new String[] {
			originalPath + "/Experimentos/MultiArmBanditProblem/0_Experimento0" // 0
			, originalPath + "/Experimentos/MultiArmBanditProblem/1_ExpEgreedy" // 1
			, originalPath + "/Experimentos/MultiArmBanditProblem/2_ExpOptimisticValues" // 2
			, originalPath + "/Experimentos/MultiArmBanditProblem/3_ExpUCB" // 3
			, originalPath + "/Experimentos/MultiArmBanditProblem/4_ExpGradientBandit" // 4
			, originalPath + "/Experimentos/MultiArmBanditProblem/5_ExpSarsa" // 5
			, originalPath + "/Experimentos/MultiArmBanditProblem/6_ExpSarsa_QLearning" // 6
	};

	public static String[] simulationDescriptionExperimentosMultiarmBanditWorld = new String[] {
			// Experimento 0
			"Ejemplo con unas pocas iteraciones para probar que el programa en R funciona\n"
					+ "y tambien para poder ver si todo se ejecuta con normalidad.",

			// e-Greedy
			"Libro de Sutton y Barto: en este experimento se intenta obtener las mismas graficas"
					+ " que en el capítulo Multi-arm Bandits->action-value methods para la selección greedy y la selección e-greedy.",

			// Optimistic values
			"Libro de Sutton y Barto: se intenta realizar el experimento de valores optimistas en el que se usa greedy con valores optimistas y"
					+ " e-greedy sin valores optimista dando mejores resultados greedy con valores optimistas.",

			// UCB
			"Libro de Sutton y Barto: se ha implementado el metodo Upper-Confident-Bound action selection y se va a comparar con el metodo e-greedy",

			// GradientBandit
			"Libro de Sutton y Barto: se ha implementado el metodo Gradient Bandits en el capitulo 2.7 el cual solo sirve si tenemos un "
					+ "solo estado en nuestro entorno. \n Este metodo hace uso de preferencias en lugar de q-values para el aprendizaje "
					+ "y probabilidades en luagr de estimaciones para la seleccion.",

			// Sarsa
			"Libro de Sutton y Barto: se ha implementado el metodo SARSA en el capitulo 6.4.",

			// Sarsa y QLearning (comparacion)
			"Libro de Sutton y Barto: se ha implementado el metodo SARSA en el capitulo 6.4." };
}
