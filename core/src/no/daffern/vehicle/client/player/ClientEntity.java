package no.daffern.vehicle.client.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import no.daffern.vehicle.common.Common;
import no.daffern.vehicle.network.packets.EntityOutputPacket;

/**
 * Created by Daffern on 15.12.2016.
 */
public class ClientEntity {


	private Vector2 newPosition = new Vector2();
	private Vector2 oldPosition = new Vector2();
	private Vector2 position = new Vector2();

	private float oldAngle, newAngle, angle;

	private double outputTime;

	private float interpolationPeriod = 0.1f;

	private static float maxInterpolationPeriod = 0.6f;

	public void receiveOutput(EntityOutputPacket eop) {

		oldPosition = newPosition;
		oldAngle = newAngle;

		newPosition = eop.position.scl(Common.unitsToPixels);

		newAngle = eop.angle * MathUtils.radiansToDegrees;

		double newOutputTime = (System.currentTimeMillis() / 1000d);

		interpolationPeriod = Math.min(maxInterpolationPeriod, (float)(newOutputTime - outputTime));//Should calculate over a few frames/maybe constant

		outputTime = newOutputTime;
	}

	protected void interpolate(){
		double currentTime = (System.currentTimeMillis() / 1000d);

		float alpha = 1.0f - (float)((outputTime + interpolationPeriod - currentTime)/interpolationPeriod);
		alpha = MathUtils.clamp(alpha , 0f, 1f);

		//position = oldPosition.lerp(newPosition,alpha);

		position =  new Vector2(newPosition.x * alpha + oldPosition.x * (1.0f - alpha), newPosition.y * alpha + oldPosition.y * (1.0f - alpha));

		angle = newAngle *alpha + (1.0f - alpha)*oldAngle;
	}
	public void snapPosition(float x, float y){
		position.x = oldPosition.x = newPosition.x = x;
		position.y = oldPosition.y = newPosition.y = y;
		outputTime = (System.currentTimeMillis() / 1000d);
	}

	public Vector2 getPosition(){
		return position;
	}
	public float getNewAngle(){
		return angle;
	}

}
