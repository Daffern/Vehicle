package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.seisw.util.geom.Point2D;
import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;
import com.seisw.util.geom.PolySimple;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.player.ServerPlayer;
import no.daffern.vehicle.server.world.CollisionCategories;
import no.daffern.vehicle.server.world.UserData;
import no.daffern.vehicle.server.world.WorldGeneratorI;
import no.daffern.vehicle.server.world.WorldHandler;
import no.daffern.vehicle.utils.Box2dUtils;
import no.daffern.vehicle.utils.Tools;

import java.util.*;

public class DestructibleWorldGenerator implements WorldGeneratorI {

	private World world;
	private MyServer myServer;
	private Body worldBody;


	float surfaceLevel = 0;

	private float chunkSize = 16;
	private Map<IntVector2, DestructibleChunk> chunks;
	private Queue<DestructibleChunk> chunksToUpdate;


	public DestructibleWorldGenerator(WorldHandler worldHandler, MyServer server) {
		this.world = worldHandler.world;
		this.myServer = server;

		worldHandler.addContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				//checkCollision(contact);
			}
		});

		chunks = new HashMap<>();
		chunksToUpdate = new LinkedList<>();

	}



	private void bruteForceCheckCollision(){
		Array<Contact> contacts = world.getContactList();

		for (Contact contact : contacts){
			checkCollision(contact);
		}

	}
	private void checkCollision(Contact contact){
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.getUserData() == null || fixtureB.getUserData() == null)
			return;

		UserData userDataA = (UserData)fixtureA.getUserData();
		UserData userDataB = (UserData)fixtureB.getUserData();

		if (userDataA.type == UserData.UserDataType.DRILL && userDataB.type == UserData.UserDataType.DESTRUCTIBLE){

			UserData.UserDataDrill userDataDrill = ((UserData.UserDataDrill)userDataA);
			DestructibleChunk chunk = ((UserData.UserDataDestructible)userDataB).chunk;

			if (userDataDrill.recentChunks.contains(chunk))
				return;

			userDataDrill.recentChunks.add(chunk);

			float[] clip = Box2dUtils.transformVertices(fixtureA.getBody().getTransform(), userDataDrill.drillVertices);

			queueClip(chunk, clip);

			//Tools.log(this,"drill-destructible collision at drill: "+fixtureA.getBody().getPosition()+", destructible: "+fixtureB.getBody().getPosition());
		}
		else if (userDataB.type == UserData.UserDataType.DRILL && userDataA.type == UserData.UserDataType.DESTRUCTIBLE){

			UserData.UserDataDrill userDataDrill = ((UserData.UserDataDrill)userDataB);
			DestructibleChunk chunk = ((UserData.UserDataDestructible)userDataA).chunk;

			if (userDataDrill.recentChunks.contains(chunk))
				return;

			userDataDrill.recentChunks.add(chunk);

			float[] clip = Box2dUtils.transformVertices(fixtureB.getBody().getTransform(), userDataDrill.drillVertices);

			queueClip(chunk, clip);
			//Tools.log(this,"drill-destructible collision at drill: "+fixtureB.getBody().getPosition()+", destructible: "+fixtureA.getBody().getPosition());

		}
	}


	@Override
	public void begin(float x, float y) {
		//this.surfaceLevel = y;
	}

	@Override
	public void update() {

		//generate chunks around each player
		Map<Integer, ServerPlayer> players = S.playerHandler.players;

		for (Map.Entry<Integer, ServerPlayer> entry : players.entrySet()) {

			Vector2 pos = entry.getValue().getPosition();

			createChunksAround(pos.x, pos.y);
		}

		bruteForceCheckCollision();

		//update clipped fixtures
		while(!chunksToUpdate.isEmpty()){

			DestructibleChunk chunk = chunksToUpdate.poll();

			Vector2 pos = chunk.body.getPosition();

			float[] vertices = new float[chunk.poly.getNumPoints()*2];
			for(int i = 0, j = 0 ; i < chunk.poly.getNumPoints() ; i++){
				vertices[j++] = (float)chunk.poly.getX(i) - pos.x;
				vertices[j++] = (float)chunk.poly.getY(i) - pos.y;
			}

			chunk.body.destroyFixture(chunk.fixture);
			chunk.fixture = createGroundFixture(chunk.body, vertices);

			chunk.fixture.setUserData(new UserData.UserDataDestructible(chunk));

		}
	}


	private void createChunksAround(float posX, float posY) {

		float minX = posX - chunkSize;
		float maxX = posX + chunkSize;
		float minY = posY - chunkSize;
		float maxY = Math.min(posY + chunkSize, surfaceLevel);

		int startX = MathUtils.round(minX / chunkSize);
		int startY = MathUtils.round(minY / chunkSize);
		int endX = MathUtils.round(maxX / chunkSize);
		int endY = MathUtils.round(maxY / chunkSize);

		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {

				IntVector2 index = new IntVector2(x, y);

				DestructibleChunk chunk = chunks.get(index);
				if (chunk == null) {
					chunk = createChunk(x, y);
					chunks.put(index, chunk);
				}
			}
		}
	}

	private DestructibleChunk createChunk(int x, int y) {

		Tools.log(this, "created chunk at: " + x + ", " + y);

		float minX = x * chunkSize, maxX = (x + 1) * chunkSize;
		float minY = y * chunkSize, maxY = (y + 1) * chunkSize;

		float lenX = maxX - minX;
		float lenY = maxY - minY;

		float[] vertices = new float[]{
				0, 0,
				0, lenY,
				lenX, lenY,
				lenX, 0
		};

		//BODY
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(minX, minY);

		Body body = world.createBody(bodyDef);

		//FIXTURE
		Fixture fixture = createGroundFixture(body, vertices);

		//POLY
		Poly poly = new PolyDefault();
		for (int i = 0 ; i < vertices.length ; ){
			//translate with body position
			Point2D point = new Point2D(vertices[i++] + minX, vertices[i++] + minY);
			poly.add(point);
		}
		poly.add(vertices[0] + minX, vertices[0] + minY);//add the final point

		DestructibleChunk chunk = new DestructibleChunk();
		chunk.fixture = fixture;
		chunk.body = body;
		chunk.poly = poly;

		chunk.fixture.setUserData(new UserData.UserDataDestructible(chunk));

		return chunk;
	}

	private Fixture createGroundFixture(Body body, float[] vertices){

		vertices = Box2dUtils.removeCloseVertices(vertices,0.05f);

		ChainShape shape = new ChainShape();
		shape.createLoop(vertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = false;
		fixtureDef.filter.categoryBits = CollisionCategories.terrain;
		fixtureDef.filter.maskBits = CollisionCategories.vehicleOutside | CollisionCategories.player;

		Fixture fixture = body.createFixture(fixtureDef);

		shape.dispose();

		return fixture;
	}

	/**
	 * for testing and stuff (there is some kind of limit on the size of this polygon)
	 * @param vertices
	 */
	public void clip(float[] vertices){
		List<DestructibleChunk> chunks = new ArrayList<>(4);

		float[] aabb = Box2dUtils.findAABB(vertices);
		for (int i = 0 ; i < aabb.length ; i+=2){

			DestructibleChunk chunk = getChunkAtPos(aabb[i],aabb[i+1]);
			if (chunk != null && !chunks.contains(chunk))
				chunks.add(chunk);
		}

		for (DestructibleChunk chunk : chunks)
			queueClip(chunk, vertices);

	}

	private void queueClip(DestructibleChunk chunk, float[] vertices){

		PolySimple clip = new PolySimple();

		for (int i = 0 ; i < vertices.length ; ){
			clip.add(new Point2D(vertices[i++], vertices[i++]));
		}

		Poly clippedPoly = chunk.poly.difference(clip);

		if (clippedPoly.getNumInnerPoly() == 0)
			return;

		//find the biggest area
		Poly biggestPoly = clippedPoly.getInnerPoly(0);
		for (int i = 0 ; i < clippedPoly.getNumInnerPoly() ; i++){
			if (clippedPoly.getInnerPoly(i).getArea() > biggestPoly.getArea())
				biggestPoly = clippedPoly.getInnerPoly(i);
		}

		chunk.poly = new PolyDefault();
		chunk.poly.add(biggestPoly);

		chunksToUpdate.offer(chunk);

	}



	@Override
	public void sendWorld(int clientId) {

	}



	private DestructibleChunk getChunkAtPos(float x, float y) {
		int chunkX = (int) x / (int) chunkSize - 1;
		int chunkY = (int) y / (int) chunkSize - 1;

		DestructibleChunk chunk = chunks.get(new IntVector2(chunkX, chunkY));
		return chunk;
	}


}
