package no.daffern.vehicle.network.packets;


import com.badlogic.gdx.math.Vector2;

/**
 * Created by Daffern on 10.11.2016.
 */
public class PlayerOutputPacket extends EntityOutputPacket{
    public int playerId;
    public float speed;
}
