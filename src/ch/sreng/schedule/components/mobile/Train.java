/**
 * 
 */
package ch.sreng.schedule.components.mobile;

import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.components.stationary.TrackNode;
import ch.sreng.schedule.procedure.DriveStrategyAbstract;

/**
 * @author koenigst
 *
 */
public class Train {

	private TrackComponent currentTrack;
	private double currentPosition;
	private TrackNode targetNode;
	private double currentSpeed;
	private final double maxSpeed;
	private final double maxAcceleration;
	private final double maxDeceleration;
	private DriveStrategyAbstract driveStrategy;
	
	public Train(DriveStrategyAbstract myDriveStrategy, double myMaxSpeed, double myMaxAcceleration, double myMaxDeceleration)
	{
		this.driveStrategy=myDriveStrategy;
		this.maxSpeed=myMaxSpeed;
		this.maxAcceleration=myMaxAcceleration;
		this.maxDeceleration=myMaxDeceleration;
	}
	
	public TrackNode getTargetNode()
	{
		return this.targetNode;
	}
	
	public void move(double timestep)
	{
		
	}
}
