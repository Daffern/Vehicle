package no.daffern.vehicle.server.world;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.FloatArray;
import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.network.packets.StartContinuousMapPacket;
import no.daffern.vehicle.network.packets.TerrainPacket;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.player.ServerPlayer;
import no.daffern.vehicle.utils.Tools;
import no.daffern.vehicle.server.world.terrainGenerator.TerrainGenerator;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daffern on 04.05.2017.
 */
public class ContinuousWorldHandler {

    MyServer myServer;
    StartContinuousMapPacket smp;

    World world;
    Body body;

    TerrainGenerator terrainGenerator;


    private static int nextChunkId = 0;
    ArrayList<Chunk> chunks;

    int numVertices = 0;

    int chunkSize = 16;//number of segments per chunk. MUST BE A POWER OF 2 FOR MIDPOINT DISPLACEMENT GENERATOR
    float segmentLength = 0.5f;

    float currentMaxX, currentMinX;
    float currentMaxY, currentMinY;



    boolean started = false;


    public ContinuousWorldHandler(World world, MyServer myServer, TerrainGenerator terrainGenerator) {
        this.myServer = myServer;
        this.world = world;
        this.terrainGenerator = terrainGenerator;
        chunks = new ArrayList<>();
    }


    public void begin(float startX, float startY) {
        this.currentMaxX = startX;
        this.currentMinX = startX;
        this.currentMinY = startY;
        this.currentMaxY = startY;


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);

        smp = new StartContinuousMapPacket();
        smp.packName = "packed/pack.atlas";
        smp.surfaceRegion = "ground/surface2";
        smp.groundRegion = "ground/ground2";

        myServer.sendToAllTCP(smp);

        started = true;
    }

    public void sendWorld(int clientId) {
        myServer.sendToTCP(clientId, smp);


        FloatArray vertices = new FloatArray();

        for (Chunk chunk : chunks) {
            vertices.addAll(chunk.vertices);
        }

        TerrainPacket terrainPacket = new TerrainPacket();
        terrainPacket.vertices = vertices.toArray();
        myServer.sendToTCP(clientId, terrainPacket);
    }

    public void update() {

        if (!started)
            return;

        Map<Integer, ServerPlayer> players = S.playerHandler.players;

        float minX = 0, maxX = 0;

        for (Map.Entry<Integer, ServerPlayer> entry : players.entrySet()) {

            float playerX = entry.getValue().getPosition().x;

            if (playerX > maxX) {
                maxX = playerX;
            }
            if (playerX < minX) {
                minX = playerX;
            }


        }


        createSegments(minX, maxX);

    }

    private void createSegments(float minX, float maxX) {

        while (maxX > currentMaxX){
            float[] newVertices = terrainGenerator.generateLines(currentMaxX,currentMaxY, segmentLength, chunkSize);

            Fixture fixture = createFixture(newVertices);

            Chunk chunk = new Chunk(fixture, newVertices);
            chunks.add(chunk);

            currentMaxX = chunk.getLastX();
            currentMaxY = chunk.getLastY();
            numVertices += chunk.getNumVertices();

            sendVertices(newVertices);


            Tools.log(this, "generated a chunk from: " + chunk.getFirstX() + " to: " + chunk.getLastX());
        }


        while (minX < currentMinX){
            float[] newVertices = terrainGenerator.generateLines(currentMinX,currentMinY, -segmentLength, chunkSize);

            Fixture fixture = createFixture(newVertices);

            Chunk chunk = new Chunk(fixture, newVertices);
            chunks.add(chunk);

            currentMinX = chunk.getLastX();
            currentMinY = chunk.getLastY();
            numVertices += chunk.getNumVertices();

            sendVertices(newVertices);


            Tools.log(this, "generated a chunk from: " + chunk.getLastX() + " to: " + chunk.getFirstX());
        }


    }

    private Fixture createFixture(float[] vertices){
        ChainShape chainShape = new ChainShape();
        chainShape.createChain(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.isSensor = false;
        fixtureDef.filter.categoryBits = CollisionCategories.terrain;
        fixtureDef.filter.maskBits = CollisionCategories.vehicleOutside | CollisionCategories.player;

        return body.createFixture(fixtureDef);

    }

    private void sendVertices(float[] vertices) {

        TerrainPacket terrainPacket = new TerrainPacket();
        terrainPacket.vertices = vertices;

        myServer.sendToAllTCP(terrainPacket);

    }



    private class Chunk {

        Fixture fixture;
        float[] vertices;
        int chunkId;

        public Chunk(Fixture fixture, float[] vertices) {
            this.fixture = fixture;
            this.vertices = vertices;
            this.chunkId = nextChunkId++;
        }

        int getNumVertices(){
            return vertices.length/2;
        }

        float getLastX() {
            return vertices[vertices.length - 2];
        }

        float getLastY() {
            return vertices[vertices.length - 1];
        }

        float getFirstX() {
            return vertices[0];
        }

        float getFirstY() {
            return vertices[1];
        }
    }
}
