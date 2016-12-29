package NeuralNetworks;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import AuxiliarNeuralNetwork.NeuralHelper;

/**
 * A main where a neural network is trained to learn a linear function
 * 
 * @author pedro
 *
 */
public class SimplifiedMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Input data (hours sleep, hours study)
		double scalar = 10.0;
		RealMatrix InputData = MatrixUtils.createRealMatrix(new double[][] { { 1.0 }, { 2.0 }, { 3.0 }, { 4.0 },
				{ 5.0 }, { 6.0 }, { 7.0 }, { 8.0 }, { 9.0 }, { 10.0 } });

		// Scaling the input data
		InputData = MatrixUtils.createRealMatrix(InputData.scalarMultiply(1.0 / scalar).getData());// = InputData.scalarMultiply(scalar);//);NeuralHelper.getMaxValue(InputData));
		System.out.println("Input data: " + InputData);

		// Output (test score)
		RealMatrix OutputData = MatrixUtils.createRealMatrix(InputData.getData());
		System.out.println("Output data: " + OutputData);

		// ARTIFICAL NEURAL NETWORK
		ArtificialNeuralNetwork nn = new ArtificialNeuralNetwork(
				new int[] { InputData.getColumnDimension(), 10, OutputData.getColumnDimension() });
		nn.epochs = 10000;
		nn.learningRate = 1.0;

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

		// Checking predictions
		int splits = 20;
		for (int i = 0; i < splits; i++) {

			double split = NeuralHelper.getMaxValue(InputData) * i / splits;
			RealMatrix newInput = MatrixUtils.createRealMatrix(new double[][] { { split } });
			double prediction = nn.feedForward(newInput).getEntry(0, 0) * scalar;
			//			chart.addPoint((float) split, (float) prediction);

			System.out.println(split + " = " + prediction);
		}

	}

}
