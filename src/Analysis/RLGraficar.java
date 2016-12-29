package Analysis;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RLGraficar {

	private boolean chooseFilesUsingConsole;

	public RLGraficar(boolean chooseFilesUsingConsole) {
		this.chooseFilesUsingConsole = chooseFilesUsingConsole;
	}

	public void crearGrafica(String urlDelExperimento, Output.Output.typeOfCharts typeOfChart,
			String specificFileToPlot, String[] specificFileToNotPlot) {
		String R_urlDelExperimento = RCodeGenerator.generateRCodeForUrlCreation(urlDelExperimento);
		String R_typeOfChart = RCodeGenerator.generateRCodeForTypeOfChart(typeOfChart);
		String nombreFicheros = specificFileToPlot == null || specificFileToPlot.isEmpty()
				? RCodeGenerator.generateRCodeForChoosingExperimentFiles(urlDelExperimento,
						this.chooseFilesUsingConsole, specificFileToNotPlot)
				: "c(\"" + specificFileToPlot + "\")";
		String command = "crearGrafico(" + R_urlDelExperimento + ", " + nombreFicheros + "," + R_typeOfChart + ")";
		System.out.println(command);
		if (true) {
			try {
				RConnection c = RCodeGenerator.configuratingWorkspace();
				c.eval(command);
			} catch (RserveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Error: un posible error es que no haya ficheros de donde sacar las graficas.");
			}
		}
	}

}
