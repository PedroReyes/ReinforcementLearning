package RL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Auxiliar.Helper;

/**
 * Esta clase se usa para cargar, dada una URL de un fichero de simulación, los parámetros de un learner concreto que se haya ejecutado 
 * con anterioridad para posteriormente con dichos parámetros cargar la policy final que se obtiene al haber ejecutado dicha simulación.
 * 
 * Se presume que la url la cual se adjunta difiere solamente del fichero qeu gaurda la policy final en que el nombre del fichero comenzaría por
 * QValues. De este modo, si el fichero que cargamso se llama 
 * 
 * "GameWorldExclusiveStates_Q_LEARNING_E_GREEDY_t2_ep10_lRate1.0_dFactor0.0_eG0.01"
 * 
 * el fichero que contiene la policy final de esta ejecución sería:
 * 
 * "QValues_GameWorldExclusiveStates_Q_LEARNING_E_GREEDY_t2_ep10_lRate1.0_dFactor0.0_eG0.01"
 * 
 * @author pedro
 *
 *///RLearnerConfigurationLoader
public class RLearnerConfigurationLoader {

	enum SIMULATION_FILE_COLUMNS {
		sizeMap, possibleActions, possibleStates, initialQValues, learningMethod, selectionMethod, numberOfTasks, numberOfEpisodes, softmax_baselineUsed, softmax_temperature, UCB_c, eGreedy_epsilon_initial, eGreedy_epsilon_final, qLearning_alpha, qLearning_gamma, qLearning_lambda, taskNumber, episodeNumber, episodeStepTime, optimalAction, lastState, actionInLastState, newState, averageReward, RMS, percentageOfChosenOptimalActionsPerEpisode, percentageOfChosenActionsPerEpisode, victories,
	};

	private RLearner learner;

	public RLearnerConfigurationLoader(String pathToSimulation, RLWorld world) {
		// Cargamos en el learner los parametros de la configuracion de la simulacion
		loadConfigurationParametersInLearner(pathToSimulation, world);

		// Establecemos la politica almacenada para el fichero de simulacion
		// Configuration line
		BufferedReader br = null;

		String pathToSimulationPolicy = pathToSimulation.substring(0, pathToSimulation.lastIndexOf("/")) + "/"
				+ RLearner.prefixForNamingPolicyFile
				+ pathToSimulation.substring(pathToSimulation.lastIndexOf("/") + 1);
		//		System.out.println(pathToSimulationPolicy);
		try {
			System.out.println(pathToSimulationPolicy);
			br = new BufferedReader(new FileReader(pathToSimulationPolicy));
			br.readLine(); // cabecera de estados
			learner.newPolicy(); // reset de la politica de acciones
			System.out.println("=========a==========");
			for (int iAction = 0; iAction < learner.getThisWorld().getActions().length; iAction++) {
				String qValuesForAction = br.readLine();
				//				System.out.println(qValuesForAction);
				for (int iState = 0; iState < learner.getThisWorld().getStates().length; iState++) {
					learner.getPolicy().setQValue(iState, iAction,
							Helper.convertStringWithExponential(qValuesForAction.split(";")[iState + 1]));
				}
			}
			System.out.println("==========b=========");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		// Debug
		System.out.println(learner);
	}

	/**
	 * Carga como parametros del learner la primera linea del fichero de simulacion que se indique.
	 * @param pathToSimulation
	 * @param world
	 */
	private void loadConfigurationParametersInLearner(String pathToSimulation, RLWorld world) {
		// Configuration line
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(pathToSimulation));
			br.readLine(); // cabecera
			String firstLineOfData = br.readLine(); // configuracion

			// Sometimes is not the first line the one that store the configuration, maybe is empty
			if (firstLineOfData.trim().isEmpty()) {
				firstLineOfData = br.readLine();
			}

			String[] configuracion = firstLineOfData.split(";");

			// ================================
			// SETTING THE EXPERIMENT'S PARAMETERS
			// ================================
			learner = Helper
					.settingLearner(Integer.parseInt(configuracion[SIMULATION_FILE_COLUMNS.numberOfTasks.ordinal()]), // total tasks
							Integer.parseInt(configuracion[SIMULATION_FILE_COLUMNS.numberOfEpisodes.ordinal()]), // total episodes
							RLearner.LEARNING_METHOD.valueOf(
									configuracion[SIMULATION_FILE_COLUMNS.learningMethod.ordinal()]), // learning method
					RLearner.SELECTION_METHOD.valueOf(configuracion[SIMULATION_FILE_COLUMNS.selectionMethod.ordinal()]), // selection method
					Double.parseDouble(configuracion[SIMULATION_FILE_COLUMNS.qLearning_alpha.ordinal()]), // learning rate
					Double.parseDouble(configuracion[SIMULATION_FILE_COLUMNS.qLearning_gamma.ordinal()]), // discount factor
					Double.parseDouble(configuracion[SIMULATION_FILE_COLUMNS.qLearning_lambda.ordinal()]), // lambda steps
					Double.parseDouble(configuracion[SIMULATION_FILE_COLUMNS.eGreedy_epsilon_initial.ordinal()]), // initial epsilon
					Double.parseDouble(configuracion[SIMULATION_FILE_COLUMNS.UCB_c.ordinal()]), // exploratory parameter c in UCB selection
					Double.parseDouble(configuracion[SIMULATION_FILE_COLUMNS.softmax_temperature.ordinal()]), // temperature in softmax
					Boolean.parseBoolean(configuracion[SIMULATION_FILE_COLUMNS.softmax_temperature.ordinal()]), // baseline used
					"", // nombre parametro 1
					"", // nombre parametro 2
					world, // world
					null, // experimentParameterValues1
					0, // indice parametro 1
					null, // experimentParameterValues1
					0, // indice parametro 2
					""// nombre de la simulacion
			);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public RLearner getRLeaner() {
		return learner;
	}

}
