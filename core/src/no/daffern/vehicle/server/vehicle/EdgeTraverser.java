package no.daffern.vehicle.server.vehicle;

import java.util.ArrayList;
import java.util.List;

@Deprecated //does not work
public class EdgeTraverser {


	private final static int LEFT = 0;
	private final static int UP = 1;
	private final static int RIGHT = 2;
	private final static int DOWN = 3;


	public static boolean traverse(List<Wall> traverseWalls){




		Wall wall = traverseWalls.remove(0);


		List<Wall> searchWalls = new ArrayList<>();
		searchWalls.addAll(traverseWalls);

		int floodFillValue = Wall.nextFloodFillValue++;

		while(searchWalls.size() > 0){

			boolean left = wall.left != null;
			boolean right = wall.right != null;
			boolean down = wall.down != null;
			boolean up = wall.up != null;




		}


		return false;
	}








	/**
	 * @param traverseWalls can only be on the traversing edge
	 * @return
	 */
	public static boolean traverseEdges(List<Wall> traverseWalls){

		List<Integer> path = new ArrayList<>();

		if (traverseClockWise(traverseWalls, path)){
			return true;
		}

		path.clear();

		if (traverseCounterClockwise(traverseWalls,path)){



		}


		return false;
	}


	public static boolean traverseClockWise(List<Wall> traverseWalls, List<Integer> path) {


		//find the upperleftmost wall
		Wall wall = traverseWalls.get(0);

		for (int i = 1 ; i < traverseWalls.size() ; i++){
			Wall searchWall = traverseWalls.get(i);
			if (searchWall.getWallX() + searchWall.getWallY() < wall.getWallX() + wall.getWallY()){
				wall = searchWall;
			}
		}

		List<Wall> goalWalls = copyAndExclude(traverseWalls, wall);


		int direction = DOWN;

		boolean searching = true;

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

		}

		return false;
	}

	public static boolean traverseCounterClockwise(List<Wall> goalNodes, List<Integer> path){


		return false;
	}

	private static String pathToString(List<Integer> path) {
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

		return output;
	}


	public static <T> List<T>  copyAndExclude(List<T> list, T exclude){

		List<T> newList = new ArrayList<>(list.size()-1);

		for (int i = 0 ; i < list.size() ; i++){

			if (list.get(i) != exclude){
				newList.add(list.get(i));
			}

		}

		return newList;
	}
}
