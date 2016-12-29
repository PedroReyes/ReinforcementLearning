# LIBRARIES
library(plot3D)

# Que desea graficar
listOfChartTypes <- list(
  # General charts (they use information from more than one file)
  "xEpisode_yOptimalAction", "xEpisode_yAverageReward", "xEpisode_yPercentageOfActions", "xEpisode_yWinProbability", "xEpisode_yAverageSteps", "xEpisode_yAverageStepsWhenWin", "xEpisode_yAverageStepsWhenLoose",
  # Specific charts (they use information from one single file)
  "histogramOfQvalues", "policyPerformance"
)
names(listOfChartTypes) <- c(
  # General charts (they use information from more than one file)
  "xEpisode_yOptimalAction", "xEpisode_yAverageReward", "xEpisode_yPercentageOfActions", "xEpisode_yWinProbability", "xEpisode_yAverageSteps", "xEpisode_yAverageStepsWhenWin", "xEpisode_yAverageStepsWhenLoose",
  # Specific charts (they use information from one single file)
  "histogramOfQvalues", "policyPerformance"
)

# Height, widht and resolutions of the picture
size <- 6
res <- 55

# Este metodo es el que habría que llamar. Este metodo asume para la creación de gráficas
# que la carpeta contiene archivos .csv los cuales lee y muestra en una grafica final
# que guardara en una subcarpeta, que debe de existir de antemano, llamada "Images"
crearGrafico <-
  function(urlDelExperimento, nombreFicheroSimulacion, typeOfChart) {
    # SALVAR EN UN FICHERO EL GRAFICO
    savePlot <- TRUE
    
    # Estableciendo el directorio donde se encuentran las simulaciones
    #workingExternalDir <- file.path("/Volumes","SGATE_BLACK","TFM","Experimentos","MultiArmBanditProblem","Experimento0")
    workingExternalDir <-
      urlDelExperimento#file.path("/Users","pedro","Google Drive","Desarrollo","BasicReinforcementLearningAlgorithm","Experimentos","MultiArmBanditProblem","Experimento0")
    workingDir <- workingExternalDir
    
    # Nombre de la imagen final que se va a crear
    #nombreImagen <- paste(typeOfChart,"_t", numberOfTasks,"_ep",numberOfEpisodes, sep = "")
    nombreImagen <- paste(typeOfChart,"_t", 5,"_ep",2, sep = "")
    
    # GRAFICAR
    #dev.new()
    graficar(workingDir, savePlot, nombreFicheroSimulacion, typeOfChart)
  }

###########################################FUNCIONES AUXILIARES############################################
# Esta función grafica los datos del directorio pasado y crea una
# gráfica por cada columna que queramos ver en la fecha que se indique
# graficando solo de fecha-intervalo(horas) a fecha+intervalo(horas).
graficar <-
  function(workingDir, savePlot, nombreFicheroSimulacion, chartType) {
    #dev.new()
    
    # Debuggin
    DEBUG <- TRUE
    
    # Titulo del grafico
    chartTitle <- ""
    
    # La matriz que almacenara los datos
    results <-
      NULL # la matriz es inicializada una única vez en el bucle
    simulationParameters <- c()
    
    # ================================================================
    # GENERAL CHARTS (THEY USE INFORMATION FROM MORE THAN ONE FILE)
    # ================================================================
    if (identical(listOfChartTypes$xEpisode_yOptimalAction, chartType)
        ||
        identical(listOfChartTypes$xEpisode_yPercentageOfActions, chartType)
        ||
        identical(listOfChartTypes$xEpisode_yWinProbability, chartType)
        ||
        identical(listOfChartTypes$xEpisode_yAverageReward, chartType)
        ||
        identical(listOfChartTypes$xEpisode_yAverageSteps, chartType)
        ||
        identical(listOfChartTypes$xEpisode_yAverageStepsWhenWin, chartType)
        ||
        identical(listOfChartTypes$xEpisode_yAverageStepsWhenLoose, chartType)) {
      # Nombre del plot cuando es salvado
      if (savePlot == TRUE) {
        if (DEBUG) {
          print(workingDir)
        }
        # Establecemos donde se debe guardar la imagen
        #setwd(paste(workingDir, "/Images", sep = ""))
        setwd(file.path(workingDir,"Images"))
        
        # Establecemos como se llamara la imagen
        nombreImagen <- paste(chartType, sep = "")
        nombreImagen <-
          paste(nombreImagen,'', sep = "") # uso png para así poder insertar tal cual la imagen en la pagina web
        if (DEBUG) {
          print(paste("Creando", nombreImagen, "..."))
        }
        
        #pdf(paste(nombreImagen,'.pdf', sep = ""))
        #png(paste(nombreImagen,'.png', sep = ""))
        png(
          paste(nombreImagen,'.png', sep = ""), width = size, height = size, units = 'in', res = res
        )
        # png(paste(nombreImagen,'.png', sep = ""), units="px", width=400, height=400, res=150)
        #jpeg(paste(nombreImagen,'.jpeg', sep = ""), quality = 100)
      }
      
      # Counting files that are not refered to files that store qValues of policies
      notPolicyFiles <- 0
      finalFiles <- c()
      for (iFile in 1:length(nombreFicheroSimulacion)) {
        if (!grepl("QValues", nombreFicheroSimulacion[iFile])) {
          notPolicyFiles <- notPolicyFiles + 1
          finalFiles[notPolicyFiles] <-
            nombreFicheroSimulacion[iFile]
        }
      }
      
      # Nombre de los ficheros que se van a graficar
      nombreFicheroSimulacion <- finalFiles
      
      # Se genera la matriz de datos
      for (iFile in 1:length(nombreFicheroSimulacion)) {
        # Extension
        extension <- ""
        if (!grepl(".csv", nombreFicheroSimulacion[iFile])) {
          extension <- ".csv"
        }
        if (grepl("QValues", nombreFicheroSimulacion[iFile])) {
          next()
          ;
        }
        
        # Directorio
        directorio <- paste(workingDir, "/",
                            nombreFicheroSimulacion[iFile], extension, sep = "")
        
        # Cogemos la información del fichero que nos interesa
        dataFile <-
          read.csv2(file = directorio, head = TRUE, sep = ";")
        
        # Capturando todos los datos del algoritmo
        possibleStates <-
          as.character(dataFile$possibleStates[dataFile$possibleStates != ""])
        possibleActions <-
          as.character(dataFile$possibleActions[dataFile$possibleActions != ""])
        initialPolicyValue <- dataFile$initialQValues[1]
        optimalAction <- dataFile$optimalAction[1]
        learningMethod <- dataFile$learningMethod[1]
        selectionMethod <- dataFile$selectionMethod[1]
        numberOfTasks <- dataFile$numberOfTasks[1]
        numberOfEpisodes <- dataFile$numberOfEpisodes[1]
        softmax_temperature <- dataFile$softmax_temperature[1]
        ucb_c <- dataFile$UCB_c[1]
        eGreedy_epsilon_initial <-
          dataFile$eGreedy_epsilon_initial[1]
        eGreedy_epsilon_final <- dataFile$eGreedy_epsilon_final[1]
        qLearning_alpha <- dataFile$qLearning_alpha[1]
        qLearning_gamma <- dataFile$qLearning_gamma[1]
        taskNumber <- dataFile$taskNumber
        episodeNumber <- dataFile$episodeNumber
        episodeStepTime <- dataFile$episodeStepTime
        lastState <- dataFile$lastState
        actionInLastState <- dataFile$actionInLastState
        newState <- dataFile$newState
        reward <- dataFile$averageReward
        percentageOfChosenOptimalActionsPerEpisode <-
          dataFile$percentageOfChosenOptimalActionsPerEpisode
        percentageOfChosenActionsPerEpisode <-
          dataFile$percentageOfChosenActionsPerEpisode
        victories <- dataFile$victories
        
        # Inicializamos el array que lleva los parametros de la ejecucion
        simulationParameters[iFile] <-
          getParameterExpression(dataFile)
        # Inicializamos la matriz que almacena los resultados una unica vez
        if ((!identical(
          listOfChartTypes$xEpisode_yPercentageOfActions, chartType
        ))) {
          if (is.null(results))
            results <-
              matrix(nrow = numberOfEpisodes + 1, ncol = notPolicyFiles)#length(nombreFicheroSimulacion))
        }
        else if ((identical(
          listOfChartTypes$xEpisode_yPercentageOfActions, chartType
        ))) {
          if (is.null(results))
            results <-
              matrix(nrow = numberOfEpisodes * length(possibleActions), ncol = notPolicyFiles)#length(nombreFicheroSimulacion))
        }
        # CODIGO MUERTO
        else if (FALSE) {
          if (is.null(results)) {
            results <-
              matrix(nrow = numberOfEpisodes + 1 + length(possibleActions), ncol = notPolicyFiles)#length(nombreFicheroSimulacion))
          }
        }
        
        # Donde se guardaran los resultados de la grafica
        partialResult <- c()
        
        # Preparando los datos
        # > PERCENTAGE_OF_OPTIMAL_ACTIONS
        if (identical(listOfChartTypes$xEpisode_yOptimalAction, chartType)) {
          # Usamos un vector para almacenar las medias
          percentageOfOptimalActions <- c()
          percentageOfOptimalActions[1] <- 0
          
          # Getting the average of all the task
          for (iTask in 1:numberOfTasks) {
            for (iEpisode in 0:(numberOfEpisodes - 1)) {
              # Variable auxiliar para indicar la posicion donde se guarda la media
              aux_iEpisode <- iEpisode + 1
              
              # Consigo el indice del episodio para la tarea concreta
              index <- (iTask - 1) * numberOfEpisodes + aux_iEpisode
              
              # Consigo el reward para el episodio de la tarea concreta
              auxReward <-
                as.numeric(as.character(percentageOfChosenOptimalActionsPerEpisode[index]))
              
              # Consigo el index del episodio que estamos tratando
              index <-
                iEpisode + 2 # se suma 2 porque el priemr elemento es 0 para asi tener en el grafico el zero como referencia
              if (is.null(percentageOfOptimalActions[index]) ||
                  is.na(percentageOfOptimalActions[index])) {
                percentageOfOptimalActions[index] <- 0.0
              }
              
              # Sumo la recompensa de este episodio a la recompensa global de todas las tareas
              percentageOfOptimalActions[index] <-
                percentageOfOptimalActions[index] + auxReward
            }
            
            # Calculating percentage completed of the process
            percentageCompleted <- (iTask * 100 / numberOfTasks)
            if (percentageCompleted %% 10 == 0) {
              if (DEBUG) {
                print(paste(percentageCompleted,"% process completed", sep = ""))
              }
            }
          }
          
          # Asigno el porcentaje de acciones optimas elegidas MEDIA global de todas las tareas
          percentageOfOptimalActions <-
            percentageOfOptimalActions / numberOfTasks
          
          # Asigno el porcentaje de acciones optimas elegidas MEDIA global progresiva obtenida a lo largo del tiempo
          # empezamos desde 2 porque el primer valor no cambia
          #for (n in 2:length(averageReward)) {averageReward[n] <- (averageReward[n-1]*(n-1)+averageReward[n])/(n)}
          
          # Guardamos el resultado parcial de lo que se muestra en la grafica
          partialResult <- percentageOfOptimalActions
        }
        # > AVERAGE_REWARD
        else if (identical(listOfChartTypes$xEpisode_yAverageReward, chartType)) {
          # Usamos un vector para almacenar las medias
          averageReward <- c()
          averageReward[1] <-
            as.numeric(as.character(initialPolicyValue[1]))
          
          # Getting the average of all the task
          for (iTask in 1:numberOfTasks) {
            for (iEpisode in 0:(numberOfEpisodes - 1)) {
              # Variable auxiliar para indicar la posicion donde se guarda la media
              aux_iEpisode <- iEpisode + 1
              
              # Consigo el indice del episodio para la tarea concreta
              index <- (iTask - 1) * numberOfEpisodes + aux_iEpisode
              
              # Consigo el reward para el episodio de la tarea concreta
              auxReward <- as.numeric(as.character(reward[index]))
              
              # Consigo el index del episodio que estamos tratando
              index <-
                iEpisode + 2 # se suma 2 porque el priemr elemento es 0 para asi tener en el grafico el zero como referencia
              if (is.null(averageReward[index]) ||
                  is.na(averageReward[index])) {
                averageReward[index] <- 0.0
              }
              
              # Sumo la recompensa de este episodio a la recompensa global de todas las tareas
              averageReward[index] <-
                averageReward[index] + auxReward
            }
            
            # Calculating percentage completed of the process
            percentageCompleted <- (iTask * 100 / numberOfTasks)
            if (percentageCompleted %% 10 == 0) {
              if (DEBUG) {
                print(paste(percentageCompleted,"% process completed", sep = ""))
              }
            }
          }
          
          # Asigno la recompensa MEDIA global de todas las tareas
          averageReward <- averageReward / numberOfTasks
          
          # Asigno la recompensa MEDIA global progresiva obtenida a lo largo del tiempo
          # empezamos desde 2 porque el primer valor no cambia
          #for (n in 2:length(averageReward)) {averageReward[n] <- (averageReward[n-1]*(n-1)+averageReward[n])/(n)}
          
          # Guardamos el resultado parcial de lo que se muestra en la grafica
          partialResult <- averageReward
        }
        # > PERCENTAGE_OF_ACTIONS
        else if (identical(listOfChartTypes$xEpisode_yPercentageOfActions, chartType)) {
          # Matriz que almacena el porcentaje medio de cada accion para cada episodio
          percentage <-
            matrix(0, nrow = length(possibleActions), ncol = numberOfEpisodes)
          
          # Getting the most common action for each episode
          for (iEpisode in 1:(numberOfEpisodes)) {
            # Variable auxiliar para indicar la posicion donde se guarda la media
            aux_iEpisode <- iEpisode + 1
            
            # Consigo el vector de las diferentes acciones realizadas
            # para este episodio en las diferentes tareas
            actionVectorForOneEpisode <- c()
            for (iTask in 1:(numberOfTasks)) {
              # Consigo el indice de las acciones cada tarea en este episodio concreto
              index <- (iTask - 1) * numberOfEpisodes + aux_iEpisode
              
              # Añado la accion al vector t Tasks
              percentages <-
                as.character(percentageOfChosenActionsPerEpisode[index - 1])
              percentages <- unlist(strsplit(percentages, "_"))
              
              for (i in 1:length(percentages)) {
                percentage[i,iEpisode] <-
                  percentage[i,iEpisode] + as.double(percentages[i])
              }
            }
          }
          
          # Average of the percentage of all tasks
          percentage <- percentage / numberOfTasks
          rownames(percentage) <- possibleActions
          partialResult <- percentage
        }
        # > WIN_PROBABILITY
        else if (identical(listOfChartTypes$xEpisode_yWinProbability, chartType)) {
          # Usamos un vector para almacenar las medias
          victoryProbability <- c()
          victoryProbability[1] <-
            as.numeric(as.character(initialPolicyValue[1]))
          
          # Getting the average of all the task
          for (iTask in 1:numberOfTasks) {
            for (iEpisode in 0:(numberOfEpisodes - 1)) {
              # Variable auxiliar para indicar la posicion donde se guarda la media
              aux_iEpisode <- iEpisode + 1
              
              # Consigo el indice del episodio para la tarea concreta
              index <- (iTask - 1) * numberOfEpisodes + aux_iEpisode
              
              # Consigo el reward para el episodio de la tarea concreta
              gameState <-
                as.numeric(as.character(victories[index])) # 0 - defeat _ 1 - victory
              
              # Consigo el index del episodio que estamos tratando
              index <-
                iEpisode + 2 # se suma 2 porque el priemr elemento es 0 para asi tener en el grafico el zero como referencia
              if (is.null(victoryProbability[index]) ||
                  is.na(victoryProbability[index])) {
                victoryProbability[index] <- 0.0
              }
              
              # Sumo la recompensa de este episodio a la recompensa global de todas las tareas
              victoryProbability[index] <-
                victoryProbability[index] + gameState
            }
            
            # Calculating percentage completed of the process
            percentageCompleted <- (iTask * 100 / numberOfTasks)
            if (percentageCompleted %% 10 == 0) {
              if (DEBUG) {
                print(paste(percentageCompleted,"% process completed", sep = ""))
              }
            }
          }
          
          # Asigno probabilidad real basado en el numero de victorias y partidas jugadas
          victoryProbability <-
            victoryProbability * 100 / numberOfTasks
          
          # Guardamos el resultado parcial de lo que se muestra en la grafica
          partialResult <- victoryProbability
        }
        # > AVERAGE_STEPS
        else if (identical(listOfChartTypes$xEpisode_yAverageSteps, chartType)
                 || identical(listOfChartTypes$xEpisode_yAverageStepsWhenWin, chartType)
                 || identical(listOfChartTypes$xEpisode_yAverageStepsWhenLoose, chartType)) {
          # Usamos un vector para almacenar las medias
          averageSteps <- c()
          averageSteps[1] <-
            as.numeric(as.character(initialPolicyValue[1]))
          divisor <- c()
          divisor[1] <- 1
          
          # Getting the average of all the task
          for (iTask in 1:numberOfTasks) {
            for (iEpisode in 0:(numberOfEpisodes - 1)) {
              # Variable auxiliar para indicar la posicion donde se guarda la media
              aux_iEpisode <- iEpisode + 1
              
              # Consigo el indice del episodio para la tarea concreta
              index <- (iTask - 1) * numberOfEpisodes + aux_iEpisode
              if (identical(listOfChartTypes$xEpisode_yAverageSteps, chartType)
                  || (identical(listOfChartTypes$xEpisode_yAverageStepsWhenWin, chartType) && as.numeric(as.character(victories[index])) == 1)
                  || (identical(listOfChartTypes$xEpisode_yAverageStepsWhenLoose, chartType) && as.numeric(as.character(victories[index])) == 0)) {
                # Consigo el reward para el episodio de la tarea concreta
                numberOfStepsInThisEpisode <-
                  as.numeric(as.character(episodeStepTime[index])) # 0 - defeat _ 1 - victory
                
                # Consigo el index del episodio que estamos tratando
                index <-
                  iEpisode + 2 # se suma 2 porque el primer elemento es 0 para asi tener en el grafico el zero como referencia
                if (is.null(averageSteps[index]) ||
                    is.na(averageSteps[index])) {
                  averageSteps[index] <- 0.0
                }
                if (is.null(divisor[index]) ||
                    is.na(divisor[index])) {
                  divisor[index] <- 1.0
                }
                
                # Sumo la recompensa de este episodio a la recompensa global de todas las tareas
                averageSteps[index] <-
                  averageSteps[index] + numberOfStepsInThisEpisode
                divisor[index] <- divisor[index] + 1
              }else{
                # Consigo el index del episodio que estamos tratando
                index <- iEpisode + 2 # se suma 2 porque el primer elemento es 0 para asi tener en el grafico el zero como referencia
                if (is.null(averageSteps[index]) ||
                    is.na(averageSteps[index])) {
                  averageSteps[index] <- 0.0
                }
                if (is.null(divisor[index]) ||
                    is.na(divisor[index])) {
                  divisor[index] <- 1.0
                }
              }
            }
            
            # Calculating percentage completed of the process
            percentageCompleted <- (iTask * 100 / numberOfTasks)
            if (percentageCompleted %% 10 == 0) {
              if (DEBUG) {
                print(paste(percentageCompleted,"% process completed", sep = ""))
              }
            }
          }
          
          # Asigno probabilidad real basado en el numero de victorias y partidas jugadas
          averageSteps <- averageSteps / divisor
          averageSteps[is.na(averageSteps)] <- 0
          print(divisor)
          print(averageSteps)
          
          # Guardamos el resultado parcial de lo que se muestra en la grafica
          partialResult <- averageSteps
        }
        
        # Salvamos el resultado parcial en la matriz de resultados
        #if(!identical(listOfChartTypes$histogramOfQvalues, chartType)){
        results[,iFile] <- c(partialResult)
        #}
        if (DEBUG) {
          print(paste(
            "Creating chart ", nombreFicheroSimulacion[iFile], "...", sep = ""
          ))
        }
        
        # Percentage of files that has been processed
        percentageCompleted <-
          (iFile * 100 / length(nombreFicheroSimulacion))
        print(paste(
          percentageCompleted,"% process completed of the whole process.", sep = ""
        ))
      }
    }
    
    # ================================================================
    # SPECIFIC CHARTS (THEY USE INFORMATION THAT IS STORED IN A DIFFERENT WAY FROM GENERAL CHARTS)
    # ================================================================
    else {
      # Este tipo de chart solo usa un fichero
      nombreFicheroSimulacion <- nombreFicheroSimulacion[1]
      
      # Nombre del plot cuando es salvado
      if (savePlot == TRUE) {
        if (DEBUG) {
          print(workingDir)
        }
        # Establecemos donde se debe guardar la imagen
        #setwd(paste(workingDir, "/Images", sep = ""))
        setwd(file.path(workingDir,"Images"))
        
        # Establecemos como se llamara la imagen
        nombreImagen <-
          paste(chartType,"_",nombreFicheroSimulacion, sep = "")
        nombreImagen <-
          paste(nombreImagen,'', sep = "") # uso png para así poder insertar tal cual la imagen en la pagina web
        if (DEBUG) {
          print(paste("Creando", nombreImagen, "..."))
        }
        #pdf(paste(nombreImagen,'.pdf', sep = ""))
        #png(paste(nombreImagen,'.png', sep = ""))
        png(
          paste(nombreImagen,'.png', sep = ""), width = size, height = size, units = 'in', res = res
        )
        # png(paste(nombreImagen,'.png', sep = ""), units="px", width=400, height=400, res=150)
        #jpeg(paste(nombreImagen,'.jpeg', sep = ""), quality = 100)
      }
      
      # Extension
      extension <- ""
      if (!grepl(".csv", nombreFicheroSimulacion[1])) {
        extension <- ".csv"
      }
      
      print(paste("Simulation file:",nombreFicheroSimulacion[1]))
      
      # Directorio
      directorio <- paste(workingDir, "/",
                          nombreFicheroSimulacion[1], extension, sep = "")
      
      # Cogemos la información del fichero que nos interesa
      dataFile <-
        read.csv2(file = directorio, head = TRUE, sep = ";")
      
      # Salvamos el resultado parcial en la matriz de resultados
      if (is.null(results)) {
        if (ncol(dataFile) == 2)
          results <- matrix(nrow = nrow(dataFile))
        else
          results <-
            matrix(nrow = nrow(dataFile), ncol = (ncol(dataFile) - 1))
      }else {
        print("Error creando la matriz de resultados para la grafica de Q-values")
      }
      
      # > HISTOGRAM_OF_Q_VALUES
      if (identical(listOfChartTypes$histogramOfQvalues, chartType)) {
        # Establecemos en el titulo la mejor accion
        chartTitle <- paste("Final policy")
        
        # Simulation parameters (en este caso son todos los estados y acciones posibles)
        frameWidthOfSimulationParameters <- 170
        actions <- ""
        aux <- ""
        for (iAction in 1:length(dataFile[,1])) {
          aux <- paste(aux, dataFile[iAction,1], "; ", sep = "")
          if (nchar(aux) > frameWidthOfSimulationParameters ||
              iAction == length(dataFile[,1])) {
            actions <- paste(actions, aux, "\n")
            aux <- ""
          }
        }
        
        states <- ""
        aux <- ""
        stateVector <- names(dataFile)[-1]
        for (iState in 1:length(stateVector)) {
          aux <- paste(aux, stateVector[iState], "; ", sep = "")
          if (nchar(aux) > frameWidthOfSimulationParameters ||
              iState == length(stateVector)) {
            states <- paste(states, aux, "\n")
            aux <- ""
          }
        }
        # Inicializamos el array que lleva los parametros de la ejecucion
        #simulationParameters <- c(paste("States:",states), paste("Actions:", actions))
        #simulationParameters <- getParameterExpression(dataFile)
        
        # Cogemos la tabla de la politica y la metemos en results
        for (iColumn in 1:(ncol(dataFile) - 1)) {
          results[,iColumn] <- as.double(as.character(dataFile[,iColumn + 1]))
        }
        
        # Normalizamos (tener en cuenta que si haces esto no se veria los datos reales de la politica)
        normalizeData <- TRUE
        if (normalizeData) {
          for (iColumn in 1:(ncol(dataFile) - 1)) {
            # Data
            x <- c(results[,iColumn])
            
            # Normalized Data
            if (max(x) - min(x) == 0) {
              normalized = 0
            }else{
              normalized = (x - min(x)) / (max(x) - min(x))
            }
            
            # Now the histogram is scaled
            results[,iColumn] <- normalized
          }
        }
        
        # Damos nombres a las columnas
        colnames(results) <- names(dataFile)[-1]
        
        # Damos nombres a las filas
        rownames(results) <- dataFile[,1]
        
        # There is only one state
        #simulationParameters <- ""
      }
      # > POLICY_PERFORMANCE
      else if (identical(listOfChartTypes$policyPerformance, chartType)) {
        # Establecemos en el titulo la mejor accion
        chartTitle <-
          paste("RMS error between estimate policy and optimal policy")
        
        # La parametrizacion se mostrara dentro del plot
        simulationParameters <- ""
        
        # Cogemos la tabla de la politica y la metemos en results
        for (iColumn in 1:(ncol(dataFile) - 1)) {
          results[,iColumn] <- as.double(as.character(dataFile[,iColumn + 1]))
        }
        
        # Damos nombres a las columnas
        nameVector <- c()
        for (i in 2:length(colnames(dataFile))) {
          x <- colnames(dataFile)[i]
          nameVector[i - 1] <-
            substr(x, 3, regexpr("\\.[^\\.]*$", x)[1] - 1)
        }
        colnames(results) <- nameVector
        
        # Damos nombres a las filas
        rownames(results) <- dataFile[,1]
      }
    }
    
    # Se crea el grafico
    crearChart(
      chartTitle, results, leg.txt <-
        simulationParameters, listOfChartTypes, chartType, nombreFicheroSimulacion, dataFile
    )
    
    # Se salva el plot en el directorio indicado al inicio de este fichero
    if (savePlot == TRUE) {
      dev.off()
    }else{
      #dev.off(dev.list()["RStudioGD"])
    }
  }

# Crea el grafico especifico qeu se pide
crearChart <-
  function(title, simulationData, simulationParameters, listOfChartTypes, chartType, nombreFicheroSimulacion, dataFile) {
    # Vector de colores
    colours <- c(
      "Green",   "Red",   "Black", "Blue",  "Orange",
      "Magenta", "Yellow","Gray",  "Brown", "Pink",
      "darkslateblue", "gold4", "dodgerblue4", "darkslategray1",
      "deeppink4", "green4", "lightblue4","lightcoral",
      "hotpink", "hotpink4","khaki", "lightpink4",
      "lightseagreen", "midnightblue", "lightslateblue", "maroon4",
      "mediumorchid", "orange4", "plum2", "plum4",
      "peru", "seagreen1", "seagreen4", "turquoise1",
      "turquoise4", "yellow4", "yellowgreen", "snow4"
    )
    
    # Vector final de colores
    if (length(nombreFicheroSimulacion) > length(colours))
      print("Error: por favor, añade mas colores al vector colours en el metodo crearChart(...)")
    
    #if(length(nombreFicheroSimulacion)==0) print("Error: length zero of length(nombreFicheroSimulacion)")
    #else coloresFinales <- colours[1:length(nombreFicheroSimulacion)]
    
    if (ncol(simulationData) == 0)
      print("Error: length zero of ncol(simulationData)")
    else
      coloresFinales <- colours[1:ncol(simulationData)]
    
    # margin bottom
    idealBottomMargin <- 16
    idealLineNumber <- 12
    customBottomMargin <-
      idealBottomMargin - (idealBottomMargin - (idealBottomMargin * ncol(simulationData) /
                                                  idealLineNumber))
    minimunBottomMargin <- 10
    if (customBottomMargin < minimunBottomMargin)
      customBottomMargin <- minimunBottomMargin
    
    # Las etiquetas qeu se muestran
    # > PERCENTAGE_OF_OPTIMAL_ACTIONS
    if (identical(listOfChartTypes$xEpisode_yOptimalAction, chartType)) {
      # Margenes del grafico
      par(mar = c(customBottomMargin,4,2,2))
      
      # Labels
      xLabel <-
        paste("(", as.character(dataFile$numberOfTasks[1]), " tasks) ", "Episodes",sep =
                "")
      yLabel <- "% Optimal action"
      
      # Se muestran los resultados
      matplot(
        xlab = xLabel, ylab = yLabel, simulationData, type = "l", lty = 1, pch =
          1,col = coloresFinales, ylim = c(0,100)
      ) #plot
      
      # Se muestra un titulo
      # README: de momento no uso titulo
    }
    # > AVERAGE_REWARD
    else if (identical(listOfChartTypes$xEpisode_yAverageReward, chartType)) {
      # Margenes del grafico
      par(mar = c(customBottomMargin,4,2,2))
      
      # Labels
      xLabel <-
        paste("(", as.character(dataFile$numberOfTasks[1]), " tasks) ", "Episodes",sep =
                "")
      yLabel <- "Average reward"
      
      # Se muestran los resultados
      matplot(
        xlab = xLabel, ylab = yLabel, simulationData, type = "l", lty = 1, pch =
          1, col = coloresFinales
      ) #plot
      
      # Se muestra un titulo
      # README: de momento no uso titulo
    }
    # > WINNING_PROBABILITY
    else if (identical(listOfChartTypes$xEpisode_yWinProbability, chartType)) {
      # Margenes del grafico
      par(mar = c(customBottomMargin,4,2,2))
      
      # Labels
      xLabel <-
        paste("(", as.character(dataFile$numberOfTasks[1]), " tasks) ", "Episodes",sep =
                "")
      yLabel <- "Winning probability"
      
      # Se muestran los resultados
      matplot(
        xlab = xLabel, ylab = yLabel, simulationData, type = "l", lty = 1, pch =
          1, col = coloresFinales, ylim = c(0,100)
      ) #plot
      
      # Se muestra un titulo
      # README: de momento no uso titulo
    }
    # > AVERAGE_STEPS
    else if (identical(listOfChartTypes$xEpisode_yAverageSteps, chartType)
             || identical(listOfChartTypes$xEpisode_yAverageStepsWhenWin, chartType)
             || identical(listOfChartTypes$xEpisode_yAverageStepsWhenLoose, chartType)) {
      # Margenes del grafico
      par(mar = c(customBottomMargin,4,2,2))
      
      # Labels
      xLabel <-
        paste("(", as.character(dataFile$numberOfTasks[1]), " tasks) ", "Episodes",sep =
                "")
      if (identical(listOfChartTypes$xEpisode_yAverageStepsWhenWin, chartType))
        yLabel <- "Average steps when win"
      else if (identical(listOfChartTypes$xEpisode_yAverageStepsWhenLoose, chartType))
        yLabel <- "Average steps when loose"
      else
        yLabel <- "Average steps"
      
      # Se muestran los resultados
      matplot(
        xlab = xLabel, ylab = yLabel, simulationData, type = "l", lty = 1, pch =
          1, col = coloresFinales
      ) #plot
      
      # Se muestra un titulo
      # README: de momento no uso titulo
    }
    # > PERCENTAGE_OF_ACTIONS
    else if (identical(listOfChartTypes$xEpisode_yPercentageOfActions, chartType)) {
      # Guradado auxiliar
      auxSimulationParameters <- simulationParameters
      
      # En caso de querer juntarlo todo en un mismo grafo habria que usar mfrow
      #par(mfrow=c(filas,columnas))
      
      for (iFile in 1:ncol(simulationData)) {
        # Margenes del grafico
        par(mar = c(10,4,2,2))
        
        # Los colores seran segun el numero de acciones y no del numero de ficheros leidos
        possibleActions <-
          as.character(dataFile$possibleActions[dataFile$possibleActions != ""])
        coloresFinales <- colours[1:length(possibleActions)]
        
        # Labels
        xLabel <-
          paste("(", as.character(dataFile$numberOfTasks[1]), " tasks) ", "Episodes", "\n",sep =
                  "")
        yLabel <- "Percentage of actions"
        
        # Extrayendo la informacion del fichero para cada accion
        data <-
          t(matrix(
            simulationData[,iFile], nrow = length(possibleActions), ncol = dataFile$numberOfEpisodes[1]
          ))
        colnames(data) <- possibleActions
        
        # Se muestran los resultados
        matplot(
          xlab = xLabel, ylab = yLabel, data, type = "l", lty = 1, pch = 1, col = coloresFinales, ylim = c(0,100)
        ) #plot
        
        # Que significa cada linea en el grafico
        simulationParameters <- possibleActions
        
        # Titulo del grafico
        title(
          "", sub = auxSimulationParameters[iFile],
          cex.main = 2,   font.main = 4, col.main = "blue",
          cex.sub = 0.75, font.sub = 3, col.sub = "black"
        )
        
        # Situamos las leyendas
        leg.txt <- simulationParameters
        
        par(
          fig = c(0, 1, 0, 1), oma = c(1, 0, 0, 0), mar = c(0, 0, 0, 0), new = TRUE
        )
        plot(
          0, 0, type = "n", bty = "n", xaxt = "n", yaxt = "n"
        )
        
        legend(
          "bottom", legend = leg.txt,
          xpd = TRUE, horiz = FALSE, inset = c(0, 0), bty = "o",
          pch = c(8, 8), col = coloresFinales, cex = 0.75
        )
      }
      
      simulationParameters <- ""
    }
    # > HISTOGRAM_OF_Q_VALUES
    else if (identical(listOfChartTypes$histogramOfQvalues, chartType)) {
      # Margenes del grafico
      numberOfStates <- length(names(dataFile)[-1])
      numberOfActions <- nrow(simulationData)
      print(paste("Number of states: ",numberOfStates))
      
      if (numberOfStates == 1) {
        # Margenes del grafico
        par(mar = c(4,8,2,2))
        
        # Labels
        xLabel <-
          ""#paste("File:",nombreFicheroSimulacion)#paste("(", as.character(dataFile$numberOfTasks[1]), " tasks) ", "Episodes (most action selected in all tasks)",sep="")
        yLabel <- "------"
        
        # Se muestran los resultados
        x <- rownames(simulationData)
        y <- c()
        for (row in 1:nrow(simulationData)) {
          y <- rbind(y, simulationData[row,1])
        }
        
        # Se muestran los resultados
        barplot(
          xlab = xLabel, horiz = TRUE, y, names = x, col = coloresFinales, beside = TRUE, las = 1, xlim = c(min(y),max(y))
        )
        
        # Se muestra un titulo
        title(title)
      }
      else{
        #z <- 1:(numberOfStates*numberOfActions); dim(z) <- c(numberOfStates,numberOfActions)
        z <- simulationData
        #z <- VADeaths
        
        imageIn3D <- TRUE
        
        if (imageIn3D) {
          hist3D(
            z = z, border = "black", xlab = "actions", ylab = "states", zlab = "qValue"
          )
          
          # CODIGO MUERTO: INTENTO DE PONER LABELS AL HISTOGRAMA EN 3D
          if (FALSE) {
            hist3D (
              x = 1:nrow(z), y = 1:ncol(z), z = z,
              bty = "g", phi = 20,  theta = -60,
              xlab = "", ylab = "", zlab = "", main = "VADeaths",
              col = "#0072B2", border = "black", shade = 0.8,
              ticktype = "detailed", space = 0.15, d = 2, cex.axis = 1e-9
            )
            
            # Use text3D to label x axis
            text3D(
              x = 1:nrow(z), y = rep(0.0, nrow(z)), z = rep(3, nrow(z)),
              labels = rownames(z),
              add = TRUE, adj = 0
            )
            
            # Use text3D to label y axis
            text3D(
              x = rep(0, ncol(z)),   y = 1:ncol(z), z = rep(0, ncol(z)),
              labels  = colnames(z),
              add = TRUE, adj = 1
            )
          }
        }else{
          image2D(
            x = 1:3, y = 1:6, z = z, border = "black"
            , xlab = c("actions", simulationParameters[2])#rownames(simulationData))
            , ylab = c("states", simulationParameters[1])#\n states = {10-armed}\n actions = {tirarPalanca1,tirarPalanca2,tirarPalanca3,tirarPalanca4,tirarPalanca5,\ntirarPalanca6,tirarPalanca7,tirarPalanca8,tirarPalanca9,tirarPalanca10}", ylab="actions",
            , clab = c("Q values \n(normalized)")
            , main = "Policy"
            , xaxt = "n"
            , yaxt = "n"
          )
          
        }
        
        # Titulo del grafico
        title(
          main = simulationParameters[1], sub = simulationParameters[1],
          cex.main = 0.5,   font.main = 4, col.main = "blue",
          cex.sub = 0.75, font.sub = 3, col.sub = "black"
        )
        
        #simulationParameters <- ""
      }
    }
    # > POLICY_PERFORMANCE
    else if (identical(listOfChartTypes$policyPerformance, chartType)) {
      # Margenes del grafico
      par(mar = c(5,4,2,2))
      
      # Getting the parameter label in axis X
      parameterFeatures <- getKeyWords(rownames(simulationData)[1])
      parameter1 = parameterFeatures[1]
      nameParameter1 <- parameterFeatures[2]
      
      # Labels
      xLabel <-
        parameter1#bquote(lambda~"=") #bquote("Q-LEARNING_LAMBDA("~Q[0] == .(as.character(dataFile$initialQValues[1]))~","~alpha == .(as.character(dataFile$qLearning_alpha))~","~gamma==.(as.character(dataFile$qLearning_gamma))~","~lambda==.(as.character(dataFile$qLearning_lambda))~")")
      yLabel <- "RMS error over 10 first episodes"
      
      # Se muestran los resultados
      matplot(
        xlab = xLabel, ylab = yLabel, y = simulationData,
        type = "l", lty = 1, pch = 1, col = coloresFinales
        ,xlim = c(1,length(rownames(simulationData)))
        ,xaxt = "n"
      ) #plot
      
      # Getting values of parameter1
      valuesParameter1 <- c()
      
      for (i in 1:length(rownames(simulationData))) {
        value <- rownames(simulationData)[i]
        valuesParameter1[i] <-
          getKeyWords(rownames(simulationData)[i])[4]#substr(value,nchar(parameter1)+3,nchar(value)-1)
      }
      
      # Putting the real axis
      #==========================================================
      print(valuesParameter1)
      axis(
        1, at = 1:length(valuesParameter1), labels = valuesParameter1, cex.axis = 1.0
      )
      #==========================================================
      
      # Putting the second parameter as text in the plot
      parameterFeatures <- getKeyWords(colnames(simulationData)[1])
      parameter2 = parameterFeatures[1]
      nameParameter2 <- parameterFeatures[2]
      expressionParameter2 <- parameterFeatures[3]
      
      maxValueInData <- max(simulationData)
      for (i in 1:length(colnames(simulationData))) {
        xPosition <- length(rownames(simulationData)) - 0.2#-(i-1)#-0.125
        #yPosition <- simulationData[length(rownames(simulationData))-i+1, length(colnames(simulationData))-i+1]#+0.002
        #yPosition <- simulationData[length(rownames(simulationData)), length(colnames(simulationData))-i+1]#+0.002
        yPosition <-
          maxValueInData - maxValueInData * 0.025 * (i - 1)
        valor <-
          as.double(as.character(getKeyWords(colnames(
            simulationData
          )[i])[4]))
        
        # Text in the graph, it only changes which is the greece letter
        if (grepl("alpha", colnames(simulationData)[1]))
          text(
            x = xPosition, y = yPosition, bquote(alpha ~ "=" ~ .(valor)), cex = 1, col = coloresFinales[length(colnames(simulationData)) -
                                                                                                          i + 1]
          )
        else if (grepl("gamma", colnames(simulationData)[1]))
          text(
            x = xPosition, y = yPosition, bquote(gamma ~ "=" ~ .(valor)), cex = 1, col = coloresFinales[length(colnames(simulationData)) -
                                                                                                          i + 1]
          )
        else if (grepl("lambda", colnames(simulationData)[1]))
          text(
            x = xPosition, y = yPosition, bquote(lambda ~ "=" ~ .(valor)), cex = 1, col = coloresFinales[length(colnames(simulationData)) -
                                                                                                           i + 1]
          )
        else if (grepl("epsilon", colnames(simulationData)[1]))
          text(
            x = xPosition, y = yPosition, bquote(epsilon ~ "=" ~ .(valor)), cex = 1, col = coloresFinales[length(colnames(simulationData)) -
                                                                                                            i + 1]
          )
      }
      
      # There is no need for simulationParameters
      simulationParameters <- ""
    }
    # > ERROR
    else{
      # Labels
      xLabel <- "--------"
      yLabel <- "--------"
    }
    
    # Situamos las leyendas
    leg.txt <- simulationParameters
    
    if (#ncol(simulationData)<7 && !is.null(leg.txt) &&
      nchar(leg.txt[1]) >= 1) {
      idealFontSize <- 0.7
      idealLineNumber <- 6
      customFontSize <-
        idealFontSize + (idealFontSize - (idealFontSize * ncol(simulationData) /
                                            idealLineNumber))
      minimunFontSize <- 0.6
      if (customFontSize < minimunFontSize)
        customFontSize <- minimunFontSize
      par(
        fig = c(0, 1, 0, 1), oma = c(1, 0, 0, 0), mar = c(0, 0, 0, 0), new = TRUE
      )
      plot(
        0, 0, type = "n", bty = "n", xaxt = "n", yaxt = "n"
      )
      
      legend(
        "bottom", legend = leg.txt,
        xpd = TRUE, horiz = FALSE, inset = c(0, 0), bty = "o",
        pch = c(8, 8), col = coloresFinales, cex = customFontSize
      )
    }
  }

# Devuelve la leyenda del gráfico qeu se crea
getParameterExpression <- function(dataFile) {
  # Metodos
  learnerMethod <- as.character(dataFile$learningMethod[1])
  selectorMethod <- as.character(dataFile$selectionMethod[1])
  
  # Metodos de aprendizaje
  listOfLearners <-
    list("Q_LEARNING", "Q_LEARNING_LAMBDA", "GRADIENT_BANDIT", "SARSA", "SARSA_LAMBDA")
  names(listOfLearners) <-
    c("Q_LEARNING", "Q_LEARNING_LAMBDA", "gradientBandit", "SARSA", "SARSA_LAMBDA")
  
  # Metodos de seleccion
  listOfActionSelectors <-
    list("E_GREEDY", "E_GREEDY_CHANGING_TEMPORALLY", "UCB", "SOFT_MAX")
  names(listOfActionSelectors) <-
    c("eGreedy", "eGreedyTemporal","UCB", "softmax")
  
  # La expresion resultante variara segun
  # el metodo de aprendizaje y seleccion elegidos
  # Asignando la expresión al metodo de aprendizaje
  learner <- ""
  # > QLEARNING
  if (identical(listOfLearners$Q_LEARNING, learnerMethod)) {
    learner <-
      bquote(
        "Map("~.(as.character(dataFile$sizeMap[1]))~"x"~.(as.character(dataFile$sizeMap[1]))~")"~
        "Q-Learning(" ~ Q[0] == .(as.character(dataFile$initialQValues[1])) ~ "," ~
          alpha == .(as.character(dataFile$qLearning_alpha)) ~ "," ~ gamma == .(as.character(dataFile$qLearning_gamma)) ~
          ")"
      )
  }
  # > QLEARNING_LAMBDA
  else if (identical(listOfLearners$Q_LEARNING_LAMBDA, learnerMethod)) {
    learner <-
      bquote(
        "Q-LEARNING_LAMBDA(" ~ Q[0] == .(as.character(dataFile$initialQValues[1])) ~
          "," ~ alpha == .(as.character(dataFile$qLearning_alpha)) ~ "," ~ gamma ==
          .(as.character(dataFile$qLearning_gamma)) ~ "," ~ lambda == .(as.character(dataFile$qLearning_lambda)) ~
          ")"
      )
  }
  # > SARSA
  else if (identical(listOfLearners$SARSA, learnerMethod)) {
    learner <-
      bquote(
        "Map("~.(as.character(dataFile$sizeMap[1]))~"x"~.(as.character(dataFile$sizeMap[1]))~")"~
          "SARSA(" ~ Q[0] == .(as.character(dataFile$initialQValues[1])) ~ "," ~ alpha == .(as.character(dataFile$qLearning_alpha)) ~
          "," ~ gamma == .(as.character(dataFile$qLearning_gamma)) ~ ")"
      )
  }
  # > SARSA_LAMBDA
  else if (identical(listOfLearners$SARSA_LAMBDA, learnerMethod)) {
    learner <-
      bquote(
        "SARSA_LAMBDA(" ~ Q[0] == .(as.character(dataFile$initialQValues[1])) ~
          "," ~ alpha == .(as.character(dataFile$qLearning_alpha)) ~ "," ~ gamma ==
          .(as.character(dataFile$qLearning_gamma)) ~ "," ~ lambda == .(as.character(dataFile$qLearning_lambda)) ~
          ")"
      )
  }
  # > GRADIENT_BANDIT
  else if (identical(listOfLearners$gradientBandit, learnerMethod)) {
    baselineUsed <- ""
    if (as.character(dataFile$softmax_baselineUsed[1]) == " true") {
      baselineUsed <- "with_baseline"
    }else{
      baselineUsed <- "without_baseline"
    }
    learner <-
      bquote("GradientBandit(" ~ alpha == .(as.character(dataFile$qLearning_alpha[1])) ~
               "," ~ .(baselineUsed) ~ ")")
  }
  # > ERROR
  else{
    learner <- "Learner not found!"
  }
  
  # Asignando la expresión al metodo de selección
  selector <- ""
  # > E_GREEDY
  if (identical(listOfActionSelectors$eGreedy, selectorMethod))
    selector <-
    bquote("eGreedy(" ~ epsilon == .(as.character(dataFile$eGreedy_epsilon_initial[1])) ~
             ")")
  # > E_GREEDY_TEMPORAL
  else if (identical(listOfActionSelectors$eGreedyTemporal, selectorMethod))
    selector <-
    bquote("eGreedyTemporal(" ~ epsilon ==  ~ "[" ~ .(as.character(dataFile$eGreedy_epsilon_initial[1])) ~
             "," ~ .(as.character(dataFile$eGreedy_epsilon_final[1])) ~ "])")
  # > UCB (Upper Confident Bound)
  else if (identical(listOfActionSelectors$UCB, selectorMethod))
    selector <-
    bquote("UCB(" ~ c == .(as.character(dataFile$UCB_c[1])) ~ ")")
  # > SOFTMAX
  else if (identical(listOfActionSelectors$softmax, selectorMethod))
    selector <-
    bquote("softmax(" ~ tau == .(as.character(dataFile$softmax_temperature[1])) ~
             ")")
  # > ERROR
  else
    selector <- "Selector not found!"
  
  # Creando la expresión final qeu es la combinacion
  # del metodo de aprendizaje y el de selección de acciones
  expression <- as.expression(bquote(.(learner) ~ .(selector)))
  return (expression)
}

# Dada una sentencia en la que existe una palabra clave,
# devuelve la primera ocurrencia de dicha palabra clave.
# Por ejemplo si en la frase viene "alpha" y tenemos
# como palabra clave dentro de esta funcion "alpha, entonces
# obtendremos como resultado "alpha"
getKeyWords <- function(parameter) {
  nameParameter <- ""
  parameterExpression <- ""
  
  value <-
    as.double(substring(
      parameter,regexpr("\\.[^\\.]*$",substr(
        parameter,1,regexpr("\\.[^\\.]*$", parameter)[1] - 1
      ))[1] + 1, nchar(parameter)
    ))
  if (is.na(value)) {
    value <-
      as.double(substring(
        parameter,regexpr("\\=[^\\=]*$", parameter)[1] + 1,nchar(parameter) - 1
      ))
  }
  
  if (grepl("alpha", parameter)) {
    parameter <- bquote(alpha);
    parameterExpression <- expression(alpha)
    nameParameter <- "alpha"
  }
  else if (grepl("gamma", parameter)) {
    parameter <- bquote(gamma)
    parameterExpression <- expression(gamma)
    nameParameter <- "gamma"
  }
  else if (grepl("lambda", parameter)) {
    #value <- as.double(substr(parameter,8,nchar(parameter)))
    parameter <- bquote(lambda)
    parameterExpression <- expression(lambda)
    nameParameter <- "lambda"
  }
  else if (grepl("epsilon", parameter)) {
    parameter <- bquote(epsilon)
    parameterExpression <- expression(epsilon)
    nameParameter <- "epsilon"
  }
  else{
    value <- as.double(-1)
    parameter <- "Error: Parameter not found!"
    parameterExpression <- "notFound"
    nameParameter <- "notFound"
  }
  
  return (c(parameter, nameParameter, parameterExpression, value))
}
