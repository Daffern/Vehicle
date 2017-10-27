package no.daffern.vehicle.server.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.common.GameItemLoader;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.network.packets.GameItemRequestPacket;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daffern on 29.03.2017.
 */
public class ItemHandler {

	private static int nextGameItemId = 1;

	private Map<Integer, GameItemPacket> idMap = new HashMap<Integer, GameItemPacket>();
	private Map<String, GameItemPacket> nameMap = new HashMap<String, GameItemPacket>();
	private Map<Integer, List<GameItemPacket>> typeMap = new HashMap<>();

	MyServer myServer;

	public ItemHandler() {

		myServer = S.myServer;

		createGameItemFromAtlas("packed/pack.atlas", "vehicle/1/center", "vehicle/1/", GameItemTypes.WALL_TYPE_SQUARE);

		createGameItemFromAtlas("packed/pack.atlas", "tools/eraser", "", GameItemTypes.REMOVE_TOOL);
		createGameItemFromAtlas("packed/pack.atlas", "tools/shovel", "", GameItemTypes.SHOVEL_TOOL);
		createGameItemFromAtlas("packed/pack.atlas", "vehicle/wheel", "", GameItemTypes.PART_TYPE_WHEEL);
		createGameItemFromAtlas("packed/pack.atlas", "vehicle/axle", "", GameItemTypes.PART_TYPE_AXLE);
		createGameItemFromAtlas("packed/pack.atlas", "vehicle/engine", "", GameItemTypes.PART_TYPE_ENGINE);
		createGameItemFromAtlas("packed/pack.atlas", "vehicle/battery", "", GameItemTypes.PART_TYPE_BATTERY);
		createGameItemFromAtlas("packed/pack.atlas", "vehicle/solar", "", GameItemTypes.PART_TYPE_SOLAR);
		createGameItemFromAtlas("packed/pack.atlas", "vehicle/wire", "", GameItemTypes.PART_TYPE_WIRE);
		createGameItemFromAtlas("packed/pack.atlas", "vehicle/drill", "", GameItemTypes.PART_TYPE_DRILL);


		//createGameItemFromAtlas("pack/pack.atlas","vehicle/triangle_wall.png", GameItemTypes.WALL_TYPE_TRIANGLE);


		myServer.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof GameItemRequestPacket) {
					sendGameItem(((GameItemRequestPacket) object).gameItemId);
				}
			}
		});
	}

	private void loadGameItemsFromFile(String filename) {
		GameItemLoader gameItemLoader = new GameItemLoader();
		GameItemPacket[] items = gameItemLoader.loadFile(filename);

		for (int i = 0; i < items.length; i++) {
			putGameItem(items[i]);
		}
	}


	private void createGameItemFromTexture(String filename, int itemType) {
		GameItemPacket serverItem = new GameItemPacket();
		//serverItem.iconName //ignored
		if (!Gdx.files.internal(filename).exists()) {
			Tools.log(this, "File with iconName:" + filename + " not found");
			return;
		}

		serverItem.packName = filename;
		serverItem.type = itemType;
		serverItem.itemId = nextGameItemId++;

		putGameItem(serverItem);
	}

	/**
	 * create a game item from an atlas
	 *
	 * @param fileName atlas filename
	 */
	private void createGameItemFromAtlas(String fileName, String iconName, String tilePath, int itemType) {

		FileHandle fileHandle = Gdx.files.internal(fileName);

		if (!fileHandle.exists()) {
			Tools.log(this, "File with name: " + fileName + " not found");
			return;
		}

		TextureAtlas.TextureAtlasData atlasData = new TextureAtlas.TextureAtlasData(fileHandle, fileHandle.parent(), false);

		GameItemPacket gameItem = new GameItemPacket();
		gameItem.type = itemType;
		gameItem.packName = fileName;
		gameItem.itemId = nextGameItemId++;
		gameItem.tilePath = tilePath;

		//check that image exists
		for (TextureAtlas.TextureAtlasData.Region region : atlasData.getRegions()) {
			if (region.name.equals(iconName)) {
				gameItem.iconName = iconName;
				break;
			}
		}//else set it to something
		if (gameItem.iconName == null)
			gameItem.iconName = atlasData.getRegions().get(atlasData.getRegions().size / 2).name;


		putGameItem(gameItem);
	}

	private void putGameItem(GameItemPacket item) {
		idMap.put(item.itemId, item);

		List<GameItemPacket> list = typeMap.get(item.type);
		if (list == null) {
			list = new ArrayList<>();
			typeMap.put(item.type, list);
		}
		list.add(item);

		if (item.iconName != null && !item.iconName.isEmpty())
			nameMap.put(item.iconName, item);
		else
			nameMap.put(item.packName, item);

	}

	private void clearGameItems() {
		nameMap.clear();
		typeMap.clear();
		idMap.clear();
	}

	public void sendGameItem(int gameItemId) {
		GameItemPacket gameItemPacket = idMap.get(gameItemId);
		if (gameItemPacket != null)
			sendGameItem(gameItemPacket);
		else
			Tools.log(this, "gameItemId not found in sendGameItem()");
	}

	public void sendGameItem(GameItemPacket gameItemPacket) {
		myServer.sendToAllTCP(gameItemPacket);
	}

	public GameItemPacket getItemById(int itemId) {
		return idMap.get(itemId);
	}

	public GameItemPacket getItemByName(String name) {
		GameItemPacket gameItemPacket = nameMap.get(name);
		if (gameItemPacket == null)
			Tools.log(this, "could not find gameItem with name: " + name);
		return gameItemPacket;
	}

	public List<GameItemPacket> getItemsOfType(int itemType) {
		return typeMap.get(itemType);
	}
}
