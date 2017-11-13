package no.daffern.vehicle.common;

import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.*;

/**
 * Created by Daff on 26.12.2016.
 */
public class Packets {

    public static Class[] networkClasses = new Class[]{
    		//input
            PlayerInputPacket.class,

		    //map
            StartTmxMapPacket.class, StartContinuousMapPacket.class, StartDestructibleMapPacket.class,
		    TerrainPacket.class, TerrainPacketDestructible.class, TerrainPacketDestructible[].class,

		    //player
            PlayerOutputPacket.class, GiveControlPacket.class, PlayerPacket.class,

            //vehicle
            VehicleLayoutPacket.class, VehicleOutputPacket.class,

            //wall
		    WallPacket.class, WallPacket[].class,

		    //part
		    PartPacket.class, PartPacket[].class, PartOutputPacket.class, PartOutputPacket[].class,

            byte[][].class, int[][].class, float[][].class,
		    int[].class, byte[].class, float[].class, Vector2.class, IntVector2.class,

            //Item packets
            InventoryPacket.class, InventoryPacket[].class, PlayerClickPacket.class, GameItemPacket.class, GameItemRequestPacket.class
    };

}
