package no.daffern.vehicle.utils;

import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Created by Daffern on 14.05.2017.
 */
public class Box2dUtils {

    public static boolean shouldCollide(Fixture fixture1, Fixture fixture2) {
        return (fixture1.getFilterData().maskBits & fixture2.getFilterData().categoryBits) != 0 &&
                (fixture1.getFilterData().categoryBits & fixture2.getFilterData().maskBits) != 0;
    }

}
