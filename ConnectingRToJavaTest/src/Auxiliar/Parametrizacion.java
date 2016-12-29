package Auxiliar;

public class Parametrizacion {

	int indice;
	double[] values;
	String nombreParametro;

	public Parametrizacion(double[] values, String nombreParametro) {
		this.values = values;
		this.indice = 0;
		this.nombreParametro = nombreParametro;
	}

	public Parametrizacion(double[] values, int indice, String nombreParametro) {
		this.values = values;
		this.indice = indice;
		this.nombreParametro = nombreParametro;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public double getValue() {
		return values[indice];
	}

	public void setValue(double value, int indice) {
		this.values[indice] = value;
	}

	@Override
	public String toString() {
		// Complete array
		String stringValues = "[";
		for (int i = 0; i < values.length; i++) {
			stringValues = stringValues + values[i] + ", ";
		}
		stringValues = stringValues + "]";

		return "Parametrizacion de " + nombreParametro + " > values[" + indice + "] = " + values[indice]
				+ "  >  Complete array: " + stringValues;
	}

}
