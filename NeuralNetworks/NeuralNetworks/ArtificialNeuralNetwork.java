package NeuralNetworks;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import Analysis.XYPlot;
import Auxiliar.Helper;
import AuxiliarNeuralNetwork.NeuralHelper;
import processing.core.PApplet;

/**
 * Ver videos en https://www.youtube.com/watch?v=UJwK6jAStmg&index=2&list=PLiaHhY2iBX9hdHaRr6b7XevZtgZRa1PoU
 * 
 * Old name of this class: NeuralNetworkWithOneSingleHiddenLayer
 * 
 * @author pedro
 *
 */

public class ArtificialNeuralNetwork {

	// Input parameters of the constructor
	int[] neuronsPerLayer; // number of input neurons, number of neurons of hidden layer 1, 2, 3 ... , number of output neurons

	// CONSTANTS
	public static final int WEIGHT_GRADIENTS = 0;
	public static final int BIAS_GRADIENTS = 1;

	// Global parameters
	int inputLayerSize;
	int outputLayerSize;
	int numberOfLayers;

	// This boolean add some code that is has not been extracted from https://www.youtube.com/playlist?list=PLiaHhY2iBX9hdHaRr6b7XevZtgZRa1PoU
	// In it's place the code that has been added has been extracted from http://ufldl.stanford.edu/wiki/index.php/Backpropagation_Algorithm
	boolean addingNumberOfSamples = true;
	boolean addingWeightDecay = true;
	boolean addingBias = true;

	// Weights (feedforward variables)
	RealMatrix[] weightsPerLayer;

	// Auxiliary variables for forward propagation (feedforward and backforward variables)
	public RealMatrix[] z; // activation (also called activity) of the neurons
	public RealMatrix[] a; // activation (also called transfer) function of the neurons
	public RealMatrix[] biasPerLayer; // (NOT IMPLEMENTED YET) bias unit = intercept term "b" in http://ufldl.stanford.edu/wiki/index.php/Neural_Networks

	// Training variables
	public int epochs = 1000; // typical value : 5000000
	public double learningRate = 0.3; // typical value: 0.3
	public double weightDecay = 0.0; // regularization term (also called weight decay term) 
	// that tends to decrease the magnitude of the weights, and helps prevent overfitting.

	// Auxiliary variables
	public boolean showUI = false;

	public ArtificialNeuralNetwork(int[] neuronsPerLayer) {
		// Recording fixed structure
		this.neuronsPerLayer = neuronsPerLayer;

		// Given the fixed structure, the network is reset
		resetNeuralNetwork(neuronsPerLayer);
	}

	/**
	 * Set the given structure as the one used in the neural network
	 * @param neuronsPerLayer
	 */
	private void resetNeuralNetwork(int[] neuronsPerLayer) {
		// Setting fixed structure
		this.inputLayerSize = neuronsPerLayer[0];//2;
		this.outputLayerSize = neuronsPerLayer[neuronsPerLayer.length - 1];//1;
		this.numberOfLayers = neuronsPerLayer.length - 2;

		// Hyperparameters
		this.z = new RealMatrix[numberOfLayers + 1];
		this.a = new RealMatrix[numberOfLayers + 1];

		// We set all the weights and bias among the input and the hidden layer to the same random value
		this.weightsPerLayer = new RealMatrix[numberOfLayers + 1]; // weights of the connections among layers
		biasPerLayer = new RealMatrix[numberOfLayers + 1]; // Bias units of the neurons

		for (int layer = 0; layer < numberOfLayers + 1; layer++) {
			// Weights
			weightsPerLayer[layer] = NeuralHelper.getRandomMatrix(neuronsPerLayer[layer], neuronsPerLayer[layer + 1]);

			// Bias
			biasPerLayer[layer] = NeuralHelper.getMatrixFilledWith(1, neuronsPerLayer[layer + 1], 0.0);
		}
	}

	// ====================================================================
	// INPUT DATA FORWARD PROPAGATION
	// ====================================================================
	/**
	 * Input data to forward over the neural network
	 * @param inputData
	 * @return
	 */
	public RealMatrix feedForward(RealMatrix inputData) {
		// Calculus of the predictions
		for (int layer = 0; layer <= numberOfLayers; layer++) {
			// Activity of the layer
			z[layer] = (layer == 0 ? inputData : a[layer - 1]).multiply(weightsPerLayer[layer]);

			if (addingBias) {

				// Getting the bias units duplicate to achieve right matrix sums for each sample
				RealMatrix layerBias = NeuralHelper.duplicate(biasPerLayer[layer], inputData.getRowDimension());

				// Adding the bias units
				z[layer] = z[layer].add(layerBias);
			}

			// Activation function of the layer
			a[layer] = sigmoid(z[layer]);
		}

		return a[numberOfLayers]; // == yHat
	}

	// ====================================================================
	// ACTIVATION FUNCTION (SIGMOID)
	// ====================================================================
	/**
	 * z is the sum of all the values in a matrix of [3x1] 
	 * @param z
	 * @return
	 */
	public RealMatrix sigmoid(RealMatrix z) {
		RealMatrix copyZ = MatrixUtils.createRealMatrix(z.getData());

		// Applying activation function to every every value of the matrix
		for (int row = 0; row < copyZ.getRowDimension(); row++) {
			for (int column = 0; column < copyZ.getColumnDimension(); column++) {
				// sigmoid function = [1]/[1+e^(-z)]
				double valueApplyingActivationFunction = 1 / (1 + Math.exp(-copyZ.getEntry(row, column)));
				copyZ.setEntry(row, column, valueApplyingActivationFunction);
			}
		}

		return copyZ;
	}

	/**
	 * derivative of sigmoid function
	 * @param z
	 * @return
	 */
	public RealMatrix sigmoidPrime(RealMatrix z) {
		RealMatrix copyZ = MatrixUtils.createRealMatrix(z.getData());
		double c = 0;

		// Derivative of sigmoid function
		for (int row = 0; row < copyZ.getRowDimension(); row++) {
			for (int column = 0; column < copyZ.getColumnDimension(); column++) {
				// derivative sigmoid function = [e^(-z)]/[(1+e^(-z))^2]
				c = copyZ.getEntry(row, column);
				double valueApplyingActivationFunction = Math.exp(-c) / (Math.pow(1 + Math.exp(-c), 2));
				copyZ.setEntry(row, column, valueApplyingActivationFunction);
			}
		}

		return copyZ;
	}

	// ====================================================================
	// COST FUNCTION (SQUARE SUM ERROR)
	// ====================================================================
	/**
	 * Given the real output and the predictions, based on the cost function and current weights, return the error
	 * 
	 * @param OutputData
	 * @param predictions
	 * @return
	 */
	public double costFunction(RealMatrix InputData, RealMatrix OutputData) {
		// TODO Auto-generated method stub
		double J = 0;
		double averageErrorTerm = 0; // first term: average sum-of-squares error term
		double regularizationTerm = 0; // second term: for preventing overfitting

		// Predictions
		RealMatrix predictions = feedForward(InputData);
		//		System.out.println("Predictions in cost function: " + predictions);

		// Checking that dimensions are right
		if ((OutputData.getRowDimension() > 1 && OutputData.getColumnDimension() > 1)
				|| (OutputData.getRowDimension() != predictions.getRowDimension()
						|| OutputData.getColumnDimension() != predictions.getColumnDimension())) {
			throw new IllegalArgumentException(
					"Error: the real output and the predictions must be equal in dimension terms");
		}

		// COST
		// First term - average sum-of-squares error term
		for (int row = 0; row < OutputData.getRowDimension(); row++) {
			for (int column = 0; column < OutputData.getColumnDimension(); column++) {
				//double error = Math.pow(realOutputData.getEntry(row, column) - predictions.getEntry(row, column), 2.0);
				double error = Math.pow(OutputData.getEntry(row, column) - predictions.getEntry(row, column), 2.0)
						/ 2.0;

				averageErrorTerm = averageErrorTerm + error;
			}
		}

		// > Divided by the number of samples to get the average square error cost function
		if (addingNumberOfSamples)
			averageErrorTerm = averageErrorTerm / (double) InputData.getRowDimension(); // RECENTLY ADDED

		// Second term - regularization term
		if (addingWeightDecay) {
			for (int layer = 0; layer < numberOfLayers + 1; layer++) {
				RealMatrix synapsesWeights = weightsPerLayer[layer];
				for (int row = 0; row < synapsesWeights.getRowDimension(); row++) {
					for (int column = 0; column < synapsesWeights.getColumnDimension(); column++) {
						regularizationTerm = regularizationTerm + Math.pow(synapsesWeights.getEntry(row, column), 2);
					}
				}
			}

			regularizationTerm = (weightDecay / 2) * regularizationTerm;

			J = averageErrorTerm + regularizationTerm;
		}

		J = averageErrorTerm + regularizationTerm;

		return J;
	}

	/**
	 * Given the real input X and the real output Y return the derivative of the cost function. This is
	 * how much the weights of the synapses must change to get closer to the optimal. In other words, this
	 * method is the one that carry out the BACKPROPAGATION effect. To do this we establish the partial
	 * derivative of J (loss function) with respect to the W (synapse weights).
	 * 
	 * oldName of the method = costFunctionPrime feedBackward
	 * @param InputData
	 * @param y
	 * @return
	 */
	public List<RealMatrix[]> costFunctionPrime(RealMatrix InputData, RealMatrix y) {
		// Auxiliary variables for backward propagation (backforward variables)
		RealMatrix[] delta; // back-propagated error of a particular layer 
		RealMatrix[] dJdW; // partial derivative of the loss function with respect to each layer of weights
		RealMatrix[] dJdb; // partial derivative of the loss function with respect to each layer of bias units

		// Initialize the auxiliary variables
		delta = new RealMatrix[numberOfLayers + 1];
		dJdW = new RealMatrix[numberOfLayers + 1];
		dJdb = new RealMatrix[numberOfLayers + 1];

		// Getting the predictions made by current weighs
		RealMatrix yHat = feedForward(InputData);

		// Calculus of the derivative of J with respect to the derivative of W (dJ/dW)
		for (int layer = numberOfLayers; layer >= 0; layer--) {
			// > Back-propagated error of the layer
			if (layer == numberOfLayers) {
				delta[layer] = NeuralHelper.getScalarMultiplication((y.subtract(yHat).scalarMultiply(-1)),
						((RealMatrix) sigmoidPrime(z[layer])));
			} else {
				delta[layer] = NeuralHelper.getScalarMultiplication(
						delta[layer + 1].multiply(weightsPerLayer[layer + 1].transpose()),
						((RealMatrix) sigmoidPrime(z[layer])));
			}

			// > Gradient - change of J with respect to W
			if (layer == 0)
				dJdW[layer] = InputData.transpose().multiply(delta[layer]);
			else
				dJdW[layer] = a[layer - 1].transpose().multiply(delta[layer]);

			// > Divided by the number of samples to get the average square error cost function derivative
			if (addingNumberOfSamples) {
				dJdW[layer] = dJdW[layer].scalarMultiply(1.0 / (double) InputData.getRowDimension());
			}

			// > Gradient - change of J with respect to W adding second term
			if (addingWeightDecay) {
				dJdW[layer] = dJdW[layer].add(weightsPerLayer[layer].scalarMultiply(weightDecay));
			}

			// > Gradient - change of J with respect to b (bias units)
			if (addingBias) {
				// Getting the bias values of the current layer
				dJdb[layer] = NeuralHelper.getMatrixFilledWith(1, neuronsPerLayer[layer + 1], 0.0);

				// Calculating bias based on the average of all the delta values
				RealMatrix averageBiasValues = MatrixUtils.createRealMatrix(1, delta[layer].getColumnDimension());

				for (int neuronIndex = 0; neuronIndex < neuronsPerLayer[layer + 1]; neuronIndex++) {
					double average = 0.0;
					for (int sampleIndex = 0; sampleIndex < InputData.getRowDimension(); sampleIndex++) {
						average = average + delta[layer].getEntry(sampleIndex, neuronIndex);
					}
					averageBiasValues.setEntry(0, neuronIndex, average / (double) InputData.getRowDimension());
				}

				dJdb[layer] = averageBiasValues;
			}
		}

		// Setting the result which will contain the gradients of the bias and the weights
		List<RealMatrix[]> gradients = new LinkedList<RealMatrix[]>();
		gradients.add(dJdW); // gradients to be applied over the synapses
		gradients.add(dJdb); // gradients to be applied over the bias

		return gradients;
	}

	// ====================================================================
	// NUMERICAL CHECKING
	// ====================================================================
	/**
	 * Simple example of numerical checking (just for give an idea of what numerical checking means)
	 * @return
	 */
	public boolean numericalChecking(RealMatrix InputData, RealMatrix OutputData) {
		// Threshold to be confirmed the numerical checking
		double threshold = 1e-08;

		// Value using limits definition -> (f(x + epsilon) - f(x - epsilon)) / (2 * epsilon);
		RealMatrix numericalGradients = computeNumericalGradients(InputData, OutputData);

		// Value using transform rules of derivatives -> fPrime(x);
		RealMatrix symbolicGradients = computeGradients(InputData, OutputData);

		// Both values should be quite equals
		double difference = ((symbolicGradients.subtract(numericalGradients)).getNorm())
				/ ((symbolicGradients.add(numericalGradients)).getNorm());

		System.out.println("Numerical Gradients: " + numericalGradients);
		System.out.println("Derivative Gradients:" + symbolicGradients);
		System.out.println(difference);

		return difference < threshold;
	}

	/**
	 * Computing numerical checking is a way to check that our calculus are correct. This is made using 
	 * the definition of limits about derivatives. So if the value of the definition is close to the value
	 * of our calculus then we are on business.
	 * @param InputData
	 * @param OutputData
	 * @return
	 */
	public RealMatrix computeNumericalGradients(RealMatrix InputData, RealMatrix OutputData) {
		RealMatrix initialWeights = getSynapseWeights(weightsPerLayer);
		RealMatrix numericGradients = MatrixUtils.createRealMatrix(1, initialWeights.getColumnDimension());
		RealMatrix perturbations = MatrixUtils.createRealMatrix(1, initialWeights.getColumnDimension());
		double epsilon = 1e-04; // 0.0001

		for (int synapseIndex = 0; synapseIndex < initialWeights.getColumnDimension(); synapseIndex++) {
			// Set perturbation vector
			perturbations.setEntry(0, synapseIndex, epsilon);

			// y2
			setSynapseWeights(initialWeights.add(perturbations));
			double loss2 = costFunction(InputData, OutputData);

			// y1
			setSynapseWeights(initialWeights.subtract(perturbations));
			double loss1 = costFunction(InputData, OutputData);

			// Compute numerical gradient
			numericGradients.setEntry(0, synapseIndex, (loss2 - loss1) / (2 * epsilon));

			// Return the value we changed to zero
			perturbations = perturbations.scalarMultiply(0);
		}

		// Return weights to original value
		setSynapseWeights(initialWeights);

		return numericGradients;
	}

	// ====================================================================
	// TRAINING
	// ====================================================================
	public void train(RealMatrix InputData, RealMatrix OutputData) {
		// Auxiliary variable for adding cost function
		List<RealMatrix[]> costFunctionPrime;

		// Auxiliary list to store cost
		List<Float> epochsIndex = new LinkedList<Float>();
		List<Float> costsPerEpoch = new LinkedList<Float>();
		float cost = 0;
		for (int i = 0; i < epochs; i++) {
			costFunctionPrime = costFunctionPrime(InputData, OutputData);

			// Adding the cost of each weight and bias of the neural network
			for (int j = 0; j < weightsPerLayer.length; j++) {
				// Synapses weights
				if (addingWeightDecay) {
					RealMatrix aux = costFunctionPrime.get(WEIGHT_GRADIENTS)[j]
							.add(weightsPerLayer[j].scalarMultiply(weightDecay));
					aux = aux.scalarMultiply(learningRate);
					weightsPerLayer[j] = weightsPerLayer[j].subtract(aux);
				} else {
					RealMatrix aux = costFunctionPrime.get(WEIGHT_GRADIENTS)[j].scalarMultiply(learningRate);
					weightsPerLayer[j] = weightsPerLayer[j].subtract(aux);
				}

				if (addingBias) {
					// Neuron bias
					biasPerLayer[j] = biasPerLayer[j]
							.subtract(costFunctionPrime.get(BIAS_GRADIENTS)[j].scalarMultiply(learningRate));
				}
			}

			// Cost of the current weights
			cost = (float) costFunction(InputData, OutputData);

			// X and Y to be plot in case that this option is selected
			costsPerEpoch.add(cost);
			epochsIndex.add((float) i);
		}

		// Plotting the results
		if (showUI) {
			XYPlot chart = new XYPlot(Helper.listToArray(epochsIndex), Helper.listToArray(costsPerEpoch));
			chart.xLabel = "Epoch";
			chart.yLabel = "Cost";
			chart.title = "Artificial Neural Network";
			chart.subtitle = "Relation cost-epoch";
			String[] a = { "MAIN" };
			PApplet.runSketch(a, chart);
		}
	}

	// ====================================================================
	// HELPER FUNCTIONS FOR INTERACTING WITH OTHER CLASSES
	// ====================================================================
	/**
	 * Convert an array of RealMatrix's into a single array
	 * @param arrayOfRealMatrix
	 * @return
	 */
	public RealMatrix getSynapseWeights(RealMatrix[] arrayOfRealMatrix) {
		// Getting number of synapses
		int numberOfSynapses = 0;
		for (int layer = 0; layer < arrayOfRealMatrix.length; layer++) {
			numberOfSynapses = numberOfSynapses
					+ (arrayOfRealMatrix[layer].getRowDimension() * arrayOfRealMatrix[layer].getColumnDimension());
		}

		// Get W1 and W2 Rolled into vector:
		RealMatrix synapsesWeights = MatrixUtils.createRealMatrix(1, numberOfSynapses);//new double[numberOfSynapses];
		int index = 0;
		for (int layer = 0; layer < arrayOfRealMatrix.length; layer++) {
			RealMatrix layerSynapses = arrayOfRealMatrix[layer];
			for (int row = 0; row < layerSynapses.getRowDimension(); row++) {
				for (int column = 0; column < layerSynapses.getColumnDimension(); column++) {
					synapsesWeights.setEntry(0, index, layerSynapses.getEntry(row, column));
					index++;
				}
			}
		}

		return synapsesWeights;
	}

	/**
	 * It is a single array that will be copy into the Matrix of each layer of weights
	 * @param arrayOfValues
	 */
	public void setSynapseWeights(RealMatrix arrayOfValues) {
		int index = 0;
		// Setting the value of the synaptic weights of the different layers
		for (int layer = 0; layer < weightsPerLayer.length; layer++) {
			// Setting synapse
			for (int synapseX = 0; synapseX < weightsPerLayer[layer].getRowDimension(); synapseX++) {
				for (int synapseY = 0; synapseY < weightsPerLayer[layer].getColumnDimension(); synapseY++) {
					weightsPerLayer[layer].setEntry(synapseX, synapseY, arrayOfValues.getEntry(0, index));
					index++;
				}
			}
		}
	}

	/**
	 * Get the change of J (loss function) with respect W (weights of the synapses)
	 * @param InputData
	 * @param OutputData
	 * @return
	 */
	public RealMatrix computeGradients(RealMatrix InputData, RealMatrix OutputData) {
		return getSynapseWeights(costFunctionPrime(InputData, OutputData).get(WEIGHT_GRADIENTS));
	}

}
