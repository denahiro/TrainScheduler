package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;

public interface Blockable {

	public abstract boolean aquire(Train requester);
	
	public abstract boolean release(Train requester);
	
}
