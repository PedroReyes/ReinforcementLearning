package Aspects;
public aspect CapturingExceptions {

	// Captura todas las excepciones lanzadas en el main
	//pointcut takeAllExceptions(long tipo): call(void Thread.sleep(long)) && args(tipo);
	//pointcut takeAllExceptions(int tipo): call(void *.sujetoReplay(int) throws InterruptedException) && args(tipo);
	pointcut takeAllExceptions(long tipo): call(void Thread.sleep(long) throws InterruptedException) && args(tipo);
	
	//after(int t): takeAllExceptions(t){}

	
	/*java.lang.InterruptedException*/
	void around(long tipo)
			//throws java.lang.InterruptedException:
			throws java.lang.InterruptedException:
			//throws Throwable: 
		takeAllExceptions(tipo){
		try {
			//System.out.println("start");
			proceed(tipo); 
			//System.out.println("end");
		} catch (InterruptedException e) {
			System.out.println("Excepción en el main");
			e.printStackTrace();
		}
	}
	
	
//	pointcut takeExceptions(): call(Integer MCTS_Planning.MonteCarloTreeSearch.runMonteCarloTreeSearch() throws InterruptedException);
//	Integer around() throws java.lang.InterruptedException: takeExceptions(){
//		try {
//			return proceed(); 
//		} catch (InterruptedException e) {
//			System.out.println("Excepción en el main");
//			e.printStackTrace();
//		}
//		return 0;
//	}
}
