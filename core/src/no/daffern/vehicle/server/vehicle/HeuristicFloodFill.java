package no.daffern.vehicle.server.vehicle;


import java.util.List;
import java.util.PriorityQueue;

public class HeuristicFloodFill {


	/**
	 * Tests if the walls has a path to each other
	 * @param walls to test
	 * @return true if path
	 */
	public static boolean hasPath(List<Wall> walls){

		PriorityQueue<Node> queue = new PriorityQueue<>();


		int floodFillValue = Wall.nextFloodFillValue++;

		Wall startWall = walls.remove(0);
		startWall.floodFillValue = floodFillValue;

		queue.add(new Node(startWall,0));

		while(queue.size() > 0){

			//get the node with the lowest heuristic
			Node node = queue.poll();

			//check if its on of the goal walls
			for (int i = 0 ; i < walls.size() ; i++){
				if (walls.get(i) == node.wall){
					walls.remove(i);
					break;
				}
			}
			//return true if all goal walls are found
			if (walls.size() == 0){
				return true;
			}


			//add nearby neighbours if they are not checked
			List<Wall> neighbours = node.wall.getNearbyWalls();

			for(Wall wall : neighbours){

				if (wall.floodFillValue == floodFillValue)
					continue;

				wall.floodFillValue = floodFillValue;


				queue.add(new Node(wall, calculateHeuristic(wall, walls)));
			}




		}





		return false;
	}

	private static int calculateHeuristic(Wall startWall, List<Wall> goalWalls){

		int h = Integer.MAX_VALUE;

		for (Wall endWall : goalWalls){
			int newH = Math.abs(startWall.getWallX() - endWall.getWallX()) + Math.abs(startWall.getWallY() - endWall.getWallY());
			if (newH < h)
				h = newH;
		}

		return h;
	}



	private static class Node implements Comparable<Node>{
		int h;
		Wall wall;

		public Node(Wall wall, int h){
			this.wall = wall;
			this.h = h;
		}

		@Override
		public int compareTo(Node o) {
			return -Math.abs(o.h - h);
		}
	}
}
