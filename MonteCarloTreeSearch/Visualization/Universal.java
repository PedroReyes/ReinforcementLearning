package Visualization;

import java.util.ArrayList;

public class Universal {
	/**
	 * Simmple graph layout system
	 * http://processingjs.nihongoresources.com/graphs
	 * This code is in the public domain
	 */

	// =============================================
	//	      Some universal helper functions
	// =============================================

	// universal helper function: get the angle (in radians) for a particular dx/dy
	public static float getDirection(double dx, double dy) {
		// quadrant offsets
		double d1 = 0.0;
		double d2 = Math.PI / 2.0;
		double d3 = Math.PI;
		double d4 = 3.0 * Math.PI / 2.0;
		// compute angle basd on dx and dy values
		double angle = 0;
		float adx = Math.abs((float) dx);
		float ady = Math.abs((float) dy);
		// Vertical lines are one of two angles
		if (dx == 0) {
			angle = (dy >= 0 ? d2 : d4);
		}
		// Horizontal lines are also one of two angles
		else if (dy == 0) {
			angle = (dx >= 0 ? d1 : d3);
		}
		// The rest requires trigonometry (note: two use dx/dy and two use dy/dx!)
		else if (dx > 0 && dy > 0) {
			angle = d1 + Math.atan(ady / adx);
		} // direction: X+, Y+
		else if (dx < 0 && dy > 0) {
			angle = d2 + Math.atan(adx / ady);
		} // direction: X-, Y+
		else if (dx < 0 && dy < 0) {
			angle = d3 + Math.atan(ady / adx);
		} // direction: X-, Y-
		else if (dx > 0 && dy < 0) {
			angle = d4 + Math.atan(adx / ady);
		} // direction: X+, Y-
			// return directionality in positive radians
		return (float) ((angle + 2 * Math.PI) % (2 * Math.PI));
	}

	// universal helper function: rotate a coordinate over (0,0) by [angle] radians
	public static int[] rotateCoordinate(float x, float y, float angle) {
		int[] rc = { 0, 0 };
		rc[0] = (int) (x * Math.cos(angle) - y * Math.sin(angle));
		rc[1] = (int) (x * Math.sin(angle) + y * Math.cos(angle));
		return rc;
	}

	// universal helper function for Processing.js - 1.1 does not support ArrayList.addAll yet
	public static void addAll(ArrayList a, ArrayList b) {
		for (Object o : b) {
			a.add(o);
		}
	}

	// distance between two points
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
}
