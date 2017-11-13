package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.seisw.util.geom.Point2D;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.utils.Tools;

import java.util.*;

public class Chunks {

	private World world;

	private Map<IntVector2, Chunk> chunks = new HashMap<>();
	private List<Chunk> chunksCreated = new ArrayList<>();

	private float spawnRadius = 16;
	private float surfaceLevel = 0;
	private float chunkSize = 16;

	public Chunks(World world) {
		this.world = world;
	}

	List<Chunk> createChunksAround(float posX, float posY) {

		chunksCreated.clear();

		float minX = posX - spawnRadius;
		float maxX = posX + spawnRadius;
		float minY = posY - spawnRadius;
		float maxY = Math.min(posY + spawnRadius, surfaceLevel);

		int startX = MathUtils.round(minX / chunkSize);
		int startY = MathUtils.round(minY / chunkSize);
		int endX = MathUtils.round(maxX / chunkSize);
		int endY = MathUtils.round(maxY / chunkSize);

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {

				IntVector2 index = new IntVector2(x, y);

				Chunk chunk = chunks.get(index);
				if (chunk == null) {
					chunk = createChunk(index);
					chunks.put(index, chunk);
					chunksCreated.add(chunk);
				}
			}
		}

		return chunksCreated;
	}

	private Chunk createChunk(IntVector2 index) {


		float minX = index.x * chunkSize, maxX = (index.x + 1) * chunkSize;
		float minY = index.y * chunkSize, maxY = (index.y + 1) * chunkSize;

		float lenX = maxX - minX;
		float lenY = maxY - minY;

		float[] vertices = new float[]{
				0, 0,
				0, lenY,
				lenX, lenY,
				lenX, 0
		};

		//POLY
		Poly poly = new PolyDefault();
		for (int i = 0; i < vertices.length; ) {
			//translate with body position
			Point2D point = new Point2D(vertices[i++] + minX, vertices[i++] + minY);
			poly.add(point);
		}

		Chunk chunk = new Chunk(world, minX, minY, index,poly);

		chunk.createFixture(vertices);

		Tools.log(this, "created chunk at: " + index);

		return chunk;
	}
	public Set<Map.Entry<IntVector2, Chunk>> entrySet(){
		return chunks.entrySet();
	}
	public float getChunkWidth(){
		return chunkSize;
	}
	public float getChunkHeight(){
		return chunkSize;
	}

	Chunk getChunkAtPos(float x, float y) {
		int chunkX = MathUtils.ceil(x / chunkSize) - 1;
		int chunkY = MathUtils.ceil(y / chunkSize) - 1;

		Chunk chunk = chunks.get(new IntVector2(chunkX, chunkY));
		return chunk;
	}

}
