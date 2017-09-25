package no.daffern.vehicle.server.vehicle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.Vector4;
import no.daffern.vehicle.server.vehicle.parts.Part;
import no.daffern.vehicle.server.world.CollisionCategories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daffern on 16.03.2017.
 */
public class VehicleShapeBuilder {

    private World world;


    Fixture[] insideWalls;
    Fixture[] outsideWalls;


    public VehicleShapeBuilder(World world) {
        this.world = world;
    }


    public Body createNewBody(float x, float y, Wall[][] walls) {


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body vehicleBody = world.createBody(bodyDef);

        generateWallRelations(walls);
        generateVehicleBody(walls, vehicleBody);

        return vehicleBody;
    }

    public Body updateBody(Wall[][] walls, Body vehicleBody) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(vehicleBody.getPosition());
        bodyDef.angle = vehicleBody.getAngle();

        destroyVehicle(walls, vehicleBody);

        generateWallRelations(walls);
        generateVehicleBody(walls, vehicleBody);

        return vehicleBody;
    }

    public void destroyVehicle(Wall[][] walls, Body vehicleBody) {


        Array<Fixture> fixtures = vehicleBody.getFixtureList();

        while (fixtures.size > 0){
            vehicleBody.destroyFixture(fixtures.get(0));
        }

    }

    private void generateWallRelations(Wall[][] walls) {

        for (int x = 0; x < walls.length; x++) {
            for (int y = 0; y < walls[x].length; y++) {

                if (walls[x][y] != null) {


                    if (x > 0)
                        walls[x][y].left = walls[x - 1][y];

                    if (x < walls.length - 1)
                        walls[x][y].right = walls[x + 1][y];

                    if (y > 0)
                        walls[x][y].down = walls[x][y - 1];

                    if (y < walls[x].length - 1)
                        walls[x][y].up = walls[x][y + 1];
                }
            }
        }
    }

    private void generateVehicleBody(Wall[][] walls, Body vehicleBody) {

        List<Vector4> wallLines = new ArrayList<>();
        List<Vector4> backWallRects = new ArrayList<>();

        for (int i = 0; i < walls.length; i++) {

            for (int j = 0; j < walls[i].length; j++) {

                Wall wall = walls[i][j];

                if (wall == null)
                    continue;

                float x = i * Wall.WALL_WIDTH;
                float y = j * Wall.WALL_HEIGHT;
                float x2 = i * Wall.WALL_WIDTH + Wall.WALL_WIDTH;
                float y2 = j * Wall.WALL_HEIGHT + Wall.WALL_HEIGHT;

                switch (wall.getType()) {
                    case GameItemTypes.WALL_TYPE_SQUARE: {
                        List<Vector4> lines = createSquareLines(wall, x, y, x2, y2);
                        wallLines.addAll(lines);

                        backWallRects.add(new Vector4(x,y,x2,y2));
                        break;
                    }
                    case GameItemTypes.WALL_TYPE_TRIANGLE:{
                        WallTriangle triWall = (WallTriangle)wall;

                        List<Vector4> lines = createTriangleLines(triWall,x,y,x2,y2);
                        wallLines.addAll(lines);


                    }
                }

                if (wall.parts == null)
                    continue;

                for (Part part : wall.parts) {

                    switch (part.getType()) {
                        case GameItemTypes.PART_TYPE_WHEEL: {


                            if (!part.isAttached())
                                part.attach(world, vehicleBody, wall);

                            break;
                        }

                    }

                }
            }
        }

        //wallLines = Vector4.mergeLines(wallLines);

        for (Vector4 vector4 : wallLines) {

            Shape shape = createBoxShape(vector4, 0.1f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 0.5f;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0.0f;
            fixtureDef.filter.categoryBits = CollisionCategories.vehicleInside;
            fixtureDef.filter.maskBits = CollisionCategories.player;

            vehicleBody.createFixture(fixtureDef);
        }

        for (Vector4 rec : backWallRects){


            PolygonShape shape = new PolygonShape();
            shape.set(new float[]{rec.x1,rec.y1,rec.x2,rec.y1,rec.x2,rec.y2,rec.x1,rec.y2});


            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 0.5f;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0.0f;
            fixtureDef.filter.categoryBits = CollisionCategories.vehicleOutside;
            fixtureDef.filter.maskBits = CollisionCategories.terrain;
            vehicleBody.createFixture(fixtureDef);
        }

    }

    private ArrayList<Vector4> createTriangleLines(WallTriangle wall, float x, float y, float x2, float y2 ){
        ArrayList<Vector4> lines = new ArrayList<Vector4>();

        Wall up = wall.up;
        Wall down = wall.down;
        Wall left = wall.left;
        Wall right = wall.right;

        byte orientation = wall.getOrientation();

        switch (orientation){
            case WallTriangle.TOP_LEFT:
                if (up == null || up.getType() == GameItemTypes.WALL_TYPE_NONE)
                    lines.add(new Vector4(x, y2, x2, y2));
                if (left == null || left.getType() == GameItemTypes.WALL_TYPE_NONE) {
                    lines.add(new Vector4(x, y, x, y2));
                }
                lines.add(new Vector4(x,y,x2,y2));


                break;
            case WallTriangle.TOP_RIGHT:
                if (up == null || up.getType() == GameItemTypes.WALL_TYPE_NONE)
                    lines.add(new Vector4(x, y2, x2, y2));
                if (right == null || right.getType() == GameItemTypes.WALL_TYPE_NONE) {
                    lines.add(new Vector4(x2, y, x2, y2));
                }
                lines.add(new Vector4(x,y2,x2,y));

                break;
            case WallTriangle.BOTTOMLEFT:
                if (down == null || down.getType() == GameItemTypes.WALL_TYPE_NONE) {
                    lines.add(new Vector4(x, y, x2, y));
                }
                if (left == null || left.getType() == GameItemTypes.WALL_TYPE_NONE) {
                    lines.add(new Vector4(x, y, x, y2));
                }
                lines.add(new Vector4(x,y2,x2,y));

                break;
            case WallTriangle.BOTTOMRIGHT:
                if (down == null || down.getType() == GameItemTypes.WALL_TYPE_NONE) {
                    lines.add(new Vector4(x, y, x2, y));
                }
                if (right == null || right.getType() == GameItemTypes.WALL_TYPE_NONE) {
                    lines.add(new Vector4(x2, y, x2, y2));
                }
                lines.add(new Vector4(x,y,x2,y2));

                break;

        }
        return lines;
    }


    //create lines from a wall
    private ArrayList<Vector4> createSquareLines(Wall wall, float x, float y, float x2, float y2) {
        ArrayList<Vector4> lines = new ArrayList<Vector4>();


        Wall up = wall.up;
        Wall down = wall.down;
        Wall left = wall.left;
        Wall right = wall.right;

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


    //create box from vector4
    private PolygonShape createBoxShape(Vector4 vector4, float boxBreadth) {
        Vector2[] box = new Vector2[4];

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
