package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.common.GameItemStates;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.server.handlers.Entity;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.Walls;

/**
 * Created by Daffern on 23.03.2017.
 */
public abstract class Part extends Entity{

    public static final float MAX_WIDTH = 3;
    public static final float MAX_HEIGHT = 3;

    protected int itemId;
    protected boolean dynamic = false;
    protected boolean attached = false;

    protected float width, height;


    public int ignoreCollisionFilter = 0;//collision with nearby parts

    public Part(int itemId, int type, boolean dynamic, float width, float height){
        super(type);

        this.itemId = itemId;
        this.dynamic = dynamic;
        this.width = width;
        this.height = height;
    }

    public int getLayer(){
    	return type;
    }
    /**
     *
     * @param world
     * @param vehicleBody
     * @param wall
     */
    public abstract void attach(World world, Body vehicleBody, Wall wall);
    public abstract void detach(World world, Body vehicleBody, Wall wall);

    //invoked one every part close to a newly added part
    public boolean checkCollision(Part otherPart){
	    return otherPart.getType() == type;
    }
    //invoked before a part is added, default true
	public boolean canPlace(Walls walls, IntVector2 wallIndex){
    	return true;
	}


    public int getItemId(){return itemId;}
    public float getWidth(){
        return width;
    }
    public float getHeight(){
        return height;
    }

    public abstract float getAngle();
    public abstract Vector2 getPosition();
	//used for battery level, on/off states. See @link PartOutputPacket
	public byte getState(){
		return GameItemStates.NONE;
	}

    public abstract boolean isActive();

    public boolean isAttached(){
        return attached;
    }
    public boolean isDynamic(){
        return dynamic;
    }

    public boolean interact1(){
        return false;
    }
    public boolean interact2(){
        return false;
    }
}
