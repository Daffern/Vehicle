package no.daffern.vehicle.common;

/**
 * Created by Daffern on 16.11.2016.
 */
public class Common {


    public final static boolean debugRender = false;

    public final static float WORLD_GRAVITY = -7f;

    public final static float TIME_STEP = 1f/50f;
    public final static int VELOCITY_ITERATIONS = 7;    //6
    public final static int POSITION_ITERATIONS = 3;    //2

    public final static int GAME_TYPE_TMX = 0;
    public final static int GAME_TYPE_CONTINUED_TERRAIN = 0;



    //1 meter = 32 pixels
    public final static float unitsToPixels = 64f;
    public final static float pixelToUnits = 1f/unitsToPixels;

    public final static int defaultTcpPort = 1001;
    public final static int defaultUdpPort = 1002;


	public final static float cameraZoom = 0.8f;


    public static float toWorldCoordinates(float val){
        val = val / unitsToPixels;
        return val;
    }
    public static float toPixelCoordinates(float val){
        val = val * unitsToPixels;
        return val;
    }




}
