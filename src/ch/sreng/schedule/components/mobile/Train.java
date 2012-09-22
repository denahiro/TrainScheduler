/**
 * 
 */
package ch.sreng.schedule.components.mobile;

import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.components.stationary.TrackNode;
import ch.sreng.schedule.procedure.DriveStrategy;
import ch.sreng.schedule.simulation.Time;

/**
 * @author koenigst
 *
 */
public class Train {

    private TrackComponent currentTrack;
//  private double currentPosition;
    private TrackNode targetNode;
    private double currentVelocity;
    private final double maxVelocity;
    private double length;
    private DriveStrategy driveStrategy;
    private Power power;

    public Train(DriveStrategy myDriveStrategy, Power myPower, double myMaxVelocity, double length)
    {
        this.power=myPower;
        this.driveStrategy=myDriveStrategy;
        this.maxVelocity=myMaxVelocity;
    }

    public void setInitialConditions(TrackComponent initialTrack,double initialPosition,double initialVelocity)
    {
        this.currentVelocity=initialVelocity;
        this.currentTrack=initialTrack;
        this.setPosition(initialPosition);
    }

    public TrackNode getTargetNode()
    {
        return this.targetNode;
    }

    public double getLength()
    {
        return this.length;
    }

    public double getVelocity()
    {
        return this.currentVelocity;
    }

    public double getMaxAcceleration()
    {
        return this.power.getMaxAcceleration();
    }

    public double getMaxDeceleration()
    {
        return this.power.getMaxDeceleration();
    }

    public double getMaxVelocity()
    {
        return this.maxVelocity;
    }

    public TrackComponent getCurrentTrack()
    {
        return this.currentTrack;
    }

    public void move(Time timer)
    {
        this.driveStrategy.getAccelerationProfile(this, timer);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean equals(Train x)
    {
        return this==x;
    }

    private void setPosition(double newPosition)
    {
        while(!this.currentTrack.setMyPosition(this, newPosition))
        {
            newPosition-=this.currentTrack.getLength(this);
            this.currentTrack=this.currentTrack.getNextTrack(this);
        }
    }
}
