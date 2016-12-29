package Auxiliar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import RL.RLWorld;
import RL.RLearner;
import RL.RLearner.LEARNING_METHOD;
import RL.RLearner.SELECTION_METHOD;

public class Helper {

	public static String getCurrentTime() {
		//		Calendar cal = Calendar.getInstance();
		//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		//        System.out.println( (new SimpleDateFormat("HH:mm:ss")).format((Calendar.getInstance()).getTime()) );
		return (new SimpleDateFormat("HH:mm:ss")).format((Calendar.getInstance()).getTime());
	}

	/**
	 * Box the double[] into Double[]
	 * @param auxState
	 * @return
	 */
	public static Double[] boxDoubleArray(double[] array) {
		//=======
		//		return Arrays.stream(array).boxed().toArray(Double[]::new);
		//=======

		Double[] result = new Double[array.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = array[i];
		}
		return result;
		//=======
	}

	/**
	 * Unbox the double[] into Double[]
	 * @param auxState
	 * @return
	 */
	public static double[] unboxDoubleArray(Double[] array) {
		//		return Stream.of(array).mapToDouble(Double::doubleValue).toArray();
		double[] result = new double[array.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = array[i];
		}
		return result;
	}

	public static float[] listToArray(List<Float> list) {
		return unboxFloatArray(list.toArray(new Float[list.size()]));
	}

	/**
	 * Box the double[] into Double[]
	 * @param auxState
	 * @return
	 */
	public static Float[] boxFloatArray(float[] array) {
		Float[] result = new Float[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Box the double[] into Double[]
	 * @param auxState
	 * @return
	 */
	public static float[] unboxFloatArray(Float[] array) {
		float[] result = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Return the round double of the "valor" with "numDecimales" decimals
	 *
	 * @param valor
	 * @param numDecimales
	 * @return
	 */
	public static double round(double valor, int numDecimales) {
		return new BigDecimal(valor + "").setScale(numDecimales, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Displays a message dialog in the middle of the screen
	 * @param title
	 * @param message
	 */
	public static void showMessageDialog(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Root Mean Square Error of the an array containing the differences between the estimates and the real value
	 * @param nums
	 * @return
	 */
	public static double rmse(double[] nums) {
		double ms = 0;
		for (int i = 0; i < nums.length; i++)
			ms += nums[i] * nums[i];
		ms /= nums.length;
		return Math.sqrt(ms);
	}

	/**
	 * Saves the text into the file (in case the file does not exist, create it)
	 * 
	 * @param text
	 * @param targetFilePath
	 * @throws IOException
	 */
	//	public static void writeToFile(String text, String targetFilePath) throws IOException {
	//		Path targetPath = Paths.get(targetFilePath);
	//		byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
	//		Files.write(targetPath, bytes, StandardOpenOption.CREATE);
	//	}

	/**
	 * Method for getting the maximum value 
	 * @param inputArray
	 * @return
	 */
	public static int getMax(int[] inputArray) {
		int maxValue = inputArray[0];
		for (int i = 1; i < inputArray.length; i++) {
			if (inputArray[i] > maxValue) {
				maxValue = inputArray[i];
			}
		}
		return maxValue;
	}

	/**
	 * Method for getting the minimum value 
	 * @param inputArray
	 * @return
	 */
	public static int getMin(int[] inputArray) {
		int minValue = inputArray[0];
		for (int i = 1; i < inputArray.length; i++) {
			if (inputArray[i] < minValue) {
				minValue = inputArray[i];
			}
		}
		return minValue;
	}

	/**
	 * Convert a string of type 2.4E-79 or so to double
	 * @param numberWithManyDecimals
	 * @return
	 */
	public static Double convertStringWithExponential(String numberWithManyDecimals) {
		double result = Double.valueOf(numberWithManyDecimals).doubleValue();
		//		System.out.println(result);
		return result;
	}

	/**
	 * Generate based on all the parameters the learner may received the name of the file
	 * @param totalTasks
	 * @param totalEpisodes
	 * @param learningMethod
	 * @param selectionMethod
	 * @param learningRate
	 * @param discountedFactor
	 * @param lambdaBackup
	 * @param epsilonSelection
	 * @param cExploratoryParameterUCB
	 * @param temperatureSoftmax
	 * @param baselineUsedSoftmax
	 * @param nombreParametro1
	 * @param nombreParametro2
	 * @param world
	 * @param experimentParameterValues1
	 * @param indiceParametro1
	 * @param experimentParameterValues2
	 * @param indiceParametro2
	 * @return
	 */
	public static String generateFileName(LEARNING_METHOD learningMethod, SELECTION_METHOD selectionMethod,
			RLWorld world, Map<String, Parametrizacion> parametrizaciones, double[] defaultLearningParameterValues,
			double epsilonFinal) {
		String worldName = world.getClass().toString().substring(world.getClass().toString().lastIndexOf(".") + 1);

		String nombreSimulacion = worldName + "_" + learningMethod.name() + "_" + selectionMethod.name() + "_t"
				+ defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalTasks.ordinal()] + "_ep"
				+ defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalEpisodes.ordinal()]

				+ "_lRate"
				// They attribute may be a parameter or a constant value on the experiment
				+ (parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.alphaLearningRate.name())
						? parametrizaciones.get(RLearner.LEARNING_PARAMETER.alphaLearningRate.name()).getValue()
						: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.alphaLearningRate.ordinal()])

				+ "_dFactor"
				// They attribute may be a parameter or a constant value on the experiment
				+ (parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name())
						? parametrizaciones.get(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name()).getValue()
						: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.gammaDiscountFactor.ordinal()])

				+ // If a lambda method is selected the name of the file will contain such atttribute
				(learningMethod.name().equals(LEARNING_METHOD.Q_LEARNING_LAMBDA.name())
						|| learningMethod.name().equals(LEARNING_METHOD.SARSA_LAMBDA.name())
								// They attribute may be a parameter or a constant value on the experiment
								? "_backup" + (parametrizaciones.keySet()
										.contains(RLearner.LEARNING_PARAMETER.lambdaSteps.name())
												? parametrizaciones.get(RLearner.LEARNING_PARAMETER.lambdaSteps.name())
														.getValue()
												: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.lambdaSteps
														.ordinal()])
								: "")

				+ // If a eGreedy selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.E_GREEDY.name())
						|| selectionMethod.name()
								.equals(SELECTION_METHOD.E_GREEDY_CHANGING_TEMPORALLY
										.name())
												// They attribute may be a parameter or a constant value on the experiment
												? "_eG" + (parametrizaciones.keySet()
														.contains(RLearner.LEARNING_PARAMETER.epsilonSelection.name())
																? parametrizaciones
																		.get(RLearner.LEARNING_PARAMETER.epsilonSelection
																				.name())
																		.getValue()
																: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.epsilonSelection
																		.ordinal()])
														+ ("_" + (selectionMethod
																.name().equals(
																		SELECTION_METHOD.E_GREEDY_CHANGING_TEMPORALLY
																				.name()) ? epsilonFinal
																						: parametrizaciones
																								.get(RLearner.LEARNING_PARAMETER.epsilonSelection
																										.name())
																								.getValue()))
												: "WTF?")

				+ // If a UCB selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.UCB.name())
						// They attribute may be a parameter or a constant value on the experiment
						? "_cUCB" + (parametrizaciones.keySet()
								.contains(RLearner.LEARNING_PARAMETER.c_UCBSelection.name())
										? parametrizaciones.get(RLearner.LEARNING_PARAMETER.c_UCBSelection.name())
												.getValue()
										: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.c_UCBSelection
												.ordinal()])
						: "")

				+ // If a softmax selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.SOFT_MAX.name())
						// They attribute may be a parameter or a constant value on the experiment
						? "_temperature" + (parametrizaciones.keySet().contains(
								RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name()) ? parametrizaciones
										.get(RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name()).getValue()
										: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection
												.ordinal()])
						: "")

				+ // If a softmax selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.SOFT_MAX.name())
						// They attribute may be a parameter or a constant value on the experiment
						? (parametrizaciones.keySet()
								.contains(RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
										? parametrizaciones
												.get(RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
												.getValue() == 1 ? "_baselineUsed" : ""
										: (defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection
												.ordinal()] == 1 ? "_baselineUsed" : ""))
						: "")

				+ // Dimensions of the map
				"_" + world.getGameMap().getDimX() + "x" + world.getGameMap().getDimY();

		return nombreSimulacion;
	}

	/**
	 * Set a Learner with the parameters specified
	 * @param totalTasks
	 * @param totalEpisodes
	 * @param learningMethod
	 * @param selectionMethod
	 * @param learningRate
	 * @param discountedFactor
	 * @param lambdaBackup
	 * @param epsilonSelection
	 * @param cExploratoryParameterUCB
	 * @param temperatureSoftmax
	 * @param baselineUsedSoftmax
	 * @param nombreParametro1
	 * @param nombreParametro2
	 * @param world
	 * @param experimentParameterValues1
	 * @param indiceParametro1
	 * @param experimentParameterValues2
	 * @param indiceParametro2
	 * @param nombreSimulacion
	 * @return
	 */
	public static RLearner settingLearner(LEARNING_METHOD learningMethod, SELECTION_METHOD selectionMethod,
			RLWorld world, Map<String, Parametrizacion> parametrizaciones, double[] defaultLearningParameterValues,
			String nombreSimulacion, boolean takeOnlyIntoAccountInRewardCalculationsLastRewardOfTheEpisode,
			boolean crearGraficasInternas, double epsilonFinal) {
		RLearner learner = new RLearner(nombreSimulacion, world);
		learner.setTasks((int) defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalTasks.ordinal()]);
		learner.setTotalEpisodes(
				(int) defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.totalEpisodes.ordinal()]);

		learner.setActionSelectionMethod(selectionMethod);

		learner.setLearningMethod(learningMethod);

		learner.setAlpha((parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.alphaLearningRate.name())
				? parametrizaciones.get(RLearner.LEARNING_PARAMETER.alphaLearningRate.name()).getValue()
				: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.alphaLearningRate.ordinal()])); // for q-learning method (=1 for deterministic environments)

		learner.setGamma((parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name())
				? parametrizaciones.get(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name()).getValue()
				: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.gammaDiscountFactor.ordinal()])); // for q-learning method (=0 if only considering current rewards)

		learner.setLambda(parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.lambdaSteps.name())
				? parametrizaciones.get(RLearner.LEARNING_PARAMETER.lambdaSteps.name()).getValue()
				: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.lambdaSteps.ordinal()]);//learner.setLambda(0.001); // for TD(lambda) methods (=0 then we have a 1-step TD backup, = 1 then we have a Monte Carlo backup)

		learner.setEpsilon((parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.epsilonSelection.name())
				? parametrizaciones.get(RLearner.LEARNING_PARAMETER.epsilonSelection.name()).getValue()
				: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.epsilonSelection.ordinal()]));

		learner.setEpsilonRange(
				(parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.epsilonSelection.name())
						? parametrizaciones.get(RLearner.LEARNING_PARAMETER.epsilonSelection.name()).getValue()
						: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.epsilonSelection.ordinal()]),
				(selectionMethod.name().equals(SELECTION_METHOD.E_GREEDY_CHANGING_TEMPORALLY.name()) ? epsilonFinal
						: parametrizaciones.get(RLearner.LEARNING_PARAMETER.epsilonSelection.name()).getValue()));

		learner.setC(parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.c_UCBSelection.name())
				? parametrizaciones.get(RLearner.LEARNING_PARAMETER.c_UCBSelection.name()).getValue()
				: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.c_UCBSelection.ordinal()]);

		learner.setTemperature(
				parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name())
						? parametrizaciones.get(RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name())
								.getValue()
						: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection
								.ordinal()]);

		learner.setBaselineUsed(
				parametrizaciones.keySet().contains(RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
						? parametrizaciones.get(RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
								.getValue() == 1
						: defaultLearningParameterValues[RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection
								.ordinal()] == 1);

		//		learner.getOnlyLastReward = takeOnlyIntoAccountInRewardCalculationsLastRewardOfTheEpisode;
		//		learner.crearGraficasInternas = crearGraficasInternas;

		return learner;
	}

	// ============================================================================================================
	// ============================================================================================================
	// OLD METHODS THAT ARE STILL USED BUT SHOULD BE REPLACED BY THE NEW ONES
	// ============================================================================================================
	// ============================================================================================================
	/**
	 * Generate based on all the parameters the learner may received the name of the file
	 * @param totalTasks
	 * @param totalEpisodes
	 * @param learningMethod
	 * @param selectionMethod
	 * @param learningRate
	 * @param discountedFactor
	 * @param lambdaBackup
	 * @param epsilonSelection
	 * @param cExploratoryParameterUCB
	 * @param temperatureSoftmax
	 * @param baselineUsedSoftmax
	 * @param nombreParametro1
	 * @param nombreParametro2
	 * @param world
	 * @param experimentParameterValues1
	 * @param indiceParametro1
	 * @param experimentParameterValues2
	 * @param indiceParametro2
	 * @return
	 */
	public static String generateFileName(final int totalTasks, final int totalEpisodes, LEARNING_METHOD learningMethod,
			SELECTION_METHOD selectionMethod, final double learningRate, final double discountedFactor,
			final double lambdaBackup, final double epsilonSelection, final double cExploratoryParameterUCB,
			final double temperatureSoftmax, final boolean baselineUsedSoftmax, String nombreParametro1,
			String nombreParametro2, RLWorld world, double[] experimentParameterValues1, int indiceParametro1,
			double[] experimentParameterValues2, int indiceParametro2) {
		String worldName = world.getClass().toString().substring(world.getClass().toString().lastIndexOf(".") + 1);

		String nombreSimulacion = worldName + "_" + learningMethod.name() + "_" + selectionMethod.name() + "_t"
				+ totalTasks + "_ep" + totalEpisodes

				+ "_lRate"
				// They attribute may be a parameter or a constant value on the experiment
				+ (nombreParametro1.equals(RLearner.LEARNING_PARAMETER.alphaLearningRate.name())
						? experimentParameterValues1[indiceParametro1]
						: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.alphaLearningRate.name())
								? experimentParameterValues2[indiceParametro2] : learningRate)))

				+ "_dFactor"
				// They attribute may be a parameter or a constant value on the experiment
				+ (nombreParametro1.equals(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name())
						? experimentParameterValues1[indiceParametro1]
						: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name())
								? experimentParameterValues2[indiceParametro2] : discountedFactor)))

				+ // If a lambda method is selected the name of the file will contain such atttribute
				(learningMethod.name()
						.equals(LEARNING_METHOD.Q_LEARNING_LAMBDA
								.name())
						|| learningMethod.name().equals(LEARNING_METHOD.SARSA_LAMBDA.name())
								// They attribute may be a parameter or a constant value on the experiment
								? "_backup" + (nombreParametro1.equals(RLearner.LEARNING_PARAMETER.lambdaSteps.name())
										? experimentParameterValues1[indiceParametro1]
										: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.lambdaSteps.name())
												? experimentParameterValues2[indiceParametro2] : lambdaBackup)))
								: "")

				+ // If a eGreedy selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.E_GREEDY.name())
						|| selectionMethod.name().equals(SELECTION_METHOD.E_GREEDY_CHANGING_TEMPORALLY.name())
								// They attribute may be a parameter or a constant value on the experiment
								? "_eG" + (nombreParametro1.equals(RLearner.LEARNING_PARAMETER.epsilonSelection.name())
										? experimentParameterValues1[indiceParametro1]
										: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.epsilonSelection.name())
												? experimentParameterValues2[indiceParametro2] : epsilonSelection)))
								: "")

				+ // If a UCB selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.UCB.name())
						// They attribute may be a parameter or a constant value on the experiment
						? "_cUCB" + (nombreParametro1.equals(RLearner.LEARNING_PARAMETER.c_UCBSelection.name())
								? experimentParameterValues1[indiceParametro1]
								: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.c_UCBSelection.name())
										? experimentParameterValues2[indiceParametro2] : cExploratoryParameterUCB)))
						: "")

				+ // If a softmax selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.SOFT_MAX.name())
						// They attribute may be a parameter or a constant value on the experiment
						? "_temperature"
								+ (nombreParametro1
										.equals(RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name())
												? experimentParameterValues1[indiceParametro1]
												: ((nombreParametro2.equals(
														RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name())
																? experimentParameterValues2[indiceParametro2]
																: temperatureSoftmax)))
						: "")

				+ // If a softmax selection method is selected the name of the file will contain such atttribute
				(selectionMethod.name().equals(SELECTION_METHOD.SOFT_MAX.name())
						// They attribute may be a parameter or a constant value on the experiment
						? "_temperature"
								+ (nombreParametro1
										.equals(RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
												? experimentParameterValues1[indiceParametro1]
												: ((nombreParametro2.equals(
														RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
																? experimentParameterValues2[indiceParametro2]
																: baselineUsedSoftmax)))
						: "");
		return nombreSimulacion;
	}

	/**
	 * Set a Learner with the parameters specified
	 * @param totalTasks
	 * @param totalEpisodes
	 * @param learningMethod
	 * @param selectionMethod
	 * @param learningRate
	 * @param discountedFactor
	 * @param lambdaBackup
	 * @param epsilonSelection
	 * @param cExploratoryParameterUCB
	 * @param temperatureSoftmax
	 * @param baselineUsedSoftmax
	 * @param nombreParametro1
	 * @param nombreParametro2
	 * @param world
	 * @param experimentParameterValues1
	 * @param indiceParametro1
	 * @param experimentParameterValues2
	 * @param indiceParametro2
	 * @param nombreSimulacion
	 * @return
	 */
	public static RLearner settingLearner(final int totalTasks, final int totalEpisodes, LEARNING_METHOD learningMethod,
			SELECTION_METHOD selectionMethod, final double learningRate, final double discountedFactor,
			final double lambdaBackup, final double epsilonSelection, final double cExploratoryParameterUCB,
			final double temperatureSoftmax, final boolean baselineUsedSoftmax, String nombreParametro1,
			String nombreParametro2, RLWorld world, double[] experimentParameterValues1, int indiceParametro1,
			double[] experimentParameterValues2, int indiceParametro2, String nombreSimulacion) {
		RLearner learner = new RLearner(nombreSimulacion, world);
		learner.setTasks(totalTasks);
		learner.setTotalEpisodes(totalEpisodes);
		learner.setEpsilon(epsilonSelection);
		learner.setActionSelectionMethod(selectionMethod);
		learner.setLearningMethod(learningMethod);

		learner.setAlpha((nombreParametro1.equals(RLearner.LEARNING_PARAMETER.alphaLearningRate.name())
				? experimentParameterValues1[indiceParametro1]
				: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.alphaLearningRate.name())
						? experimentParameterValues2[indiceParametro2] : learningRate)))); // for q-learning method (=1 for deterministic environments)

		learner.setGamma((nombreParametro1.equals(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name())
				? experimentParameterValues1[indiceParametro1]
				: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.gammaDiscountFactor.name())
						? experimentParameterValues2[indiceParametro2] : discountedFactor)))); // for q-learning method (=0 if only considering current rewards)

		learner.setLambda((nombreParametro1.equals(RLearner.LEARNING_PARAMETER.lambdaSteps.name())
				? experimentParameterValues1[indiceParametro1]
				: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.lambdaSteps.name())
						? experimentParameterValues2[indiceParametro2] : lambdaBackup))));//learner.setLambda(0.001); // for TD(lambda) methods (=0 then we have a 1-step TD backup, = 1 then we have a Monte Carlo backup)

		learner.setEpsilonRange((nombreParametro1.equals(RLearner.LEARNING_PARAMETER.epsilonSelection.name())
				? experimentParameterValues1[indiceParametro1]
				: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.epsilonSelection.name())
						? experimentParameterValues2[indiceParametro2] : epsilonSelection))),
				0);

		learner.setC((nombreParametro1.equals(RLearner.LEARNING_PARAMETER.c_UCBSelection.name())
				? experimentParameterValues1[indiceParametro1]
				: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.c_UCBSelection.name())
						? experimentParameterValues2[indiceParametro2] : cExploratoryParameterUCB))));

		learner.setTemperature(
				(nombreParametro1
						.equals(RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name())
								? experimentParameterValues1[indiceParametro1]
								: ((nombreParametro2
										.equals(RLearner.LEARNING_PARAMETER.temperatureSoftmaxSelection.name())
												? experimentParameterValues2[indiceParametro2] : temperatureSoftmax))));

		learner.setBaselineUsed(
				(nombreParametro1.equals(RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
						? experimentParameterValues1[indiceParametro1] == 1.0
						: ((nombreParametro2.equals(RLearner.LEARNING_PARAMETER.baselineUsedSoftmaxSelection.name())
								? experimentParameterValues2[indiceParametro2] == 1.0 : baselineUsedSoftmax))));
		return learner;
	}

}
