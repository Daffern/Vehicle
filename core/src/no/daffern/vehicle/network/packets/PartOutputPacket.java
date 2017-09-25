package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 28.04.2017.
 */
public class PartOutputPacket {
    public int wallX, wallY, layer;
    public float angle;
    public byte state;
    public PartOutputPacket(){

    }
    public PartOutputPacket(int wallX, int wallY, int layer, float angle) {
        this.wallX = wallX;
        this.wallY = wallY;
        this.layer = layer;
        this.angle = angle;
    }
}
