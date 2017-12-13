package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.utils.Tools;

import java.util.*;

public class Chunks {

	private World world;

	private Map<IntVector2, Chunk> chunks = new HashMap<>();
	private List<Chunk> chunksCreated = new ArrayList<>();

	private float spawnRadius = 16;
	private float surfaceLevel = 0;

	public Chunks(World world) {
		this.world = world;
	}

	List<Chunk> createChunksAround(float posX, float posY) {

		chunksCreated.clear();

		float minX = posX - spawnRadius;
		float maxX = posX + spawnRadius;
		float minY = posY - spawnRadius;
		float maxY = Math.min(posY + spawnRadius, surfaceLevel);

		int startX = MathUtils.round(minX / Chunk.chunkSize);
		int startY = MathUtils.round(minY / Chunk.chunkSize);
		int endX = MathUtils.round(maxX / Chunk.chunkSize);
		int endY = MathUtils.round(maxY / Chunk.chunkSize);

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {

				IntVector2 index = new IntVector2(x, y);

				Chunk chunk = chunks.get(index);
				if (chunk == null) {
					chunk = new Chunk(world, index);
					chunks.put(index, chunk);
					chunksCreated.add(chunk);

					Tools.log(this, "created chunk at: " + index);
				}
			}
		}

		return chunksCreated;
	}

	public Set<Map.Entry<IntVector2, Chunk>> entrySet(){
		return chunks.entrySet();
	}
	public float getChunkWidth(){
		return Chunk.chunkSize;
	}
	public float getChunkHeight(){
		return Chunk.chunkSize;
	}

	Chunk getChunkAtPos(float x, float y) {
		int chunkX = MathUtils.ceil(x / Chunk.chunkSize) - 1;
		int chunkY = MathUtils.ceil(y / Chunk.chunkSize) - 1;

		Chunk chunk = chunks.get(new IntVector2(chunkX, chunkY));
		return chunk;
	}

}
