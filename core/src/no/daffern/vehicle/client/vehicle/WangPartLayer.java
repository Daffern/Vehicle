package no.daffern.vehicle.client.vehicle;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.GameItemPacket;
import no.daffern.vehicle.utils.Tools;

import java.util.HashMap;
import java.util.Map;

public class WangPartLayer implements PartLayerI {

	private static int NORTH =  0b0001;
	private static int EAST =   0b0010;
	private static int SOUTH =  0b0100;
	private static int WEST =   0b1000;

	TextureRegion[] tileSet;

	Map<IntVector2, Tile> tiles = new HashMap<>();

	public WangPartLayer(int itemId) {
		C.itemHandler.loadGameItem(itemId, new ItemHandler.GameItemListener() {
			@Override
			public void onGameItemLoaded(TextureAtlas textureAtlas, GameItemPacket gameItem) {
				TextureAtlas.AtlasRegion region = textureAtlas.findRegion(gameItem.iconName);

				TextureRegion[][] regions = region.split(region.originalWidth/4, region.getRegionHeight()/4);

				tileSet = toWangTiles(regions);
			}
		});
	}

	@Override
	public void add(IntVector2 wallIndex, int itemId, float width, float height) {

		Tile left = tiles.get(wallIndex.left());
		Tile right = tiles.get(wallIndex.right());
		Tile down = tiles.get(wallIndex.down());
		Tile up = tiles.get(wallIndex.up());

		int sum = 0;
		if (left != null) {
			sum += WEST;

			left.sum |= EAST;
			left.textureRegion = tileSet[left.sum];
		}
		if (right != null) {
			sum += EAST;

			right.sum |= WEST;
			right.textureRegion = tileSet[right.sum];
		}
		if (down != null) {
			sum += SOUTH;

			down.sum |= NORTH;
			down.textureRegion = tileSet[down.sum];
		}
		if (up != null) {
			sum += NORTH;

			up.sum |= SOUTH;
			up.textureRegion = tileSet[up.sum];
		}

		tiles.put(wallIndex, new Tile(tileSet[sum],sum));

	}

	@Override
	public void remove(IntVector2 wallIndex) {
		Tile left = tiles.get(wallIndex.left());
		Tile right = tiles.get(wallIndex.right());
		Tile down = tiles.get(wallIndex.down());
		Tile up = tiles.get(wallIndex.up());

		if (left != null) {
			left.sum &= ~EAST;
			left.textureRegion = tileSet[left.sum];
		}
		if (right != null) {
			right.sum &= ~WEST;
			right.textureRegion = tileSet[right.sum];
		}
		if (down != null) {
			down.sum &= ~NORTH;
			down.textureRegion = tileSet[down.sum];
		}
		if (up != null) {
			up.sum &= ~SOUTH;
			up.textureRegion = tileSet[up.sum];
		}

		tiles.remove(wallIndex);
	}

	@Override
	public void update(IntVector2 wallIndex, float angle) {

	}

	@Override
	public void render(Batch batch, float posX, float posY, float tileWidth, float tileHeight, float angle) {
		for (Map.Entry<IntVector2, Tile> entry : tiles.entrySet()) {

			IntVector2 index = entry.getKey();


			Vector2 point = Tools.rotatePoint(
					posX + index.x * tileWidth + tileWidth / 2,
					posY + index.y * tileHeight + tileHeight / 2,
					posX, posY, MathUtils.degreesToRadians * angle);

			batch.draw(entry.getValue().textureRegion, point.x - tileWidth / 2, point.y - tileHeight / 2,
					tileWidth / 2, tileHeight / 2, tileWidth, tileHeight, 1, 1, angle);


		}
	}

	private class Tile {
		public Tile(TextureRegion textureRegion, int sum){
			this.textureRegion = textureRegion;
			this.sum = sum;
		}
		TextureRegion textureRegion;
		int sum;
	}

	private TextureRegion[] toWangTiles(TextureRegion[][] regions) {
		assert (regions.length == 4 && regions[0].length == 4);
		//http://cr31.co.uk/stagecast/wang/intro.html
		TextureRegion[] tileSet = new TextureRegion[16];
		tileSet[0] = regions[3][0];
		tileSet[1] = regions[2][0];
		tileSet[2] = regions[3][1];
		tileSet[3] = regions[2][1];
		tileSet[4] = regions[0][0];
		tileSet[5] = regions[1][0];
		tileSet[6] = regions[0][1];
		tileSet[7] = regions[1][1];
		tileSet[8] = regions[3][3];
		tileSet[9] = regions[2][3];
		tileSet[10] = regions[3][2];
		tileSet[11] = regions[2][2];
		tileSet[12] = regions[0][3];
		tileSet[13] = regions[1][3];
		tileSet[14] = regions[0][2];
		tileSet[15] = regions[1][2];

		return tileSet;
	}
}
