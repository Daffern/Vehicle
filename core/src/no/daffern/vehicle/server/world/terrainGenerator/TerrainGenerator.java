package no.daffern.vehicle.server.world.terrainGenerator;

/**
 * Created by Daffern on 13.05.2017.
 */
public interface TerrainGenerator {
    float[] generateLines(float startX, float startY, float segmentLength, int segmentsNum);
}
