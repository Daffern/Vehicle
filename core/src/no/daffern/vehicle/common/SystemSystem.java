package no.daffern.vehicle.common;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 02.04.2017.
 */
public class SystemSystem implements SystemInterface{

    SystemInterface[] systems;

    /**
     * Order defines rendering order
     * @param systems
     */

    public SystemSystem(SystemInterface... systems){
        this.systems = systems;
    }

    @Override
    public void preStep() {
        for (SystemInterface systemInterface : systems){
            systemInterface.preStep();
        }
    }

    @Override
    public void postStep() {
        for (SystemInterface systemInterface : systems){
            systemInterface.postStep();
        }
    }

    @Override
    public void render(Batch batch, float delta) {
        for (SystemInterface systemInterface : systems){
            systemInterface.render(batch, delta);
        }
    }
}
