package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.container.DynamicMultiArray;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 17.05.2017.
 */
public class Walls {

	World world;
	Body vehicleBody;

	DynamicMultiArray<Wall> array;

	List<Wall> animatedWalls;


	public Walls(World world, Body vehicleBody, int initialWidth, int initialHeight) {
		array = new DynamicMultiArray<>(initialWidth, initialHeight);
		this.world = world;
		this.vehicleBody = vehicleBody;
		this.animatedWalls = new ArrayList<>();
	}


	public boolean setWall(Wall wall, boolean force) {

		int x = wall.getWallX();
		int y = wall.getWallY();

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

		updateWall(x, y, wall);

		return true;
	}

	public Wall removeWall(int x, int y) {
		Wall wall = array.get(x,y);
		updateWall(x,y,null);
		return wall;
	}

	private void updateWall(int x, int y, Wall wall) {

		Wall previousWall = array.get(x, y);

		if (previousWall != null) {
			previousWall.destroyWall(vehicleBody);
		}

		array.set(x, y, wall);

		Wall left = array.get(x - 1, y);
		Wall right = array.get(x + 1, y);
		Wall down = array.get(x, y - 1);
		Wall up = array.get(x, y + 1);

		if (wall != null) {
			wall.left = left;
			wall.right = right;
			wall.up = up;
			wall.down = down;
			wall.createWall(vehicleBody);
		}
		if (left != null) {
			left.right = wall;
			left.updateWall(vehicleBody);
		}
		if (right != null) {
			right.left = wall;
			right.updateWall(vehicleBody);
		}
		if (up != null) {
			up.down = wall;
			up.updateWall(vehicleBody);
		}
		if (down != null) {
			down.up = wall;
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

		//TODO se på det her igjen (noen deler trenger ikke kollidere med hverandre)
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


		for (int x = startX; x < endX; x++)
			for (int y = startY; y < endY; y++) {

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

						if (partRadiusWidth + tempPartRadiusWidth < distanceX ) {
							if (partRadiusHeight + tempPartRadiusHeight < distanceY)
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

		return partIndex;
	}
	//TODO
	/*
    public boolean hasPath(int i1, int j1, int i2, int j2){
        Wall from = walls[i1][j1];
        Wall to = walls[i2][j2];




    }*/

	public int removePart(IntVector2 wallIndex, Part part) {
		Wall wall = array.get(wallIndex.x, wallIndex.y);

		part.detach(world, vehicleBody);

		int partIndex = wall.removePart(part);
		//remove from the dynamic list
		if (!wall.hasAnimatedPart()){
			animatedWalls.remove(wall);
		}

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
		if (array.get(x, y + 1) != null)
			return true;
		return false;
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
