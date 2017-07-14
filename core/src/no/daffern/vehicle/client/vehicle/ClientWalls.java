package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.container.DynamicMultiArray;
import no.daffern.vehicle.network.packets.PartOutputPacket;
import no.daffern.vehicle.network.packets.PartPacket;
import no.daffern.vehicle.network.packets.VehicleLayoutPacket;
import no.daffern.vehicle.network.packets.WallPacket;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daffern on 08.06.2017.
 */
public class ClientWalls {

	int noTile;


	DynamicMultiArray<ClientWall> walls;

	float posX, posY;
	float angle;
	float tileWidth, tileHeight;

	public ClientWalls() {

	}

	public void initialize(VehicleLayoutPacket vlp) {
		this.walls = new DynamicMultiArray<>(10, 10);
		this.posX = Common.toPixelCoordinates(vlp.x);
		this.posY = Common.toPixelCoordinates(vlp.y);
		this.tileWidth = Common.toPixelCoordinates(vlp.partWidth);
		this.tileHeight = Common.toPixelCoordinates(vlp.partHeight);
		this.angle = 0;

		this.noTile = vlp.noTile;


		for (int i = 0; i < vlp.wallPackets.length; i++) {

			WallPacket wallPacket = vlp.wallPackets[i];

			if (wallPacket.itemId == noTile)
				continue;

			setWall(wallPacket);

		}

}

	public void setWall(WallPacket wallPacket) {

		if (wallPacket.itemId == noTile){
			walls.set(wallPacket.x, wallPacket.y, null);
			return;
		}

		ClientWall wall = new ClientWall(wallPacket.itemId);

		walls.set(wallPacket.x, wallPacket.y, wall);

		updateWallTexture(wallPacket.x, wallPacket.y);

		if (wallPacket.partPackets == null)
			return;

		ClientPart[] clientParts = new ClientPart[wallPacket.partPackets.length];

		for (int i = 0 ; i < wallPacket.partPackets.length ; i++){
			PartPacket partPacket = wallPacket.partPackets[i];

			clientParts[i] = new ClientPart(partPacket.itemId,Common.toPixelCoordinates(partPacket.width),Common.toPixelCoordinates(partPacket.height));

		}

		wall.setParts(clientParts);
	}




	public void updateParts(PartOutputPacket[] pops) {

		for (PartOutputPacket pop : pops) {
			ClientWall wall = walls.get(pop.wallX, pop.wallY);

			if (pop.partIndex < 0 || pop.partIndex >= wall.getParts().length) {
				Tools.log(this, "Part index out of bounds received????");
				return;
			}

			ClientPart part = wall.getParts()[pop.partIndex];

			part.angle = MathUtils.radiansToDegrees * pop.angle;
		}
	}


	public void update(float posX, float posY, float angle) {
		this.posX = posX;
		this.posY = posY;
		this.angle = angle;
	}

	private void updateWallTexture(int x, int y) {

		ClientWall wall = walls.get(x, y);
		ClientWall left = walls.get(x - 1, y);
		ClientWall right = walls.get(x + 1, y);
		ClientWall up = walls.get(x, y + 1);
		ClientWall down = walls.get(x, y - 1);


		if (wall != null) {
			wall.left = left;
			wall.right = right;
			wall.up = up;
			wall.down = down;


			wall.updateWallTexture(noTile);
		}
		if (left != null) {
			left.right = wall;

			left.updateWallTexture(noTile);
		}
		if (right != null) {
			right.left = wall;

			right.updateWallTexture(noTile);
		}
		if (down != null) {
			down.up = wall;

			down.updateWallTexture(noTile);
		}
		if (up != null) {
			up.down = wall;

			up.updateWallTexture(noTile);
		}

	}

	//TODO need optimization?
	public void render(Batch batch) {

		for (int x = walls.startX(); x <= walls.endX(); x++) {
			for (int y = walls.startY(); y <= walls.endY(); y++) {


				ClientWall wall = walls.get(x, y);

				if (wall == null)
					continue;

				//render wall
				if (wall.getWallTexture() != null) {
					batch.draw(wall.getWallTexture(),
							posX + (x * tileWidth), posY + (y * tileHeight),
							-(x * tileWidth), -(y * tileHeight),
							tileWidth, tileHeight,
							1, 1, angle);
				}
			}
		}

		for (int x = walls.startX(); x <= walls.endX(); x++) {
			for (int y = walls.startY(); y <= walls.endY(); y++) {

				ClientWall wall = walls.get(x, y);

				if (wall == null)
					continue;

				//render parts
				if (wall.getParts() != null) {

					for (ClientPart part : wall.getParts()) {

						Vector2 point = Tools.rotatePoint(
								posX + x * tileWidth + tileWidth / 2,
								posY + y * tileHeight + tileHeight / 2,
								posX, posY, MathUtils.degreesToRadians * angle);

						if (part.region != null)
							batch.draw(part.region,
									point.x - part.width / 2, point.y - part.height / 2,
									part.width / 2, part.height / 2,
									part.width, part.height,
									1, 1, part.angle - angle);
					}
				}
			}
		}
	}
}
