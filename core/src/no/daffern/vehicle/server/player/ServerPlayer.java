package no.daffern.vehicle.server.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.container.IntVector2;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.network.packets.PlayerInputPacket;
import no.daffern.vehicle.network.packets.PlayerOutputPacket;
import no.daffern.vehicle.network.packets.PlayerPacket;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.handlers.Entity;
import no.daffern.vehicle.server.vehicle.ServerVehicle;
import no.daffern.vehicle.server.world.CollisionCategories;
import no.daffern.vehicle.utils.Box2dUtils;

/**
 * Created by Daffern on 14.11.2016.
 */
public class ServerPlayer extends Entity {

	final static float radius = 0.35f;

	PlayerInputPacket playerInputPacket = new PlayerInputPacket();

	float horizontalSpeed = 5f;

	private World world;
	public Body wheelBody;
	public Body fixedBody;
	public RevoluteJoint bodyJoint;
	public Fixture wheelFixture;


	int playerId;

	public ServerInventory inventory;
	public ServerVehicle serverVehicle;


	boolean inAir = false;
	Fixture touchingFixture;


	public ServerPlayer(int pId, ServerVehicle serverVehicle, float startX, float startY) {
		super(GameItemTypes.PLAYER);
		this.playerId = pId;
		this.serverVehicle = serverVehicle;

		inventory = new ServerInventory(this);

		world = S.worldHandler.world;


		createBody(startX, startY);

	}

	public void receiveInput(PlayerInputPacket input) {
		this.playerInputPacket = input;
	}

	public void receiveInventoryUsePacket(PlayerClickPacket pcp) {
		float x = Common.toWorldCoordinates(pcp.x);
		float y = Common.toWorldCoordinates(pcp.y);
		switch (pcp.clickType) {
			case PlayerClickPacket.CLICK_TYPE_USE:
				inventory.useItem(pcp.itemId, x, y);
				break;

			case PlayerClickPacket.CLICK_TYPE_INTERACT1:
				IntVector2 index = serverVehicle.findTileIndex(x,y);
				serverVehicle.interactPart1(index.x, index.y);
				break;
		}

	}

	public Body createBody(float startX, float startY) {

		//upper wheelBody
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(startX, startY);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.bullet = true;

		wheelBody = world.createBody(bodyDef);

/*
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);*/


		PolygonShape shape = new PolygonShape();
		shape.setAsBox(radius, radius);

		//wheel fixture
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.25f;
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0.75f;
		fixtureDef.friction = 1f;
		fixtureDef.filter.categoryBits = CollisionCategories.player;
		fixtureDef.filter.maskBits = CollisionCategories.vehicleInside | CollisionCategories.terrain;

		wheelFixture = wheelBody.createFixture(fixtureDef);

		//fixedBody (no fixture)
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.position.set(startX, startY);
		bodyDef2.type = BodyDef.BodyType.DynamicBody;
		bodyDef2.fixedRotation = true;

		fixedBody = world.createBody(bodyDef2);

		shape.dispose();

		//Joint
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = wheelBody;
		jointDef.bodyB = fixedBody;
		jointDef.enableMotor = true;
		jointDef.maxMotorTorque = 15f;
		jointDef.motorSpeed = 5f;


		bodyJoint = (RevoluteJoint) world.createJoint(jointDef);

		return wheelBody;
	}


	public PlayerPacket getPlayerInfo() {

		Vector2 position = wheelBody.getPosition();

		PlayerPacket playerPacket = new PlayerPacket();
		playerPacket.vehicleId = serverVehicle.vehicleId;
		playerPacket.playerId = playerId;
		playerPacket.x = position.x;
		playerPacket.y = position.y;
		playerPacket.width = radius * 2;
		playerPacket.height = radius * 2;

		return playerPacket;

	}

	public Vector2 getPosition() {
		return wheelBody.getPosition();
	}


	public void preStep() {

		inAir = true;


		if (playerInputPacket.upPressed || playerInputPacket.leftPressed || playerInputPacket.rightPressed) {

			Vector2 bodyPos = wheelBody.getWorldCenter();

			world.rayCast(new RayCastCallback() {
				@Override
				public float reportRayFixture(final Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

					inAir = false;
					touchingFixture = fixture;

					return 0;
				}
			}, bodyPos.x, bodyPos.y, bodyPos.x, bodyPos.y - 0.5f);
		}


		if (playerInputPacket.rightPressed) {
			bodyJoint.enableMotor(true);
			bodyJoint.setMotorSpeed(horizontalSpeed);
		}
		else if (playerInputPacket.leftPressed) {
			bodyJoint.enableMotor(true);

			bodyJoint.setMotorSpeed(-horizontalSpeed);
		}
		else {
			bodyJoint.enableMotor(false);
		}

		if (inAir && (playerInputPacket.leftPressed || playerInputPacket.rightPressed)) {
			Vector2 impulse = new Vector2();

			if (playerInputPacket.rightPressed && wheelBody.getLinearVelocity().x < 2) {
				impulse.x = 1f * wheelBody.getMass();
			}
			if (playerInputPacket.leftPressed && wheelBody.getLinearVelocity().x > -2) {
				impulse.x = -1f * wheelBody.getMass();
			}

			wheelBody.applyLinearImpulse(impulse, wheelBody.getWorldCenter(), true);

		}
		if (!inAir) {

			if (playerInputPacket.upPressed) {
				if (touchingFixture != null && Box2dUtils.shouldCollide(wheelFixture, touchingFixture)) {
					wheelBody.setLinearVelocity(wheelBody.getLinearVelocity().x, 5);
					fixedBody.setLinearVelocity(wheelBody.getLinearVelocity().x, 5);

					Vector2 impulse = new Vector2(0, -5 * (wheelBody.getMass() + fixedBody.getMass()));

					Vector2 bodyPos = wheelBody.getWorldCenter();

					touchingFixture.getBody().applyLinearImpulse(impulse, bodyPos, true);
				}
			}
		}

	}

	double counter = 0;

	public void postStep() {


		PlayerOutputPacket playerOutputPacket = new PlayerOutputPacket();
		playerOutputPacket.position = wheelBody.getPosition();
		playerOutputPacket.playerId = playerId;
		playerOutputPacket.speed = wheelBody.getAngularVelocity();
		playerOutputPacket.angle = wheelBody.getAngle();

		S.myServer.sendToAllUDP(playerOutputPacket);
		counter = 0;

	}
}
