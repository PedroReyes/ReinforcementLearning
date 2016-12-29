switch(Sys.info()[['sysname']],
       Windows= {#WINDOWS
         source("C:/Users/papa/Dropbox/Desarrollo/BasicReinforcementLearningAlgorithm/src/Analysis/RLGraficar.R")
       },
       Darwin = {# MAC
         source("/Users/pedro/Google Drive/Desarrollo/BasicReinforcementLearningAlgorithm/src/Analysis/RLGraficar.R")
       }
)


crearGrafico(file.path("/Users","pedro","Google Drive","Desarrollo","BasicReinforcementLearningAlgorithm","Experimentos","GameProblemWithMap","newRewardFunction_5x5"), c("GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5","QValues_GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5"),"xEpisode_yAverageReward")
crearGrafico(file.path("/Users","pedro","Google Drive","Desarrollo","BasicReinforcementLearningAlgorithm","Experimentos","GameProblemWithMap","newRewardFunction_5x5"), c("GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5","QValues_GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5"),"xEpisode_yAverageStepsWhenWin")
crearGrafico(file.path("/Users","pedro","Google Drive","Desarrollo","BasicReinforcementLearningAlgorithm","Experimentos","GameProblemWithMap","newRewardFunction_5x5"), c("GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5","QValues_GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5"),"xEpisode_yPercentageOfActions")
crearGrafico(file.path("/Users","pedro","Google Drive","Desarrollo","BasicReinforcementLearningAlgorithm","Experimentos","GameProblemWithMap","newRewardFunction_5x5"), c("GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5","QValues_GameWorldSimpleMap_SARSA_E_GREEDY_CHANGING_TEMPORALLY_t100.0_ep150.0_lRate1.0_dFactor0.7_eG0.5_0.05_5x5"),"xEpisode_yWinProbability")