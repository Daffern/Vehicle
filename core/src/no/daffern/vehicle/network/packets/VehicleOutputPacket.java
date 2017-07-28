package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 11.12.2016.
 */
public class VehicleOutputPacket extends EntityOutputPacket{
    public int vehicleId;
    public PartOutputPacket[] partUpdates;
}
