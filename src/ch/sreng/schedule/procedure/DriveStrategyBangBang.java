package ch.sreng.schedule.procedure;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.simulation.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Comparator;

/**
 * @author koenigst
 *
 */
public class DriveStrategyBangBang implements DriveStrategy {
    private double reactionTime;
    private double minimalDistance;

    public DriveStrategyBangBang(double myReactionTime, double myMinimalDistance)
    {
        this.reactionTime=myReactionTime;
        this.minimalDistance=myMinimalDistance;
    }

    /**
     * @param requester Train for which the brick wall distance should be calculated.
     * @return Returns the brick wall distance calculated from the end of the train.
     */
    final protected double brickWallDistance(Train requester,double currentVelocity)
    {
        return requester.getLength()+this.minimalDistance+currentVelocity*this.reactionTime+
                0.5*currentVelocity*currentVelocity*requester.getMaxDeceleration();
    }

    final protected ArrayList<MaxVelocity> getMaxVelocityList(Train requester, Time timer)
    {
        //Compute how far in front of the train we need to search for velocity changes
        double maxAchievableVelocity=requester.getVelocity()+requester.getMaxAcceleration()*timer.getDeltaTime();
        double maxCheckDistance=1.2*(0.5*requester.getMaxAcceleration()*timer.getDeltaTime()*timer.getDeltaTime()+
                this.brickWallDistance(requester, maxAchievableVelocity));
        //Keep track of the distance to the back of the train
        double tmpDistance=0;
        //Generate a list of maximum velocities and at what distance they begin
        ArrayList<MaxVelocity> maxVelocity=new ArrayList<MaxVelocity>();
        //Add my current velocity to the beginning
        maxVelocity.add(new MaxVelocity(0, requester.getVelocity()));
        //Distance from the beginning of the TrackComponent to the back of the train,
        //only important for firt TrackComponent
        TrackComponent currentTrack=requester.getCurrentTrack();
        double distanceBeginTrain=currentTrack.getTrainEndPosition(requester);
        //Go through all tracks in front until we searched far enough
        while(tmpDistance<maxCheckDistance)
        {
            if(currentTrack==null)
            {
                maxVelocity.add(new MaxVelocity(tmpDistance, 0));
                tmpDistance=maxCheckDistance;
            }
            else
            {
                //Add velocity contraint of currentTrack
                maxVelocity.add(new MaxVelocity(tmpDistance, currentTrack.getMaxVelocity(requester)));
                //Get all trains on current track and get their velocity constraint
                for(Train otherTrain: currentTrack.getOtherTrains(requester))
                {
                    maxVelocity.add(new MaxVelocity(
                            tmpDistance+currentTrack.getTrainEndPosition(otherTrain)
                            -this.brickWallDistance(requester, otherTrain.getVelocity()),
                            otherTrain.getVelocity()));
                }

                //Go to next track segment
                tmpDistance+=currentTrack.getLength(requester)-distanceBeginTrain;
                currentTrack=currentTrack.getNextTrack(requester);
                distanceBeginTrain=0;
            }
        }

        //Sort for distances
        Collections.sort(maxVelocity,new CompareDistance());
        
        return maxVelocity;
    }

    public List<AccelerationAtTime> getAccelerationProfile(Train requester, Time timer) {
        //Get a list of distances at which the max velocity changes
        ArrayList<MaxVelocity> maxVelocity=this.getMaxVelocityList(requester, timer);
        ArrayList<MaxVelocity> minVelocity=new ArrayList<MaxVelocity>(maxVelocity);
        Collections.sort(minVelocity,new CompareVelocity());
        //çççççççççççççççççç
        System.out.println(maxVelocity);
        System.out.println(minVelocity);
        //çççççççççççççççççç
        $$$$$$$$$$$$$$$$$$
                need to solve how to get from sorted lists to accel graph
        $$$$$$$$$$$$$$$$$$
//        ListIterator<MaxVelocity> it=maxVelocity.listIterator(maxVelocity.size()-1);
//        MaxVelocity lastElement=it.previous();
//        while(it.hasPrevious())
//        {
//            MaxVelocity currentElement=it.previous();
//            lastElement=currentElement;
//        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    final protected class MaxVelocity{
        public final double distance;
        public final double maxVelocity;
        
        public MaxVelocity(double myDistance,double myMaxVelocity) {
            this.distance=myDistance;
            this.maxVelocity=myMaxVelocity;
        }

        @Override
        public String toString()
        {
            return "["+Double.toString(this.distance)+","+Double.toString(this.maxVelocity)+"]";
        }
    }

    final protected class CompareDistance implements Comparator<MaxVelocity>{
        public int compare(MaxVelocity o1, MaxVelocity o2) {
            return Double.compare(o1.distance, o2.distance);
        }
    }

    final protected class CompareVelocity implements Comparator<MaxVelocity>{
        public int compare(MaxVelocity o1, MaxVelocity o2) {
            return Double.compare(o1.maxVelocity, o2.maxVelocity);
        }
    }
}
