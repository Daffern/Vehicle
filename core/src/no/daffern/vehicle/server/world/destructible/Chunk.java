package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.seisw.util.geom.Point2D;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolySimple;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.server.world.CollisionCategories;
import no.daffern.vehicle.server.world.UserData;

public class Chunk {
	public final static float chunkSize = 16;

	float[] vertices = new float[]{
			0, 0,
			0, chunkSize,
			chunkSize, chunkSize,
			chunkSize, 0
	};

	public IntVector2 index;
	public Body body;

	public Chunk(World world, IntVector2 index){

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(index.x * chunkSize, index.y * chunkSize);

		this.body = world.createBody(bodyDef);
		this.index = index;

		createFixture(vertices);
	}

	public void createFixture(float[] vertices){
		//vertices = Box2dUtils.snapVertices(vertices, .2f);
		//vertices = Box2dUtils.removeCloseVertices(vertices, 0.05f );

		//create fixture
		ChainShape shape = new ChainShape();
		shape.createLoop(vertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = false;
		fixtureDef.filter.categoryBits = CollisionCategories.terrain;
		fixtureDef.filter.maskBits = CollisionCategories.vehicleOutside | CollisionCategories.player;

		Fixture fixture = body.createFixture(fixtureDef);

		shape.dispose();

		//create poly
		float x = index.x * chunkSize, y = index.y * chunkSize;
		Poly poly = new PolySimple();
		for (int i = 0; i < vertices.length; ) {
			//translate with body position
			Point2D point = new Point2D(vertices[i++] + x, vertices[i++] + y);
			poly.add(point);
		}

		fixture.setUserData(new UserData.UserDataDestructible(this, poly));
	}


	public void destroyFixture(Fixture fixture){
		body.destroyFixture(fixture);
	}

	public int getNumFixtures(){
		return body.getFixtureList().size;
	}


	public float[][] getVertices(){

		Array<Fixture> fixtures = body.getFixtureList();

		float[][] vertexArray = new float[fixtures.size][];

		for (int i = 0 ; i < fixtures.size ; i++){
			Fixture fixture = fixtures.get(i);

			UserData.UserDataDestructible udd = (UserData.UserDataDestructible)fixture.getUserData();

			float[] vertices;

			//retrieve vertices from poly
			if (udd.poly != null){
				vertices = new float[udd.poly.getNumPoints()*2];
				for (int j = 0, k = 0; j < udd.poly.getNumPoints() ; j++){
					vertices[k++] = (float)udd.poly.getX(j);
					vertices[k++] = (float)udd.poly.getY(j);
				}
			}
			//retrieve from shape
			else{
				ChainShape shape = (ChainShape)fixture.getShape();
				vertices = new float[shape.getVertexCount()];
				for (int j = 0, k = 0 ; j < shape.getVertexCount() ; j++){
					Vector2 vertex = new Vector2();
					shape.getVertex(j, vertex);
					vertices[k++] = vertex.x;
					vertices[k++] = vertex.y;
				}
			}
			vertexArray[i] = vertices;
		}

		return vertexArray;
	}

}
