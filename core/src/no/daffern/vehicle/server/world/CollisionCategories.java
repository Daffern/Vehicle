package no.daffern.vehicle.server.world;

/**
 * Created by Daffern on 14.05.2017.
 */
public class CollisionCategories {

    public static final short player =          0b1;
    public static final short vehicleInside =   0b10;
    public static final short vehicleOutside =  0b100;
    public static final short terrain =         0b1000;
    public static final short ladder =          0b10000;

}
