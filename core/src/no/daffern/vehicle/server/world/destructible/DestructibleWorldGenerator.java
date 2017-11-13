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

public class DestructibleWorldGenerator implements WorldGeneratorI {

	private World world;
	private MyServer myServer;

	private Chunks chunks;
	private ChunkClipper chunkClipper;

	public DestructibleWorldGenerator(WorldHandler worldHandler, MyServer server) {
		this.world = worldHandler.world;
		this.myServer = server;

		worldHandler.addContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				checkCollision(contact);
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

		chunks = new Chunks(world);
		chunkClipper = new ChunkClipper();

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

		//forceCheckCollision();

		List<Chunk> chunksUpdated = chunkClipper.clipQueued();
		for (Chunk chunk : chunksUpdated)
			sendChunk(chunk);
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

	private void forceCheckCollision() {
		Array<Contact> contacts = world.getContactList();
		for (Contact contact : contacts) {
			checkCollision(contact);
		}
	}

	private void checkCollision(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.getUserData() == null || fixtureB.getUserData() == null)
			return;

		UserData userDataA = (UserData) fixtureA.getUserData();
		UserData userDataB = (UserData) fixtureB.getUserData();

		if (userDataA.type == UserData.UserDataType.DRILL && userDataB.type == UserData.UserDataType.DESTRUCTIBLE) {

			processCollision((UserData.UserDataDrill) userDataA, fixtureA, (UserData.UserDataDestructible) userDataB, fixtureB);

		}
		else if (userDataB.type == UserData.UserDataType.DRILL && userDataA.type == UserData.UserDataType.DESTRUCTIBLE) {

			processCollision((UserData.UserDataDrill) userDataB, fixtureB, (UserData.UserDataDestructible) userDataA, fixtureA);

		}
	}

	private void processCollision(UserData.UserDataDrill userDataDrill,
	                              Fixture drillFixture,
	                              UserData.UserDataDestructible userDataDestructible,
	                              Fixture destructibleFixture) {

		Chunk chunk = userDataDestructible.chunk;

		//destroy the drill fixture sensor
		drillFixture.getBody().destroyFixture(drillFixture);

		//update chunk
		float[] clip = Box2dUtils.transformVertices(drillFixture.getBody().getTransform(), userDataDrill.drillVertices);

		chunkClipper.clipAndQueueFixtures(chunk, clip, destructibleFixture);


	}

	/**
	 * try to clip chunks with provided vertices
	 *
	 * @param vertices in world coordinates
	 */

	public void tryClip(final float[] vertices) {

		Box2dUtils.AABB aabb = Box2dUtils.findAABB(vertices);

		world.QueryAABB(new QueryCallback() {
			@Override
			public boolean reportFixture(Fixture fixture) {

				if (fixture.getUserData() == null || ((UserData) fixture.getUserData()).type != UserData.UserDataType.DESTRUCTIBLE)
					return true;

				UserData.UserDataDestructible ud = (UserData.UserDataDestructible) fixture.getUserData();

				chunkClipper.clipAndQueueFixtures(ud.chunk, vertices, fixture);

				return false;
			}
		}, aabb.minX, aabb.minY, aabb.maxX, aabb.maxY);


	}

	public String getDebugString(){
		int numChunks = chunks.entrySet().size();
		int numFixtures = 0;
		for (Map.Entry<IntVector2, Chunk> entry : chunks.entrySet())
			numFixtures += entry.getValue().getNumFixtures();

		return "numChunks: " + numChunks + ", totalFixtures: " + numFixtures;
	}
}
