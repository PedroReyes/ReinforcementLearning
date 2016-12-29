package Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import Output.Output.typeOfCharts;

public class RCodeGenerator {

	public static String aux = "";
	public static String classPath = (aux = RCodeGenerator.class
			.getResource(RCodeGenerator.class.getSimpleName() + ".class").toString().substring(5)).replace("bin", "src")
					.replace("%20", " ")
					.substring(!RCodeGenerator.isOperativeSystemWindows() ? 0 : 1, aux.lastIndexOf("/") - 2);

	// Para establecer conexion con Rserve 
	// (esta variable solo se usa en Windows que por algun extraño motivo no puedo crear graficas en parpalelo)
	public static RConnection c = null;

	/**
	 * MAIN para probar cosas
	 * @param args
	 */
	public static void main(String[] args) {
		String path = RCodeGenerator.class.getResource(RCodeGenerator.class.getSimpleName() + ".class").toString();
		System.out.println(path);
		path = path.substring(5).replace("%20", " ").replace("bin", "src");
		System.out.println(path);
		path = path.substring(0, path.lastIndexOf("/"));
		System.out.println(path);

		String aux = (aux = RCodeGenerator.class.getResource(RCodeGenerator.class.getSimpleName() + ".class").toString()
				.substring(5)).replace("bin", "src").replace("%20", " ")
						.substring(!RCodeGenerator.isOperativeSystemWindows() ? 0 : 1, aux.lastIndexOf("/") - 2);
		System.out.println("result > " + aux);
		// System.out.println(System.getProperty("user.dir") + "/src/");
		// System.out.println(new File("").getAbsolutePath());
	}

	/**
	 * Establece la configuración inicial de source("scripts necesarios aqui") así como 
	 * establecer el workspace (setwd("aqui el workspace"))
	 * 
	 * @return
	 */
	public static RConnection configuratingWorkspace() {
		// Checking connection with Rserve (README: Just re-run if this got an
		// error the very first time)
		try {
			Runtime.getRuntime().exec("Rscript " + Output.Output.rScriptForSetUpRserve);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Estableciendo conexion 
		RConnection c = null;
		try {
			if (c == null && RCodeGenerator.isOperativeSystemWindows()) {
				RCodeGenerator.c = new RConnection();
				c = RCodeGenerator.c;
			} else if (c == null) {
				c = new RConnection();
			}
			//			REXP x = c.eval("R.version.string");
			//			try {
			//				System.out.println(x.asString());
			//			} catch (REXPMismatchException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
		} catch (RserveException e2) {
			// TODO Auto-generated catch block
			System.err.println(
					"Por favor, ejecuta nuevamente el programa ya que la primera ejecución es para cargar el servidor Rserve de manera que se puedan crear graficas de manera automatica desde este programa.");
			e2.printStackTrace();
		}
		//System.out.println("Rserve iniciado para la posterior creación de gráficas.");

		// Estableciendo el directorio
		try {
			//			System.out.println("=================================================================================");
			//			System.out.println(RCodeGenerator.classPath);
			//			System.out.println(RCodeGenerator.generateRCodeForUrlCreation(RCodeGenerator.classPath));
			//			System.out.println("=================================================================================");
			c.eval("setwd(" + RCodeGenerator.generateRCodeForUrlCreation(RCodeGenerator.classPath) + ")");
			//			System.out.println("=================================================================================");
			//			System.out.println(Output.Output.rScriptForCharting);
			//			System.out.println(RCodeGenerator.generateRCodeForUrlCreation(Output.Output.rScriptForCharting));
			//			System.out.println("=================================================================================");
			c.eval("source(" + RCodeGenerator.generateRCodeForUrlCreation(Output.Output.rScriptForCharting) + ")");
			//			c.eval("source(\"" + Output.Output.rScriptForCharting + "\")");
		} catch (RserveException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return c;
	}

	/**
	 * Permite elegir que ficheros .csv quiere incluir en la graficación. Para ello se utiliza un 
	 * vector c() de R que es el que devuelve en forma de String este metodo y que se le pasa al metodo crearGrafica()
	 * 
	 * @param urlDelExperimento
	 * @param chooseFiles
	 * @return
	 */
	public static String generateRCodeForChoosingExperimentFiles(String urlDelExperimento, boolean chooseFiles,
			String[] specificFileToNotPlot) {
		// Choosing files
		List<File> experimentFiles = new LinkedList<File>();
		List<File> chosenExperimentFiles = new LinkedList<File>();
		File folder = new File(urlDelExperimento);
		File[] listOfFiles = folder.listFiles();
		if (chooseFiles) {
			System.out.println("Those are the files that may be used in the chart with their indices:");
		}
		int count = 0;
		if (chooseFiles) {
			System.out.println(count + ". " + "Reading all .csv files");
		}
		count++;

		// Si no hay ficheros .csv no se puede hacer nada
		if (listOfFiles.length == 0) {
			try {
				throw new IOException("No hay ficheros .csv en esta carpeta de los que poder obtener ninguna grafica.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Listamos en la lista todos los ficheros
		for (int i = 0; i < listOfFiles.length; i++) {
			File aux = listOfFiles[i];
			if (aux.isDirectory() || aux.isHidden() || !aux.getName().contains(".csv")) {

			} else {
				if (chooseFiles) {
					System.out.println(count + ". " + listOfFiles[i].getName());
				}
				experimentFiles.add(listOfFiles[i]);
				count++;
			}
		}

		if (chooseFiles) {
			System.out.println("Introduced file indices to be used in the chart (i.e., 1" + Output.Output.separator
					+ "2" + Output.Output.separator + "5" + Output.Output.separator + "):");
			System.out.print("> ");
		}

		String inputForChoosingFiles = "";

		if (chooseFiles) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				inputForChoosingFiles = br.readLine();
				System.out.println(inputForChoosingFiles);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			inputForChoosingFiles = "0;";
		}

		if (chooseFiles) {
			System.out.println(inputForChoosingFiles);
		} 
//		else if (false) {
//			System.out.println("All files have been chosen.");
//		}

		if (inputForChoosingFiles.isEmpty()) {
			try {
				throw new Exception("The input cannot be empty.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String[] inputSplit = inputForChoosingFiles.split(Output.Output.separator);
		for (int i = 0; i < inputSplit.length; i++) {
			int indice = Integer.valueOf(inputSplit[i].trim());
			if (indice == 0) {
				chosenExperimentFiles = experimentFiles;
				break;
			}
			chosenExperimentFiles.add(experimentFiles.get(indice - 1));
		}

		// Generating the c() vector in R language syntax
		String nombreFicheros = "";
		String nameFile;
		for (int i = 0; i < chosenExperimentFiles.size(); i++) {
			File chosenFile = chosenExperimentFiles.get(i);

			if (chooseFiles) {
				System.out.println(chosenFile.getName());
			}

			nameFile = chosenFile.getName().substring(0, chosenFile.getName().length() - 4);

			// Solo formaran parte del array final aquellos elementos que no esten en el array
			// de String donde se especifica qeu archivos no se deben tener en cuenta
			if (specificFileToNotPlot == null || !Arrays.asList(specificFileToNotPlot).contains(nameFile)) {
				nombreFicheros = nombreFicheros + "\"" + nameFile + "\"";

				if (i + 1 != chosenExperimentFiles.size()) {
					nombreFicheros = nombreFicheros + ",";
				}
			}
		}
		nombreFicheros = "c(" + nombreFicheros + ")";

		return nombreFicheros;
	}

	/**
	 * Devuelve el tipo de chart en codigo R pero de tipo String
	 * 
	 * @param type
	 * @return
	 */
	public static String generateRCodeForTypeOfChart(typeOfCharts type) {
		String typeOfChart = type.name();
		typeOfChart = "\"" + typeOfChart + "\"";
		return typeOfChart;
	}

	/**
	 * Genera la URL (url clasica de un S.O. cualquiera) pasada en modo R a través del metodo file.path() de R.
	 * 
	 * @param url
	 * @return
	 */
	public static String generateRCodeForUrlCreation(String url) {
		String urlDelExperimento = url;
		String[] directories = urlDelExperimento.split("/");
		for (int i = 1; i < directories.length; i++) {
			String subdirectory = directories[i];
			if (i == 1) {
				urlDelExperimento = (!RCodeGenerator.isOperativeSystemWindows() ? "\"/" + subdirectory
						: "\"" + directories[i - 1] + "/\"," + "\"" + subdirectory + "") + "\"";
			} else {
				urlDelExperimento = urlDelExperimento + ",\"" + subdirectory + "\"";
			}
		}
		urlDelExperimento = "file.path(" + urlDelExperimento + ")";
		return urlDelExperimento;
	}

	/**
	 * Devuelve true si el S.O. es Windows
	 * 
	 * @return
	 */
	public static boolean isOperativeSystemWindows() {
		return System.getProperty("os.name").contains("Window");
	}

}
