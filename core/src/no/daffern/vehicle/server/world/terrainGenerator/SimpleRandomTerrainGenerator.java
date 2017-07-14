package no.daffern.vehicle.server.world.terrainGenerator;

import com.badlogic.gdx.math.RandomXS128;

import java.util.Random;

/**
 * Created by Daffern on 13.05.2017.
 */
public class SimpleRandomTerrainGenerator implements TerrainGenerator {

    private Random random;
    private float maxElevation;

    public SimpleRandomTerrainGenerator(float maxElevation){
        random = new RandomXS128();
        this.maxElevation = maxElevation;
    }

    @Override
    public float[] generateLines(float startX, float startY, float segmentLength, int segmentsNum) {

        float x = startX;
        float y = startY;

        float[] vertices = new float[segmentsNum*2];

        for (int i = 0 ; i < segmentsNum*2 ; i++){
            vertices[i] = x;
            i++;
            vertices[i] = y;



            startX += segmentLength;
            startY += (2 * random.nextFloat() - 1) * maxElevation;
        }

        return vertices;
    }
}
