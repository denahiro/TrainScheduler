package ch.sreng.schedule.procedure;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.simulation.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.ListIterator;

/**
 * @author koenigst
 *
 */
public class DriveStrategyBangBang implements DriveStrategy {
//    private SafetyStrategy safetyStrategy;
//
//    public DriveStrategyBangBang(SafetyStrategy mySafety)
//    {
//        this.safetyStrategy=mySafety;
//    }

//    /**
//     * @param requester Train for which the brick wall distance should be calculated.
//     * @return Returns the brick wall distance calculated from the end of the train.
//     */
//    final protected double brickWallDistance(Train requester,double currentVelocity)
//    {
//        return requester.getLength()+this.minimalDistance+currentVelocity*this.reactionTime+
//                0.5*currentVelocity*currentVelocity*requester.getMaxDeceleration();
//    }

    final protected List<MaxVelocity> getMaxVelocityList(Train requester, Time timer)
    {
        //Compute how far in front of the train we need to search for velocity changes
        double maxAchievableVelocity=requester.getVelocity()+requester.getMaxAcceleration()*timer.getDeltaTime();
        double maxCheckDistance=1.2*(0.5*requester.getMaxAcceleration()*timer.getDeltaTime()*timer.getDeltaTime()+
                requester.getSafetyStrategy().brickWallDistance(requester, maxAchievableVelocity));
        //Keep track of the distance to the back of the train
        double tmpDistance=0;
        //Generate a list of maximum velocities and at what distance they begin
        ArrayList<MaxVelocity> maxVelocityTrack=new ArrayList<MaxVelocity>();
        MaxVelocity maxVelocityTrain=null;
        //Distance from the beginning of the TrackComponent to the back of the train,
        //only important for first TrackComponent
        TrackComponent currentTrack=requester.getCurrentTrack();
        double distanceBeginTrain=currentTrack.getTrainEndPosition(requester);
        //Go through all tracks in front until we searched far enough
        while(tmpDistance<maxCheckDistance)
        {
            if(currentTrack==null)
            {
                maxVelocityTrack.add(new MaxVelocity(tmpDistance, 0));
                tmpDistance=maxCheckDistance;
            }
            else
            {
                //Add velocity contraint of currentTrack
                maxVelocityTrack.add(new MaxVelocity(tmpDistance, currentTrack.getMaxVelocity(requester)));
                //Get all trains on current track and get their velocity constraint
                for(Train otherTrain: currentTrack.getOtherTrains(requester))
                {
                    double tmpTrainBrakePosition=tmpDistance+currentTrack.getTrainEndPosition(otherTrain)
                            -requester.getSafetyStrategy().brickWallDistance(requester, otherTrain.getVelocity());
                    if(maxVelocityTrain==null || maxVelocityTrain.distance>tmpTrainBrakePosition)
                    {
                        maxVelocityTrain=new MaxVelocity(tmpTrainBrakePosition,otherTrain.getVelocity());
                    }
                }

                //Go to next track segment
                tmpDistance+=currentTrack.getLength(requester)-distanceBeginTrain;
                currentTrack=currentTrack.getNextTrack(requester);
                distanceBeginTrain=0;
            }
        }

        //Sort for distances
        Collections.sort(maxVelocityTrack);

        ArrayList<MaxVelocity> maxVelocityReturn;
        if(maxVelocityTrain==null)
        {
            maxVelocityReturn=maxVelocityTrack;
        } else {
            maxVelocityReturn=new ArrayList<MaxVelocity>();
            ListIterator<MaxVelocity> cleanIterator=maxVelocityTrack.listIterator();
            while(cleanIterator.hasNext())
            {
                MaxVelocity currentElement=cleanIterator.next();
                if(currentElement.distance<maxVelocityTrain.distance)
                {
                    maxVelocityReturn.add(currentElement);
                }
            }
            maxVelocityReturn.add(maxVelocityTrain);
        }
        
        return maxVelocityReturn;
    }

    public List<AccelerationAtTime> getAccelerationProfile(Train requester, Time timer) {
        //Get a list of distances at which the max velocity changes
        List<MaxVelocity> maxVelocity=this.getMaxVelocityList(requester, timer);
//        ArrayList<MaxVelocity> minVelocity=new ArrayList<MaxVelocity>(maxVelocity);
//        Collections.sort(minVelocity,new CompareVelocity());

        //çççççççççççççççççç
        System.out.println(maxVelocity);
//        System.out.println(minVelocity);
        //çççççççççççççççççç

        ArrayList<AccelerationAtTime> returnList=new ArrayList<AccelerationAtTime>();
        double currentVelocity=requester.getVelocity();
        double currentTime=0;
        ListIterator<MaxVelocity> limitIterator=maxVelocity.listIterator();
        while(limitIterator.hasNext())
        {
            System.out.println(currentTime);
            System.out.println(currentVelocity);
            MaxVelocity currentLimit=limitIterator.next();
            ListIterator<MaxVelocity> secondIterator=maxVelocity.listIterator(limitIterator.nextIndex());
            double currentMinBrakeDistance=Double.POSITIVE_INFINITY;
            double currentBrakeTime=0;
            double currentTargetVelocity=currentLimit.maxVelocity;
            int currentLimitAdd=0;
            int targetLimitAdd=0;
            while(secondIterator.hasNext())
            {
                MaxVelocity nextLimit=secondIterator.next();
                if(nextLimit.maxVelocity<currentLimit.maxVelocity)
                {
                    double tmpBrakeTime=(currentLimit.maxVelocity-nextLimit.maxVelocity)
                            /requester.getMaxDeceleration();
                    double tmpBrakeDistance=nextLimit.distance-currentLimit.distance
                            -nextLimit.maxVelocity*tmpBrakeTime-requester.getMaxDeceleration()
                            *tmpBrakeTime*tmpBrakeTime/2;

                    if(tmpBrakeDistance<currentMinBrakeDistance)
                    {
                        targetLimitAdd=currentLimitAdd;
                        currentMinBrakeDistance=tmpBrakeDistance;
                        currentBrakeTime=tmpBrakeTime;
                        currentTargetVelocity=nextLimit.maxVelocity;
                    }
                }
                ++currentLimitAdd;
            }
            
            if(currentVelocity>currentLimit.maxVelocity)
            {
                System.out.println(currentTime);
                System.out.println(currentVelocity-currentLimit.maxVelocity);
                System.out.println(currentVelocity);
                System.out.println(currentLimit.maxVelocity);
                throw new RuntimeException("Velocity too large");
            } else if (currentVelocity<currentLimit.maxVelocity) //Accelerate
            {
                $$$$$$$$$$
                throw new UnsupportedOperationException("Not supported yet.");
            } else //Velocity is already at maximum
            {
                returnList.add(new AccelerationAtTime(currentTime, 0));
                double tmpTimeUntilBrake=currentMinBrakeDistance/currentVelocity;
                returnList.add(new AccelerationAtTime(currentTime+tmpTimeUntilBrake, -requester.getMaxDeceleration()));
                currentTime+=tmpTimeUntilBrake+currentBrakeTime;
                currentVelocity=currentTargetVelocity;
            }

            //Jump over the already calculated parts
            for(int i=0;i<targetLimitAdd;++i) limitIterator.next();
        }
        
//        $$$$$$$$$$$$$$$$$$
//                need to solve how to get from sorted lists to accel graph
//        $$$$$$$$$$$$$$$$$$
//        ListIterator<MaxVelocity> it=maxVelocity.listIterator(maxVelocity.size()-1);
//        MaxVelocity lastElement=it.previous();
//        while(it.hasPrevious())
//        {
//            MaxVelocity currentElement=it.previous();
//            lastElement=currentElement;
//        }
        System.out.println(returnList);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    final protected class MaxVelocity implements Comparable<MaxVelocity>{
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

        public int compareTo(MaxVelocity other) {
            return Double.compare(this.distance, other.distance);
        }
    }

//    final protected class CompareDistance implements Comparator<MaxVelocity>{
//        public int compare(MaxVelocity o1, MaxVelocity o2) {
//            return Double.compare(o1.distance, o2.distance);
//        }
//    }
//
//    final protected class CompareVelocity implements Comparator<MaxVelocity>{
//        public int compare(MaxVelocity o1, MaxVelocity o2) {
//            return Double.compare(o1.maxVelocity, o2.maxVelocity);
//        }
//    }
}
