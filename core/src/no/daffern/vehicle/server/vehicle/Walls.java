package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.container.DynamicMultiArray;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.server.vehicle.parts.Part;
import no.daffern.vehicle.server.vehicle.parts.network.NetworkManager;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 17.05.2017.
 *
 * NO NETWORKING IN HERE
 */
public class Walls {

	World world;
	Body vehicleBody;

	DynamicMultiArray<Wall> array;

	List<Wall> animatedWalls;

	NetworkManager networkManager;

	public Walls(World world, Body vehicleBody, int initialWidth, int initialHeight) {
		this.array = new DynamicMultiArray<>(initialWidth, initialHeight);
		this.world = world;
		this.vehicleBody = vehicleBody;
		this.animatedWalls = new ArrayList<>();
		this.networkManager = new NetworkManager(world);
	}


	public boolean setWall(Wall wall, boolean force) {

		int x = wall.getWallX();
		int y = wall.getWallY();


		Wall oldWall = array.get(x,y);
		if (oldWall != null)
			return false;

		if (!force && wall != null) {
			if (!hasNearbyWall(x, y)) {
				Tools.log(this, "cannot place wall at: " + x + " " + y + " - has no nearby walls");
				return false;
			}

			if (wall.checkCollision(vehicleBody, 0.01f)) {
				Tools.log(this, "cannot place wall at: " + x + " " + y + " - collision");
				return false;
			}
		}

		array.set(x, y, wall);

		updateWallRelations(x, y);
		createWall(x, y);//update physics

		return true;
	}
	//true if removed
	public boolean removeWall(int x, int y) {

		Wall wall = array.get(x, y);

		List<Wall> nearbyWalls = wall.getNearbyWalls();

		//set to null and update relations
		array.set(x, y, null);
		updateWallRelations(x, y);

		//if there are more than 1 nearby walls, check if they all can still connect to each other
		boolean hasPath = true;
		if (nearbyWalls.size() > 1) {
			hasPath = HeuristicFloodFill.hasPath(nearbyWalls);
		}

		//update the physics
		if (hasPath) {
			if (wall != null) {
				wall.destroyWall(vehicleBody);
			}
			createWall(x, y);
		}
		//reset wall
		else {
			array.set(x, y, wall);
			updateWallRelations(x, y);
		}

		return hasPath;
	}

	private void updateWallRelations(int x, int y) {

		Wall wall = array.get(x, y);
		Wall left = array.get(x - 1, y);
		Wall right = array.get(x + 1, y);
		Wall down = array.get(x, y - 1);
		Wall up = array.get(x, y + 1);

		if (wall != null) {
			wall.left = left;
			wall.right = right;
			wall.up = up;
			wall.down = down;
		}
		if (left != null) {
			left.right = wall;
		}
		if (right != null) {
			right.left = wall;
		}
		if (up != null) {
			up.down = wall;
		}
		if (down != null) {
			down.up = wall;
		}
	}

	public void createWall(int x, int y) {
		Wall wall = array.get(x, y);

		Wall left = array.get(x - 1, y);
		Wall right = array.get(x + 1, y);
		Wall down = array.get(x, y - 1);
		Wall up = array.get(x, y + 1);

		if (wall != null) {
			wall.createWall(vehicleBody);
		}
		if (left != null) {
			left.updateWall(vehicleBody);
		}
		if (right != null) {
			right.updateWall(vehicleBody);
		}
		if (up != null) {
			up.updateWall(vehicleBody);
		}
		if (down != null) {
			down.updateWall(vehicleBody);
		}
	}


	/**
	 * @param wallIndex
	 * @param part
	 * @return index to the part added (-1 if not added)
	 */

	public int addPart(IntVector2 wallIndex, Part part) {
		Wall wall = array.get(wallIndex.x, wallIndex.y);

		if (wall == null)
			return -1;

		//check for intersection with another part
		int searchRadiusX = (int) Math.ceil(part.getWidth() + Part.MAX_WIDTH / Wall.WALL_WIDTH);
		int searchRadiusY = (int) Math.ceil(part.getHeight() + Part.MAX_HEIGHT / Wall.WALL_HEIGHT);
		float partRadiusWidth = part.getWidth() / 2;
		float partRadiusHeight = part.getHeight() / 2;

		//start and end search walls
		int startX = Math.max(array.startX(), wallIndex.x - searchRadiusX);
		int endX = Math.min(array.endX(), wallIndex.x + searchRadiusX);

		int startY = Math.max(array.startY(), wallIndex.y - searchRadiusY);
		int endY = Math.min(array.endY(), wallIndex.y + searchRadiusY);


		for (int x = startX; x <= endX; x++)
			for (int y = startY; y <= endY; y++) {

				Wall searchWall = array.get(x, y);

				if (searchWall == null || searchWall.getNumParts() == 0)
					continue;

				//distance between the two walls
				float distanceX = Math.abs(wallIndex.x - x) * Wall.WALL_WIDTH;
				float distanceY = Math.abs(wallIndex.y - y) * Wall.WALL_HEIGHT;

				for (int k = 0; k < searchWall.getNumParts(); k++) {

					Part searchPart = searchWall.parts.get(k);

					//check if the part needs to collide with the other part
					if (searchPart.checkCollision(part)) {

						float tempPartRadiusWidth = searchPart.getWidth() / 2;
						float tempPartRadiusHeight = searchPart.getHeight() / 2;

						if (partRadiusWidth + tempPartRadiusWidth > distanceX) {
							if (partRadiusHeight + tempPartRadiusHeight > distanceY)
								return -1;
						}
					}

				}
			}


		//add before attach because axle network creation ordering
		int partIndex = wall.addPart(part);

		part.attach(world, vehicleBody, wall);

		if (part.isDynamic()) {
			animatedWalls.add(wall);
		}

		//do the PartNetwork stuff
		networkManager.tryAddPart(part, wall);


		return partIndex;
	}


	public int removePart(IntVector2 wallIndex, Part part) {
		Wall wall = array.get(wallIndex.x, wallIndex.y);

		int partIndex = wall.removePart(part);

		//remove from the dynamic list
		if (!wall.hasAnimatedPart()) {
			animatedWalls.remove(wall);
		}

		//detach last
		part.detach(world, vehicleBody, wall);

		//do the network stuff
		networkManager.tryRemovePart(part, wall);


		return partIndex;
	}

	public List<Wall> getAnimatedWalls() {
		return animatedWalls;
	}


	public boolean hasNearbyWall(int x, int y) {

		if (array.get(x - 1, y) != null)
			return true;
		if (array.get(x + 1, y) != null)
			return true;
		if (array.get(x, y - 1) != null)
			return true;
		return array.get(x, y + 1) != null;
	}


	public Wall get(int x, int y) {
		return array.get(x, y);
	}

	public int getOffsetX() {
		return array.getOffsetX();
	}

	public int getOffsetY() {
		return array.getOffsetY();
	}

	public int startX() {
		return array.startX();
	}

	public int startY() {
		return array.startY();
	}

	public int endX() {
		return array.endX();
	}

	public int endY() {
		return array.endY();
	}

	public int getSizeX() {
		return array.getSizeX();
	}

	public int getSizeY() {
		return array.getSizeY();
	}
}
