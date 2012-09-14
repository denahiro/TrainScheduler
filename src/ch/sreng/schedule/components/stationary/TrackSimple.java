package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;

/**
 * @author koenigst
 *
 */
public abstract class TrackSimple implements TrackComponent{

	private TrackComponent nextTrack;
	
	private double maxSpeed;
	
	private double length;
	
	public TrackComponent getNextTrack(Train requester)
	{
		return this.nextTrack;
	}
	
	public double getMaxSpeed(Train requester)
	{
		return this.maxSpeed;
	}
	
	public double getLength(Train requester)
	{
		return this.length;
	}
}
