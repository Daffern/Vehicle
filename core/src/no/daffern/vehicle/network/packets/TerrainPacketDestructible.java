package no.daffern.vehicle.network.packets;

import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.container.IntVector2;

public class TerrainPacketDestructible {
	public IntVector2 index;
	public Vector2 position;
	public float[][] vertexList;

	public void multiplyVertexList(float factor) {
		for (int i = 0; i < vertexList.length; i++)
			for (int j = 0; j < vertexList[i].length; j++)
				vertexList[i][j] = vertexList[i][j] * factor;
	}
}
