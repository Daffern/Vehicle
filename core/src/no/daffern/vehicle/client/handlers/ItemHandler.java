package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

public class ItemHandler implements SystemInterface {


	Map<Integer, GameItemPair> gameItemMap = new HashMap<>();

	Map<Integer, List<GameItemListener>> requestedGameItems = new HashMap<>();

	public ItemHandler() {

		C.myClient.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof GameItemPacket) {

					final GameItemPacket gameItem = (GameItemPacket) object;

					Tools.log(ItemHandler.class, "Received GameItemPacket with ID: " + gameItem.itemId + " loading asset: " + gameItem.name);


					if (requestedGameItems.containsKey(gameItem.itemId)) {
						ResourceManager.loadAsset(gameItem.packName, TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {

							@Override
							public void onAssetLoaded(TextureAtlas asset) {

								GameItemPair gameItemPair = new GameItemPair(asset, gameItem);

								gameItemMap.put(gameItem.itemId, gameItemPair);


								List<GameItemListener> listeners = requestedGameItems.remove(gameItem.itemId);

								for (GameItemListener listener : listeners){
									listener.onGameItemLoaded(asset, gameItem);
								}


							}
						});
					}
				}
			}
		});
	}

	public void loadGameItem(int itemId, GameItemListener gameItemListener) {
		GameItemPair gameItem = gameItemMap.get(itemId);

		//check if gameitem exists and request if not
		if (gameItem == null) {

			List<GameItemListener> listeners = requestedGameItems.get(itemId);
			if (listeners == null) {
				listeners = new ArrayList<>();
				requestedGameItems.put(itemId, listeners);

				//request gameitem if there are 0 listeners
				requestGameItem(itemId);
			}
			listeners.add(gameItemListener);


		}else{
			gameItemListener.onGameItemLoaded(gameItem.textureAtlas, gameItem.gameItem);
		}
	}
	private void requestGameItem(int gameItemId) {
		GameItemRequestPacket gameItemRequestPacket = new GameItemRequestPacket();
		gameItemRequestPacket.gameItemId = gameItemId;
		C.myClient.sendTCP(gameItemRequestPacket);
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

	public interface GameItemListener {
		void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem);
	}

	private class GameItemPair {
		GameItemPacket gameItem;
		TextureAtlas textureAtlas;
		public GameItemPair(TextureAtlas textureAtlas, GameItemPacket gameItem){
			this.gameItem = gameItem;
			this.textureAtlas = textureAtlas;
		}
	}

}
