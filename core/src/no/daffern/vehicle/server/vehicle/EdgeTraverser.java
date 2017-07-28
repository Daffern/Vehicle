package no.daffern.vehicle.server.vehicle;

import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class EdgeTraverser {


	private final static int LEFT = 0;
	private final static int UP = 1;
	private final static int RIGHT = 2;
	private final static int DOWN = 3;

	/**
	 * @param start
	 * @param goalWalls can only be on the traversing edge
	 * @return
	 */
	public static boolean traverse(Wall start, List<Wall> goalWalls) {

		Wall wall = start;

		int direction = UP;

		boolean searching = true;

		List<Integer> path = new ArrayList<>();

		while (searching) {

			boolean left = wall.left != null;
			boolean right = wall.right != null;
			boolean down = wall.down != null;
			boolean up = wall.up != null;

			//clockwise search
			switch (direction) {
				case LEFT:
					if (down) {
						wall = wall.down;
						direction = DOWN;
					}
					else if (left) {
						wall = wall.left;
						direction = LEFT;
					}
					else if (up) {
						wall = wall.up;
						direction = UP;
					}
					else if (right) {
						wall = wall.right;
						direction = RIGHT;
					}
					break;
				case UP:
					if (left) {
						wall = wall.left;
						direction = LEFT;
					}
					else if (up) {
						wall = wall.up;
						direction = UP;
					}
					else if (right) {
						wall = wall.right;
						direction = RIGHT;
					}
					else if (down) {
						wall = wall.down;
						direction = DOWN;
					}
					break;
				case RIGHT:
					if (up) {
						wall = wall.up;
						direction = UP;
					}
					else if (right) {
						wall = wall.right;
						direction = RIGHT;
					}
					else if (down) {
						wall = wall.down;
						direction = DOWN;
					}
					else if (left) {
						wall = wall.left;
						direction = LEFT;
					}
					break;
				case DOWN:
					if (right) {
						wall = wall.right;
						direction = RIGHT;
					}
					else if (down) {
						wall = wall.down;
						direction = DOWN;
					}
					else if (left) {
						wall = wall.left;
						direction = LEFT;
					}
					else if (up) {
						wall = wall.up;
						direction = UP;
					}
					break;

			}

			path.add(direction);

			//remove goalNode if found
			for (int i = 0; i < goalWalls.size(); i++) {
				if (goalWalls.get(i) == wall) {
					goalWalls.remove(i);
					break;
				}
			}

			//all goal nodes found
			if (goalWalls.size() == 0) {
				printPath(path);
				return true;
			}
			//traversed the edge and ended up at start
			else if (wall == start) {
				printPath(path);
				return false;
			}

		}

		return false;
	}

	public static boolean traverseClockwise(Wall start, List<Wall> goalNodes){


		return false;
	}

	private static void printPath(List<Integer> path) {
		String output = "";

		for (int i : path) {

			switch (i) {
				case LEFT:
					output += "left\n";
					break;
				case RIGHT:
					output += "right\n";
					break;
				case DOWN:
					output += "down\n";
					break;
				case UP:
					output += "up\n";
					break;

			}
		}

		Tools.log(EdgeTraverser.class,output);

	}

}
