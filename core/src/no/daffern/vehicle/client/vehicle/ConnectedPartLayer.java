package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.HashMap;
import java.util.Map;

public class ConnectedPartLayer implements PartLayerI {


	private final static int CENTER = 0b0001;
	private final static int DOWN = 0b0010;
	private final static int RIGHT = 0b0100;

	Map<IntVector2, Tile> tiles = new HashMap<>();

	TextureRegion centerTile, connectionTile;

	public ConnectedPartLayer(int itemId) {


		C.itemHandler.loadGameItem(itemId, new ItemHandler.GameItemListener() {
			@Override
			public void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem) {
				Array<TextureAtlas.AtlasRegion> regions = textureAtlas.findRegions(gameItem.iconName);
				centerTile = regions.get(0);
				connectionTile = regions.get(1);

			}
		});


	}

	@Override
	public void add(IntVector2 wallIndex, int itemId, float width, float height) {
		tiles.put(wallIndex, new Tile());

		update(wallIndex);
	}

	@Override
	public void remove(IntVector2 index) {
		tiles.remove(index);

		update(index);
	}

	@Override
	public void update(IntVector2 wallIndex, float angle) {

	}


	private void update(IntVector2 index) {

		Tile center = tiles.get(index);
		Tile left = tiles.get(new IntVector2(index.x - 1, index.y));
		Tile right = tiles.get(new IntVector2(index.x + 1, index.y));
		Tile up = tiles.get(new IntVector2(index.x, index.y + 1));
		Tile down = tiles.get(new IntVector2(index.x, index.y - 1));


		if (center != null) {

			if (left != null) {
				left.sum = enableBit(left.sum, RIGHT);
			}
			if (up != null) {
				up.sum = enableBit(up.sum, DOWN);
			}

			int sum = 0;
			if (right != null)
				sum = sum | RIGHT;
			if (down != null)
				sum = sum | DOWN;

			center.sum = CENTER | sum;
		}
		else{
			if (left != null) {
				left.sum = disableBit(left.sum, RIGHT);
			}
			if (up != null) {
				up.sum = disableBit(up.sum, DOWN);
			}
		}

	}

	private int disableBit(int input, int bit) {
		return input & (~bit);
	}
	private int enableBit(int input, int bit){
		return input | bit;
	}

	@Override
	public void render(Batch batch, float posX, float posY, float tileWidth, float tileHeight, float angle) {

		for (Map.Entry<IntVector2, Tile> entry : tiles.entrySet()) {

			IntVector2 index = entry.getKey();
			int sum = entry.getValue().sum;



			Vector2 point = Tools.rotatePoint(
					posX + index.x * tileWidth + tileWidth / 2,
					posY + index.y * tileHeight + tileHeight / 2,
					posX, posY, MathUtils.degreesToRadians * angle);

			if ((sum & CENTER) == CENTER)
				batch.draw(centerTile, point.x - tileWidth/2, point.y - tileHeight/2,
						tileWidth/2,tileHeight/2 , tileWidth, tileHeight, 1, 1, angle);

			if ((sum & RIGHT) == RIGHT)
				batch.draw(connectionTile, point.x - tileWidth/2, point.y - tileHeight/2,
						tileWidth/2,tileHeight/2, tileWidth * 2, tileHeight, 1, 1, angle);

			if ((sum & DOWN) == DOWN)
				batch.draw(connectionTile, point.x - tileWidth/2, point.y - tileHeight/2,
						tileWidth/2,tileHeight/2, tileWidth * 2, tileHeight, 1, 1, angle - 90);


		}

	}


	class Tile {
		int sum;
	}
}
