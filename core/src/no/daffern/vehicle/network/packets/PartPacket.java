package no.daffern.vehicle.network.packets;


/**
 * Created by Daffern on 08.04.2017.
 *
 *
 */
public class PartPacket {
    public int itemId;
    public int type;
    public int layer;//use same as type???
    public float width, height;
    public float angle;
    public byte state; //see OutputPartPacket

    public PartPacket(){
    }

    //use ItemId = 0 to remove part
    public PartPacket(int itemId){
	    this.itemId = itemId;
    }
    public PartPacket(int itemId, int type, int layer, float width, float height, float angle, byte state){
        this.itemId = itemId;
        this.type = type;
        this.layer = layer;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.state = state;
    }
}