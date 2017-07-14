package no.daffern.vehicle.server.vehicle;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.handlers.Entity;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Daffern on 14.11.2016.
 */
public abstract class Wall extends Entity {

	public static final float WALL_WIDTH = 1f;
	public static final float WALL_HEIGHT = 1f;

	private int itemId;

	private int wallX, wallY;

	public Wall left, right, up, down;

	protected List<Part> parts;

	protected boolean isCreated = false;

	public Wall(int itemId, int type, int wallX, int wallY) {
		super(GameItemTypes.WALL_TYPE_NONE);

		this.itemId = itemId;
		this.type = type;
		this.wallX = wallX;
		this.wallY = wallY;

	}


	/**
	 * adds a part and sorts the parts after type(from lowest to highest)
	 *
	 * @param part
	 * @return index to the part added, -1 if part type already exists
	 */
	int addPart(Part part) {

		int partIndex = -1;
		if (parts == null || parts.size() == 0) {
			parts = new Vector<>(1, 1);
			parts.add(part);
			partIndex = 0;
		}
		else {
			//insert in order of type
			int type = part.getType();

			for (int i = 0; i < parts.size(); i++) {
				int searchType = parts.get(i).getType();

				if (type > searchType) {
					//continue if there are more parts to check, else add
					if (parts.size() - i > 1)
						continue;
					else{
						parts.add(part);
						partIndex =  parts.size()-1;
						break;
					}
				}
				else if (type < searchType) {
					parts.add(i, part);
					partIndex = i;
					break;
				}
				else if (type == searchType) {
					Tools.log(this, "Did not add partType: " + type + ", it already exists on this wall");
					partIndex = -1;
					break;
				}
			}
		}
		return partIndex;
	}

	/**
	 * @param part
	 * @return index of the part removed
	 */
	int removePart(Part part) {
		if (parts == null)
			return -1;

		int index = -1;

		for (int i = 0; i < parts.size(); i++) {
			if (part == parts.get(i)) {
				index = i;
				parts.remove(i);
				break;
			}
		}

		if (parts.size() == 0)
			parts = null;

		return index;
	}
	//

	public Part getPart(int part) {
		return parts.get(part);
	}

	public boolean hasAnimatedPart(){
		for (int i = 0 ; i < getNumParts() ; i++){
			if (getPart(i).isDynamic())
				return true;

		}
		return false;
	}

	public boolean containsPartType(int partType) {
		if (parts == null)
			return false;
		for (Part part : parts) {
			if (part.getType() == partType)
				return true;
		}
		return false;
	}

	public Part findPart(int partType) {
		if (parts == null)
			return null;
		for (Part part : parts) {
			if (part.getType() == partType)
				return part;
		}
		return null;
	}

	public int getNumParts() {
		if (parts == null)
			return 0;
		else return parts.size();
	}

	abstract void createWall(Body vehicleBody);

	abstract void destroyWall(Body vehicleBody);

	abstract boolean updateWall(Body vehicleBody);

	abstract boolean checkCollision(Body vehicleBody, float margin);

	public boolean hasNearbyWall() {

		if (left != null && left.type != GameItemTypes.WALL_TYPE_NONE)
			return true;
		if (down != null && down.type != GameItemTypes.WALL_TYPE_NONE)
			return true;
		if (right != null && right.type != GameItemTypes.WALL_TYPE_NONE)
			return true;
		if (up != null && up.type != GameItemTypes.WALL_TYPE_NONE)
			return true;

		return false;
	}

	public boolean isCreated() {
		return isCreated;
	}

	public int getItemId() {
		return itemId;
	}

	public int getWallX() {
		return wallX;
	}

	public int getWallY() {
		return wallY;
	}

	public float getLocalX() {
		return wallX * WALL_WIDTH;
	}

	public float getLocalY() {
		return wallY * WALL_HEIGHT;
	}
}
