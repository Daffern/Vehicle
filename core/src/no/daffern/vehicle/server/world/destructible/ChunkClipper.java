package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.seisw.util.geom.Point2D;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;
import com.seisw.util.geom.PolySimple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ChunkClipper {

	private Queue<QueuedChunk> chunksToUpdate = new LinkedList<>();
	private double chunkMinSize = 1;


	private List<Chunk> clippedChunks = new ArrayList<>(chunksToUpdate.size());

	void clipAndQueueFixtures(Chunk chunk, float[] vertices, Fixture prevFixture) {


		PolySimple clip = new PolySimple();

		for (int i = 0; i < vertices.length; ) {
			clip.add(new Point2D(vertices[i++], vertices[i++]));
		}

		chunk.poly = chunk.poly.difference(clip);

		chunksToUpdate.add(new QueuedChunk(chunk, prevFixture));

	}

	List<Chunk> clipQueued() {
		clippedChunks.clear();

		while (!chunksToUpdate.isEmpty()) {
			QueuedChunk queuedChunk = chunksToUpdate.poll();

			queuedChunk.destroyOldFixture();

			updateFixtures(queuedChunk.chunk);

			clippedChunks.add(queuedChunk.chunk);
		}
		return clippedChunks;
	}

	private void updateFixtures(Chunk chunk) {
		//first filter out unwanted polys
		Poly newPoly = new PolyDefault();

		for (int i = 0; i < chunk.poly.getNumInnerPoly(); i++) {

			Poly poly = chunk.poly.getInnerPoly(i);

			if (poly.isEmpty() || poly.isHole())
				continue;

			if (poly.getArea() < chunkMinSize)
				continue;

			newPoly.add(poly);
		}

		chunk.poly = newPoly;

		//create fixture for each poly
		Vector2 pos = chunk.body.getPosition();
		for (int i = 0; i < chunk.poly.getNumInnerPoly(); i++) {

			Poly poly = chunk.poly.getInnerPoly(i);

			float[] newVertices = new float[poly.getNumPoints() * 2];
			for (int j = 0, k = 0; j < poly.getNumPoints(); j++) {
				newVertices[k++] = (float) poly.getX(j) - pos.x;
				newVertices[k++] = (float) poly.getY(j) - pos.y;
			}

			chunk.createFixture(newVertices);
		}
	}


	private class QueuedChunk {
		Chunk chunk;
		Fixture oldFixture;

		public QueuedChunk(Chunk chunk, Fixture oldFixture) {
			this.chunk = chunk;
			this.oldFixture = oldFixture;
		}

		public void destroyOldFixture() {
			chunk.destroyFixture(oldFixture);
		}


	}
}
