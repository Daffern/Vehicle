package no.daffern.vehicle.server.handlers;

import no.daffern.vehicle.utils.Tools;

import java.util.List;
import java.util.Vector;

public class TickHandler {


	List<TickListener> listeners = new Vector<>(1,1);

	public void addTickListener(TickListener tickListener, byte interval){

		if (interval < 0)
			Tools.log(this, "Interval is negative: " + interval);

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

	public abstract static class TickListener{
		private byte interval = 0;
		private byte step = 0;
		protected abstract void onTick();
	}
}
