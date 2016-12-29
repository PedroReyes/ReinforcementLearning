package Aspects;

import java.util.Arrays;

public aspect DebuggingAspect {
	
	private String[] classesToBeDebugged = new String[]{
		
			//"MultiArmBanditWorld", 
			//"RLearner"
			//"AnalysisSimulationRecord"
			};

	// Pointcut for controlling what debuggin we observe on the console
//	pointcut callPrintln(String message) : 
//		call(void java.io.PrintStream.println(String)) &&
//		!within(Aspects.DebuggingAspect) &&
//		args(message);
//	
//	pointcut callPrint(String message) : 
//		call(void java.io.PrintStream.print(String)) &&
//		!within(Aspects.DebuggingAspect) &&
//		args(message);
//
//	void around(String message) : callPrintln(message)// || callPrint(message)
//	{
//		String classThatIsPrinting = thisJoinPoint.getThis()==null? "main" : thisJoinPoint.getThis().getClass().toString();
//		printing(message, classThatIsPrinting, true);
//	}
//	
//	void around(String message) : callPrint(message)
//	{
//		String classThatIsPrinting = thisJoinPoint.getThis()==null? "main" : thisJoinPoint.getThis().getClass().toString();
//		printing(message, classThatIsPrinting, false);
//	}
//	
//	void printing(String message, String classThatIsPrinting, boolean line){
//		// Class that is printing something
//		classThatIsPrinting = classThatIsPrinting.substring(classThatIsPrinting.lastIndexOf(".")+1);
//		//System.out.println(classThatIsPrinting);
//		
//		// Checking if the class must be printed in console or not.
//		// It will be printed either is in the array or the array is empty.
//		if(Arrays.asList(classesToBeDebugged).contains(classThatIsPrinting)
//				|| classesToBeDebugged.length==0){
//			if(message.contains("crearGrafico")){
//				System.out.println(message);
//			}else{
//				if(line){
//					System.out.println(classThatIsPrinting.substring(classThatIsPrinting.lastIndexOf(".")+1) + ": " + message);
//				}else{
//					System.out.print(classThatIsPrinting.substring(classThatIsPrinting.lastIndexOf(".")+1) + ": " + message);
//				}
//			}
//		}
//	}
}
