package no.daffern.vehicle.client.vehicle;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.tiledmappacker.TiledMapPacker;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.handlers.ItemHandler;
import no.daffern.vehicle.utils.Tools;


import java.util.List;
import java.util.Vector;

/**
 * Created by Daffern on 08.06.2017.
 */
public class ClientWall {


	public ClientWall left, right, down, up;


	private AtlasRegion wallTexture;
	private int itemId;

	public ClientWall(int itemId) {
		this.itemId = itemId;
	}


	public void updateWallTexture(final int noTile) {

		C.itemHandler.loadTileset(itemId, new ItemHandler.TilesetListener() {

			public void onTilesetLoaded(ItemHandler.Tileset tileset) {
				updateWallTexture2(noTile, tileset);
			}
		});

	}

	private void updateWallTexture2(int noTile, ItemHandler.Tileset tileset) {

		if (itemId == noTile) {
			wallTexture = null;
			return;
		}


		int left = noTile;
		int right = noTile;
		int up = noTile;
		int down = noTile;

		if (this.left != null)
			left = this.left.getItemId();
		if (this.right != null)
			right = this.right.getItemId();
		if (this.down != null)
			down = this.down.getItemId();
		if (this.up != null)
			up = this.up.getItemId();


		if (up == noTile && down == noTile && left == noTile && right == noTile) {
			wallTexture = tileset.center;
			return;
		}

		if (up != noTile && down != noTile && right != noTile && left == noTile) {
			wallTexture = tileset.centerLeft;
			return;
		}
		if (up != noTile && down != noTile && right == noTile && left != noTile) {
			wallTexture = tileset.centerRight;
			return;
		}

		if (up != noTile && down == noTile && right != noTile && left != noTile) {
			wallTexture = tileset.bottomCenter;
			return;
		}
		if (up == noTile && down != noTile && right != noTile && left != noTile) {
			wallTexture = tileset.topCenter;
			return;
		}


		if (up == noTile && down != noTile && right != noTile && left == noTile) {
			wallTexture = tileset.topLeft;
			return;
		}
		if (up == noTile && down != noTile && right == noTile && left != noTile) {
			wallTexture = tileset.topRight;
			return;
		}

		if (up != noTile && down == noTile && right != noTile && left == noTile) {
			wallTexture = tileset.bottomLeft;
			return;
		}
		if (up != noTile && down == noTile && right == noTile && left != noTile) {
			wallTexture = tileset.bottomRight;
			return;
		}

		if (up == noTile && down == noTile && right != noTile && left != noTile) {
			wallTexture = tileset.horizontal;
			return;
		}

		if (up != noTile && down != noTile && right == noTile && left == noTile) {
			wallTexture = tileset.vertical;
			return;
		}

		if (up == noTile && down != noTile && right == noTile && left == noTile) {
			wallTexture = tileset.top;
			return;
		}
		if (up != noTile && down == noTile && right == noTile && left == noTile) {
			wallTexture = tileset.bottom;
			return;
		}
		if (up == noTile && down == noTile && right != noTile && left == noTile) {
			wallTexture = tileset.left;
			return;
		}
		if (up == noTile && down == noTile && right == noTile && left != noTile) {
			wallTexture = tileset.right;
			return;
		}

		wallTexture = tileset.center;

	}


	public void render(Batch batch, float x, float y, float originX, float originY, float tileWidth, float tileHeight, float angle) {

		if (wallTexture != null)
			batch.draw(wallTexture,
					x, y,
					originX, originY,
					tileWidth, tileHeight,
					1, 1, angle);
	}

	public int getItemId() {
		return itemId;
	}
}
