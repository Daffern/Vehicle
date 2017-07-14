package no.daffern.vehicle.network.packets;

/**
 * Created by Daffern on 14.04.2017.
 */
public class GameItemPacket {
    public String name;
    public int itemId;
    public int type;
    public boolean isInfinite;
    public String packName;//atlas name
    public String iconName;//name of the region (for non-tile items)
    public String tilePath;
}
