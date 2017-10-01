package no.daffern.vehicle.server.handlers;

import java.util.List;
import java.util.Vector;

public class TickHandler {


	List<TickListener> listeners = new Vector<>(1,1);

	public void addTickListener(TickListener tickListener, byte interval){
		tickListener.interval = interval;

		listeners.add(tickListener);
	}
	public void removeTickListener(TickListener tickListener){
		listeners.remove(tickListener);
	}


	public void step(){
		for (TickListener tickListener : listeners){
			tickListener.step++;
			if (tickListener.step == tickListener.interval){
				tickListener.onTick();
				tickListener.step = 0;
			}
		}
	}

	public abstract class TickListener{
		private byte interval = 0;
		private byte step = 0;
		abstract void onTick();
	}
}
