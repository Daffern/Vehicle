package no.daffern.vehicle.common;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by Daffern on 02.04.2017.
 */
public interface SystemInterface {
    void preStep();
    void postStep();
    void render(Batch batch, float delta);
}
