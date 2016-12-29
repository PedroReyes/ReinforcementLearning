package Aspects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.rosuda.REngine.Rserve.RConnection;

import Analysis.RLGraficar;
import Auxiliar.Helper;
import RL.Action;
import RL.State;

public aspect RLAnalysisSimulationRecord {

	// ==================================================================================================
	// VARIABLES INTRODUCED
	// ==================================================================================================
	// Separator of file
	//private String RL.RLearner.separator = ";";
	public static boolean DEBUG = false;
	
	// Place where simulations are saved
	public static String RL.RLearner.simulationPath = "";//Output.Output.simulationPathExperimentosMultiarmBanditWorld[0];
	//"/Volumes/SGATE_BLACK/TFM/Experimentos/MultiArmBanditProblem/FixRewards";
	public static String RL.RLearner.simulationDescription = "";//Output.Output.simulationDescriptionExperimentosMultiarmBanditWorld[0];
	//System.getProperty("user.dir") + "/MultiArmBandits/Simulaciones";
	
	// Variables para el guardado del grafo en un fichero con formato .dgs
	private Scanner RL.RLearner.scanner;
	
	// Variable para el guardado del grafo en un fichero
	private Queue<String> RL.RLearner.recordSimulationQ; // se usa una cola de tamaño finito la cual se va renovando sus valores conforme see va guardando
										// de este modo se asegura que no hay un over-exceeded memory error
	
	// Prefix file for naming policy files
	public static String RL.RLearner.prefixForNamingPolicyFile = "QValues_";
	
	// ===============================================================
	// DATA ANALISYS
	// ===============================================================
	// This variable is used for create plots of small charge on CPU
	public boolean RL.RLearner.crearGraficasInternas;
	
	// For evaluate performance we use this measure to track an average of the RMSE produced by the difference
	// among the policy value and the true value of the policy that is stored in the World object
	public double[] RL.RLearner.performanceBasedOnRMSErrors;
	
	// For storing the average reward getting over the steps of a single episode
	public int RL.RLearner.currentEpisode;
	public double RL.RLearner.beforeAverageRewardPerEpisode;
	public double RL.RLearner.averageRewardPerEpisode;
	public boolean RL.RLearner.getOnlyLastReward;
	
	// For storing the percentage of optimal actions chosen over the steps of a single episode
	public double RL.RLearner.beforeNumberOfOptimalActionsChosenInAnEpisode;
	public double RL.RLearner.numberOfOptimalActionsChosenInAnEpisode;
	
	// Percentage of actions chosen over all the episodes
	public double[] RL.RLearner.percentageOfActions;
	public double[] RL.RLearner.counterOfActions;
	public int RL.RLearner.numberOfStepLastEpisode;

	// ==================================================================================================
	// METHODS INTRODUCED
	// ==================================================================================================
	/**
	 * Crea el fichero donde se ven reflejados las acciones y parametros del agente
	 * 
	 * @param nameDgsFile
	 */
	public void RL.RLearner.createSimulationFile() {
		try {
			String filePath = simulationPath + "/" + simulationName;
			// Se crea el fichero, fuente de los eventos que se producen en el mapa
			//(new File(simulationPath.substring(0, simulationPath.lastIndexOf('/')))).mkdirs();
			(new File(simulationPath)).mkdirs(); // resultados en forma de texto
			(new File(simulationPath + "/Images")).mkdirs(); // resultados en forma de imagenes
			
			// Create the simulation file that stored the simulation
			File file = new File(filePath);

			if (file.createNewFile()) {
				if(DEBUG){
					System.out.println("File is created!");
				}
			} else {
				if(DEBUG){
					System.err.println("File already exist.");
				}
			}

			// Inicializamos la matriz
			this.recordSimulationQ = new CircularFifoQueue<>(Integer.max(this.getThisWorld().getStates().length, Integer.max(this.getTotalEpisodes(), this.getThisWorld().getActions().length)));
			
			// Se inicializa el fichero
			Path path = Paths.get(filePath);
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				String initialText = "sizeMap; "
						+ "possibleActions; possibleStates; initialQValues; learningMethod; selectionMethod; selectionMethodForSimulationMCTS; "
						+ "numberOfTasks; numberOfEpisodes; softmax_baselineUsed; softmax_temperature; UCB_c; "
						+ "eGreedy_epsilon_initial; eGreedy_epsilon_final; qLearning_alpha; "
						+ "qLearning_gamma; qLearning_lambda; maxStepsForSimulationMCTS; simulationDepthChargeMCTS; taskNumber; episodeNumber; episodeStepTime; "
						+ "optimalAction; lastState; actionInLastState; newState; averageReward; RMS; "
						+ "percentageOfChosenOptimalActionsPerEpisode; percentageOfChosenActionsPerEpisode; victories;";
				
				recordSimulationQ.offer(initialText);
				
				writer.write(initialText + "\n");//getCsvText(simulacion));
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ===============================================================
		// DATA ANALISYS
		// ===============================================================
		
	}
	
	/**
	 * Separate the content of the list into lines into a String
	 * @param simulacion
	 * @return
	 */
	public static String RL.RLearner.getCsvTextWithList(List<String> simulacion) {
		// TODO Auto-generated method stub
		String csvContent = "";
		for (int i = 0; i < simulacion.size(); i++) {
			csvContent = csvContent + simulacion.get(i) + "\n";
		}
		return csvContent;
	}
	
	/**
	 * Gives the content among the index specified separating
	 *  the content of the list into lines into a String
	 * @param s - simulacion
	 * @return
	 */
	public static String RL.RLearner.getCsvText(List<String> s, int indexStart, int indexEnd) {
		// TODO Auto-generated method stub
		String csvContent = "";
		for (int i = indexStart; i < indexEnd; i++) {
			csvContent = csvContent + s.get(i) + "\n";//(i+1==indexEnd?"":"\n");
		}
		return csvContent;
	}
	
	/**
	 * Separate the content of the list into lines into a String
	 * @param simulacion
	 * @return
	 */
	public static synchronized String RL.RLearner.getCsvTextWithQueue(Queue<String> s, int sizeToBeStored){
		String csvContent = "";
		int size = sizeToBeStored;//s.size();
		for (int i = 0; i < size; i++) {
			csvContent = csvContent + s.poll() + "\n";//(i+1==indexEnd?"":"\n");
		}
		
		return csvContent;
	}

	/**
	 * Saves the task number of the simulation that is specified
	 * 
	 * @param actionExecutedFromParentState
	 * @throws IOException
	 */
	public void RL.RLearner.saveSimulation(int numberOfTask) throws IOException {
		// Main variables
		String filePath = simulationPath + "/" + simulationName;
		
		// Get the indexes to be appended to the end of the file
		int tasks = this.getTotalTasks();
			
		// Apprended the content amongst the indexes specified
		try(FileWriter fw = new FileWriter(filePath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw)){
			String newText = getCsvTextWithQueue(this.recordSimulationQ, tasks==this.getTaskNumber()+1?this.recordSimulationQ.size():this.getTotalEpisodes());
			out.println(newText);
		} catch (IOException e) {
			//exception handling left as an exercise for the reader
		}
		
	}
	
	/**
	 * Saves a file with the description provided
	 *
	 * @throws IOException
	 */
	public void RL.RLearner.createFileWithExperimentDescription(RL.RLearner learner) throws IOException {
		// Main variables
		String descriptionFileName = "SimulationDescription.txt";
		String filePath = simulationPath + "/" + descriptionFileName;
		(new File(filePath)).createNewFile();
		
		if(DEBUG){
			System.out.println("Saving file description of the simulation.");
		}
		
		if((new File(filePath)).exists()){
			// File scanner
			scanner = new Scanner(new File(filePath));
			
			// Escribimos el contenido completo de la lista
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {	
				//writer.write(beforeText + newText + afterText);
				writer.write(RL.RLearner.simulationDescription);
				writer.write("\n\n\n");
				writer.write("===============GLOBAL PARAMETERS OF THE EXPERIMENT===============");
				writer.write("\n"+"Learning method: " + RL.RLearner.LEARNING_METHOD.values()[learner.getLearningMethod()].toString());
				writer.write("\n"+"Selection method: " + RL.RLearner.SELECTION_METHOD.values()[learner.getActionSelectionMethod()].toString());
				writer.write("\n"+"Total tasks: " + learner.getTotalTasks());
				writer.write("\n"+"Total episodes: " + learner.getTotalEpisodes());
				writer.write("\n\n");
				writer.write("===============IMAGES DESCRIPTION===============");
				writer.write("- xEpisode_yWinProbability: measure the probability of winning a game with the policy acquired in the current episode");
				writer.write("- xEpisode_yAverageReward: average of [average reward of each step in an episode] of each episode per task.");
				writer.write("- xEpisode_yOptimalAction: average of [percentage of optimal actions chosen in an episode] of each episode per task.");
				writer.write("- xEpisode_yPercentageOfActions: histogram of the average of the [percentage of each action chosen over an episode] of each episode per task.");
				writer.write("- xEpisode_yAverageSteps: average of all the tasks of steps needed in an episode");
				writer.write("- histogramOfQvalues: histogram of the QValues obtained in the last episode of the last task");
				writer.write("- policyPerformance: Description");
			}
		}else{
			System.err.println("Error: " + descriptionFileName + " was not created.");
		}
		
	}
	
	/**
	 * Saves a file with the policy obtained
	 *
	 * @throws IOException
	 */
	public void RL.RLearner.createFileWithPolicyObtained(RL.RLearner learner) throws IOException {
		// Main variables
		String policyFilename = RLearner.prefixForNamingPolicyFile + (learner.simulationName.contains(".csv")?learner.simulationName.substring(0,learner.simulationName.indexOf(".csv")): learner.simulationName)+".csv" ;
		String filePath = simulationPath + "/" + policyFilename;
		(new File(filePath)).createNewFile();
		
		if(DEBUG){
			System.out.println("Saving file with the policy obtained.");
		}
		
		if((new File(filePath)).exists()){
			// File scanner
			scanner = new Scanner(new File(filePath));
			
			// Escribimos el contenido completo de la lista
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {	
				//writer.write(beforeText + newText + afterText);
				// Obtenemos los nombres de los estados
				String states = ";";
				State[] statesNames = learner.getThisWorld().getStates();
				Action[] actionsNames = learner.getThisWorld().getActions();
				for (int i = 0; i < statesNames.length; i++) {
					states = states + statesNames[i].getShortDescription() + ((i+1)==statesNames.length?"":";");
				}
				
				writer.write(states + "\n");
				
				// Obtenemos los valores Q para cada estado y accion
				String qValues = "";
				for (int iAction = 0; iAction < actionsNames.length; iAction++) {
					
					qValues = actionsNames[iAction].name() + ";";
					for (int jState = 0; jState < statesNames.length; jState++) {
						learner.getPolicy().getQValue(jState, iAction);
						qValues = qValues + learner.getPolicy().getQValue(jState, iAction) + (jState==statesNames.length-1?"":";");
					}
					
					writer.write(qValues + "\n");
					
					qValues = "";
				}
			}
		}else{
			System.err.println("Error: " + policyFilename + " was not created.");
		}
		
		// Creating the chart
		// Se crean graficas especificas de este RLearner
		if(learner.crearGraficasInternas){
			learner.createSpecificCharts(learner, policyFilename);
		}
	}
	
	/**
	 * Crea charts que son espeficos del RLearner, es decir, crea charts en los qeu solo aparecen
	 * datos relacionados con la simulacion de una instancia de un RLearner
	 * @param learner
	 */
	private void RL.RLearner.createSpecificCharts(RL.RLearner learner, String specificFile) {
		boolean chooseFiles = false;
		specificFile = specificFile.substring(0, specificFile.indexOf(".csv"));
		RLGraficar graficar = new RLGraficar(chooseFiles);

		// Checking R connection with Rserve
		try {
			new RConnection();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Sep up Rserve: goes to R class Analysis->SetUpRserve.R and run it",
					"Rserve not setup", JOptionPane.ERROR_MESSAGE);
		}
		
		// HISTOGRAM OF QVALUES
		if(true){
			graficar.crearGrafica(RLearner.simulationPath, Output.Output.typeOfCharts.histogramOfQvalues, specificFile, null);
		} else {
			final String specificFilePath = new String(specificFile);
			Thread t = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					System.out.println("WTF");
					graficar.crearGrafica(RLearner.simulationPath, Output.Output.typeOfCharts.histogramOfQvalues, specificFilePath, null);
				}
			};
			t.start();
		}
	}
	
	/**
	 * Save in the simulation List the possible pairs <state,actions>
	 */
	@SuppressWarnings("serial")
	public void RL.RLearner.savePossiblePairActionStates(){
		// Row separator
		String separator = ";";

		// Adding the size of the map
		Queue<String> thisSimulationQ = this.recordSimulationQ;
		int parameterColumn = 0;
		String dimX = String.valueOf(this.getThisWorld().getGameMap().getDimX());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(dimX);}});
		
		// Adding possible actions to final csv file
		parameterColumn++;
		parameterColumn = 1;
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, listOfActions(this.getThisWorld().getActions()));
		
		// Adding possible states to final csv file
		parameterColumn++;
		parameterColumn = 2;
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, listOfStates(this.getThisWorld().getStates()));
		
		// Adding the initial value of the policy
		parameterColumn++;
		parameterColumn = 3;
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, initialPolicyValues(this.getThisWorld().getInitValues()));
		
		// Adding the learning method
		parameterColumn++;
		parameterColumn = 4;
		int learningMethodIndice = this.getLearningMethod();
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(RL.RLearner.LEARNING_METHOD.values()[learningMethodIndice].name());}});
		
		// Adding the selection method
		parameterColumn++;
		parameterColumn = 5;
		int selectionMethodIndice = this.getActionSelectionMethod();
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(RL.RLearner.SELECTION_METHOD.values()[selectionMethodIndice].name());}});
		
		// Adding the total number of tasks
		parameterColumn++;
		parameterColumn = 6;
		int selectionMethodIndiceForMCTS = this.getActionSelectionMethodForSimulation();
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(RL.RLearner.SELECTION_METHOD.values()[selectionMethodIndiceForMCTS].name());}});
		
		// Adding the total number of episodes
		parameterColumn++;
		parameterColumn = 7;
		String totalTasks = String.valueOf(this.getTotalTasks());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(totalTasks);}});
		
		
		// Adding the usage of baseline
		parameterColumn++;
		parameterColumn = 8;
		String totalEpisodes = String.valueOf(this.getTotalEpisodes());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(totalEpisodes);}});
		
		// Adding the temperature of the algorithm softmax
		parameterColumn++;
		parameterColumn = 9;
		String baseline = String.valueOf(this.isBaselineUsed());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(baseline);}});
		
		// Adding the c exploration parameter of algorithm UCB
		parameterColumn++;
		parameterColumn = 10;
		String softmaxTemperature = String.valueOf(this.getTemperature());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(softmaxTemperature);}});
		
		// Adding the final epsilon parameter value
		parameterColumn++;
		parameterColumn = 11;
		String c_ExplorationParamater = String.valueOf(this.getC());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(c_ExplorationParamater);}});
		
		// Adding the initial epsilon parameter value 
		parameterColumn++;
		parameterColumn = 12;
		String epsilonInicial = String.valueOf(this.getEpsilonRange()[0]);
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(epsilonInicial);}});
		
		// Adding the alpha q learning parameter 
		parameterColumn++;
		parameterColumn = 13;
		String epsilonFinal = String.valueOf(this.getEpsilonRange()[1]);
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(epsilonFinal);}});
		
		// Adding the alpha q learning parameter 
		parameterColumn++;
		parameterColumn = 14;
		String alpha = String.valueOf(this.getAlpha());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(alpha);}});
		
		// Adding the alpha q learning parameter 
		parameterColumn++;
		parameterColumn = 15;
		String gamma = String.valueOf(this.getGamma());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(gamma);}});
		
		// Adding the number of steps for simulation in the MCTS algorithm 
		parameterColumn++;
		parameterColumn = 16;
		String lambda = String.valueOf(this.getLambda());
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(lambda);}});
		
		// Adding the number of steps for simulation in the MCTS algorithm 
		parameterColumn++;
		parameterColumn = 17;
		String totalStepsMCTS = String.valueOf(this.getMcts().totalNumberSteps);
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(totalStepsMCTS);}});
		
		// Adding the number of steps in a particular simulation for getting the reward in the MCTS algorithm 
		parameterColumn++;
		parameterColumn = 18;
		String simulationDepthCharge = String.valueOf(this.getMcts().simulationDepthCharge);
		thisSimulationQ = insertListInColumn(thisSimulationQ, separator, parameterColumn, new LinkedList<String>(){{add(simulationDepthCharge);}});
				
		// Final result
		this.recordSimulationQ = new CircularFifoQueue<>(
				Integer.max(this.getThisWorld().getStates().length, 
						Integer.max(this.getTotalEpisodes(), this.getThisWorld().getActions().length)));
		
		// Create the queque with the initial information
		int cont = 0;
		for (Iterator<String> iterator = thisSimulationQ.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			this.recordSimulationQ.offer(string);
		}
		
		// The first line is repeated so we remove it from the queue (PARCHE)
		if(this.getTotalEpisodes()>this.getThisWorld().getStates().length){
//			System.err.println(">polling<");
			this.recordSimulationQ.poll();
		}
	}
	
	/**
	 * It is used each time a reward is given storing the current state of the world and the learner
	 * @param learner
	 */
	private void updateRecording(RL.RLearner learner) {
		// Parameters to seek for the row we will modify
		int nT = learner.getTaskNumber() + 1;
		int nE = learner.getEpisodeNumber() + 1;
		int nStep = learner.getTimeStep() + 1;
		int episodes = learner.getTotalEpisodes();
		int tasks = learner.getTotalTasks();
		
		// ===============================================================
		// DATA ANALISYS
		// ===============================================================
		// ROOT MEAN SQUARE ERROR OF THE EPISODE COMPARED WITH THE OPTIMAL POLICY, IF EXIST.
		// In case the array of errors is not initialized, then initialized it
		learner.performanceBasedOnRMSErrors = (learner.performanceBasedOnRMSErrors==null ? 
				new double[learner.getTotalEpisodes()]:
				learner.performanceBasedOnRMSErrors);
		
		// Modificamos el parametro que mide el rendimiento del algoritmo en base a
		// la diferencia cuadratica media entre la estimacion y lo que se estima
		if(learner.getThisWorld().endState()){
			int errorIndex = nE - 1;//(nT-1)*episodes + nE - 1;
			// Acumulative average over the episodes of each task
			learner.performanceBasedOnRMSErrors[errorIndex] = learner.getRootMeanSquareErrorFromCurrentPolicy(); 
					//((learner.performanceBasedOnRMSErrors[errorIndex] * (nT-1))+learner.getRootMeanSquareErrorFromCurrentPolicy())/nT;
		}
		
		if(learner.getOnlyLastReward){
			// SAVING LAST REWARD OF EACH EPISODE
			learner.averageRewardPerEpisode = learner.getReward();
		}else{
			// SAVING AVERAGE REWARD OF EACH STEP IN A SINGLE EPISODE
			learner.averageRewardPerEpisode = (learner.beforeAverageRewardPerEpisode*(nStep-1) + learner.getReward())/nStep;
			learner.beforeAverageRewardPerEpisode = learner.averageRewardPerEpisode;
		}
		
		// SAVING NUMBER OF OPTIMAL ACTION OF EACH STEP IN A SINGLE EPISODE
		learner.numberOfOptimalActionsChosenInAnEpisode = nStep-1==0?0:learner.numberOfOptimalActionsChosenInAnEpisode; 
		learner.numberOfOptimalActionsChosenInAnEpisode = learner.getThisWorld().getOptimalAction()!=null && learner.getAction()==learner.getThisWorld().getOptimalAction().ordinal()?learner.numberOfOptimalActionsChosenInAnEpisode+1:learner.numberOfOptimalActionsChosenInAnEpisode;
		
		// SAVING THE PERCENTAGE OF ACTIONS
		// In case the array of errors is not initialized, then initialized it
		learner.percentageOfActions = (learner.percentageOfActions==null ? new double[learner.getThisWorld().getActions().length]:learner.percentageOfActions);
		learner.counterOfActions = (learner.counterOfActions==null ? new double[learner.getThisWorld().getActions().length]:learner.counterOfActions);
		
		// Counting the actions
		learner.counterOfActions[learner.getAction()] = learner.counterOfActions[learner.getAction()] + 1;
		
		// if nStep-1==0 then we are in a new episode
		if(learner.getThisWorld().endState()){
			// We measure the percentage for this episode
			for (int action = 0; action < learner.getThisWorld().getActions().length; action++) {
				// Average all the percentage for each action
				learner.percentageOfActions[action] = Helper.round(learner.counterOfActions[action]*100/((double)nStep), Output.Output.maxDecimales);
				
				// resetting the counter for the next episode
				learner.counterOfActions[action] = 0;
			}
		}
		
		// ===============================================================
		// SAVING THE DATA IN THE RIGHT POSITION
		// ===============================================================
		// Index of the row we seek
		int index = (nT-1)*episodes + nE;
		index = nE - 1;
		
		
		// Getting the row which must be modified
		String[] splitRow = null;
		
		if(index<learner.recordSimulationQ.size()){
			List<String> auxList = learner.recordSimulationQ.stream().collect(Collectors.toList());
			String row = auxList.get(index);
//			if(learner.getTimeStep()<10){
//				System.out.println("row STEP " + learner.getTimeStep() + "> " + row);
//			}
			splitRow = row.split(";", -1);
		}
		
		// Defining the value of the row
		String episodeStep = "";
		int parameterColumn = 0;
		episodeStep = episodeStep + (splitRow!=null? splitRow[0] + ";" : ";"); // map size
		episodeStep = episodeStep + (splitRow!=null? splitRow[1] + ";" : ";"); // possible actions
		episodeStep = episodeStep + (splitRow!=null? splitRow[2] + ";" : ";"); // possible states
		episodeStep = episodeStep + (splitRow!=null? splitRow[3] + ";" : ";"); // initialValues
		episodeStep = episodeStep + (splitRow!=null? splitRow[4] + ";" : ";"); //RL.RLearner.LEARNING_METHOD for the learner
		episodeStep = episodeStep + (splitRow!=null? splitRow[5] + ";" : ";"); //RL.RLearner.SELECTION_METHOD for the learner
		episodeStep = episodeStep + (splitRow!=null? splitRow[6] + ";" : ";"); //RL.RLearner.SELECTION_METHOD for MCTS in simulation phase
		episodeStep = episodeStep + (splitRow!=null? splitRow[7] + ";" : ";"); //;learner.getTotalTasks() + "; ";
		episodeStep = episodeStep + (splitRow!=null? splitRow[8] + ";" : ";"); //;learner.getTotalEpisodes() + ";";
		episodeStep = episodeStep + (splitRow!=null? splitRow[9] + ";" : ";"); //learner.isBaselineUsed() + "; ";
		episodeStep = episodeStep + (splitRow!=null? splitRow[10] + ";" : ";"); //learner.getSoftmaxTemperature() + "; ";
		episodeStep = episodeStep + (splitRow!=null? splitRow[11] + ";" : ";"); //learner.getC() + "; ";
		episodeStep = episodeStep + (splitRow!=null? splitRow[12] + ";" : ";"); //learner.getEpsilonRange()[0] + ";";
		episodeStep = episodeStep + (splitRow!=null? splitRow[13] + ";" : ";"); //learner.getEpsilonRange()[1] + ";";
		episodeStep = episodeStep + (splitRow!=null? splitRow[14] + ";" : ";"); //learner.getAlpha() + ";";
		episodeStep = episodeStep + (splitRow!=null? splitRow[15] + ";" : ";"); //learner.getGamma() + ";";
		episodeStep = episodeStep + (splitRow!=null? splitRow[16] + ";" : ";"); //learner.getGamma() + ";";
		episodeStep = episodeStep + (splitRow!=null? splitRow[17] + ";" : ";"); //number of max steps for simulation in the MCTS algorithm
		episodeStep = episodeStep + (splitRow!=null? splitRow[18] + ";" : ";"); //number of max steps in a particular simulation for getting the reward in the MCTS algorithm
		episodeStep = episodeStep + learner.getTaskNumber() + ";"; // task number
		episodeStep = episodeStep + learner.getEpisodeNumber() + ";"; // episode number
		episodeStep = episodeStep + learner.getTimeStep() + ";"; // episode step time
		episodeStep = episodeStep + (learner.getThisWorld().getOptimalAction()!=null?learner.getThisWorld().getOptimalAction().name():"NO_OPTIMAL_ACTION") + ";"; // real optimal action to be taken
		episodeStep = episodeStep + learner.getThisWorld().getStates()[learner.getStateBefore().getOrdinalState()].getShortDescription() + ";"; // last state
		episodeStep = episodeStep + learner.getThisWorld().getActions()[learner.getAction()].name() + ";"; // action in last state
		episodeStep = episodeStep + learner.getThisWorld().getStates()[learner.getNewState().getOrdinalState()].getShortDescription() + ";"; // new state
		episodeStep = episodeStep + learner.averageRewardPerEpisode + ";"; // reward
		episodeStep = episodeStep + learner.getRootMeanSquareErrorFromCurrentPolicy() + ";"; // RMS error between final episode policy and optimal policy
		episodeStep = episodeStep + ((learner.numberOfOptimalActionsChosenInAnEpisode*100)/nStep) + ";"; // percentage of optimal actions chosen in an episode
		String[] percentageOfActions = ((Arrays.toString(learner.percentageOfActions).substring(1, Arrays.toString(learner.percentageOfActions).length() - 1)).split(", "));
		episodeStep = episodeStep + String.join("_", percentageOfActions) + ";"; // percentage of actions chosen in an episode
		int[] victoryStates = learner.getThisWorld().getVictoryStates()==null?null:Stream.of(learner.getThisWorld().getVictoryStates()).mapToInt(a -> a.getOrdinalState()).toArray();
		episodeStep = episodeStep + (victoryStates!=null?(IntStream.of(victoryStates).anyMatch(x -> x == learner.getNewState().getOrdinalState())? 1: 0): 0) + ";"; // 1 for a won game and 0 for a lost game
		
//		if(learner.getTimeStep()<10){
		if(false)
			System.out.println("Task " + learner.getTaskNumber() + ", Episode " + learner.getEpisodeNumber() + 
					", from state " + learner.getThisWorld().getStates()[learner.getStateBefore().getOrdinalState()].getShortDescription() + 
					", with action " + learner.getThisWorld().getActions()[learner.getAction()].name() + 
					", to state " + learner.getThisWorld().getStates()[learner.getNewState().getOrdinalState()].getShortDescription());
//		}
		
		// Modificado o añadiendo la estructura usada para almacenar los datos
		if(index>=learner.recordSimulationQ.size()){
			learner.recordSimulationQ.offer(episodeStep);
		}else{
			List<String> auxList = learner.recordSimulationQ.stream().collect(Collectors.toList());
			auxList.set(index, episodeStep);
							
			// Final result
			learner.recordSimulationQ = new CircularFifoQueue<>(
					Integer.max(learner.getThisWorld().getStates().length, 
							Integer.max(learner.getTotalEpisodes(), learner.getThisWorld().getActions().length))
//					learner.getTotalEpisodes()>=learner.getThisWorld().getActions().length?
//					learner.getTotalEpisodes():learner.getThisWorld().getActions().length
						);
					
			for (int i = 0; i < auxList.size(); i++) {
				String string = auxList.get(i);
				learner.recordSimulationQ.offer(string);
			}
		}
	}
	
	// ==================================================================================================
	// POINTCUTS AND ADVICES
	// ==================================================================================================
	/**
	 * Running a base operation of select an action given an state and record the reward, state before and next state
	 * @param learner
	 */
	pointcut trialCompleted(RL.RLearner learner):
			this(learner) &&
			call(void RL.RLearner.trialCompleted());

	after(RL.RLearner learner) returning: trialCompleted(learner){
			//System.out.println("TRIAL COMPLETED");
		try {
			// Se guarda un fichero con la descripcion del experimento
			learner.createFileWithExperimentDescription(learner);
			
			// Se guarda un fichero con la politica de estados final que se tenga
			learner.createFileWithPolicyObtained(learner);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * when an epoch is completed, we check the percentage of completed process done 
	 */
	pointcut epochCompleted(RL.RLearner learner):
		this(learner) &&
		call(void RL.RLearner.runEpoch());

	after(RL.RLearner learner) returning: epochCompleted(learner){
		int nT = learner.getTaskNumber() + 1;
		int nE = learner.getEpisodeNumber() + 1;
		int tasks = learner.getTotalTasks();
		int episodes = learner.getTotalEpisodes();
		double percentageCompleted = (double)(nT*nE*100)/(double)(tasks*episodes);
		if(nE==(episodes)){
			if(percentageCompleted%10==0){
				// OPTION 1
				//System.out.println(nT + " " + nE + " " + tasks + " " + episodes);
				
				// OPTION 2
				System.out.println("-----> "+ percentageCompleted + "% completed <----- Episode " + learner.getEpisodeNumber() + " of Task " + learner.getTaskNumber());
				
				// OPTION 3
				if(percentageCompleted==100) System.out.println("FINISHED: " + learner.simulationName);
			}
			try {learner.saveSimulation(nT);} catch (Exception e) {e.printStackTrace();}
		}
	}

	
	/**
	 * Running a base operation of select an action given an state and record the reward, state before and next state
	 * @param learner
	 */
	pointcut manualRecord(RL.RLearner learner): // <--- alternativa: se establece manualmente caundo se hace un update
		this(learner) &&
		call(void RL.RLearner.recordCurrentStateOfRLearner());
	
	pointcut gettingReward(RL.RLearner learner): // <--- alternativa: se hace el update cuando se obtnega un reward
		this(learner) &&
		call(double RL.RLearner.getWorldReward());
	
	pointcut runBaseProcess(RL.RLearner learner): // <--- alternativa: un metodo global (no es posible por sarsa)
		this(learner) &&
		call(void RL.RLearner.reinforcementLearningBaseProcess());

	after(RL.RLearner learner) returning: manualRecord(learner){
		updateRecording(learner);
	}
	
	// CREATING A NEW LEARNER
	/**
	 *  Do something right after creating a RLeaner 
	 *  Warning: this pointcut must be at the end
	 * @param createdObject
	 */
	pointcut learnerCreation(RL.RLearner createdObject) :// alternativa: se crean los ficheros cuando se crea el learner
	    (execution(RL.RLearner.new(..)) )
	        && this(createdObject);
	
	pointcut runningTrial(RL.RLearner learner): // alternativa: se crean los ficheros cuando se corre el trial
		call(void RL.RLearner.runningTrial()) && this(learner);
	
//	after(RL.RLearner createdObject) : learnerCreation(createdObject) {
//	    createdObject.createSimulationFile();
//	    createdObject.savePossiblePairActionStates();
//	}
	
	after(RL.RLearner learner): runningTrial(learner) {
		learner.createSimulationFile();
		learner.savePossiblePairActionStates();
	}
	
	// ==================================================================================================
	// AUXILIAR METHODS
	// ==================================================================================================
	/**
	 * Devuelve el array en forma de lista
	 * @param possibleActions
	 * @return
	 */
	private static List<String> listOfActions(Action[] possibleActions) {
		return Arrays.stream(possibleActions).map(a -> a.name().toString()).collect(Collectors.toList());
	}
	
	/**
	 * Devuelve el array en forma de lista
	 * @param possibleStates
	 * @return
	 */
	private static List<String> listOfStates(State[] possibleStates) {
		// TODO Auto-generated method stub
		return Arrays.stream(possibleStates).map(s -> s.getShortDescription()).collect(Collectors.toList());
	}
	
	/**
	 * Devuelve el array en forma de lista
	 * @param policy
	 * @return
	 */
	private static List<String> initialPolicyValues(double initialValue) {
		List<String> list = new LinkedList<String>();
		list.add(String.valueOf(initialValue));
		
		return list;
	}
	
	/**
	 * Inserta en la lista "thisSimulation" 
	 * en la columna indicada "parameterColumn" 
	 * la lista "listOfPossibleActions" 
	 * usando como separador de columna "separator"
	 * @param thisSimulation
	 * @param separator
	 * @param parameterColumn
	 * @param listToBeStored
	 */
	private static Queue<String> insertListInColumn(Queue<String> thisSimulationQ, String separator, int parameterColumn, List<String> listToBeStored) {
		List<String> thisSimulation = thisSimulationQ.stream().collect(Collectors.toList());
		
		String step = "";
		
		for (int i = 0; i < listToBeStored.size(); i++) {
			// May or not exist a row to add this attribute
			if(i>=thisSimulation.size()-1){
				// Creating the row
				int timesSeparator = thisSimulation.get(0).split(";").length;
				step = new String(new char[timesSeparator]).replace("\0", separator);
				String[] aux = step.split(";", -1); // new line
				aux[parameterColumn] = listToBeStored.get(i); // new line
				step = String.join(separator, aux); 
				
				// Adding new row
				thisSimulation.add(step);
			}else{
				// Creating the row
				step = thisSimulation.get(i+1);
				String[] aux = step.split(";",-1);
				aux[parameterColumn] = listToBeStored.get(i);
				step = String.join(separator, aux);
				
				// Setting existent row
				thisSimulation.set(i+1, step);
			}
		}
		
		return new CircularFifoQueue<>(thisSimulation);
	}
}
