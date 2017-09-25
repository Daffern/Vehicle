package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.parts.network.PartNode;
import no.daffern.vehicle.server.world.CollisionCategories;

/**
 * Created by Daffern on 16.11.2016.
 */
public class PartWheel extends PartNode {

    private Body wheelBody;
    private RevoluteJoint revoluteJoint;


    public PartWheel(int itemId) {
        super(itemId, GameItemTypes.PART_TYPE_WHEEL, true, 2, 2);
    }

    public RevoluteJoint getJoint(){
        return revoluteJoint;
    }
    public Body getBody(){
    	return wheelBody;
    }



    @Override
    public void attach(World world, Body vehicleBody, Wall wall) {


		Vector2 localPos = new Vector2(wall.getLocalX() + Wall.WALL_WIDTH / 2, wall.getLocalY() + Wall.WALL_HEIGHT / 2);
        Vector2 position = vehicleBody.getWorldPoint(localPos);

        //wheel
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        wheelBody = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(width < height ? width / 2 : height / 2);
        //shape.setPosition(new Vector2(x,y));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 1;
        fixtureDef.filter.categoryBits = CollisionCategories.vehicleOutside;
        fixtureDef.filter.maskBits = CollisionCategories.terrain;

        wheelBody.createFixture(fixtureDef);

        //joint

        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.bodyA = vehicleBody;
        revoluteJointDef.bodyB = wheelBody;

        revoluteJointDef.localAnchorA.set(localPos);
        //wheelJointDef.localAnchorB.set(vehiclePos.x + x ,vehiclePos.y + y);

        //wheelJointDef.localAxisA.set(x,y);

        revoluteJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);

        attached = true;


    }

    @Override
    public void detach(World world, Body vehicleBody, Wall wall) {
        world.destroyJoint(revoluteJoint);
        world.destroyBody(wheelBody);
        revoluteJoint = null;
        wheelBody = null;
        attached = false;

    }

    @Override
    public int getLayer(){
    	return -1;
    }

    @Override
    public boolean checkCollision(Part otherPart) {
        return otherPart.getType() == getType();
    }

    @Override
    public boolean isActive() {
        if (attached)
            return wheelBody.isActive();
        else return false;
    }


    @Override
    public float getAngle() {
        if (attached)
            return wheelBody.getAngle();
        else
            return 0;
    }

    @Override
    public Vector2 getPosition() {
        return wheelBody.getPosition();
    }


    @Override
    public boolean interact1() {


        return false;
    }

    @Override
    public boolean interact2() {
        return false;
    }



}
