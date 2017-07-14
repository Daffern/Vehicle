package no.daffern.vehicle.network;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.common.SystemSystem;

import java.util.List;
import java.util.Queue;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/**
 * Created by Daffern on 09.12.2016.
 */
public class MyClient extends Client implements SystemInterface {


    public MyClient(){
        super();

    }

    public void register(Class... classes){
        Kryo kryo = getKryo();
        for (Class c : classes){
            kryo.register(c);
        }


    }




    @Override
    public void preStep() {

    }

    @Override
    public void postStep() {

    }

    @Override
    public void render(Batch batch, float delta) {

    }
}
