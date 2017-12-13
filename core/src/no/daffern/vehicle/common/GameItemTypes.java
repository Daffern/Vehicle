package no.daffern.vehicle.common;


/**
 * Created by Daffern on 29.03.2017.
 */
public class GameItemTypes {



	public final static int PLAYER = 10000;
	public final static int REMOVE_TOOL = 10001;
	public final static int SHOVEL_TOOL = 10002;


    public final static int WALL_TYPE_NONE = 0;
    public final static int WALL_TYPE_SQUARE = 1;
    public final static int WALL_TYPE_TRIANGLE = 2;

    //order defines order of the walls, from lowest to highest (see Wall.addPart())

	public final static int PART_TYPE_AXLE =    100;
	public final static int PART_TYPE_WIRE =    101;

	public final static int PART_TYPE_WHEEL =   1000;//overriden by part.getLayer()
	public final static int PART_TYPE_LADDER =  1001;
	public final static int PART_TYPE_GENERATOR =  1002;
	public final static int PART_TYPE_ENGINE =  1003;
	public final static int PART_TYPE_SOLAR =   1004;
	public final static int PART_TYPE_BATTERY = 1005;
	public final static int PART_TYPE_DRILL = 1006;







}
