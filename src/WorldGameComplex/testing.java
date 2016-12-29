package WorldGameComplex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class testing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] sizes = new int[] { 5, 3, 5, 2 };
		int[] indexCombination = new int[sizes.length];
		System.out.println(indexCombination.length);

		int numberOfCombinations = 1;
		for (int i = 0; i < sizes.length; i++) {
			numberOfCombinations = numberOfCombinations * sizes[i];
		}
		System.out.println(numberOfCombinations);

		Set<String> setOfCombinations = new HashSet<>();
		System.out.println(5 % 4);

		int i = indexCombination.length - 1;
		for (int iterations = 0; iterations < numberOfCombinations; iterations++) {
			// Checking
			String combination = Arrays.toString(indexCombination);

			if (setOfCombinations.contains(combination)) {
				System.err.println("Already contained: " + combination);
				break;
			} else if (!validCombination(sizes, indexCombination)) {
				System.err.println("No valid combination: " + combination);
				break;
			} else {
				System.out.println(iterations + ": " + combination);
				setOfCombinations.add(combination);
			}

			boolean b = true;
			if (b) {
				// ============
				// CODIGO UTIL
				// ============
				boolean change = false;
				while (!change) {
					indexCombination[i] = indexCombination[i] + 1;

					if (indexCombination[i] == sizes[i]) {
						indexCombination[i] = 0;
						i = i - 1 < 0 ? indexCombination.length - 1 : i - 1;
					} else {
						change = true;
					}
				}

				i = indexCombination.length - 1;
			}

		}

	}

	private static boolean validCombination(int[] sizes, int[] index) {
		boolean validCombination = true;
		for (int j = 0; j < index.length; j++) {
			if (index[j] >= sizes[j]) {
				validCombination = false;
			}
		}
		return validCombination;
	}

}
