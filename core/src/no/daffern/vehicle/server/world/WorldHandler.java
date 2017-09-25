package no.daffern.vehicle.server.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.network.packets.StartTmxMapPacket;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.player.ServerPlayer;
import no.daffern.vehicle.server.world.terrainGenerator.MidpointDisplacementTerrainGenerator;
import no.daffern.vehicle.utils.ContactListenerMultiplexer;

import java.util.Map;
import java.util.Set;

/**
 * Created by Daff on 25.12.2016.
 */
public class WorldHandler {
	public World world;

	private Box2DDebugRenderer debugRenderer;

	public OrthographicCamera camera;

	private ContactListenerMultiplexer contactListenerMultiplexer;

	String currentTmxMap;

	private ContinuousWorldHandler worldGenerator;

	private enum WorldType {
		Continuous,
		Tmx
	}

	public WorldType activeWorld;

	public WorldHandler() {
		camera = new OrthographicCamera();
		camera.viewportWidth = Common.toWorldCoordinates(Gdx.graphics.getWidth());
		camera.viewportHeight = Common.toWorldCoordinates(Gdx.graphics.getHeight());
		camera.zoom = Common.cameraZoom;
		camera.update();

		world = new World(new Vector2(0, -9.8f), true);

		debugRenderer = new Box2DDebugRenderer();

		contactListenerMultiplexer = new ContactListenerMultiplexer();
		world.setContactListener(contactListenerMultiplexer);

		S.myServer.addListener(new Listener() {
			public void connected(Connection connection) {
				switch (activeWorld) {
					case Tmx:
						StartTmxMapPacket startTmxMapPacket = new StartTmxMapPacket();
						startTmxMapPacket.tmxMap = currentTmxMap;

						connection.sendTCP(startTmxMapPacket);
						break;

					case Continuous:
						worldGenerator.sendWorld(connection.getID());
				}

			}
		});


	}


	public void addContactListener(ContactListener contactListener) {
		contactListenerMultiplexer.addContactListener(contactListener);
	}

	public void removeContactListener(ContactListener contactListener) {
		contactListenerMultiplexer.removeContactListener(contactListener);
	}

	public void loadContinuousWorld() {
		if (worldGenerator == null)
			//worldGenerator = new ContinuousWorldHandler(world, S.myServer, new SimpleRandomTerrainGenerator(0.2f));
			worldGenerator = new ContinuousWorldHandler(world, S.myServer, new MidpointDisplacementTerrainGenerator(3f,1f));
			//worldGenerator = new ContinuousWorldHandler(world, S.myServer, new SimplexNoiseTerrainGenerator());

		worldGenerator.begin(0, -5);

		activeWorld = WorldType.Continuous;
	}

	public void loadWorld(String tmxMap) {
		TmxWorldLoader loader = new TmxWorldLoader();
		loader.loadMap(world, tmxMap, Common.pixelToUnits);

		StartTmxMapPacket startTmxMapPacket = new StartTmxMapPacket();
		startTmxMapPacket.tmxMap = tmxMap;

		S.myServer.sendToAllTCP(startTmxMapPacket);
		currentTmxMap = tmxMap;

		activeWorld = WorldType.Tmx;
	}

	public void worldStep() {
		if (worldGenerator != null)
			worldGenerator.update();
		world.step(Common.TIME_STEP, Common.VELOCITY_ITERATIONS, Common.POSITION_ITERATIONS);

		//translateWorldToPlayers();
	}

	//for collision tests without stepping the world
	public void zeroWorldStep() {
		world.step(0, 0, 0);

	}

	public void translateWorldToPlayers() {

		Set<Map.Entry<Integer, ServerPlayer>> entrySet = S.playerHandler.players.entrySet();
		if (entrySet.size() > 0) {
			Vector2 avgPosition = new Vector2();

			for (Map.Entry<Integer, ServerPlayer> entry : entrySet) {
				avgPosition.add(entry.getValue().getPosition());
			}
			avgPosition.scl(1 / entrySet.size());


			translateBodies(-avgPosition.x, -avgPosition.y);
		}
	}

	public void translateBodies(float x, float y) {
		Array<Body> bodies = new Array<>(world.getBodyCount());
		world.getBodies(bodies);

		for (Body body : bodies) {
			float angle = body.getAngle();
			Vector2 position = body.getPosition();

			position.x += x;
			position.y += y;

			body.setTransform(position, angle);
		}
	}

	public void debugRender() {


		if (C.cameraHandler != null) {
			Vector3 pos = C.cameraHandler.gameCamera.position;
			camera.position.x = Common.toWorldCoordinates(pos.x);
			camera.position.y = Common.toWorldCoordinates(pos.y);
			camera.update();
		}

        /*
        ServerPlayer player = S.playerHandler.players.get(1);
        if (player != null) {
            Vector2 position = player.wheelBody.getPosition();
            camera.position.x = position.x;
            camera.position.y = position.y;
            camera.update();
        }*/


		debugRenderer.render(world, camera.combined);

	}
}
