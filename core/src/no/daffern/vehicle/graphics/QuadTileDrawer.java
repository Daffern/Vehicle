package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.daffern.vehicle.container.IntVector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daffern on 28.07.2017.
 */
public class QuadTileDrawer {

	//adjacents
	private static final int NORTH = 0b00000001;
	private static final int NORTHEAST = 0b00000010;
	private static final int EAST = 0b00000100;
	private static final int SOUTHEAST = 0b00001000;
	private static final int SOUTH = 0b00010000;
	private static final int SOUTHWEST = 0b00100000;
	private static final int WEST = 0b01000000;
	private static final int NORTHWEST = 0b10000000;

	//sum values
	private static final int SUMNORTH = ~NORTH & 0xff;
	private static final int SUMNORTHEAST = ~NORTHEAST & 0xff;
	private static final int SUMEAST = ~EAST & 0xff;
	private static final int SUMSOUTHEAST = ~SOUTHEAST & 0xff;
	private static final int SUMSOUTH = ~SOUTH & 0xff;
	private static final int SUMSOUTHWEST = ~SOUTHWEST & 0xff;
	private static final int SUMWEST = ~WEST & 0xff;
	private static final int SUMNORTHWEST = ~NORTHWEST & 0xff;

	private static final int SUMCENTER = 0xff;

	private static final int SUMCENTERNORTHEAST = ~NORTH & ~EAST & 0xff;
	private static final int SUMCENTERSOUTHEAST = ~SOUTH & ~EAST & 0xff;
	private static final int SUMCENTERSOUTHWEST = ~SOUTH & ~WEST & 0xff;
	private static final int SUMCENTERNORTHWEST = ~NORTH & ~WEST & 0xff;


	Map<IntVector2, QuadTile> quadTiles = new HashMap<>();

	Map<Integer, QuadTileset> tilesets = new HashMap<>();

	public void setTile(IntVector2 index) {


		quadTiles.put(index, new QuadTile());
	}


	public void resolve(int x, int y) {


		QuadTileset tileset = tilesets.get(1);

		QuadTile center = get(x, y);

		QuadTile north = get(x, y + 1);
		QuadTile northEast = get(x + 1, y + 1);
		QuadTile east = get(x + 1, y);
		QuadTile southEast = get(x + 1, y - 1);
		QuadTile south = get(x, y - 1);
		QuadTile southWest = get(x - 1, y - 1);
		QuadTile west = get(x - 1, y);
		QuadTile northWest = get(x - 1, y + 1);

		resolveNorthWestQuad(tileset, center, north, northWest, west);
		resolveNorthEastQuad(tileset, center, north, northEast, east);
		resolveSouthEastQuad(tileset, center, south, southEast, east);
		resolveSouthWestQuad(tileset, center, south, southWest, west);

	}

	private void resolveNorthWestQuad(QuadTileset tileset, QuadTile center, QuadTile north, QuadTile northWest, QuadTile west) {

		if (north != null && west != null && northWest != null)
			center.northWest = tileset.center;

		if (north != null && northWest == null && west != null)
			center.northWest = tileset.topLeftEdge;

		if (north == null && west == null)
			center.northWest = tileset.topLeftCorner;

		if (north == null && west != null)
			center.northWest = tileset.top;

		if (north != null && west == null)
			center.northWest = tileset.left;
	}

	private void resolveNorthEastQuad(QuadTileset tileset, QuadTile center, QuadTile north, QuadTile northEast, QuadTile east) {

		if (north != null && east != null && northEast != null)
			center.northEast = tileset.center;

		if (north != null && northEast == null && east != null)
			center.northEast = tileset.topRightEdge;

		if (north == null && east == null)
			center.northEast = tileset.topRightCorner;

		if (north == null && east != null)
			center.northEast = tileset.top;

		if (north != null && east == null)
			center.northEast = tileset.right;
	}

	private void resolveSouthEastQuad(QuadTileset tileset, QuadTile center, QuadTile south, QuadTile southEast, QuadTile east) {

		if (south != null && east != null && southEast != null)
			center.southEast = tileset.center;

		if (south != null && southEast == null && east != null)
			center.southEast = tileset.bottomRightEdge;

		if (south == null && east == null)
			center.southEast = tileset.bottomRightCorner;

		if (south == null && east != null)
			center.southEast = tileset.bottom;

		if (south != null && east == null)
			center.southEast = tileset.right;
	}

	private void resolveSouthWestQuad(QuadTileset tileset, QuadTile center, QuadTile south, QuadTile southWest, QuadTile west) {

		if (south != null && west != null && southWest != null)
			center.southWest = tileset.center;

		if (south != null && southWest == null && west != null)
			center.southWest = tileset.bottomLeftEdge;

		if (south == null && west == null)
			center.southWest = tileset.bottomLeftCorner;

		if (south == null && west != null)
			center.southWest = tileset.bottom;

		if (south != null && west == null)
			center.southWest = tileset.left;
	}


	private QuadTile get(int x, int y) {
		return quadTiles.get(new IntVector2(x, y));
	}

	public void render(Batch batch, float posX, float posY, float quadWidth, float quadHeight, float angle){

		float quadHalfWidth = quadWidth / 2;
		float quadHalfHeight = quadHeight / 2;


		for(Map.Entry<IntVector2, QuadTile> entry : quadTiles.entrySet()){

			IntVector2 index = entry.getKey();

			float x = index.x * quadWidth;
			float y = index.y * quadHeight;


			if (entry.getValue().southWest != null)
			batch.draw(entry.getValue().southWest,
					posX + x, posY + y,
					-x, -y,
					quadHalfWidth, quadHalfHeight,
					1, 1, angle);

			if (entry.getValue().northWest != null)
				batch.draw(entry.getValue().northWest,
					posX + x, posY + y + quadHalfHeight,
					-x, -y,
					quadHalfWidth, quadHalfHeight,
					1, 1, angle);

			if (entry.getValue().northEast != null)
				batch.draw(entry.getValue().southWest,
					posX + x + quadHalfWidth, posY + y + quadHalfHeight,
					-x, -y,
					quadHalfWidth, quadHalfHeight,
					1, 1, angle);

			if (entry.getValue().southEast != null)
				batch.draw(entry.getValue().southWest,
					posX + x + quadHalfWidth, posY + y,
					-x, -y,
					quadHalfWidth, quadHalfHeight,
					1, 1, angle);

		}




	}

	class QuadTile {
		TextureRegion northWest;
		TextureRegion northEast;
		TextureRegion southWest;
		TextureRegion southEast;
	}

	class QuadTileset {

		AtlasRegion top;
		AtlasRegion topLeftCorner;
		AtlasRegion left;
		AtlasRegion bottomLeftCorner;
		AtlasRegion bottom;
		AtlasRegion bottomRightCorner;
		AtlasRegion right;
		AtlasRegion topRightCorner;

		AtlasRegion center;

		AtlasRegion topLeftEdge;
		AtlasRegion bottomLeftEdge;
		AtlasRegion bottomRightEdge;
		AtlasRegion topRightEdge;


		public QuadTileset(TextureAtlas textureAtlas) {

		}


		private AtlasRegion findRegion(TextureAtlas textureAtlas, String name){

		}
	}
}
