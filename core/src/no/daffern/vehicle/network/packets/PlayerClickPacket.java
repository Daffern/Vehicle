package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 02.04.2017.
 */
public class PlayerClickPacket {

	public final static byte CLICK_TYPE_BUILD = 0;
	public final static byte CLICK_TYPE_REMOVE = 1;
	public final static byte CLICK_TYPE_INTERRACT1 = 2;
	public final static byte CLICK_TYPE_INTERRACT2 = 3;

    public byte clickType;
    public int itemId;
    public float x, y;
    public int wallX, wallY;
}
