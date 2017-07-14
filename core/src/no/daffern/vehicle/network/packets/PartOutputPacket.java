package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 28.04.2017.
 */
public class PartOutputPacket {
    public int wallX, wallY, partIndex;
    public float angle;
    public PartOutputPacket(){

    }
    public PartOutputPacket(int wallX, int wallY, int partIndex, float angle) {
        this.wallX = wallX;
        this.wallY = wallY;
        this.partIndex = partIndex;
        this.angle = angle;
    }
}
