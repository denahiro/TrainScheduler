/**
 * 
 */
package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;

/**
 * @author koenigst
 *
 */
public interface TrackComponent {
	
//	protected Hashtable<Train, Double> trainPositions=new Hashtable<Train,Double>();
	
//	public void move(Train requester, double distanceTravelled)
//	{
//		
//	}
	
	public abstract double getMaxSpeed(Train requester);
	
	public abstract TrackComponent getNextTrack(Train requester);
	
	public abstract void draw(java.awt.Graphics g);
	
	public abstract double getLength(Train requester);
}
