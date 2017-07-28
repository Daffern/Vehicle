package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.network.packets.GameItemRequestPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daffern on 16.04.2017.
 */
public class ItemHandler implements SystemInterface {

	Map<Integer, GameItemPacket> gameItemMap = new HashMap<>();

	Map<Integer, TextureAtlas.AtlasRegion> regions = new HashMap<>();
	Map<Integer, List<AtlasRegionListener>> requestedRegions = new HashMap<>();

	Map<Integer, Tileset> tilesets = new HashMap<>();
	Map<Integer, List<TilesetListener>> requestedTilesets = new HashMap<>();


	public ItemHandler() {

		C.myClient.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof GameItemPacket) {

					final GameItemPacket gameItem = (GameItemPacket) object;
					gameItemMap.put(gameItem.itemId, gameItem);

					Tools.log(ItemHandler.class, "Received GameItemPacket with ID: " + gameItem.itemId);



					//load tilesets
					if (requestedTilesets.containsKey(gameItem.itemId)) {
						ResourceManager.loadAsset(gameItem.packName, TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {

							@Override
							public void onAssetLoaded(TextureAtlas asset) {

								Tileset tileset = new Tileset(asset, gameItem.tilePath);

								List<TilesetListener> tilesetListeners = requestedTilesets.remove(gameItem.itemId);

								for (TilesetListener tilesetListener : tilesetListeners) {
									tilesetListener.onTilesetLoaded(tileset);
								}
								Tools.log(ItemHandler.class, "Loaded tileset with id: " + gameItem.itemId + " for " + tilesetListeners.size() +" objects");

							}
						});
					}
					if (requestedRegions.containsKey(gameItem.itemId)) {
						ResourceManager.loadAsset(gameItem.packName, TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {

							@Override
							public void onAssetLoaded(TextureAtlas asset) {
								AtlasRegion atlasRegion = asset.findRegion(gameItem.iconName);

								List<AtlasRegionListener> regionListeners = requestedRegions.remove(gameItem.itemId);

								for (AtlasRegionListener atlasRegionListener : regionListeners) {
									atlasRegionListener.onRegionLoaded(atlasRegion);
								}
								Tools.log(ItemHandler.class, "Loaded AtlasRegion with id: " + gameItem.itemId+ " for " + regionListeners.size() +" objects");

							}

						});
					}
				}
			}
		});


	}

	public GameItemPacket getGameItem(int gameItemId) {
		return gameItemMap.get(gameItemId);
	}



/*
	public <T> void loadAsset(int gameItemId, Class c, ResourceManager.AssetListener<T> assetListener) {

		GameItemPacket gameItem = gameItemMap.get(gameItemId);

		if (gameItem == null) {
			Tools.log(this, "loadAsset(): client did not find gameItem: " + gameItemId + " requesting from server...");

			List<ResourceManager.AssetListener> assetListeners = queuedAssets.get(gameItemId);
			if (assetListeners == null) {
				assetListeners = new ArrayList<>();
				queuedAssets.put(gameItemId, assetListeners);
			}
			assetListeners.add(assetListener);

			if (assetListeners.size() == 1)
				requestGameItem(gameItemId);
			return;
		}
		else {
			String filename = gameItem.packName;

			ResourceManager.loadAsset(filename, c, assetListener);
		}
	}*/



	public void loadRegion(int itemId, final AtlasRegionListener regionListener){
		final GameItemPacket gameItem = gameItemMap.get(itemId);

		//check if gameitem exists and request if not
		if (gameItem == null) {

			List<AtlasRegionListener> regionListeners = requestedRegions.get(itemId);
			if (regionListeners == null) {
				regionListeners = new ArrayList<>();
				requestedRegions.put(itemId, regionListeners);
			}
			regionListeners.add(regionListener);

			//only send request if not already requested
			if (getListenerCountForItemId(itemId) == 1){
				Tools.log(this, "loadTileset(): failed to load AtlasRegion with id: " + itemId + ", requesting from server");
				requestGameItem(itemId);
			}

			return;
		}

		AtlasRegion atlasRegion = regions.get(gameItem.itemId);

		if (atlasRegion != null){
			regionListener.onRegionLoaded(atlasRegion);
		}
		else {
			ResourceManager.loadAsset(gameItem.packName, TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {
				@Override
				public void onAssetLoaded(TextureAtlas asset) {

					AtlasRegion atlasRegion = asset.findRegion(gameItem.iconName);

					regions.put(gameItem.itemId, atlasRegion);

					regionListener.onRegionLoaded(atlasRegion);


				}
			});

		}
	}



	/**
	 * Special method for Textureatlases (tilesets with orientation)
	 *
	 * @param itemId
	 * @param tilesetListener
	 */

	public void loadTileset(int itemId, final TilesetListener tilesetListener) {
		final GameItemPacket gameItem = gameItemMap.get(itemId);

		//check if gameitem exists and request if not
		if (gameItem == null) {

			List<TilesetListener> tileListeners = requestedTilesets.get(itemId);
			if (tileListeners == null) {
				tileListeners = new ArrayList<>();
				requestedTilesets.put(itemId, tileListeners);
			}
			tileListeners.add(tilesetListener);

			if (getListenerCountForItemId(itemId) == 1){
				Tools.log(this, "loadTileset(): failed to load tileset with id: " + itemId + ", requesting from server");
				requestGameItem(itemId);
			}

			return;
		}

		//check if tileset is loaded and invoke, else load from resourcemanager
		Tileset tileset = tilesets.get(gameItem.itemId);

		if (tileset != null) {
			tilesetListener.onTilesetLoaded(tileset);
		}
		else {

			ResourceManager.loadAsset(gameItem.packName, TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {
				@Override
				public void onAssetLoaded(TextureAtlas asset) {

					Tileset tileset = new Tileset(asset, gameItem.tilePath);

					tilesets.put(gameItem.itemId, tileset);
					tilesetListener.onTilesetLoaded(tileset);
				}
			});

		}

	}
	private void requestGameItem(int gameItemId) {
		GameItemRequestPacket gameItemRequestPacket = new GameItemRequestPacket();
		gameItemRequestPacket.gameItemId = gameItemId;
		C.myClient.sendTCP(gameItemRequestPacket);
	}

	private int getListenerCountForItemId(int itemId){
		int count = 0;

		List<AtlasRegionListener> regionListeners = requestedRegions.get(itemId);
		List<TilesetListener> tilesetListeners = requestedTilesets.get(itemId);

		if (regionListeners != null) count += regionListeners.size();
		if (tilesetListeners != null) count += tilesetListeners.size();

		return count;
	}
	@Override
	public void preStep() {

	}

	@Override
	public void postStep() {

	}

	@Override
	public void render(Batch batch, float delta) {

	}

	public interface AtlasRegionListener{
		void onRegionLoaded(TextureAtlas.AtlasRegion atlasRegion);
	}

	public interface  TilesetListener{
		void onTilesetLoaded(Tileset tileset);
	}


	public static class Tileset {
		public TextureAtlas.AtlasRegion topLeft;
		public TextureAtlas.AtlasRegion topCenter;
		public TextureAtlas.AtlasRegion topRight;
		public TextureAtlas.AtlasRegion centerRight;
		public TextureAtlas.AtlasRegion bottomRight;
		public TextureAtlas.AtlasRegion bottomCenter;
		public TextureAtlas.AtlasRegion bottomLeft;
		public TextureAtlas.AtlasRegion centerLeft;
		public TextureAtlas.AtlasRegion center;

		public TextureAtlas.AtlasRegion bottom;
		public TextureAtlas.AtlasRegion top;
		public TextureAtlas.AtlasRegion left;
		public TextureAtlas.AtlasRegion right;

		public TextureAtlas.AtlasRegion horizontal;
		public TextureAtlas.AtlasRegion vertical;


		public Tileset(TextureAtlas textureAtlas, String tilePath ) {
			topLeft = findRegion(textureAtlas,tilePath + "topLeft");
			topCenter = findRegion(textureAtlas,tilePath + "topCenter");
			topRight = findRegion(textureAtlas,tilePath + "topRight");

			centerLeft = findRegion(textureAtlas,tilePath + "centerLeft");
			center = findRegion(textureAtlas,tilePath + "center");
			centerRight = findRegion(textureAtlas,tilePath + "centerRight");

			bottomRight = findRegion(textureAtlas,tilePath + "botRight");
			bottomCenter = findRegion(textureAtlas,tilePath + "botCenter");
			bottomLeft = findRegion(textureAtlas,tilePath + "botLeft");

			horizontal = findRegion(textureAtlas,tilePath + "horizontal");
			vertical = findRegion(textureAtlas,tilePath + "vertical");

			bottom = findRegion(textureAtlas,tilePath + "bot");
			top = findRegion(textureAtlas,tilePath + "top");
			left = findRegion(textureAtlas,tilePath + "left");
			right = findRegion(textureAtlas,tilePath + "right");

		}
		private TextureAtlas.AtlasRegion findRegion(TextureAtlas textureAtlas, String region){
			TextureAtlas.AtlasRegion atlasRegion = textureAtlas.findRegion(region);
			if (atlasRegion == null)
				Tools.log(this, "Did not find region with name: " + region);
			return atlasRegion;
		}
	}
}
