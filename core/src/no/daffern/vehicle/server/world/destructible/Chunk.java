package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.seisw.util.geom.Poly;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.server.world.CollisionCategories;
import no.daffern.vehicle.server.world.UserData;
import no.daffern.vehicle.utils.Box2dUtils;

import java.util.List;

public class Chunk {
	public IntVector2 index;
	public Body body;
	public Poly poly;

	public Chunk(World world, float x, float y, IntVector2 index, Poly poly){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(x, y);

		this.body = world.createBody(bodyDef);
		this.poly = poly;
		this.index = index;
	}

	public void createFixture(float[] vertices){
		vertices = Box2dUtils.removeCloseVertices(vertices, 0.05f * 0.05f);

		ChainShape shape = new ChainShape();
		shape.createLoop(vertices);

		createFixture(shape);

		shape.dispose();
	}

	public void createFixture(ChainShape shape){
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = false;
		fixtureDef.filter.categoryBits = CollisionCategories.terrain;
		fixtureDef.filter.maskBits = CollisionCategories.vehicleOutside | CollisionCategories.player;

		Fixture fixture = body.createFixture(fixtureDef);

		fixture.setUserData(new UserData.UserDataDestructible(this));
	}

	public void createFixtures(List<float[]> vertexList){
		for (float[] vertices : vertexList) {
			createFixture(vertices);
		}
	}

	public void destroyFixture(Fixture fixture){
		body.destroyFixture(fixture);
	}

	public int getNumFixtures(){
		return body.getFixtureList().size;
	}

	public void destroyFixtures(){
		Array<Fixture> fixtures = body.getFixtureList();
		while(fixtures.size > 0){
			body.destroyFixture(fixtures.first());
		}
	}


	public float[][] getVertices(){
		float[][] vertexArray = new float[poly.getNumInnerPoly()][];
		for (int i = 0 ; i < poly.getNumInnerPoly() ; i++){
			Poly innerPoly = poly.getInnerPoly(i);
			float[] vertices = new float[innerPoly.getNumPoints()*2];
			for (int j = 0, k = 0; j < innerPoly.getNumPoints() ; j++){
				vertices[k++] = (float)innerPoly.getX(j);
				vertices[k++] = (float)innerPoly.getY(j);
			}
			vertexArray[i] = vertices;
		}
		return vertexArray;
	}

}
