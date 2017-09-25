package no.daffern.vehicle.server.player;

import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.network.packets.InventoryPacket;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.vehicle.ServerVehicle;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.WallSquare;
import no.daffern.vehicle.server.vehicle.WallTriangle;
import no.daffern.vehicle.server.vehicle.parts.Part;
import no.daffern.vehicle.server.vehicle.parts.PartAxle;
import no.daffern.vehicle.server.vehicle.parts.PartEngine;
import no.daffern.vehicle.server.vehicle.parts.PartWheel;
import no.daffern.vehicle.utils.Tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daffern on 29.03.2017.
 */
public class ServerInventory {


	private Map<Integer, ItemSlot> inventory;
	private ServerPlayer player;
	private MyServer myServer;

	public ServerInventory(ServerPlayer player) {
		this.player = player;
		inventory = new HashMap<Integer, ItemSlot>();
		myServer = S.myServer;


	}


	public void useItem(PlayerClickPacket playerClickPacket) {
		playerClickPacket.x = Common.toWorldCoordinates(playerClickPacket.x);
		playerClickPacket.y = Common.toWorldCoordinates(playerClickPacket.y);

		ItemSlot itemSlot = inventory.get(playerClickPacket.itemId);

		if (itemSlot == null || itemSlot.getCount() <= 0)
			return;

		ServerVehicle serverVehicle = player.serverVehicle;
		if (serverVehicle == null)
			return;

		IntVector2 wallIndex = serverVehicle.findTileIndex(playerClickPacket.x, playerClickPacket.y);
		if (wallIndex == null)
			return;

		Wall wall = serverVehicle.getWall(wallIndex.x, wallIndex.y);


		//set new item
		switch (itemSlot.gameItem.type) {

			case GameItemTypes.REMOVE_TOOL: {
				if (wall == null)
					return;

				if (wall.getNumParts() > 0) {
					Part part = wall.getPart(wall.getNumParts() - 1);
					serverVehicle.removePart(wallIndex, part);
					addOneItem(part.getItemId());
				}
				else {
					if (serverVehicle.removeWall(wallIndex)) {
						addOneItem(wall.getItemId());
					}
				}
				break;
			}

			case GameItemTypes.WALL_TYPE_SQUARE: {
				if (wall == null) {
					if (serverVehicle.setWall(new WallSquare(itemSlot.gameItem.itemId, wallIndex))) {
						itemSlot.addCount(-1);
					}
				}
				else if (wall.getNumParts() == 0) {
					if (serverVehicle.setWall(new WallSquare(itemSlot.gameItem.itemId, wallIndex))) {
						itemSlot.addCount(-1);
						addOneItem(wall.getItemId());
					}
				}
				break;
			}

			case GameItemTypes.WALL_TYPE_TRIANGLE: {
				if (wall == null) {
					if (serverVehicle.setWall(new WallTriangle(itemSlot.gameItem.itemId, wallIndex, WallTriangle.TOP_LEFT))) {
						itemSlot.addCount(-1);
					}
				}
				else if (wall.getNumParts() == 0) {
					if (serverVehicle.setWall(new WallTriangle(itemSlot.gameItem.itemId, wallIndex, WallTriangle.TOP_LEFT))) {
						itemSlot.addCount(-1);
						addOneItem(wall.getItemId());
					}
				}
			}
			case GameItemTypes.PART_TYPE_WHEEL: {
				if (wall == null)
					return;
				if (wall.containsPartType(GameItemTypes.PART_TYPE_WHEEL))
					return;

				if (wall.getType() == GameItemTypes.WALL_TYPE_SQUARE) {
					if (serverVehicle.addPart(wallIndex, new PartWheel(itemSlot.gameItem.itemId)))
						itemSlot.addCount(-1);
				}

				break;
			}
			case GameItemTypes.PART_TYPE_AXLE: {
				if (wall == null)
					return;
				if (wall.containsPartType(GameItemTypes.PART_TYPE_AXLE))
					return;
				if (serverVehicle.addPart(wallIndex, new PartAxle(itemSlot.gameItem.itemId))) {
					itemSlot.addCount(-1);
				}

				break;
			}
			case GameItemTypes.PART_TYPE_ENGINE: {
				if (wall == null)
					return;
				if (wall.containsPartType(GameItemTypes.PART_TYPE_ENGINE))
					return;
				if (serverVehicle.addPart(wallIndex, new PartEngine(itemSlot.gameItem.itemId))) {
					itemSlot.addCount(-1);
				}

				break;
			}


			default: {
				Tools.log(this, "Unhandled item type: " + itemSlot.gameItem.type);
				return;
			}

		}


	}

	public void addOneItem(int gameItemId) {
		GameItemPacket gameItemPacket = S.itemHandler.getItemById(gameItemId);
		if (gameItemPacket != null) {
			addItem(gameItemPacket, 1, false);
		}
	}

	public void addItem(GameItemPacket gameItem, int count, boolean isInfinite) {

		ItemSlot itemSlot = inventory.get(gameItem.itemId);
		if (itemSlot == null) {
			itemSlot = new ItemSlot(gameItem, count, isInfinite);
			inventory.put(gameItem.itemId, itemSlot);

			//only send gameitem the first time
			sendItemSlot(itemSlot, true);
		}
		else {
			itemSlot.count += count;

			sendItemSlot(itemSlot, false);

		}


	}

	private void sendItemSlot(ItemSlot itemSlot, boolean sendGameItem) {

		InventoryPacket inventoryPacket = new InventoryPacket();
		inventoryPacket.count = itemSlot.count;
		inventoryPacket.itemId = itemSlot.gameItem.itemId;
		inventoryPacket.playerId = player.playerId;
/*
        if (sendGameItem)
            S.itemHandler.sendGameItem(itemSlot.gameItem);
*/
		myServer.sendToTCP(player.playerId, inventoryPacket);
	}


	private class ItemSlot {
		GameItemPacket gameItem;
		int count;
		boolean isInfinite;

		public ItemSlot(GameItemPacket gameItem, int count, boolean isInfinite) {
			this.gameItem = gameItem;
			this.count = count;
			this.isInfinite = isInfinite;
		}

		public void addCount(int count) {
			if (isInfinite)
				return;
			this.count += count;
			sendItemSlot(this, false);
		}

		public int getCount() {
			if (isInfinite)
				return Integer.MAX_VALUE;
			return count;
		}

	}
}
