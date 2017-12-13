package no.daffern.vehicle.client.handlers.controller;

import no.daffern.vehicle.client.C;
import no.daffern.vehicle.client.handlers.ClientInventory;
import no.daffern.vehicle.common.SystemInterface;
import no.daffern.vehicle.network.MyClient;
import no.daffern.vehicle.network.packets.PlayerClickPacket;
import no.daffern.vehicle.network.packets.PlayerInputPacket;

public abstract class Controller implements SystemInterface {

	//number of udp packets to send after input
	private final int UDP_PACKETS_NUM = 5;

	private int udpPulses = UDP_PACKETS_NUM;
	private PlayerInputPacket pip = new PlayerInputPacket();


	private MyClient myClient;

	protected ClientInventory inventory;


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


	protected void setUp(boolean up) {
		pip.upPressed = up;
		udpPulses = UDP_PACKETS_NUM;
	}


	protected void sendClickPacket(byte clickType, float x, float y, float angle) {
		PlayerClickPacket pcp = new PlayerClickPacket();
		pcp.clickType = clickType;
		pcp.x = x;
		pcp.y = y;
		pcp.angle = angle;


		ClientInventory.InventorySlot slot = inventory.getSelected();
		if (slot != null)
			pcp.itemId = inventory.getSelected().getItemId();

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
