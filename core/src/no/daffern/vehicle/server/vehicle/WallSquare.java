package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.container.Vector4;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.world.CollisionCategories;
import no.daffern.vehicle.utils.AbstractContactListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 16.11.2016.
 */
public class WallSquare extends Wall {

    private static final float WALL_BREADTH = 0.1f;


    Fixture[] walls;
	public WallSquare(int itemId, IntVector2 wallIndex){
		this(itemId,wallIndex.x, wallIndex.y);
	}
    public WallSquare(int itemId, int wallX, int wallY){
    	super(itemId, GameItemTypes.WALL_TYPE_SQUARE, wallX, wallY);
    }

    @Override
    void createWall(Body vehicleBody){

    	float x = getLocalX();
    	float y = getLocalY();

        //player walls
        List<Vector4> wallLines = createSquareLines(x,y,x+WALL_WIDTH,y+WALL_HEIGHT);
        walls = new Fixture[wallLines.size()+1];


        for (int i = 0 ; i < wallLines.size() ; i++) {

            Shape shape = createBoxShape(wallLines.get(i), WALL_BREADTH);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 0.5f;
            fixtureDef.friction = 0.7f;
            fixtureDef.restitution = 0.5f;
            fixtureDef.filter.categoryBits = CollisionCategories.vehicleInside;
            fixtureDef.filter.maskBits = CollisionCategories.player;

            walls[i] = vehicleBody.createFixture(fixtureDef);
            shape.dispose();
        }

        //back wall
        PolygonShape shape = new PolygonShape();
        shape.set(new float[]{x,y,x+WALL_WIDTH,y,x+WALL_WIDTH,y + WALL_HEIGHT,x,y + WALL_HEIGHT});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = CollisionCategories.vehicleOutside;
        fixtureDef.filter.maskBits = CollisionCategories.terrain;

        walls[walls.length-1] = vehicleBody.createFixture(fixtureDef);
		shape.dispose();

        isCreated = true;
    }

    @Override
    void destroyWall(Body vehicleBody) {
        if (isCreated) {
            for (int i = 0 ; i < walls.length ; i++) {
                vehicleBody.destroyFixture(walls[i]);
                walls[i] = null;
            }
            walls = null;
            isCreated = false;
        }
    }

    @Override
    boolean updateWall(Body vehicleBody) {
        if (isCreated) {
            destroyWall(vehicleBody);
            createWall(vehicleBody);
            return true;
        }
        return false;
    }

    @Override
    boolean checkCollision(Body vehicleBody, float margin){

    	float x1 = getLocalX() + margin;
    	float y1 = getLocalY() + margin;
    	float x2 = getLocalX() + WALL_WIDTH - margin;
    	float y2 = getLocalY() + WALL_HEIGHT - margin;


	    PolygonShape shape = new PolygonShape();
	    shape.set(new float[]{x1, y1, x2, y1, x2, y2, x1, y2});

	    FixtureDef fixtureDef = new FixtureDef();
	    fixtureDef.shape = shape;
	    fixtureDef.isSensor = true;
	    fixtureDef.filter.categoryBits = CollisionCategories.vehicleOutside;
	    fixtureDef.filter.maskBits = CollisionCategories.terrain;


	    final Fixture fixture = vehicleBody.createFixture(fixtureDef);
	    final boolean[] collision = {false};

	    ContactListener contactListener = new AbstractContactListener() {
		    @Override
		    public void beginContact(Contact contact) {
			    if (contact.getFixtureA() == fixture || contact.getFixtureB() == fixture) {
				    collision[0] = true;
			    }
		    }
	    };

	    S.worldHandler.addContactListener(contactListener);

	    S.worldHandler.zeroWorldStep();

	    S.worldHandler.removeContactListener(contactListener);

	    vehicleBody.destroyFixture(fixture);

	    return collision[0];
	}


    private ArrayList<Vector4> createSquareLines(float x, float y, float x2, float y2) {
        ArrayList<Vector4> lines = new ArrayList<Vector4>();

        if (up == null || up.getType() == GameItemTypes.WALL_TYPE_NONE) {
            lines.add(new Vector4(x, y2, x2, y2));
        }
        if (down == null || down.getType() == GameItemTypes.WALL_TYPE_NONE) {
            lines.add(new Vector4(x, y, x2, y));
        }
        if (left == null || left.getType() == GameItemTypes.WALL_TYPE_NONE) {
            lines.add(new Vector4(x, y, x, y2));
        }
        if (right == null || right.getType() == GameItemTypes.WALL_TYPE_NONE) {
            lines.add(new Vector4(x2, y, x2, y2));
        }

        return lines;
    }

    private PolygonShape createBoxShape(Vector4 vector4, float boxBreadth) {
        Vector2[] box = new Vector2[4];

        //check if direction is horizontal or vertical
        if (Math.abs(vector4.x1 - vector4.x2) > Math.abs(vector4.y1 - vector4.y2)) {

            box[0] = new Vector2(vector4.x1, vector4.y1 - boxBreadth);
            box[1] = new Vector2(vector4.x1, vector4.y1 + boxBreadth);

            box[2] = new Vector2(vector4.x2, vector4.y2 + boxBreadth);
            box[3] = new Vector2(vector4.x2, vector4.y2 - boxBreadth);
        } else {
            box[0] = new Vector2(vector4.x1 - boxBreadth, vector4.y1);
            box[1] = new Vector2(vector4.x1 + boxBreadth, vector4.y1);

            box[2] = new Vector2(vector4.x2 + boxBreadth, vector4.y2);
            box[3] = new Vector2(vector4.x2 - boxBreadth, vector4.y2);
        }

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(box);

        return polygonShape;

    }

}