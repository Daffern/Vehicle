package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 28.04.2017.
 */
public class PartOutputPacket {

	public int vehicleId;//optional
	public int wallX, wallY, layer;
    public float angle;
    public byte state;

    public PartOutputPacket(){

    }
	public PartOutputPacket(int vehicleId, int wallX, int wallY, int layer, float angle, byte state) {
		this.vehicleId = vehicleId;
		this.wallX = wallX;
		this.wallY = wallY;
		this.layer = layer;
		this.angle = angle;
		this.state = state;
	}

    public PartOutputPacket(int wallX, int wallY, int layer, float angle, byte state) {
        this.wallX = wallX;
        this.wallY = wallY;
        this.layer = layer;
        this.angle = angle;
        this.state = state;
    }
}
