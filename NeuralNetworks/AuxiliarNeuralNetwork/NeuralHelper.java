package AuxiliarNeuralNetwork;

import java.util.Random;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class NeuralHelper {

	/**
	 * This method makes a scalar multiplication of a matrix in the way that a is a (n X m) matrix
	 * and b is a (n x 1) matrix or vice versa
	 * @param MatrixA
	 * @param b
	 * @return
	 */
	public static RealMatrix getScalarMultiplication(RealMatrix a, RealMatrix b) {
		RealMatrix result = MatrixUtils.createRealMatrix(a.getColumnDimension() == 1 ? b.getData() : a.getData());

		if (a.getColumnDimension() != b.getColumnDimension()) {
			throw new IllegalArgumentException("Error: differen column dimensions.");
		}

		if (a.getRowDimension() != b.getRowDimension()) {
			throw new IllegalArgumentException("Error: differen row dimensions.");
		}

		RealMatrix matrixA = a;
		RealMatrix matrixB = b;

		for (int i = 0; i < matrixA.getRowDimension(); i++) {
			for (int j = 0; j < matrixA.getColumnDimension(); j++) {
				result.setEntry(i, j, matrixA.getEntry(i, j) * matrixB.getEntry(i, j));
			}
		}

		return result;
	}

	/**
	 * Get a matrix of random values with the dimensions established
	 * @param numberOfRows
	 * @param numberOfColumns
	 * @return
	 */
	static double value = 0.1;
	static Random r = new Random();

	public static RealMatrix getRandomMatrix(int numberOfRows, int numberOfColumns) {
		RealMatrix result = MatrixUtils.createRealMatrix(numberOfRows, numberOfColumns);

		for (int row = 0; row < numberOfRows; row++) {
			for (int column = 0; column < numberOfColumns; column++) {
				result.setEntry(row, column, r.nextDouble());
				//				result.setEntry(row, column, value);
				//				value = value + 0.1;
			}
		}

		return result;
	}

	/**
	 * 
	 * @param numberOfRows
	 * @param numberOfColumns
	 * @return
	 */
	public static RealMatrix getMatrixFilledWith(int numberOfRows, int numberOfColumns, double filling) {
		RealMatrix result = MatrixUtils.createRealMatrix(numberOfRows, numberOfColumns);

		for (int row = 0; row < numberOfRows; row++) {
			for (int column = 0; column < numberOfColumns; column++) {
				result.setEntry(row, column, filling);
			}
		}

		return result;
	}

	public static RealMatrix duplicate(RealMatrix vector, int times) {
		int numberOfRows = 0;
		int numberOfColumns = 0;

		if (vector.getRowDimension() == 1) {
			numberOfRows = times;
			numberOfColumns = vector.getColumnDimension();
		} else if (vector.getColumnDimension() == 1) {
			numberOfRows = vector.getRowDimension();
			numberOfColumns = times;
			vector = vector.transpose();
		} else {
			System.out.println("vector(lenght): " + vector.getRowDimension() + "x" + vector.getColumnDimension());
			throw new IllegalArgumentException("The matrix must be a vector, that is, must be nx1 or 1xn");
		}

		RealMatrix result = MatrixUtils.createRealMatrix(numberOfRows, numberOfColumns);

		for (int row = 0; row < numberOfRows; row++) {
			for (int column = 0; column < numberOfColumns; column++) {
				result.setEntry(row, column, vector.getEntry(0, column));
			}
		}

		return result;
	}

	/**
	 * Given the matrix, return the max value
	 * @param InputData
	 * @return
	 */
	public static double getMaxValue(RealMatrix InputData) {
		// TODO Auto-generated method stub
		double maxValue = Double.MIN_VALUE;
		for (int row = 0; row < InputData.getRowDimension(); row++) {
			for (int column = 0; column < InputData.getColumnDimension(); column++) {
				maxValue = Double.max(maxValue, InputData.getEntry(row, column));
			}
		}

		return maxValue;
	}

	/**
	 * Sum the values of a matrix with a unique row or a unique column.
	 * @param activityLayer
	 * @return
	 */
	public RealMatrix sumOfValues(RealMatrix m) {
		double sum = 0;
		int numberOfRows = m.getRowDimension();
		int numberOfColumns = m.getColumnDimension();

		if (numberOfRows > 1 && numberOfColumns > 1)
			throw new IllegalArgumentException("Error: matrix must be a matrix of dimensions [nx1] or [1xn]");

		for (int row = 0; row < numberOfRows; row++) {
			for (int column = 0; column < numberOfColumns; column++) {
				sum = sum + m.getEntry(row, column);
			}
		}

		RealMatrix result = MatrixUtils.createRealMatrix(1, 1);
		result.setEntry(1, 1, sum);

		return result;
	}

}
