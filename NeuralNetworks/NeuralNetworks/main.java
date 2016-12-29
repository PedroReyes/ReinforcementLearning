package NeuralNetworks;

import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import AuxiliarNeuralNetwork.NeuralHelper;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Input data (hours sleep, hours study)
		double scalar = 10.0;
		//		RealMatrix InputData = MatrixUtils.createRealMatrix(new double[][] { { 3.0, 5.0 }, { 5.0, 1.0 }, { 10.0, 2.0 } });
		RealMatrix InputData = MatrixUtils.createRealMatrix(new double[][] { { 1.0 }, { 2.0 }, { 3.0 }, { 4.0 },
				{ 5.0 }, { 6.0 }, { 7.0 }, { 8.0 }, { 9.0 }, { 10.0 } });

		// Scaling the input data
		InputData = MatrixUtils.createRealMatrix(InputData.scalarMultiply(1.0 / scalar).getData());// = InputData.scalarMultiply(scalar);//);NeuralHelper.getMaxValue(InputData));
		System.out.println("Input data: " + InputData);

		// Output (test score)
		RealMatrix OutputData = MatrixUtils.createRealMatrix(InputData.getData());
		//		RealMatrix OutputData = MatrixUtils.createRealMatrix(new double[][] { { 30.0 }, { 50.0 }, { 100.0 } });
		//		RealMatrix OutputData = MatrixUtils.createRealMatrix(new double[][] { { 1.0 }, { 3.0 }, { 5.0 }, { 10.0 } });
		//		OutputData = MatrixUtils.createRealMatrix(new double[][] { { 100 } });

		// Scaling the output data
		//		OutputData = OutputData.scalarMultiply(1.0 / 10.0);
		System.out.println("Output data: " + OutputData);

		// ARTIFICAL NEURAL NETWORK
		ArtificialNeuralNetwork nn = new ArtificialNeuralNetwork(
				new int[] { InputData.getColumnDimension(), 10, OutputData.getColumnDimension() });
		nn.epochs = 1000;
		nn.addingBias = false;
		nn.learningRate = 1.0;
		nn.showUI = true;
		//		nn.weightDecay = 0.00000001;

		boolean debugCostFunction = false;
		boolean debugNumericalChecking = true;
		boolean debugTraining = true;

		// This is for testing if the ANN is correctly implemented
		if (debugNumericalChecking) {
			// Example of Numerical Checking
			if (nn.numericalChecking(InputData, OutputData))
				System.out.println("YES, NUMERICAL CHECKING PROVED");
			else
				System.err.println("NO NUMERICAL CHECKING PROVED");
		}

		if (debugTraining) {
			// =============================
			// Predictions
			// =============================
			System.out.println("=========================================");
			System.out.println("Prediction: " + nn.feedForward(InputData).scalarMultiply(scalar));

			for (int i = 0; i <= nn.numberOfLayers; i++) {
				System.out.println("W" + i + ": " + nn.weightsPerLayer[i]);
				System.out.println("b" + i + ": " + nn.biasPerLayer[i]);
			}

			// =============================
			nn.train(InputData, OutputData);
			// =============================

			// =============================
			// Predictions
			// =============================
			System.out.println("=========================================");
			System.out.println("Prediction: " + nn.feedForward(InputData).scalarMultiply(scalar));
			for (int i = 0; i <= nn.numberOfLayers; i++) {
				System.out.println("W" + i + ": " + nn.weightsPerLayer[i]);
				System.out.println("b" + i + ": " + nn.biasPerLayer[i]);
			}

			System.out.println("=========================================");
			System.out.println("Prediction for a new value: " + nn.feedForward(
					MatrixUtils.createRealMatrix(new double[][] { { 8.0 } }).scalarMultiply(1.0 / scalar)));
		}

		if (debugCostFunction) {

			// Scalar
			double learningRate = 3; // alpha

			// Getting errors between our predictions and the real output
			double costFunction1 = nn.costFunction(InputData, OutputData);
			System.out.println("Cost function 1: " + costFunction1);

			// Adding cost function
			List<RealMatrix[]> costFunctionPrime = nn.costFunctionPrime(InputData, OutputData);

			// Subtracting cost function so we are minimizing the error
			costFunctionPrime = nn.costFunctionPrime(InputData, OutputData);
			for (int i = 0; i <= nn.numberOfLayers; i++) {
				// Weights modification
				nn.weightsPerLayer[i] = nn.weightsPerLayer[i]
						.add(costFunctionPrime.get(ArtificialNeuralNetwork.WEIGHT_GRADIENTS)[i]
								.scalarMultiply(-learningRate));

				// Bias modification
				nn.biasPerLayer[i] = nn.biasPerLayer[i].add(
						costFunctionPrime.get(ArtificialNeuralNetwork.BIAS_GRADIENTS)[i].scalarMultiply(-learningRate));
			}

			// Getting errors between our predictions and the real output
			double costFunction3 = nn.costFunction(InputData, OutputData);
			System.out.println("Cost function 3: " + costFunction3);

		}

		// Checking with some new numbers
		int splits = 20;
		for (int i = 0; i < splits; i++) {
			double split = NeuralHelper.getMaxValue(InputData) * ((double) i) / ((double) splits); // division
			RealMatrix newInput = MatrixUtils.createRealMatrix(new double[][] { { split } }); // input
			double prediction = nn.feedForward(newInput).getEntry(0, 0) * scalar; // prediction

			//			System.out.println(i + "(" + split + ")" + " = " + prediction);
			System.out.println(prediction);
		}
	}

}
