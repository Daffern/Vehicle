package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.*;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.vehicle.parts.Part;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 14.11.2016.
 */
public class ServerVehicle {

	private static int nextVehicleId = 0;

	private static int vehicleWidth = 10;
	private static int vehicleHeight = 10;


	Walls walls;

	World world;

	Body vehicleBody;

	public int vehicleId;

	float x, y, width, height;

	//VehicleShapeBuilder vehicleShapeBuilder;

	public ServerVehicle(float sx, float sy) {
		world = S.worldHandler.world;

		vehicleId = nextVehicleId++;

		this.x = Common.toWorldCoordinates(sx);
		this.y = Common.toWorldCoordinates(sy);
		this.width = vehicleWidth * Wall.WALL_WIDTH;
		this.height = vehicleWidth * Wall.WALL_HEIGHT;


		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		vehicleBody = world.createBody(bodyDef);


		walls = new Walls(world, vehicleBody, vehicleWidth, vehicleHeight);


		int wallId = S.itemHandler.getItemsOfType(GameItemTypes.WALL_TYPE_SQUARE).get(0).itemId;

		walls.setWall(new WallSquare(wallId, 0, 0), true);
		walls.setWall(new WallSquare(wallId, 1, 0), true);
		walls.setWall(new WallSquare(wallId, 2, 0), true);


		//vehicleShapeBuilder = new VehicleShapeBuilder(world);
		//vehicleBody = vehicleShapeBuilder.createNewBody(x, y, walls);


		int wheelId = S.itemHandler.getItemsOfType(GameItemTypes.PART_TYPE_WHEEL).get(0).itemId;

/*
        addPart(new IntVector2(9, 0), new PartWheel(wheelId));
        addPart(new IntVector2(4, 0), new PartWheel(wheelId));
        addPart(new IntVector2(0, 0), new PartWheel(wheelId));*/

		sendVehicleLayout();
	}


	public boolean setWall(Wall wall) {

		boolean wallSet = walls.setWall(wall, false);

		if (wallSet) {
			sendWallPacket(wall.getWallX(), wall.getWallY());

		}


		return wallSet;
	}

	public boolean removeWall(IntVector2 wallIndex) {
		if (walls.removeWall(wallIndex.x, wallIndex.y)) {

			sendWallPacket(wallIndex);
			return true;

		}
		return false;
	}

	public Wall getWall(int x, int y) {
		return walls.get(x, y);
	}
	public Wall getWallByPos(float x, float y){
		IntVector2 index = findTileIndex(x,y);
		if (index == null)
			return null;
		return walls.get(index.x, index.y);
	}

	public boolean addPart(IntVector2 wallIndex, Part part) {
		int partIndex = walls.addPart(wallIndex, part);

		if (partIndex >= 0) {

			sendWallPacket(wallIndex);

			return true;
		}

		return false;
	}


	public boolean removePart(IntVector2 wallIndex, Part part) {

		int index = walls.removePart(wallIndex, part);

		if (index >= 0) {
			sendWallPacket(wallIndex);
			return true;
		}

		return false;
	}
	public void interactPart1(int x, int y){
		Wall wall = walls.get(x,y);
		if (wall == null)
			return;
		for (int i = 0 ; i < wall.getNumParts() ; i++){
			Part part = wall.getPart(i);
			if (part.interact1()){
				sendPartUpdate(x,y,part);
			}
		}
	}

	public void sendVehicleLayout() {

		List<WallPacket> wallPackets = new ArrayList<>();

		for (int x = walls.startX(); x <= walls.endX(); x++) {
			for (int y = walls.startY(); y <= walls.endY(); y++) {

				Wall wall = walls.get(x, y);

				if (wall == null)
					return;


				WallPacket wallPacket = new WallPacket();
				wallPacket.vehicleId = vehicleId;
				wallPacket.itemId = wall.getItemId();
				wallPacket.x = wall.getWallX();
				wallPacket.y = wall.getWallY();

				int numParts = wall.getNumParts();

				if (numParts > 0) {
					wallPacket.partPackets = new PartPacket[numParts];

					for (int i = 0; i < wall.getNumParts(); i++) {

						Part part = wall.getPart(i);

						wallPacket.partPackets[i] = new PartPacket(part.getItemId(), part.getType(), part.getLayer(), part.getWidth(), part.getHeight(), part.getAngle(), part.getState());


					}
				}

				wallPackets.add(wallPacket);


			}
		}

		VehicleLayoutPacket vehicleLayoutPacket = new VehicleLayoutPacket();
		vehicleLayoutPacket.wallPackets = wallPackets.toArray(new WallPacket[wallPackets.size()]);
		vehicleLayoutPacket.vehicleId = vehicleId;
		vehicleLayoutPacket.x = x;
		vehicleLayoutPacket.y = y;
		vehicleLayoutPacket.width = width;
		vehicleLayoutPacket.height = height;
		vehicleLayoutPacket.wallWidth = Wall.WALL_WIDTH;
		vehicleLayoutPacket.wallHeight = Wall.WALL_HEIGHT;
		vehicleLayoutPacket.noTile = 0;

		S.myServer.sendToAllTCP(vehicleLayoutPacket);
	}


	private void sendWallPacket(IntVector2 wallIndex) {
		sendWallPacket(wallIndex.x, wallIndex.y);
	}

	private void sendWallPacket(int x, int y) {

		WallPacket wallPacket = new WallPacket();
		wallPacket.vehicleId = vehicleId;
		wallPacket.x = x;
		wallPacket.y = y;

		Wall wall = walls.get(x,y);

		if (wall == null)
			wallPacket.itemId = 0;
		else {
			wallPacket.itemId = wall.getItemId();

			int numParts = wall.getNumParts();

			if (numParts > 0) {
				wallPacket.partPackets = new PartPacket[numParts];

				for (int i = 0; i < wall.getNumParts(); i++) {

					Part part = wall.getPart(i);

					wallPacket.partPackets[i] = new PartPacket(part.getItemId(), part.getType(), part.getLayer(), part.getWidth(), part.getHeight(), part.getAngle(), part.getState());

				}
			}
		}
		S.myServer.sendToAllTCP(wallPacket);
	}
	private void sendPartUpdate(int x, int y, Part part){
		PartOutputPacket pop = new PartOutputPacket();
		pop.vehicleId = vehicleId;
		pop.wallX = x;
		pop.wallY = y;
		pop.angle = part.getAngle();
		pop.layer = part.getLayer();
		pop.state = part.getState();
		S.myServer.sendToAllTCP(pop);
	}

	public IntVector2 findTileIndex(float x, float y) {
		Vector2 pos = vehicleBody.getPosition();

		Vector2 rp = Tools.rotatePoint(x, y, pos.x, pos.y, -vehicleBody.getAngle());

        /*
        if (rp.x < pos.x || rp.x > pos.x + width || rp.y < pos.y || rp.y > pos.y + height)
            return null;
        */

		IntVector2 vec = new IntVector2();


		vec.x = MathUtils.floor((rp.x - pos.x) / Wall.WALL_WIDTH);
		vec.y = MathUtils.floor((rp.y - pos.y) / Wall.WALL_HEIGHT);

		return vec;
	}

	public Vector2 findWallPosition(int i, int j) {

		Vector2 vehiclePos = vehicleBody.getPosition();

		float wallX = vehiclePos.x + i * Wall.WALL_WIDTH + Wall.WALL_WIDTH / 2;
		float wallY = vehiclePos.y + j * Wall.WALL_HEIGHT + Wall.WALL_HEIGHT / 2;

		Vector2 rp = Tools.rotatePoint(wallX, wallY, vehiclePos.x, vehiclePos.y, vehicleBody.getAngle());

		return rp;
	}


	public boolean isInside(float x, float y) {
		Vector2 pos = vehicleBody.getPosition();

		Vector2 rp = Tools.rotatePoint(x, y, pos.x, pos.y, -vehicleBody.getAngle());

		return !(rp.x < pos.x) && !(rp.x > pos.x + width) && !(rp.y < pos.y) && !(rp.y > pos.y + height);

	}

	public Vector2 findPlayerSpawnPoint() {

		for (int x = walls.startX(); x <= walls.endX(); x++) {
			for (int y = walls.startY(); y <= walls.endY(); y++) {

				Wall wall = walls.get(x, y);

				if (wall != null && wall.getType() == GameItemTypes.WALL_TYPE_SQUARE && wall.getNumParts() == 0) {

					return findWallPosition(x, y);
				}
			}
		}
		return null;
	}



	public void preStep() {

	}


	public void postStep() {

		//send the animated walls. optimize?
		List<Wall> animatedWalls = walls.getAnimatedWalls();

		List<PartOutputPacket> partUpdates = new ArrayList<>();

		for (int i = 0; i < animatedWalls.size(); i++) {

			Wall wall = animatedWalls.get(i);

			for (int j = 0 ; j < wall.getNumParts() ; j++){
				Part part = wall.getPart(j);
				if (part.isDynamic()){
					partUpdates.add(new PartOutputPacket(wall.getWallX(), wall.getWallY(), part.getLayer(), part.getAngle(), part.getState()));
				}
			}
		}

		//send packet
		Vector2 position = vehicleBody.getPosition();

		VehicleOutputPacket vehicleOutputPacket = new VehicleOutputPacket();
		vehicleOutputPacket.vehicleId = vehicleId;
		vehicleOutputPacket.position = position;
		vehicleOutputPacket.angle = vehicleBody.getAngle();
		vehicleOutputPacket.partUpdates = partUpdates.toArray(new PartOutputPacket[partUpdates.size()]);

		S.myServer.sendToAllUDP(vehicleOutputPacket);


	}
}
