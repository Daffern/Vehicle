package no.daffern.vehicle.network.packets;


/**
 * Created by Daffern on 08.04.2017.
 *
 *
 */
public class PartPacket {
    public int itemId;
    public float width, height;
    public float angle;

    public PartPacket(){
    }

    //use ItemId = 0 to remove part
    public PartPacket(int itemId){
	    this.itemId = itemId;
    }
    public PartPacket(int itemId, float width, float height, float angle ){
        this.itemId = itemId;
        this.width = width;
        this.height = height;
        this.angle = angle;
    }
}