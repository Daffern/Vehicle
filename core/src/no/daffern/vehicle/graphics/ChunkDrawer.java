package no.daffern.vehicle.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import no.daffern.vehicle.container.IntVector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkDrawer {

	private EarClippingTriangulator triangulator;

	private Map<IntVector2, Chunk> chunks;

	private List<ChunkToBeLoaded> chunksToBeLoaded;
	private boolean loaded = false;

	private float chunkWidth, chunkHeight;
	private TextureAtlas.AtlasRegion textureRegion;


	public ChunkDrawer() {
		triangulator = new EarClippingTriangulator();
		chunks = new HashMap<>();
		chunksToBeLoaded = new ArrayList<>();
	}

	/**
	 * can be called after addChunk
	 * @param atlasRegion
	 * @param chunkWidth
	 * @param chunkHeight
	 */
	public void initialize(TextureAtlas.AtlasRegion atlasRegion, float chunkWidth, float chunkHeight) {
		this.textureRegion = atlasRegion; atlasRegion.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		this.chunkWidth = chunkWidth;
		this.chunkHeight = chunkHeight;


		for (ChunkToBeLoaded chunkToBeLoaded : chunksToBeLoaded) {
			Chunk chunk = new Chunk(textureRegion, chunkToBeLoaded.vertices, chunkToBeLoaded.index, chunkWidth, chunkHeight);
			chunks.put(chunkToBeLoaded.index, chunk);
		}
		chunksToBeLoaded.clear();

		loaded = true;

	}

	public void addChunk(IntVector2 index, float[][] vertexList) {
		if (loaded) {
			Chunk chunk = new Chunk(textureRegion, vertexList, index, chunkWidth, chunkHeight);
			chunks.put(index, chunk);
		}
		else {
			ChunkToBeLoaded chunkToBeLoaded = new ChunkToBeLoaded(index, vertexList);
			chunksToBeLoaded.add(chunkToBeLoaded);
		}
	}


	public void draw(Batch batch) {
		PolygonSpriteBatch pBatch = ((PolygonSpriteBatch) batch);

		for (Map.Entry<IntVector2, Chunk> entry : chunks.entrySet()) {
			IntVector2 index = entry.getKey();
			float x = index.x * chunkWidth, y = index.y * chunkHeight;
			Chunk chunk = entry.getValue();

			if (chunk != null) {
				for (PolygonRegion region : chunk.regions)
					pBatch.draw(region, 0, 0);//vertices are global
			}

		}


	}


	class Chunk {
		PolygonRegion[] regions;

		public Chunk(TextureRegion textureRegion, float[][] vertexList, IntVector2 index, float width, float height) {
			regions = new PolygonRegion[vertexList.length];
			for (int i = 0; i < vertexList.length; i++) {

				float[] vertices = vertexList[i];

				regions[i] = new PolygonRegion(textureRegion, vertices, triangulator.computeTriangles(vertices).toArray(),
						index.x*width, index.y*height, width, height);
			}
		}
	}



	class ChunkToBeLoaded {
		IntVector2 index;
		float[][] vertices;

		public ChunkToBeLoaded(IntVector2 index, float[][] vertices) {
			this.index = index;
			this.vertices = vertices;
		}
	}
}
