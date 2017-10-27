package no.daffern.vehicle.client;


import no.daffern.vehicle.client.handlers.*;
import no.daffern.vehicle.client.handlers.controller.Controller;
import no.daffern.vehicle.client.player.ClientInventory;
import no.daffern.vehicle.network.MyClient;

/**
 * Created by Daff on 23.12.2016.
 */
public class C {


    public static MyClient myClient;

    public static ClientVehicleHandler vehicleHandler;
    public static ClientPlayerHandler playerHandler;


    public static CameraHandler cameraHandler;
    public static MapHandler mapHandler;


    public static Controller controller;
    public static ClientInventory clientInventory;
    public static ItemHandler itemHandler;


}
