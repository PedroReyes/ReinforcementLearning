package Auxiliar;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import WorldGameComplex.GameWorldSimpleMap.STATES;

public class GameMap {

	Map<Point, STATES> map; // map point, state point
	public static int dimX;
	public static int dimY;
	public static boolean usePersonalizedMaps = false;

	// =======================
	// MAP
	// =======================
	// ZA, ZS, ZS, ZS, ZA, ZA
	// ZS, ZS, ZE, ZE, ZA, ZA
	// ZS, ZS, ZE, ZE, ZS, ZS
	// ZS, ZS, ZE, ZE, ZS, ZS
	// ZR, ZS, ZS, ZS, ZS, ZS
	// 3x3
	public static STATES[][] map3x3 = new STATES[][] {
			{ STATES.ZONA_REPARACION, STATES.ZONA_SEGURA, STATES.ZONA_ANTIBOMBARDEOS }, // 1
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 2
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_ATAQUE }, // 3
	};

	public static STATES[][] map4x4 = new STATES[][] {
			{ STATES.ZONA_REPARACION, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_ANTIBOMBARDEOS }, // 1
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA }, // 2
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 3
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_ATAQUE }, // 4 
	};

	// 5x5
	public static STATES[][] map5x5 = new STATES[][] {
			{ STATES.ZONA_REPARACION, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_ANTIBOMBARDEOS }, // 1
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA,
					STATES.ZONA_SEGURA }, // 2
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA }, // 3
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA,
					STATES.ZONA_SEGURA }, // 4 
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_ATAQUE } // 5
	};

	// 6x6
	public static STATES[][] map6x6 = new STATES[][] {
			{ STATES.ZONA_REPARACION, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_ANTIBOMBARDEOS }, // 1
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA }, // 2
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 3
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA }, // 4
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 5
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_ATAQUE } // 6
	};

	// 7x7
	public static STATES[][] map7x7 = new STATES[][] {
			{ STATES.ZONA_REPARACION, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_ANTIBOMBARDEOS }, // 1 
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 2
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA }, // 3
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 4
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA }, // 5
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 6
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_ATAQUE } // 7
	};

	// 8x8
	public static STATES[][] map8x8 = new STATES[][] {
			{ STATES.ZONA_REPARACION, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_ANTIBOMBARDEOS }, // 1 
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA }, // 2
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 3
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA }, // 4
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 5
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA }, // 6
			{ STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA }, // 7
			{ STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA,
					STATES.ZONA_EMBOSCADA, STATES.ZONA_SEGURA, STATES.ZONA_ATAQUE } // 8
	};

	public GameMap() {
		resetMap();
	}

	/**
	 * Set the map to initial state
	 */
	public void resetMap() {
		// Inicializamos el mapa
		map = new HashMap<>();

		// Dimensiones del mapa
		this.dimX = this.dimX == 0 ? 3 : this.dimX;
		this.dimY = this.dimY == 0 ? 3 : this.dimY;

		// ZONAS SEGURAS
		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				map.put(new Point(x, y), STATES.ZONA_SEGURA);
			}
		}

		// ZONAS DE ANTIBOMBARDEO (arriba a la izquierda)
		map.put(new Point(0, dimY - 1), STATES.ZONA_ANTIBOMBARDEOS);

		// ZONAS DE REPARACION (abajo a la izquierda)
		map.put(new Point(0, 0), STATES.ZONA_REPARACION);

		// ZONAS DE ATAQUE (arriba a la derecha)
		map.put(new Point(dimX - 1, dimY - 1), STATES.ZONA_ATAQUE);

		// ZONAS DE EMBOSCADA (parte central)
		int startX = dimX > 1 ? 1 : 1; // si no es mayor de 1 no habra zona de emboscada o sino el agente estaria constatememte en zona de emboscada
		int endX = dimX > 1 ? dimX - 1 : dimX - 1;
		int startY = dimY > 1 ? 1 : 1; // si no es mayor de 1 no habra zona de emboscada o sino el agente estaria constatememte en zona de emboscada
		int endY = dimY > 1 ? dimY - 1 : dimY - 1;

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				map.put(new Point(x, y), STATES.ZONA_EMBOSCADA);
			}
		}

		if (usePersonalizedMaps)
			loadPersonalizedMap();
	}

	/**
	 * Instead of using the map by default, we use a personalized map
	 */
	private void loadPersonalizedMap() {
		// 3x3
		if (dimX == 3 && usePersonalizedMaps) {
			map = new HashMap<>();
			for (int x = 0; x < dimX; x++) {
				for (int y = 0; y < dimY; y++) {
					map.put(new Point(x, y), map3x3[x][y]);
				}
			}
		}

		// 5x5
		if (dimX == 5 && usePersonalizedMaps) {
			map = new HashMap<>();
			for (int x = 0; x < dimX; x++) {
				for (int y = 0; y < dimY; y++) {
					map.put(new Point(x, y), map5x5[x][y]);
				}
			}
		}

		// 6x6
		if (dimX == 6 && usePersonalizedMaps) {
			map = new HashMap<>();
			for (int x = 0; x < dimX; x++) {
				for (int y = 0; y < dimY; y++) {
					map.put(new Point(x, y), map6x6[x][y]);
				}
			}
		}

		// 7x7
		if (dimX == 7 && usePersonalizedMaps) {
			map = new HashMap<>();
			for (int x = 0; x < dimX; x++) {
				for (int y = 0; y < dimY; y++) {
					map.put(new Point(x, y), map7x7[x][y]);
				}
			}
		}

		// 7x7
		if (dimX == 8 && usePersonalizedMaps) {
			map = new HashMap<>();
			for (int x = 0; x < dimX; x++) {
				for (int y = 0; y < dimY; y++) {
					map.put(new Point(x, y), map8x8[x][y]);
				}
			}
		}
	}

	@Override
	public String toString() {
		String mapToString = "\n";

		for (int y = 0; y < dimY; y++) {
			for (int x = 0; x < dimX; x++) {
				mapToString = mapToString + (x == 0 ? "[" : (x == dimX ? "" : ","))
						+ map.get(new Point(x, dimY - y - 1));
			}
			mapToString = mapToString + "]\n";
		}

		mapToString = mapToString + "\n";
		return "GameMap [map=" + mapToString + ", dimX=" + dimX + ", dimY=" + dimY + "]";

	}

	// ===================================
	// AUXILIAR METHODS FOR MAP PURPOSES
	// ===================================
	/**
	 * Check that the coordenates are not out of the map bounds
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean legal(int x, int y) {
		return ((x >= 0) && (x < getDimX()) && (y >= 0) && (y < getDimY()));
	}

	// ==================
	// GETTERS & SETTERS
	// ==================
	public Map<Point, STATES> getMap() {
		return map;
	}

	public String[][] getMapAsMatrix() {
		String[][] map = new String[getDimX()][getDimY()];
		for (Map.Entry<Point, STATES> entry : getMap().entrySet()) {
			//			System.out.println(entry.getKey() + "/" + entry.getValue());
			Point pos = entry.getKey();
			map[pos.x][pos.y] = entry.getValue().name();
		}
		return map;
	}

	public void setMap(Map<Point, STATES> map) {
		this.map = map;
	}

	public int getDimX() {
		return dimX;
	}

	public void setDimX(int dimX) {
		this.dimX = dimX;
	}

	public int getDimY() {
		return dimY;
	}

	public void setDimY(int dimY) {
		this.dimY = dimY;
	}

}
