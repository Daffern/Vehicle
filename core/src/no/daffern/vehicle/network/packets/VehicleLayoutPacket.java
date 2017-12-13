package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 16.11.2016.
 */
public class VehicleLayoutPacket{


    public int vehicleId;
    public WallPacket[] wallPackets;
    public int noTile;
    public float x,y,width,height;//boundingBox
    public float wallWidth, wallHeight;

}
