package NeuralNetworks;

import org.apache.commons.math3.linear.RealMatrix;

public class TRASH {
	private RealMatrix[] extracted(RealMatrix InputData, RealMatrix OutputData) {
		{
			//			// ============================================================
			//			// COMPUTING COST FUNCTION DERIVATIVE WITH RESPECT TO W1 AND W2
			//			// ============================================================
			//			// Getting the predictions made by current weighs
			//			RealMatrix yHat = forward(InputData);
			//
			//			// =================
			//			// UPDATING THE W_2
			//			// =================
			//			// > delta3 = -(yHat-y)*(sigmoidPrime(z3)) = propagated error of the left side of the output layer
			//			RealMatrix delta3 = NeuralHelper.getScalarMultiplication(((RealMatrix) sigmoidPrime(z[numberOfLayers])),
			//					(OutputData.subtract(yHat).scalarMultiply(-1)));
			//			// > dJW2 = (a^T)x(delta3)
			//			RealMatrix dJdW2 = a[numberOfLayers - 1].transpose().multiply(delta3);
			//
			//			// =================
			//			// UPDATING THE W_1
			//			// =================
			//			// > delta2 = (delta3x(W_2^T))*f'(z[0]) = propagated error of the left side of the first layer
			//			RealMatrix delta2 = NeuralHelper.getScalarMultiplication(
			//					delta3.multiply(weightsPerLayer[numberOfLayers].transpose()),
			//					((RealMatrix) sigmoidPrime(z[numberOfLayers - 1])));
			//			// > djw1 = (X^T)x(delta2)
			//			RealMatrix dJdW1 = InputData.transpose().multiply(delta2);
			//
			//			//		System.out.println("InputData (" + InputData.getRowDimension() + "x" + InputData.getColumnDimension() + ")");
			//			//		System.out.println("delta2 (" + delta2.getRowDimension() + "x" + delta2.getColumnDimension() + ")");
			//			//		System.out.println("djw1 (" + dJdW1.getRowDimension() + "x" + dJdW1.getColumnDimension() + ")");
			//			//		System.out.println("djw2 (" + dJdW2.getRowDimension() + "x" + dJdW2.getColumnDimension() + ")");
			//
			//			return new RealMatrix[] { dJdW1, dJdW2 };

			return null;
		}
	}

}
