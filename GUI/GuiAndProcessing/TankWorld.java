package GuiAndProcessing;

import java.awt.Point;
import java.util.Map;

import Auxiliar.GameMap;
import Output.Output;
import RL.State;
import WorldGameComplex.GameWorldSimpleMap.STATES;
import processing.core.PApplet;
import processing.core.PImage;

public class TankWorld extends PApplet {

	// Run this project as Java application and this
	// method will launch the sketch
	public static void main(String[] args) {
		//		String[] a = { "MAIN" };
		//		PApplet.runSketch(a, new FooWinTest());
		PApplet.main(TankWorld.class.getName());

		GameMap map = new GameMap();
		System.out.println(map.getMapAsMatrix());
		String[][] aux = map.getMapAsMatrix();
		for (int i = 0; i < aux.length; i++) {
			for (int j = 0; j < aux[0].length; j++) {
				System.out.print(aux[i][j] + " ");
			}
			System.out.println();
		}
	}

	public TankWorld() {
		w = 8;
		h = w;
	}

	// RLearner
	//	RLearner learner;
	State[] nextStates;

	public TankWorld(GameMap map) {
		w = map.getDimX();
		h = map.getDimY();
		this.mapState = map.getMap();
		//		this.learner = learner;
	}

	// Window size
	int widthWindow = 500, heightWindow = widthWindow / 2;

	int strokeWeight = 3;

	int w, h, blockSizeX, blockSizeY, dir = 2;

	// Agent's life
	double agentLife = 100;
	int agentLifePositionX, agentLifePositionY;
	Point agentPosition;
	int sizeForAgentBarLife = 5;

	// Agent's life
	double enemyLife = 80;
	int enemyLifePositionX, enemyLifePositionY;

	// Map state
	Map<Point, STATES> mapState;

	// Auxiliary images
	PImage imageBackground;
	PImage imageEnemyTank;
	PImage imageAgentTank;

	// Current state of the agent
	State currentState;

	public void settings() {
		agentPosition = new Point(0, 0);

		size(widthWindow, heightWindow);

		agentLifePositionX = widthWindow - sizeForAgentBarLife;
		agentLifePositionY = heightWindow - sizeForAgentBarLife;

		enemyLifePositionX = sizeForAgentBarLife;
		enemyLifePositionY = heightWindow - sizeForAgentBarLife;

		blockSizeX = (widthWindow / w);
		blockSizeY = (heightWindow / h);

		// Images must be in the "data" directory to load correctly
		System.out.println(Output.originalPath + "/Imagenes/tank/" + "background.png");
	}

	public void setup() {
		// IMAGES
		imageBackground = loadImage(Output.originalPath + "/Imagenes/tank/" + "background.png");
		imageAgentTank = loadImage(Output.originalPath + "/Imagenes/tank/" + "agentTank.png");
		imageEnemyTank = loadImage(Output.originalPath + "/Imagenes/tank/" + "enemyTank.png");
	}

	public void draw() {
		//		System.out.println("redrawing");

		// BACKGROUND
		imageBackground.resize(widthWindow, heightWindow);
		background(imageBackground); // 255

		// CHECKING IF THE GAME HAS FINISHED
		if (currentState != null && currentState.getState()[STATES.AGENTE_DESTRUIDO.ordinal()] == 1
				|| currentState != null && currentState.getState()[STATES.ENEMIGO_DESTRUIDO.ordinal()] == 1 || false) {
			// Loading a victory or fail image
			PImage imagen = null;

			if (currentState != null && currentState.getState()[STATES.AGENTE_DESTRUIDO.ordinal()] == 1)
				imagen = loadImage(Output.originalPath + "/Imagenes/tank/" + "AGENTE_DESTRUIDO" + ".png");
			else if (currentState != null && currentState.getState()[STATES.ENEMIGO_DESTRUIDO.ordinal()] == 1)
				imagen = loadImage(Output.originalPath + "/Imagenes/tank/" + "ENEMIGO_DESTRUIDO" + ".png");

			// Displaying the image
			fill(0);
			noStroke();
			rect((int) (w / 2) * blockSizeX, (int) (h / 2) * blockSizeY, (int) (blockSizeX), (int) (blockSizeY));
			image(imagen, (int) (w / 2) * blockSizeX, (int) (h / 2) * blockSizeY, (int) (blockSizeX),
					(int) (blockSizeY));

			// Stop drawing
			noLoop();
		}

		// DRAWING LINES
		stroke(0);
		for (int i = 0; i < w; i++) {
			strokeWeight(strokeWeight);
			line(i * blockSizeX, 0, i * blockSizeX, height);
		}

		for (int i = 0; i < h; i++) {
			strokeWeight(strokeWeight);
			line(0, i * blockSizeY, width, i * blockSizeY);
		}

		// DRAWING ZONES
		if (mapState != null) {
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					if (mapState.get(new Point(i, j)) != null) {
						PImage imagen = loadImage(Output.originalPath + "/Imagenes/tank/"
								+ mapState.get(new Point(i, j)).name() + ".png");
						image(imagen, (int) i * blockSizeX, (int) (h - j - 1) * blockSizeY, blockSizeX, blockSizeY);
					}
				}
			}
		}

		// SETTING THE ENEMY
		image(imageEnemyTank, (int) (w - 1) * blockSizeX, (int) 0 * blockSizeY, blockSizeX, blockSizeY);

		// DRAWING THE TANK
		image(imageAgentTank, (int) agentPosition.getX() * blockSizeX, (int) agentPosition.getY() * blockSizeY,
				blockSizeX, blockSizeY);

		// CHECKING AIR ATTACK
		if (currentState != null && currentState.getState()[STATES.RECIBIENDO_ATAQUE_AEREO.ordinal()] == 1) {
			ellipseMode(CORNER);
			ellipse((int) agentPosition.getX() * blockSizeX + (int) (blockSizeX / (6.5)),
					(int) agentPosition.getY() * blockSizeY + (int) (blockSizeY / (6.5)), (int) (blockSizeX / (1.5)),
					(int) (blockSizeY / (1.5)));

			PImage imagen = loadImage(Output.originalPath + "/Imagenes/tank/" + "RECIBIENDO_ATAQUE_AEREO" + ".png");

			image(imagen, (int) agentPosition.getX() * blockSizeX + (int) (blockSizeX / 4),
					(int) agentPosition.getY() * blockSizeY + (int) (blockSizeY / 4), (int) (blockSizeX / (2)),
					(int) (blockSizeY / (2)));
		}

		// DRAWING THE LIFE OF THE AGENT TANK (USING THE PERCENTAGE OF LIFE)
		stroke(0, 255, 0);
		strokeWeight(12);
		strokeCap(ROUND);
		line(agentLifePositionX, agentLifePositionY, agentLifePositionX,
				(int) (0 + heightWindow * (100 - agentLife) / 100));
		stroke(0);

		// DRAWING THE LIFE OF THE ENEMY TANK (USING THE PERCENTAGE OF LIFE)
		stroke(255, 0, 0);
		strokeWeight(12);
		strokeCap(ROUND);
		line(enemyLifePositionX, enemyLifePositionY, enemyLifePositionX,
				(int) (0 + heightWindow * (100 - enemyLife) / 100));
		stroke(0);

		// DRAW GREEDY PATH
		if (true) {
			//			nextStates = learner.getNextStates(1, learner.getNewState());
			if (nextStates != null) {
				noFill();
				strokeWeight(2);
				stroke(0, 0, 255);

				for (int i = 0; i < nextStates.length; i++) {
					double[] agentPosition = nextStates[i].agentPosition();
					rect((int) agentPosition[0] * blockSizeX + (int) (blockSizeX * (0)),
							(int) (h - agentPosition[1] - 1) * blockSizeY - (int) (blockSizeY * (1 / 4)),
							(int) (blockSizeX / (1)), (int) (blockSizeY / (1)));
				}
			} else {
				System.out.println("no next states");
			}
		}

		// Stop drawing (each time something change in the map will be redraw by the setting methods)
		noLoop();
	}

	// GUI CHANGERS METHODS
	public void setAgentLife(double agentLife) {
		this.agentLife = agentLife;
		redraw();
	}

	public void setAgentPosition(Point agentPosition) {
		this.agentPosition = new Point();
		this.agentPosition.setLocation(agentPosition.getX(), h - agentPosition.getY() - 1);
		//		System.out.println(this.agentPosition);
		redraw();
	}

	public void setEnemyLife(double enemyLife) {
		this.enemyLife = enemyLife;
		redraw();
	}

	public void setMapState(Map<Point, STATES> mapState) {
		this.mapState = mapState;
		redraw();
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
		redraw();
	}

	public void setNextStates(State[] nextStates) {
		this.nextStates = nextStates;
	}

}