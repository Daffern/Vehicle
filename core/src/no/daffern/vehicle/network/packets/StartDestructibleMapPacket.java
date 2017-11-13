package no.daffern.vehicle.network.packets;

public class StartDestructibleMapPacket {
	public String packName;
	public String surfaceRegion;
	public String groundRegion;

	public float chunkWidth, chunkHeight;
	public TerrainPacketDestructible[] terrainPacket;
}
