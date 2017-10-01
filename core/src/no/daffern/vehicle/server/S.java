package no.daffern.vehicle.server;

import no.daffern.vehicle.network.MyServer;
import no.daffern.vehicle.server.handlers.ItemHandler;
import no.daffern.vehicle.server.handlers.ServerPlayerHandler;
import no.daffern.vehicle.server.handlers.ServerVehicleHandler;
import no.daffern.vehicle.server.handlers.TickHandler;
import no.daffern.vehicle.server.world.WorldHandler;

/**
 * Created by Daff on 25.12.2016.
 */
public class S {

    public static MyServer myServer;
    public static WorldHandler worldHandler;
    public static ServerPlayerHandler playerHandler;
    public static ServerVehicleHandler vehicleHandler;
    public static ItemHandler itemHandler;
    public static TickHandler tickHandler;
}
