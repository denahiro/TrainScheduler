/**
 * 
 */
package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;

import java.util.Hashtable;

/**
 * @author koenigst
 *
 */
public abstract class TrackBranch implements TrackComponent{

//	private Hashtable<TrackNode,TrackComponent> nextTrack=new Hashtable<TrackNode,TrackComponent>();
//	
//	private Hashtable<TrackNode,Double> maxSpeed=new Hashtable<TrackNode,Double>();
//	
//	private Hashtable<TrackNode,Double> length=new Hashtable<TrackNode,Double>();
	
	private Hashtable<TrackNode,TrackSimple> trackParts;
	
	public TrackComponent getNextTrack(Train requester)
	{
		return this.trackParts.get(requester.getTargetNode()).getNextTrack(requester);
	}
	
	public double getMaxSpeed(Train requester)
	{
		return this.trackParts.get(requester.getTargetNode()).getMaxSpeed(requester);
	}
	
	public double getLength(Train requester)
	{
		return this.trackParts.get(requester.getTargetNode()).getLength(requester);
	}
}
