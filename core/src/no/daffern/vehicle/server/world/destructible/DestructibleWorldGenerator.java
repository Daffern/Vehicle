package no.daffern.vehicle.server.world.destructible;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.network.packets.StartDestructibleMapPacket;
import no.daffern.vehicle.network.packets.TerrainPacketDestructible;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.player.ServerPlayer;
import no.daffern.vehicle.server.world.UserData;
import no.daffern.vehicle.server.world.WorldGeneratorI;
import no.daffern.vehicle.server.world.WorldHandler;
import no.daffern.vehicle.utils.Box2dUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestructibleWorldGenerator implements WorldGeneratorI, ContactListener {

	private World world;
	private MyServer myServer;

	private Chunks chunks;
	private ChunkClipper chunkClipper;

	private Filter emptyFilter;

	public DestructibleWorldGenerator(WorldHandler worldHandler, MyServer server) {
		this.world = worldHandler.world;
		this.myServer = server;

		worldHandler.addContactListener(this);

		chunks = new Chunks(world);
		chunkClipper = new ChunkClipper();

		emptyFilter = new Filter();
		emptyFilter.categoryBits = 0;
		emptyFilter.maskBits = 0;

	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.getUserData() == null || fixtureB.getUserData() == null)
			return;

		UserData userDataA = (UserData) fixtureA.getUserData();
		UserData userDataB = (UserData) fixtureB.getUserData();

		if (userDataA.type == UserData.UserDataType.DRILL && userDataB.type == UserData.UserDataType.DESTRUCTIBLE) {

			UserData.UserDataDrill udd = (UserData.UserDataDrill) userDataA;

			udd.fixtures.add(fixtureB);

		}
		else if (userDataB.type == UserData.UserDataType.DRILL && userDataA.type == UserData.UserDataType.DESTRUCTIBLE) {

			UserData.UserDataDrill udd = (UserData.UserDataDrill) userDataB;

			udd.fixtures.add(fixtureA);

		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.getUserData() == null || fixtureB.getUserData() == null)
			return;

		UserData userDataA = (UserData) fixtureA.getUserData();
		UserData userDataB = (UserData) fixtureB.getUserData();

		if (userDataA.type == UserData.UserDataType.DRILL && userDataB.type == UserData.UserDataType.DESTRUCTIBLE) {

			UserData.UserDataDrill udd = (UserData.UserDataDrill) userDataA;
			udd.fixtures.remove(fixtureB);
		}
		else if (userDataB.type == UserData.UserDataType.DRILL && userDataA.type == UserData.UserDataType.DESTRUCTIBLE) {

			UserData.UserDataDrill udd = (UserData.UserDataDrill) userDataB;
			udd.fixtures.remove(fixtureA);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		//checkCollision(contact);
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

			List<Chunk> chunksCreated = chunks.createChunksAround(pos.x, pos.y);
			for (Chunk chunk : chunksCreated)
				sendChunk(chunk);
		}


	}

	private void forceCheckCollision() {
		Array<Contact> contacts = world.getContactList();
		for (Contact contact : contacts) {
			beginContact(contact);
		}
	}

	@Override
	public void sendWorld(int clientId) {

		List<TerrainPacketDestructible> packets = new ArrayList<>();

		for (HashMap.Entry<IntVector2, Chunk> entry : chunks.entrySet()) {
			Chunk chunk = entry.getValue();
			IntVector2 index = entry.getKey();

			float[][] vertexList = chunk.getVertices();

			TerrainPacketDestructible terrainPacket = new TerrainPacketDestructible();
			terrainPacket.vertexList = vertexList;
			terrainPacket.index = index;

			packets.add(terrainPacket);
		}

		StartDestructibleMapPacket startPacket = new StartDestructibleMapPacket();
		startPacket.terrainPacket = packets.toArray(new TerrainPacketDestructible[packets.size()]);
		startPacket.chunkWidth = chunks.getChunkWidth();
		startPacket.chunkHeight = chunks.getChunkHeight();
		startPacket.packName = "packed/pack.atlas";
		startPacket.groundRegion = "ground/ground2";

		myServer.sendToTCP(clientId, startPacket);
	}

	public void sendChunk(Chunk chunk) {
		TerrainPacketDestructible tpd = new TerrainPacketDestructible();
		tpd.index = chunk.index;
		tpd.position = chunk.body.getPosition();
		tpd.vertexList = chunk.getVertices();
		myServer.sendToAllTCP(tpd);
	}


	public void clipChunkFixture(Fixture fixture, float[] vertices){


		if (fixture.getUserData() == null || ((UserData)fixture.getUserData()).type != UserData.UserDataType.DESTRUCTIBLE)
			return;

		chunkClipper.clipChunk(fixture, vertices);

		Chunk chunk = ((UserData.UserDataDestructible)fixture.getUserData()).chunk;

		sendChunk(chunk);
	}

	/**
	 * try to clip chunks with provided vertices
	 * @param vertices in world coordinates
	 */

	public void tryClipChunkFixture(final float[] vertices) {

		Box2dUtils.AABB aabb = Box2dUtils.findAABB(vertices);

		final List<Fixture> fixtures = new ArrayList<>();

		world.QueryAABB(new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {

				if (fixture.getUserData() == null || ((UserData) fixture.getUserData()).type != UserData.UserDataType.DESTRUCTIBLE)
					return true;

				fixtures.add(fixture);

				return true;
			}
		}, aabb.minX, aabb.minY, aabb.maxX, aabb.maxY);

		for (Fixture fixture : fixtures) {
			chunkClipper.clipChunk(fixture, vertices);

			sendChunk(((UserData.UserDataDestructible) fixture.getUserData()).chunk);
		}
	}

	public String getDebugString() {
		int numChunks = chunks.entrySet().size();
		int numFixtures = 0;
		for (Map.Entry<IntVector2, Chunk> entry : chunks.entrySet())
			numFixtures += entry.getValue().getNumFixtures();

		return "numChunks: " + numChunks + ", totalFixtures: " + numFixtures;
	}


	private class CollisionEvent {

		Fixture chunkFixture, drillFixture;
		UserData.UserDataDestructible userDataDestructible;
		UserData.UserDataDrill userDataDrill;

		public CollisionEvent(Fixture chunkFixture, UserData.UserDataDestructible userDataDestructible, Fixture drillFixture, UserData.UserDataDrill userDataDrill) {
			this.chunkFixture = chunkFixture;
			this.userDataDestructible = userDataDestructible;
			this.drillFixture = drillFixture;
			this.userDataDrill = userDataDrill;
		}
	}
}
