package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.graphics.QuadTileDrawer;
import no.daffern.vehicle.network.packets.*;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Daffern on 08.06.2017.
 */
public class ClientWalls {

	int noTile;

	//layers, parts
	Map<Integer, PartLayerI> partLayers;

	QuadTileDrawer wallDrawer;
	//queue for the wallDrawer
	List<WallPacket> queuedWalls;

	float tileWidth, tileHeight;

	public ClientWalls() {

	}

	public void initialize(VehicleLayoutPacket vlp) {
		//this.wallDrawer = new DynamicMultiArray<>(10, 10);
		this.wallDrawer = new QuadTileDrawer();
		queuedWalls = new ArrayList<>();
		this.partLayers = new TreeMap<>();
		this.tileWidth = Common.toPixelCoordinates(vlp.wallWidth);
		this.tileHeight = Common.toPixelCoordinates(vlp.wallHeight);


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
			wallDrawer.remove(wallIndex);
		}else{
			//queue stuff
			if (!wallDrawer.hasTileset(wallPacket.itemId)){

				queuedWalls.add(wallPacket);

				C.itemHandler.loadGameItem(wallPacket.itemId, new ItemHandler.GameItemListener(){

					@Override
					public void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem) {
						wallDrawer.addTileset(textureAtlas, gameItem.tilePath, gameItem.itemId);

						while (queuedWalls.size() > 0){
							WallPacket wallPacket1 = queuedWalls.remove(0);
							setWall(wallPacket1);
						}

					}
				});

			}
			else{//or not
				wallDrawer.set(wallIndex, wallPacket.itemId);
			}
		}

		//loop through layers and remove parts on this index
		for (Map.Entry<Integer, PartLayerI> entry : partLayers.entrySet()) {
			entry.getValue().remove(wallIndex);
		}


		//add new parts
		if (wallPacket.partPackets == null)
			return;

		for (int i = 0; i < wallPacket.partPackets.length; i++) {
			PartPacket partPacket = wallPacket.partPackets[i];

			PartLayerI partLayer = partLayers.get(partPacket.layer);

			if (partLayer == null) {

				switch (partPacket.type){
					case GameItemTypes.PART_TYPE_WIRE:
					case GameItemTypes.PART_TYPE_AXLE:
						partLayer = new WangPartLayer(partPacket.itemId);
						break;

					default:
						partLayer = new BasicPartLayer();
						break;
				}

				partLayers.put(partPacket.layer, partLayer);
			}

			partLayer.add(wallIndex, partPacket);
		}

	}
	public int getWallItemId(IntVector2 index){
		QuadTileDrawer.QuadTile quadTile = wallDrawer.get(index);
		if (quadTile == null)
			return 0;
		return quadTile.tileId;
	}

	public void updatePart(PartOutputPacket pop){
		PartLayerI partLayer = partLayers.get(pop.layer);

		IntVector2 index = new IntVector2(pop.wallX,pop.wallY);

		partLayer.update(index, pop);
	}



/*

	private void updateWallTexture(IntVector2 wallIndex) {

		int x = wallIndex.x;
		int y = wallIndex.y;

		ClientWall wall = wallDrawer.get(wallIndex);
		ClientWall left = wallDrawer.get(new IntVector2(x - 1, y));
		ClientWall right = wallDrawer.get(new IntVector2(x + 1, y));
		ClientWall up = wallDrawer.get(new IntVector2(x, y + 1));
		ClientWall down = wallDrawer.get(new IntVector2(x, y - 1));

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
		PartLayerI layer0 = partLayers.get(-1);
		if (layer0 != null) {
			layer0.render(batch,posX,posY,tileWidth,tileHeight,angle);
		}

		//render wallDrawer
		wallDrawer.render(batch, posX, posY, tileWidth, tileHeight, angle);


		//render the other parts
		for (Map.Entry<Integer, PartLayerI> layerEntry : partLayers.entrySet()){

			if (layerEntry.getKey() == -1)
				continue;

			layerEntry.getValue().render(batch,posX,posY,tileWidth,tileHeight,angle);
		}
	}

	private void renderPart(Batch batch, Map.Entry<IntVector2,ClientPart> entry, float posX, float posY, float angle){
		IntVector2 wallIndex = entry.getKey();

		Vector2 point = Tools.rotatePoint(
				posX + wallIndex.x * tileWidth + tileWidth / 2,
				posY + wallIndex.y * tileHeight + tileHeight / 2,
				posX, posY, MathUtils.degreesToRadians * angle);

		ClientPart clientPart = entry.getValue();

		clientPart.render(batch, point.x, point.y, angle);

	}


/*
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
*/


}
