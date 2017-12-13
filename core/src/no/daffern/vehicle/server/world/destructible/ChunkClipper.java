package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.FloatArray;
import com.seisw.util.geom.Point2D;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;
import com.seisw.util.geom.PolySimple;
import no.daffern.vehicle.server.world.UserData;
import no.daffern.vehicle.utils.Tools;


public class ChunkClipper {

	private double chunkMinSize = 1;


	void clipChunk(Fixture fixture, float[] vertices) {
		if (fixture.getUserData() == null || ((UserData) fixture.getUserData()).type != UserData.UserDataType.DESTRUCTIBLE){
			Tools.log(this, "Cannot clip fixture with userdata != DESTRUCTIBLE");
			return;
		}

		UserData.UserDataDestructible udd = (UserData.UserDataDestructible) fixture.getUserData();

		Chunk chunk = udd.chunk;
		Poly poly = udd.poly;

		if (poly == null) {
			udd.poly = poly = polyFromFixture(fixture);
			Tools.log(this, "Poly was null, creating");
		}


		Poly clip = new PolySimple();
		for (int i = 0; i < vertices.length; ) {
			clip.add(new Point2D(vertices[i++], vertices[i++]));
		}

		clipChunk(chunk, fixture, poly, clip);
	}

	void clipChunk(Chunk chunk, Fixture fixture, Poly subjectPoly, Poly clipPoly) {

		chunk.body.destroyFixture(fixture);

		Poly diffPoly = new PolyDefault();
		diffPoly.add(subjectPoly);

		diffPoly = diffPoly.difference(clipPoly);

		diffPoly = filterPoly(diffPoly);


		Vector2 pos = chunk.body.getPosition();
		for (int i = 0; i < diffPoly.getNumInnerPoly(); i++) {

			Poly poly = diffPoly.getInnerPoly(i);

			float[] newVertices = fixVertices(poly, pos);

			chunk.createFixture(newVertices);
		}

	}

	Poly filterPoly(Poly poly) {
		Poly newPoly = new PolyDefault();

		for (int i = 0; i < poly.getNumInnerPoly(); i++) {

			Poly p = poly.getInnerPoly(i);

			if (p.isEmpty() || p.isHole())
				continue;

			if (p.getArea() < chunkMinSize)
				continue;

			newPoly.add(p);
		}
		return newPoly;
	}

	/**
	 * Translates vertices by pos. Removes vertices too close and aligns vertices with chunks
	 *
	 * @param poly
	 * @return
	 */
	float[] fixVertices(Poly poly, Vector2 pos) {

		float x1 = (float) poly.getX(0) - pos.x;
		float y1 = (float) poly.getY(0) - pos.y;
		float x2, y2;

		FloatArray vertices = new FloatArray(poly.getNumPoints());
		vertices.add(x1);
		vertices.add(y1);

		for (int j = 1; j < poly.getNumPoints(); j++) {

			x2 = (float) poly.getX(j) - pos.x;
			y2 = (float) poly.getY(j) - pos.y;

			float dx = x1 - x2;
			float dy = y1 - y2;

			x1 = x2;
			y1 = y2;

			if ((dx * dx) + (dy * dy) < 0.05f * 0.05f) {
				Tools.log(this, "removed a vertice");
				continue;
			}

			//align chunks
			float minChunkAlignDistance = 0.05f;

			float modX = x2 % Chunk.chunkSize;
			float modY = y2 % Chunk.chunkSize;

			if (x2 >= 0) {
				// 0 ... 0.05
				if (modX <= minChunkAlignDistance)
					x2 = MathUtils.floorPositive(x2);
				// 15.95 ... 16
				else if (modX >= Chunk.chunkSize - minChunkAlignDistance)
					x2 = MathUtils.ceilPositive(x2);
			}else{
				// -0.05 ... 0
				if (modX >= -minChunkAlignDistance)
					x2 = MathUtils.ceil(x2);
				// -16 ... -15.95
				else if (modX <= -Chunk.chunkSize + minChunkAlignDistance)
					x2 = MathUtils.floor(x2);
			}
			if (y2 >= 0) {
				// 0 ... 0.05
				if (modY <= minChunkAlignDistance)
					y2 = MathUtils.floorPositive(y2);
					// 15.95 ... 16
				else if (modY >= Chunk.chunkSize - minChunkAlignDistance)
					y2 = MathUtils.ceilPositive(y2);
			}else{
				// -0.05 ... 0
				if (modY >= -minChunkAlignDistance)
					y2 = MathUtils.ceil(y2);
					// -16 ... -15.95
				else if (modY <= -Chunk.chunkSize + minChunkAlignDistance)
					y2 = MathUtils.floor(y2);
			}


			vertices.add(x2);
			vertices.add(y2);

		}
		return vertices.toArray();
	}

	Poly polyFromFixture(Fixture fixture) {

		Vector2 pos = fixture.getBody().getPosition();
		ChainShape shape = (ChainShape) fixture.getShape();
		Vector2 vertex = new Vector2();

		Poly poly = new PolySimple();


		for (int i = 0; i < shape.getVertexCount(); i += 2) {
			shape.getVertex(i, vertex);
			poly.add(new Point2D(vertex.x + pos.x, vertex.y + pos.y));
		}

		return poly;
	}


}
