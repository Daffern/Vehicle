package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.HashMap;
import java.util.Map;

public class BasicPartLayer implements PartLayerI {


	Map<IntVector2, Part> tiles = new HashMap<>();



	@Override
	public void remove(IntVector2 wallIndex) {
		tiles.remove(wallIndex);
	}

	@Override
	public void add(IntVector2 wallIndex, int itemId, float width, float height) {
		tiles.put(wallIndex, new Part(itemId, width, height));
	}

	@Override
	public void update(IntVector2 wallIndex, float angle) {
		Part part = tiles.get(wallIndex);

		if (part != null)
			part.angle = angle * MathUtils.radiansToDegrees;
	}

	@Override
	public void render(Batch batch, float posX, float posY, float tileWidth, float tileHeight, float angle) {

		for (Map.Entry<IntVector2, Part> entry : tiles.entrySet()){

			IntVector2 wallIndex = entry.getKey();
			Part part = entry.getValue();

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

	public class Part {
		int itemId;
		//position x,y on the vehicle
		float width, height;
		float angle;
		TextureAtlas.AtlasRegion region;

		public Part(int itemId, float width, float height) {
			this(itemId, width, height, 0);
		}

		public Part(int itemId, float width, float height, float angle) {
			this.itemId = itemId;

			this.width = width;
			this.height = height;
			this.angle = angle;

			C.itemHandler.loadGameItem(itemId, new ItemHandler.GameItemListener(){

				@Override
				public void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem) {
					region = textureAtlas.findRegion(gameItem.iconName);
				}


			});

		}
	}
}
