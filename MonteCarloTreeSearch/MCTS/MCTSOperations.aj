package MCTS;


public aspect MCTSOperations {

//	// ===============================================================================
//	// Point-cut for saving state of the world and backing it up after when simulating
//	// ===============================================================================
//     pointcut saveState(MCTS.MonteCarloTreeSearch mcts, MCTS.Node node):
// 		this(mcts) && call(double MCTS.MonteCarloTreeSearch.simulate(MCTS.Node)) && args(node);
//
//     
//    // Operations before executing a simulation
//	before(MCTS.MonteCarloTreeSearch mcts,MCTS.Node node): saveState(mcts, node){
//		if(mcts.debug && false){
//		System.out.println("Saving current state of the world for simulation purposes");
//		}
//		mcts.model.saveState();
//	}
//	
//    // Operations after executing a simulation
//	after(MCTS.MonteCarloTreeSearch mcts,MCTS.Node node) returning: saveState(mcts, node){
//		if(mcts.debug && false){
//		System.out.println("Backing up the state of the world before the simulation started");
//		}
//		mcts.model.resetToLastSavedState();
//	}
	
}
