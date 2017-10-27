package no.daffern.vehicle.client.handlers.controller;

import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.player.ClientInventory;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.MyClient;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.network.packets.PlayerInputPacket;

public abstract class Controller implements SystemInterface {

	private final int UDP_PACKETS_NUM = 5;

	private int udpPulses = UDP_PACKETS_NUM;
	private PlayerInputPacket pip = new PlayerInputPacket();

	private ClientInventory inventory;

	private MyClient myClient;



	public Controller() {
		myClient = C.myClient;
		inventory = C.clientInventory;
	}


	protected void setLeft(boolean left) {
		pip.leftPressed = left;
		udpPulses = UDP_PACKETS_NUM;
	}

	protected void setRight(boolean right) {
		pip.rightPressed = right;
		udpPulses = UDP_PACKETS_NUM;
	}

	protected void setDown(boolean down) {

	}

	protected void setUp(boolean up) {
		pip.upPressed = up;
		udpPulses = UDP_PACKETS_NUM;
	}

	protected void sendClickPacket(byte clickType, float x, float y) {
		PlayerClickPacket pcp = new PlayerClickPacket();
		pcp.clickType = clickType;

		ClientInventory.InventorySlot slot = inventory.getSelected();
		if (slot != null)
			pcp.itemId = inventory.getSelected().getItemId();

		pcp.x = x;
		pcp.y = y;

		myClient.sendTCP(pcp);
	}

	@Override
	public void preStep() {

	}

	@Override
	public void postStep() {
		if (C.myClient.isConnected()) {

			if (udpPulses > 0) {
				udpPulses--;
				C.myClient.sendUDP(pip);
			}

		}
	}
}
