package no.daffern.vehicle.client.handlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.ResourceManager;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.graphics.ChunkDrawer;
import no.daffern.vehicle.graphics.MyOrthogonalTiledMapRenderer;
import no.daffern.vehicle.graphics.MyTmxMapLoader;
import no.daffern.vehicle.graphics.TerrainDrawer;
import no.daffern.vehicle.network.packets.*;
import no.daffern.vehicle.server.world.TmxWorldLoader;
import no.daffern.vehicle.utils.ContactListenerMultiplexer;
import no.daffern.vehicle.utils.Tools;

/**
 * Created by Daff on 25.12.2016.
 */
public class MapHandler implements SystemInterface {

    public World world;
    private Box2DDebugRenderer debugRenderer;
    private ContactListenerMultiplexer contactListenerMultiplexer;

    ChunkDrawer chunkDrawer;
    TerrainDrawer terrainDrawer;
    MyOrthogonalTiledMapRenderer mapRenderer;



    StartTmxMapPacket lastTmxMap;

    private enum MapState {
        None,
        LoadTmxMap,
        InTmxMap,
        LoadContinuousMap,
        InContinuousMap,
        LoadDestructibleMap,
	    InDestructibleMap
    }

    public MapState mapState = MapState.None;

    public MapHandler() {

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        debugRenderer.SHAPE_AWAKE.set(new Color(0.0f, 0.0f, 1.0f, 1));

        contactListenerMultiplexer = new ContactListenerMultiplexer();
        world.setContactListener(contactListenerMultiplexer);

        terrainDrawer = new TerrainDrawer();

        C.myClient.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {

                if (object instanceof TerrainPacket) {
                    receiveTerrainPacket((TerrainPacket) object);
                }
                else if (object instanceof TerrainPacketDestructible){
					receiveDestructibleTerrain((TerrainPacketDestructible)object);
                }
                else if (object instanceof StartDestructibleMapPacket){
                	receiveStartDestructibleTerrain((StartDestructibleMapPacket)object);
                	mapState = MapState.InDestructibleMap;
                }
                else if (object instanceof StartContinuousMapPacket) {
                    StartContinuousMapPacket lcm = (StartContinuousMapPacket) object;
                    terrainDrawer.initialize(lcm.packName,lcm.surfaceRegion,lcm.groundRegion);

                    receiveTerrainPacket(lcm.terrainPacket);
                    mapState = MapState.InContinuousMap;
                }
                else if (object instanceof StartTmxMapPacket) {
                    lastTmxMap = (StartTmxMapPacket) object;

                    mapState = MapState.LoadTmxMap;
                }

            }
        });

    }
	private void receiveStartDestructibleTerrain(final StartDestructibleMapPacket packet){
    	if (chunkDrawer == null)
    		chunkDrawer = new ChunkDrawer();

    	for (TerrainPacketDestructible tpd : packet.terrainPacket){
    		tpd.multiplyVertexList(Common.unitsToPixels);
    		chunkDrawer.addChunk(tpd.index,tpd.vertexList);
	    }

		ResourceManager.loadAsset(packet.packName, TextureAtlas.class, new ResourceManager.AssetListener<TextureAtlas>() {
			@Override
			public void onAssetLoaded(TextureAtlas asset) {

				chunkDrawer.initialize(asset.findRegion(packet.groundRegion), packet.chunkWidth * Common.unitsToPixels, packet.chunkHeight * Common.unitsToPixels);

			}
		});

	}

    private void receiveDestructibleTerrain(TerrainPacketDestructible tpd){
	    tpd.multiplyVertexList(Common.unitsToPixels);
	    chunkDrawer.addChunk(tpd.index,tpd.vertexList);
    }

	private void receiveTerrainPacket(TerrainPacket tp) {
		if (terrainDrawer != null && terrainDrawer.isInitialized()) {
			terrainDrawer.receiveTerrainPacket(tp);
		} else {
			Tools.log(this, "terrainDrawer is not initiliazed");
		}
	}
    public void addContactListener(ContactListener contactListener) {
        contactListenerMultiplexer.addContactListener(contactListener);
    }


    private void loadMap(String tmxMap, Batch batch) {
        MyTmxMapLoader loader = new MyTmxMapLoader();
        TmxMapLoader.Parameters parameters = new TmxMapLoader.Parameters();
        //parameters.generateMipMaps = true;

        TiledMap tiledMap = loader.load(tmxMap, parameters);

        if (mapRenderer == null)
            mapRenderer = new MyOrthogonalTiledMapRenderer(tiledMap, batch);
        else
            mapRenderer.setMap(tiledMap);

        TmxWorldLoader tmxWorldLoader = new TmxWorldLoader();
        tmxWorldLoader.loadMap(world, tiledMap, Common.pixelToUnits);

        mapState = MapState.InTmxMap;
    }

    public void worldStep() {
        world.step(Common.TIME_STEP, Common.VELOCITY_ITERATIONS, Common.POSITION_ITERATIONS);
    }


    public void debugRender() {
        debugRenderer.render(world, C.cameraHandler.debugCamera.combined);

    }

    @Override
    public void preStep() {

    }

    @Override
    public void postStep() {

    }

    @Override
    public void render(Batch batch, float delta) {
        switch (mapState) {
            case LoadTmxMap:
                loadMap(lastTmxMap.tmxMap, batch);
                mapState = MapState.InTmxMap;
                break;

            case InTmxMap:
                mapRenderer.setView(C.cameraHandler.gameCamera);
                mapRenderer.render();
                break;

            case InContinuousMap:
                terrainDrawer.draw(batch);
                break;
	        case InDestructibleMap:
	        	chunkDrawer.draw(batch);
	        	break;

        }


    }
}
