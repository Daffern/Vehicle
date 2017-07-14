package no.daffern.vehicle.server.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import java.util.ArrayList;

/**
 * Created by Daff on 13.11.2016.
 */
public class TmxWorldLoader {

    private final static String SOLID_LAYER = "solid";

    private final static int box2dMaxVerticeCount = 8;

    private float pixelToWU;

    public TmxWorldLoader(){
    }

    public void loadMap(World world, String mapName, float pixelToWU) {
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        TiledMap tiledMap = tmxMapLoader.load(mapName);
        loadMap(world, tiledMap, pixelToWU);
    }

    public void loadMap(World world, TiledMap tiledMap, float pixelToWU){
        this.pixelToWU = pixelToWU;

        MapLayer mapLayer = tiledMap.getLayers().get(SOLID_LAYER);

        MapObjects mapObjects = mapLayer.getObjects();

        Array<PolygonMapObject> polygons = mapObjects.getByType(PolygonMapObject.class);

        for (PolygonMapObject poly : polygons) {

            float[] vertices = poly.getPolygon().getTransformedVertices();

            vertices = getWUvertices(vertices);

            createChainShape(world, vertices);
        }
    }

    private void createPolygon(World world, float[] vertices){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;

        body.createFixture(fixtureDef);
    }
    private void createChainShape(World world, float[] vertices){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        body.createFixture(fixtureDef);
    }
    private float[] getWUvertices(float[] vertices){

        for (int i = 0 ; i < vertices.length ; i++){
            vertices[i] = vertices[i] * pixelToWU;
        }
        return vertices;
    }

    private ArrayList<float[]> splitPolygon(float[] vertices){
        EarClippingTriangulator earClippingTriangulator = new EarClippingTriangulator();
        ShortArray shortArray = earClippingTriangulator.computeTriangles(vertices);

        ArrayList<float[]> triangles = new ArrayList<float[]>();

        for (int i = 0; i < shortArray.size; ) {


            float[] triangle = new float[6];
            triangle[0] = vertices[2 * shortArray.get(i)];
            triangle[1] = vertices[2 * shortArray.get(i++) + 1];
            triangle[2] = vertices[2 * shortArray.get(i)];
            triangle[3] = vertices[2 * shortArray.get(i++) + 1];
            triangle[4] = vertices[2 * shortArray.get(i)];
            triangle[5] = vertices[2 * shortArray.get(i++) + 1];

            triangles.add(triangle);
        }
        return triangles;
    }
}
