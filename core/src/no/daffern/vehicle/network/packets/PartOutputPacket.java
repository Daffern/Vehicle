package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 28.04.2017.
 */
public class PartOutputPacket {

    //first 100 states reserved for 0-100% (for battery etc)
    public static final int STATE_NONE = 101;
	public static final int STATE_ON = 102;
	public static final int STATE_OFF = 103;


	public int wallX, wallY, layer;
    public float angle;
    public byte state;
    public PartOutputPacket(){

    }
    public PartOutputPacket(int wallX, int wallY, int layer, float angle, byte state) {
        this.wallX = wallX;
        this.wallY = wallY;
        this.layer = layer;
        this.angle = angle;
        this.state = state;
    }
}
