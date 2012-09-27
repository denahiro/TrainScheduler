/**
 * 
 */
package ch.sreng.schedule.components.mobile;

import ch.sreng.schedule.components.stationary.Station;
import ch.sreng.schedule.components.stationary.TrackComponent;
//import ch.sreng.schedule.components.stationary.TrackNode;
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
    private Station targetStation;
    private Double departureTime;
    private Double lastDepartureTime;
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

    public void setInitialConditions(TrackComponent initialTrack,Station initialTargetStation
            ,double initialPosition,double initialVelocity)
    {
        this.currentVelocity=initialVelocity;
        this.currentTrack=initialTrack;
        this.setPosition(initialPosition);
        this.targetStation=initialTargetStation;
    }

    public Station getTargetStation()
    {
        return this.targetStation;
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
        if(this.targetStation.arrived(this)) {
            if(this.departureTime==null) {
                if(this.lastDepartureTime==null) {
                    this.departureTime=new Double(this.targetStation.getWaitTime()+timer.getTime());
                } else {
                    double travelTime=timer.getTime()-this.lastDepartureTime;
                    this.departureTime=new Double(this.targetStation.getWaitTime()
                            +this.safetyStrategy.travelTimeSafetyWaitTime(travelTime)
                            +timer.getTime());
                }
                this.currentVelocity=0;
            } else if(this.departureTime<timer.getTime()) {
                this.lastDepartureTime=new Double(timer.getTime());
                this.departureTime=null;
                this.targetStation=this.targetStation.getNextStation();
            }
        } else {
            List<DriveStrategy.AccelerationAtTime> accelProfile=this.driveStrategy.getAccelerationProfile(this, timer);
//            System.out.println(accelProfile);
            double timePassed=0;
            ListIterator<DriveStrategy.AccelerationAtTime> it=accelProfile.listIterator();
            DriveStrategy.AccelerationAtTime currentAcceleration=it.next();
            while(timePassed<timer.getDeltaTime())
            {
                double accelerationTime;
                DriveStrategy.AccelerationAtTime nextAcceleration=null;
                if(it.hasNext()) {
                    nextAcceleration=it.next();
                    if(nextAcceleration.time<timer.getDeltaTime()) {
                        accelerationTime=nextAcceleration.time-timePassed;
                    } else {
                        accelerationTime=timer.getDeltaTime()-timePassed;
                    }
                } else {
                    accelerationTime=timer.getDeltaTime()-timePassed;
                }

                if(accelerationTime<0) {
                    throw new RuntimeException("Negative acceleration time.");
                }

                this.setPosition(this.currentTrack.getTrainEndPosition(this)+this.currentVelocity*accelerationTime
                        +currentAcceleration.acceleration*accelerationTime*accelerationTime/2);
                this.currentVelocity+=currentAcceleration.acceleration*accelerationTime;
                timePassed+=accelerationTime;
                currentAcceleration=nextAcceleration;
            }
        }
//        System.out.println("{"+Double.toString(this.currentTrack.getTrainEndPosition(this))+","
//                +Double.toString(this.currentVelocity)+"}");
    }

    public boolean equals(Train x)
    {
        return this==x;
    }

//    public double getRelativePosition(TrackComponent origo)
//    {
//        double returnLength=0;
//        TrackComponent currentTrack=origo;
//        while(currentTrack!=this.currentTrack)
//        {
//            returnLength+=currentTrack.getLength(this);
//            currentTrack=currentTrack.getNextTrack(this);
//        }
//        return returnLength+this.currentTrack.getTrainEndPosition(this);
//    }

    public double getAbsolutePosition() {
        return this.currentTrack.getAbsoluteTrainEndPosition(this);
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
