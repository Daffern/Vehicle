package no.daffern.vehicle.server.vehicle.parts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import no.daffern.vehicle.common.GameItemTypes;
import no.daffern.vehicle.server.S;
import no.daffern.vehicle.server.handlers.TickHandler;
import no.daffern.vehicle.server.vehicle.Wall;
import no.daffern.vehicle.server.vehicle.parts.network.NetworkHandler;
import no.daffern.vehicle.server.vehicle.parts.network.PartEdge;
import no.daffern.vehicle.server.vehicle.parts.network.PartNode;

import java.util.Vector;

public class PartWire extends PartEdge {
	public PartWire(int itemId) {
		super(itemId, GameItemTypes.PART_TYPE_WIRE, false, 1, 1);
	}

	@Override
	public NetworkHandler newNetworkHandler() {
		return new WireNetworkHandler();
	}

	public class WireNetworkHandler extends NetworkHandler {

		TickHandler.TickListener tickListener = new TickHandler.TickListener() {

			@Override
			protected void onTick() {

				if (engines.size() == 0 && solars.size() == 0)
					return;

				//update power usage

				int supply = 0;
				for (PartSolarPanel solar : solars) {
					supply += solar.getPowerSupply();
				}

				for (PartBattery battery : batteries) {
					supply += battery.takePower();
				}

				int restPower = 0;

				if (engines.size() > 0) {
					int avgSupply = supply / engines.size();

					for (PartEngine engine : engines) {
						restPower += engine.supplyPower(avgSupply);
					}
				}else{
					restPower = supply;
				}

				if (batteries.size() > 0) {

					int avgRestPower = restPower / batteries.size();
					for (PartBattery battery : batteries) {
						battery.supplyPower(avgRestPower);
					}
				}
			}
		};

		Vector<PartBattery> batteries = new Vector<>(1, 1);
		Vector<PartEngine> engines = new Vector<>(1, 1);
		Vector<PartSolarPanel> solars = new Vector<>(1, 1);

		@Override
		protected void onCreate(World world) {
			onDestroy(world);

			for (PartNode partNode : getNodes()) {
				switch (partNode.getType()) {
					case GameItemTypes.PART_TYPE_SOLAR:
						solars.add((PartSolarPanel) partNode);

						break;
					case GameItemTypes.PART_TYPE_BATTERY:
						batteries.add((PartBattery) (partNode));

						break;

					case GameItemTypes.PART_TYPE_ENGINE:
						engines.add((PartEngine) partNode);

						break;
				}
			}

			//one tick per one step
			S.tickHandler.addTickListener(tickListener, (byte) 2);

		}

		@Override
		protected void onDestroy(World world) {
			for (PartEngine partEngine : engines){
				partEngine.resetMotor();
			}
			batteries.clear();
			engines.clear();
			solars.clear();
			S.tickHandler.removeTickListener(tickListener);
		}

	}

	@Override
	public void attach(World world, Body vehicleBody, Wall wall) {

	}

	@Override
	public void detach(World world, Body vehicleBody, Wall wall) {

	}

	@Override
	public boolean checkCollision(Part otherPart) {
		return false;
	}

	@Override
	public float getAngle() {
		return 0;
	}

	@Override
	public Vector2 getPosition() {
		return null;
	}

	@Override
	public boolean isActive() {
		return false;
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
