package no.daffern.vehicle.server.vehicle;

import no.daffern.vehicle.container.IntVector2;

import java.util.ArrayList;
import java.util.List;


public class WallsPathfinder {

	private static int nextFloodFillValue = 0;

	public boolean floodFillSearch(Wall start, Wall goal, List<Wall> path) {

		int floodFillValue = getNextFloodFillValue();

		return recursiveFloodFill(start, goal, floodFillValue, path);

	}

	private boolean recursiveFloodFill(Wall current, Wall goal, int floodValue, List<Wall> path) {

		if (current == null)
			return false;

		if (current.floodFillValue == floodValue)
			return false;

		current.floodFillValue = floodValue;

		path.add(current);

		if (current == goal)
			return true;

		//TODO do some heuristic?
		recursiveFloodFill(current.left, goal, floodValue, path);
		recursiveFloodFill(current.right, goal, floodValue, path);
		recursiveFloodFill(current.down, goal, floodValue, path);
		recursiveFloodFill(current.up, goal, floodValue, path);

		path.remove(current);

		return false;
	}


	private int getNextFloodFillValue() {
		nextFloodFillValue++;
		if (nextFloodFillValue == Integer.MAX_VALUE) {
			nextFloodFillValue = 0;
		}
		return nextFloodFillValue;
	}
}
