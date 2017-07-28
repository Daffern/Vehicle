package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.OrderedMap;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.graphics.QuadTileDrawer;
import no.daffern.vehicle.network.packets.PartOutputPacket;
import no.daffern.vehicle.network.packets.PartPacket;
import no.daffern.vehicle.network.packets.VehicleLayoutPacket;
import no.daffern.vehicle.network.packets.WallPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daffern on 08.06.2017.
 */
public class ClientWalls {

	int noTile;

	//layers, parts
	OrderedMap<Integer, Map<IntVector2, ClientPart>> partLayers;


	QuadTileDrawer walls;

	float tileWidth, tileHeight;

	public ClientWalls() {

	}

	public void initialize(VehicleLayoutPacket vlp) {
		//this.walls = new DynamicMultiArray<>(10, 10);
		this.walls = new QuadTileDrawer();
		this.partLayers = new OrderedMap<>();
		this.tileWidth = Common.toPixelCoordinates(vlp.partWidth);
		this.tileHeight = Common.toPixelCoordinates(vlp.partHeight);


		this.noTile = vlp.noTile;


		for (int i = 0; i < vlp.wallPackets.length; i++) {

			WallPacket wallPacket = vlp.wallPackets[i];

			if (wallPacket.itemId == noTile)
				continue;

			setWall(wallPacket);

		}

	}

	public void setWall(WallPacket wallPacket) {

		IntVector2 wallIndex = new IntVector2(wallPacket.x, wallPacket.y);

		if (wallPacket.itemId == noTile) {
			walls.remove(wallIndex);
		}else{
			walls.set(wallIndex, wallPacket.itemId);
		}


		//loop through layers and remove parts on this index
		for (OrderedMap.Entry<Integer, Map<IntVector2, ClientPart>> entry : partLayers.entries()) {
			entry.value.remove(wallIndex);
		}


		//add new parts
		if (wallPacket.partPackets == null)
			return;

		for (int i = 0; i < wallPacket.partPackets.length; i++) {
			PartPacket partPacket = wallPacket.partPackets[i];

			Map<IntVector2, ClientPart> parts = partLayers.get(partPacket.layer);

			if (parts == null) {
				parts = new HashMap<>();
				partLayers.put(partPacket.layer, parts);
			}

			parts.put(wallIndex, new ClientPart(partPacket.itemId, Common.toPixelCoordinates(partPacket.width), Common.toPixelCoordinates(partPacket.height)));

		}

	}


	public void updateParts(PartOutputPacket[] pops) {

		for (PartOutputPacket pop : pops) {

			Map<IntVector2, ClientPart> parts = partLayers.get(pop.layer);

			ClientPart part = parts.get(new IntVector2(pop.wallX, pop.wallY));

			if (part == null) {
				Tools.log(this, "Part was null?????");
				continue;
			}

			part.angle = MathUtils.radiansToDegrees * pop.angle;
		}
	}

/*

	private void updateWallTexture(IntVector2 wallIndex) {

		int x = wallIndex.x;
		int y = wallIndex.y;

		ClientWall wall = walls.get(wallIndex);
		ClientWall left = walls.get(new IntVector2(x - 1, y));
		ClientWall right = walls.get(new IntVector2(x + 1, y));
		ClientWall up = walls.get(new IntVector2(x, y + 1));
		ClientWall down = walls.get(new IntVector2(x, y - 1));

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

	}*/

	//TODO this whole thing
	public void render(Batch batch, float posX, float posY, float angle) {

		//render below wall layer
		Map<IntVector2, ClientPart> layer0 = partLayers.get(-1);
		if (layer0 != null) {
			for (Map.Entry<IntVector2, ClientPart> entry : layer0.entrySet()) {
				renderPart(batch, entry.getValue(), entry.getKey(), posX, posY, angle);
			}
		}

		//render walls
		walls.render(batch, posX, posY, tileWidth, tileHeight, angle);


		//render the other parts
		for (OrderedMap.Entry<Integer, Map<IntVector2,ClientPart>> layerEntry : partLayers.entries()){

			if (layerEntry.key == -1)
				continue;

			for (Map.Entry<IntVector2, ClientPart> entry : layerEntry.value.entrySet()) {
				renderPart(batch, entry.getValue(), entry.getKey(), posX, posY, angle);
			}
		}



	}

	private void renderPart(Batch batch, ClientPart part, IntVector2 wallIndex, float posX, float posY, float angle) {
		Vector2 point = Tools.rotatePoint(
				posX + wallIndex.x * tileWidth + tileWidth / 2,
				posY + wallIndex.y * tileHeight + tileHeight / 2,
				posX, posY, MathUtils.degreesToRadians * angle);

		if (part.region != null)
			batch.draw(part.region,
					point.x - part.width / 2, point.y - part.height / 2,
					part.width / 2, part.height / 2,
					part.width, part.height,
					1, 1, part.angle + angle);
	}



}
