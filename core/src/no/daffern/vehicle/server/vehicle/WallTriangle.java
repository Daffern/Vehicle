package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;

/**
 * Created by Daffern on 14.05.2017.
 */
public class WallTriangle extends Wall {

    public static final byte TOP_LEFT = 1;
    public static final byte TOP_RIGHT = 2;
    public static final byte BOTTOMLEFT = 3;
    public static final byte BOTTOMRIGHT = 4;

    private byte orientation;

    public WallTriangle(int itemId, IntVector2 wallIndex, byte orientation){
    	this(itemId,wallIndex.x,wallIndex.y,orientation);
    }

    public WallTriangle(int itemId, int x, int y, byte orientation) {
        super(itemId, GameItemTypes.WALL_TYPE_TRIANGLE, x, y);
        this.orientation = orientation;
    }

    public byte getOrientation() {
        return orientation;
    }

    @Override
    void createWall(Body vehicleBody) {

    }

    @Override
    void destroyWall(Body vehicleBody) {

    }

    @Override
    boolean updateWall(Body vehicleBody) {
        return false;
    }

    @Override
    boolean checkCollision(Body vehicleBody, float margin) {
        return false;
    }
}
