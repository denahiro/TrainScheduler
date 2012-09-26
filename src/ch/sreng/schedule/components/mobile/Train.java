/**
 * 
 */
package ch.sreng.schedule.components.mobile;

import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.components.stationary.TrackNode;
import ch.sreng.schedule.procedure.DriveStrategy;
import ch.sreng.schedule.procedure.SafetyStrategy;
import ch.sreng.schedule.simulation.Time;
import java.util.List;
import java.util.ListIterator;

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
    private SafetyStrategy safetyStrategy;
    private Power power;

    public Train(DriveStrategy myDriveStrategy, SafetyStrategy mySafetyStrategy,
            Power myPower, double myMaxVelocity, double myLength)
    {
        this.driveStrategy=myDriveStrategy;
        this.safetyStrategy=mySafetyStrategy;
        this.power=myPower;
        this.maxVelocity=myMaxVelocity;
        this.length=myLength;
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
        List<DriveStrategy.AccelerationAtTime> accelProfile=this.driveStrategy.getAccelerationProfile(this, timer);
        System.out.println(accelProfile);
        double timePassed=0;
        ListIterator<DriveStrategy.AccelerationAtTime> it=accelProfile.listIterator();
        DriveStrategy.AccelerationAtTime currentAcceleration=it.next();
        while(timePassed<timer.getDeltaTime())
        {
            DriveStrategy.AccelerationAtTime nextAcceleration=it.next();
            double accelerationTime;
            if(nextAcceleration.time<timer.getDeltaTime()) {
                accelerationTime=nextAcceleration.time-timePassed;
            } else {
                accelerationTime=timer.getDeltaTime()-timePassed;
            }
            this.setPosition(this.currentTrack.getTrainEndPosition(this)+this.currentVelocity*accelerationTime
                    +currentAcceleration.acceleration*accelerationTime*accelerationTime/2);
            this.currentVelocity+=currentAcceleration.acceleration*accelerationTime;
            timePassed+=accelerationTime;
            currentAcceleration=nextAcceleration;
        }
        System.out.println("{"+Double.toString(this.currentTrack.getTrainEndPosition(this))+","
                +Double.toString(this.currentVelocity)+"}");
    }

    public boolean equals(Train x)
    {
        return this==x;
    }

    public double getRelativePosition(TrackComponent origo)
    {
        double returnLength=0;
        TrackComponent currentTrack=origo;
        while(currentTrack!=this.currentTrack)
        {
            returnLength+=currentTrack.getLength(this);
            currentTrack=currentTrack.getNextTrack(this);
        }
        return returnLength+this.currentTrack.getTrainEndPosition(this);
    }

    public SafetyStrategy getSafetyStrategy()
    {
        return this.safetyStrategy;
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
