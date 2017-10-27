package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.network.packets.PartOutputPacket;
import no.daffern.vehicle.network.packets.PartPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.HashMap;
import java.util.Map;

public class BasicPartLayer implements PartLayerI {

	private static final Vector2 ON_OFF_POS = new Vector2(12, 4.5f);
	private static final Vector2 POWER_METER_POS = new Vector2(24.3f, 8.1f);//because scaling at shit

	private Vector2 powerPos;

	TextureRegion onTexture, offTexture, powerTexture;

	Map<IntVector2, Part> tiles = new HashMap<>();


	public BasicPartLayer() {
		ResourceManager.loadAsset("packed/pack.atlas", TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {
			@Override
			public void onAssetLoaded(TextureAtlas asset) {
				onTexture = asset.findRegion("state/on");
				offTexture = asset.findRegion("state/off");
				powerTexture = asset.findRegion("state/power");
			}
		});
	}

	@Override
	public void remove(IntVector2 wallIndex) {
		tiles.remove(wallIndex);
	}

	@Override
	public void add(IntVector2 wallIndex, PartPacket pp) {
		tiles.put(wallIndex, new Part(pp.itemId, Common.toPixelCoordinates(pp.width), Common.toPixelCoordinates(pp.height), pp.state));
	}

	@Override
	public void update(IntVector2 wallIndex, PartOutputPacket pop) {
		Part part = tiles.get(wallIndex);

		if (part != null) {
			part.angle = pop.angle * MathUtils.radiansToDegrees;
			part.state = pop.state;
		}
	}

	@Override
	public void render(Batch batch, float posX, float posY, float tileWidth, float tileHeight, float angle) {

		powerPos = Tools.rotatePoint(POWER_METER_POS.x, POWER_METER_POS.y,0,0,MathUtils.degreesToRadians * angle);

		for (Map.Entry<IntVector2, Part> entry : tiles.entrySet()) {

			IntVector2 wallIndex = entry.getKey();
			Part part = entry.getValue();

			Vector2 point = Tools.rotatePoint(
					posX + wallIndex.x * tileWidth + tileWidth / 2,
					posY + wallIndex.y * tileHeight + tileHeight / 2,
					posX, posY, MathUtils.degreesToRadians * angle);



			part.render(batch, point.x - part.width / 2, point.y - part.height / 2, angle);

		}
	}


	public class Part {
		int itemId;
		float width, height;
		float angle;
		byte state;
		TextureAtlas.AtlasRegion region;

		public Part(int itemId, float width, float height, byte state) {
			this(itemId, width, height, 0, state);
		}

		public Part(int itemId, float width, float height, float angle, byte state) {
			this.itemId = itemId;

			this.width = width;
			this.height = height;
			this.angle = angle;
			this.state = state;

			C.itemHandler.loadGameItem(itemId, new ItemHandler.GameItemListener() {

				@Override
				public void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem) {
					region = textureAtlas.findRegion(gameItem.iconName);
				}


			});

		}

		public void render(Batch batch, float x, float y, float angle) {
			if (region != null)
				batch.draw(region,
						x, y,
						width / 2, height / 2,
						width, height,
						1, 1, this.angle + angle);

			//render state
			switch (state) {
				case PartOutputPacket.STATE_NONE:
					break;
				case PartOutputPacket.STATE_ON:

					break;
				case PartOutputPacket.STATE_OFF:

					break;

				default:
					if (powerTexture != null)
					batch.draw(powerTexture, x + powerPos.x, y + powerPos.y,
							width/2, height/2,
							7.2f, 13.5f * state / 100,//because scaling etc..
							1, 1, angle);


					break;
			}
		}
	}
}
