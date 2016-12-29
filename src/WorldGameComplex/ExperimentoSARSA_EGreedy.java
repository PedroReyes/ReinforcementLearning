package WorldGameComplex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.Rserve.RConnection;

import Analysis.RCodeGenerator;
import Analysis.RLGraficar;
import Auxiliar.GameMap;
import Auxiliar.Helper;
import Auxiliar.Parametrizacion;
import Output.Output;
import RL.RLWorld;
import RL.RLearner;
import RL.RLearner.LEARNING_METHOD;
import RL.RLearner.SELECTION_METHOD;

public class ExperimentoSARSA_EGreedy {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ELAPSED TIME
		long tStart = System.nanoTime();

		// SHOWING TIME WHEN THE EXECUTION STARTED
		SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		String current_time_str = time_formatter.format(System.currentTimeMillis());
		System.out.println(current_time_str);

		// Experimento
		int numeroDeExperimento = 0;
		String urlDelExperimento = Output.simulationPathExperimentosGameWorldWithMap[numeroDeExperimento];
		String descriptionDelExperimento = Output.simulationDescriptionExperimentosGameWorldWithMap[numeroDeExperimento];

		// This lines are just for getting the size of the map out of the name of the experiment
		System.out.println(urlDelExperimento);
		GameMap.dimX = Integer.parseInt(urlDelExperimento.substring(urlDelExperimento.lastIndexOf("_") + 1,
				urlDelExperimento.lastIndexOf("x")));
		GameMap.dimY = Integer.parseInt(
				urlDelExperimento.substring(urlDelExperimento.lastIndexOf("x") + 1, urlDelExperimento.length() - 0));
		GameMap.usePersonalizedMaps = true;
		RL.RLearner.simulationPath = urlDelExperimento;
		RL.RLearner.simulationDescription = descriptionDelExperimento;
		boolean takeOnlyIntoAccountInRewardCalculationsLastRewardOfTheEpisode = true;

		System.out.println(new GameMap());

		// =======================================
		// CONTANTS OF THE EXPERIMENT (PARAMETERS)
		// =======================================
		// Ejecutar el experimento y crear las graficas
		boolean ejecutarExperimento = true;
		boolean crearGraficasInternas = false; // el learner crea internamente ciertas graficas como las de la policy
		boolean crearGrafica = false;
		boolean existOptimalPolicy = false;

		// Global parameters to any reinforcement learning experiment
		final double[] defaultLearningParameterValues = new double[RLearner.LEARNING_PARAMETER.values().length];
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalTasks.ordinal()] = 100; // total de tareas
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalEpisodes.ordinal()] = 150; // total de episodias por cada atrea

		// Learning and selection method
		LEARNING_METHOD learningMethod = LEARNING_METHOD.SARSA;
		SELECTION_METHOD selectionMethod = SELECTION_METHOD.SOFT_MAX;//_CHANGING_TEMPORALLY;

		// Learning method parameters
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.alphaLearningRate.ordinal()] = 1; // if selected Q_LEARNING, SARSA, TD(LAMBDA) methods
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.gammaDiscountFactor.ordinal()] = 1; // if selected Q_LEARNING, SARSA, TD(LAMBDA) methods
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.lambdaSteps.ordinal()] = 0.8; // if selected Q_LEARNING, SARSA, TD(LAMBDA) methods

		// Selected method parameters
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.epsilonSelection.ordinal()] = 0.1; // if selected eGreedySelection
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.c_UCBSelection.ordinal()] = 0.5; // if selected UCB_selection
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.ordinal()] = 0.2; // if selected soft-max selection
		defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.ordinal()] = 1; // if selected soft-max selection
		double initialPolicyValues = 0; // Q values start with this value
		double epsilonFinal = 0.05; // just in case of temporal epsilon
		final double[] epsilonRange = new double[] {
				defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.epsilonSelection.ordinal()], 0 }; // if selected eGreedySelection temporal

		// ================================
		// PARAMETER THAT IS UNDER ANALISYS
		// ================================
		Map<String, Parametrizacion> parametrizaciones = new HashMap<>();

		// PARAMETRO 1
		String nombreParametro1 = RLearner.LEARNING_PARAMETER.alphaLearningRate.name();
		int divisionesParametro1 = 1;//4;
		double[] learningRateValues = new double[divisionesParametro1];
		if (true) { // while a factor approaching 1 will make it strive for a long-term high reward.
			learningRateValues[0] = 1.0;
		}
		parametrizaciones.put(nombreParametro1, new Parametrizacion(learningRateValues, nombreParametro1));

		System.out.println("Change 4");

		// PARAMETRO 2 (THERE MAY NOT BE A SECOND PARAMETER TO EVALUATE)
		String nombreParametro2 = RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name();
		int divisionesParametro2 = 1;//6;
		double[] discountFactorValues = new double[divisionesParametro2];
		if (true) { // (=0 then we have a 1-step TD backup, = 1 then we have a Monte Carlo backup)
			discountFactorValues[0] = 0.6;
			//			discountFactorValues[1] = 0.6;
			//			discountFactorValues[2] = 0.8;
			//			discountFactorValues[3] = 0.4;
			//			discountFactorValues[4] = 0.9;
			//			discountFactorValues[5] = 0.9;
		}
		parametrizaciones.put(nombreParametro2, new Parametrizacion(discountFactorValues, nombreParametro2));

		// PARAMETRO 3 (THERE MAY NOT BE A SECOND PARAMETER TO EVALUATE)
		String nombreParametro3 = RLearner.LEARNING_PARAMETER.epsilonSelection.name();
		int divisionesParametro3 = 1;//6;
		double[] epsilonValues = new double[divisionesParametro3];
		if (true) { // High temperatures cause the actions to be all (nearly) equiprobable.
			epsilonValues[0] = 0.4;
		}
		parametrizaciones.put(nombreParametro3, new Parametrizacion(epsilonValues, nombreParametro3));

		// PARAMETRO 4 (THERE MAY NOT BE A SECOND PARAMETER TO EVALUATE)
		String nombreParametro4 = RLearner.LEARNING_PARAMETER.c_UCBSelection.name();
		int divisionesParametro4 = 1;//6;
		double[] cValue = new double[divisionesParametro4];
		if (true) { // High temperatures cause the actions to be all (nearly) equiprobable.
			cValue[0] = 0.01;
		}
		parametrizaciones.put(nombreParametro4, new Parametrizacion(cValue, nombreParametro4));

		// PARAMETRO 5 (THERE MAY NOT BE A SECOND PARAMETER TO EVALUATE)
		String nombreParametro5 = RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name();
		int divisionesParametro5 = 1;//6;
		double[] softmaxValues = new double[divisionesParametro5];
		if (true) { // High temperatures cause the actions to be all (nearly) equiprobable.
			softmaxValues[0] = 0.2;
			//			softmaxValues[1] = 0.2;
			//			softmaxValues[2] = 0.3;
			//			softmaxValues[3] = 0.4;
		}
		parametrizaciones.put(nombreParametro5, new Parametrizacion(softmaxValues, nombreParametro5));

		// DEBUG DE COMBINACION DE PARAMETROS
		System.out.println("PARAMETRIZACIONES del experimento " + numeroDeExperimento + " (ejecucón del experimento_"
				+ (ejecutarExperimento ? "YES" : "NO") + ", crear graficas_" + (crearGrafica ? "YES" : "NO") + "):");
		System.out.println("Size map: " + (new GameMap()).getDimX() + "x" + (new GameMap()).getDimY());
		System.out.println("[Tareas, Episodios] = " + "["
				+ (int) defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalTasks.ordinal()] + ", "
				+ (int) defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalEpisodes.ordinal()] + "]");

		int[] indexCombination = new int[parametrizaciones.size()];
		String[] nombreParametros = Arrays.copyOf(parametrizaciones.keySet().toArray(),
				parametrizaciones.keySet().toArray().length, String[].class);

		System.out.println("[" + String.join(",", nombreParametros) + "]");

		// ================================
		// EJECUCION DEL EXPERIMENTO
		// ================================
		if (ejecutarExperimento) {
			experimentsExecution(tStart, urlDelExperimento,
					takeOnlyIntoAccountInRewardCalculationsLastRewardOfTheEpisode, crearGraficasInternas,
					defaultLearningParameterValues, learningMethod, selectionMethod, parametrizaciones,
					nombreParametro1, divisionesParametro1, learningRateValues, nombreParametro2, divisionesParametro2,
					discountFactorValues, indexCombination, nombreParametros, epsilonFinal, initialPolicyValues);
		}

		// ================================
		// GRAFICAS DEL EXPERIMENTO
		// ================================
		if (crearGrafica) {
			plottingExecution(urlDelExperimento, existOptimalPolicy);
		}

	}

	// ========================================================================================================================
	// ========================================================================================================================
	// ========================================================================================================================
	// ========================================================================================================================
	// ========================================================================================================================

	private static void plottingExecution(String urlDelExperimento, boolean existOptimalPolicy) {
		System.out.println("======================");
		System.out.println("Creating the charts...");
		System.out.println("======================");
		boolean chooseFiles;
		RLGraficar graficar = new RLGraficar(chooseFiles = false);

		// Checking R connection with Rserve
		try {
			new RConnection();
		} catch (Exception e) {
			//			JOptionPane.showMessageDialog(null, "Sep up Rserve: goes to R class Analysis->SetUpRserve.R and run it",
			//					"Rserve not setup", JOptionPane.ERROR_MESSAGE);
		}

		// File that must not be taken into account
		String[] specificFileToNotPlot = new String[] { Output.typeOfCharts.policyPerformance.name() };

		// Por algun extrano motivo Windows no realiza una nueva conexion para cada conexion con Rserve por lo que
		// no se puedan usar Threads en windows para maximizar la creacion de graficas
		if (RCodeGenerator.isOperativeSystemWindows()) {
			System.out.println("1");
			graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yAverageReward, null, null);
			System.out.println("2");
			graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yOptimalAction, null, null);
			System.out.println("3");
			graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yPercentageOfActions, null, null);
		} else {
			// AVERAGE REWARD
			new Thread() {

				public void run() {
					graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yAverageReward, null,
							specificFileToNotPlot);
				}
			}.start();

			// AVERAGE STEPS NEEDED PER EPISODE
			new Thread() {
				public void run() {
					graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yAverageStepsWhenWin, null,
							specificFileToNotPlot);
				}
			}.start();

			// HISTOGRAM OF ACTIONS PERCENTAGE PER EACH LEARNER
			//for (Iterator<String> iterator = fileSimulationNames.iterator(); iterator.hasNext();) {
			String fileName = null;//(String) iterator.next();
			new Thread() {
				public void run() {
					graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yPercentageOfActions,
							fileName, specificFileToNotPlot);
				}
			}.start();
			//}

			// PERFORMANCE MEASURED IN TERMS OF VICTORIES
			new Thread() {
				public void run() {
					graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yWinProbability, null,
							specificFileToNotPlot);
				}
			}.start();

			if (existOptimalPolicy) {
				// OPTIMAL ACTION (we must know the optimal policy)
				new Thread() {
					public void run() {
						graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.xEpisode_yOptimalAction, null,
								specificFileToNotPlot);
					}
				}.start();

				// PERFORMANCE MEASURED IN RMS ERROR USING AN OPTIMAL POLICY (we must know the optimal policy)
				new Thread() {
					public void run() {
						graficar.crearGrafica(urlDelExperimento, Output.typeOfCharts.policyPerformance,
								Output.typeOfCharts.policyPerformance.name(), null);
					}
				}.start();
			}
		}
	}

	// ========================================================================================================================
	// ========================================================================================================================
	// ========================================================================================================================
	// ========================================================================================================================
	// ========================================================================================================================
	private static void experimentsExecution(long tStart, String urlDelExperimento,
			boolean takeOnlyIntoAccountInRewardCalculationsLastRewardOfTheEpisode, boolean crearGraficasInternas,
			final double[] defaultLearningParameterValues, LEARNING_METHOD learningMethod,
			SELECTION_METHOD selectionMethod, Map<String, Parametrizacion> parametrizaciones, String nombreParametro1,
			int divisionesParametro1, double[] gammaValues, String nombreParametro2, int divisionesParametro2,
			double[] lambdaValues, int[] indexCombination, String[] nombreParametros, double epsilonFinal,
			double initialPolicyValues) {
		// ================================
		// STATES AND ACTIONS (if nothing is set, the actions and states will be the ones by default in the world)
		// ================================
		//Actions.Action[] actions = Actions.generateActions();
		//State[] states = MultiArmBanditWorld.generateStates(actions);

		// ================================
		// PERFORMANCE MEASURES
		// ================================
		double[][] performanceMeasures = new double[divisionesParametro1][divisionesParametro2];

		// ================================
		// THE LEARNER
		// ================================
		// Name of the files to be extracted the plots from
		List<String> fileSimulationNames = new LinkedList<>();

		// Getting all the possible combinations
		int numberOfParameterCombinations = 1;
		for (Iterator<Parametrizacion> iterator = parametrizaciones.values().iterator(); iterator.hasNext();) {
			Parametrizacion parametrizacion = (Parametrizacion) iterator.next();
			numberOfParameterCombinations = numberOfParameterCombinations * parametrizacion.getValues().length;
		}

		System.out.println("Numero total de parametrizaciones: " + numberOfParameterCombinations);

		// Executing the algorithm for several parameter values
		Thread[] threadsParametrizaciones = new Thread[numberOfParameterCombinations]; // +1 = epsilon temporal, el codigo esta insertado manualmente abajo

		boolean ejecutarThreads = true;

		int indice = 0;

		int i = indexCombination.length - 1;

		for (int iterations = 0; iterations < numberOfParameterCombinations; iterations++) {
			// ================================
			// DEBUGGIN
			// ================================
			System.out.println(Arrays.toString(indexCombination));
			String experimento = "";
			for (int j = 0; j < indexCombination.length; j++) {
				experimento = experimento + (j == 0 ? "[" : ", ") + nombreParametros[j];
			}
			experimento = experimento + "] = ";

			//			for (int j = 0; j < indexCombination.length; j++) {
			//				System.out.print(indexCombination[j] + "_");
			//			}
			//			System.out.println();

			for (int j = 0; j < indexCombination.length; j++) {
				String nombreParametro = nombreParametros[j];
				int parameterCombination = indexCombination[j];
				//				System.out.println(nombreParametro);
				//				System.out.println(parametrizaciones.get(nombreParametro));
				//				System.out.println(parametrizaciones.get(nombreParametro).getValues()[parameterCombination]);
				experimento = experimento + (j == 0 ? "[" : ", ")
						+ parametrizaciones.get(nombreParametro).getValues()[parameterCombination];
			}
			experimento = experimento + "]";

			//			System.out.println(experimento);

			// ================================
			// SETTING PARAMETERS
			// ================================
			// Setting the parameters of the experiment
			for (int j = 0; j < indexCombination.length; j++) {
				experimento = experimento + (j == 0 ? "[" : ", ")
						+ parametrizaciones.get(nombreParametros[j]).getValues()[indexCombination[j]];
				int indiceParametro = indexCombination[j];
				String nombreParametro = nombreParametros[j];

				parametrizaciones.put(nombreParametro, new Parametrizacion(
						parametrizaciones.get(nombreParametro).getValues(), indiceParametro, nombreParametro));
			}

			// ================================
			// THE WORLD
			// ================================
			RLWorld world = new GameWorldSimpleMap(null, null, new GameMap());
			world.setInitValues(initialPolicyValues);

			// ================================
			// SETTING THE NAME OF THE FILE THAT IS CREATED
			// ================================
			String nombreSimulacion = Helper.generateFileName(learningMethod, selectionMethod, world, parametrizaciones,
					defaultLearningParameterValues, epsilonFinal, null);

			// ================================
			// SETTING THE EXPERIMENT LEARNER'S PARAMETERS
			// ================================
			RLearner learner = Helper.settingLearner(learningMethod, selectionMethod, world, parametrizaciones,
					defaultLearningParameterValues, nombreSimulacion,
					takeOnlyIntoAccountInRewardCalculationsLastRewardOfTheEpisode, crearGraficasInternas, epsilonFinal,
					null);

			if (ejecutarThreads) {
				// Executing a thread for each experiment (each set of parameters is an experiment)
				(threadsParametrizaciones[indice] = new Thread() {
					public void run() {
						// Executing the experiment
						executeExperiment(learner, nombreSimulacion);
					}

					private void executeExperiment(RLearner learner, String nombreSimulacion) {
						// ================================
						// RUNNING THE REINFORCEMENT
						// LEARNING ALGORITHM
						// ================================
						learner.runTrial();

						// ==================================
						// PERFORMANCE MEASURES ARE CAPTURED
						// ==================================
						// performanceMeasures[indiceParametro1][indiceParametro2] = learner.performanceBasedOnRMSErrors == null
						//	? -1 : ((new Mean()).evaluate(learner.performanceBasedOnRMSErrors));

						// ==================================
						// SAVING SIMULATION NAME OF THE EXPERIMENT
						// ==================================
						synchronized (fileSimulationNames) {
							fileSimulationNames.add(nombreSimulacion);
						}
					}

				}).start();
			}

			// Incrementamos el indice en el que se guardara el Thread
			indice++;

			// ================================
			// SETTING UP NEXT PARAMETER COMBINATION
			// ================================
			boolean change = false;
			if (numberOfParameterCombinations > 1) {
				while (!change) {
					indexCombination[i] = indexCombination[i] + 1;

					if (indexCombination[i] == parametrizaciones.get(nombreParametros[i]).getValues().length) {
						indexCombination[i] = 0;
						i = i - 1 < 0 ? indexCombination.length - 1 : i - 1;
					} else {
						change = true;
					}
				}
				i = indexCombination.length - 1;
			}

			// =================================================
			// CODIGO OPTATIVO PARA NO SOBRECARGAR EL ORDENADOR
			// =================================================
			if (false) {
				try {
					Thread.sleep(1000 * 60 * 4);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int numeroDeNucleosEnElProcesador = Runtime.getRuntime().availableProcessors() - 1;
		}

		// ==============================================================
		// AQUI ESPERAMOS A QUE TODAS LAS HEBRAS SE TERMINEN DE EJECUTAR
		// ==============================================================
		if (ejecutarThreads) {
			System.out.println("=================================");
			System.out.println("WAITING FOR THE EXECUTING THREADS");
			System.out.println("=================================");
			indice = 0;
			for (int j = 0; j < threadsParametrizaciones.length; j++) {
				try {
					threadsParametrizaciones[j].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("=================================");
			System.out.println("THREADS HAVE BEEN SUCCESSFULLY EXECUTED");
			System.out.println("=================================");
		}

		// ==================================================================================
		// ELAPSED TIME
		// ==================================================================================
		long tEnd = System.nanoTime();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000000000.0;

		System.out.println("==================================================================");
		System.out.println("(seconds) EXECUTION TIME FOR RUNNING TASKS AND EPISODES: " + elapsedSeconds);
		System.out.println("(minutes) EXECUTION TIME FOR RUNNING TASKS AND EPISODES: " + (elapsedSeconds / 60));
		System.out.println("==================================================================");

		// ===============================================================================
		// ALMACENAMOS LOS RESULTADOS DE RENDIMIENTO PARA LAS DIFERENTES PARAMETRIZACIONES
		// Nota: por cada parametrizacion se obtiene solo un unico dato, RMSE, que mide
		// el error producido de media entre la estimacion producida por el algoritmo
		// y la politica optima. Evidentemente, si se quiere obtener este dato ha de saberse
		// cual es la politica optima y, consecuentemente, implementarla en el objeto World.
		// ===============================================================================
		// Showing the results
		String performance = "";
		System.out.println("=================================================");
		performance = ";";
		for (int j = 0; j < lambdaValues.length; j++) {
			performance = performance + "(" + nombreParametro2 + "=" + lambdaValues[j] + ") ";
			performance = performance + (j + 1 == lambdaValues.length ? "" : ";");
		}
		performance = performance + "\n";

		for (int g = 0; g < gammaValues.length; g++) {
			performance = performance + "(" + nombreParametro1 + "=" + gammaValues[g] + "); ";
			for (int j = 0; j < lambdaValues.length; j++) {
				performance = performance + Helper.round(performanceMeasures[g][j], Output.maxDecimales);
				performance = performance + (j + 1 == lambdaValues.length ? "" : ";");
			}
			performance = performance + "\n";
		}
		System.out.println(performance);
		System.out.println("=================================================");
		try {
			Helper.writeToFile(performance, urlDelExperimento + "/" + Output.typeOfCharts.policyPerformance + ".csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
