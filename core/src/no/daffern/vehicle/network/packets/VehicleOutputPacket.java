package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 11.12.2016.
 */
public class VehicleOutputPacket{
    public int vehicleId;
    public float x, y;
    public float angle;
    public PartOutputPacket[] partUpdates;
}
