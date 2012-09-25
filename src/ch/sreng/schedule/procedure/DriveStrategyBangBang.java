package ch.sreng.schedule.procedure;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.simulation.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * @author koenigst
 *
 */
public class DriveStrategyBangBang implements DriveStrategy {

    final protected List<MaxVelocity> getMaxVelocityList(Train requester, Time timer)
    {
        //Compute how far in front of the train we need to search for velocity changes
        double maxAchievableVelocity=requester.getVelocity()+requester.getMaxAcceleration()*timer.getDeltaTime();
        double maxCheckDistance=1.2*(0.5*requester.getMaxAcceleration()*timer.getDeltaTime()*timer.getDeltaTime()
                +requester.getSafetyStrategy().brickWallDistance(requester, maxAchievableVelocity)
                +requester.getLength());
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
                maxVelocityTrack.add(new MaxVelocity(tmpDistance,
                        Math.min(currentTrack.getMaxVelocity(requester),requester.getMaxVelocity())));
                //Get all trains on current track and get their velocity constraint
                for(Train otherTrain: currentTrack.getOtherTrains(requester))
                {
                    if(otherTrain.getVelocity()<requester.getVelocity())
                    {
                        double tmpTrainBrakePosition=tmpDistance+currentTrack.getTrainEndPosition(otherTrain)
                                -requester.getSafetyStrategy().brickWallDistance(requester, otherTrain.getVelocity());
                        tmpTrainBrakePosition=requester.getVelocity()*tmpTrainBrakePosition
                                /(requester.getVelocity()-otherTrain.getVelocity());
                        if(maxVelocityTrain==null || maxVelocityTrain.distance>tmpTrainBrakePosition)
                        {
                            maxVelocityTrain=new MaxVelocity(tmpTrainBrakePosition,
                                    Math.min(otherTrain.getVelocity(),requester.getMaxVelocity()));
                        }
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
            double lastAddedVelocity=Double.POSITIVE_INFINITY;
            while(cleanIterator.hasNext())
            {
                MaxVelocity currentElement=cleanIterator.next();
                if(currentElement.distance<maxVelocityTrain.distance)
                {
                    lastAddedVelocity=currentElement.maxVelocity;
                    maxVelocityReturn.add(currentElement);
                }
            }
            if(maxVelocityTrain.maxVelocity<lastAddedVelocity)
            {
                maxVelocityReturn.add(maxVelocityTrain);
            }
        }
        
        return maxVelocityReturn;
    }

    @Override
    public List<AccelerationAtTime> getAccelerationProfile(Train requester, Time timer) {
        //Get a list of distances at which the max velocity changes
        List<MaxVelocity> maxVelocity=this.getMaxVelocityList(requester, timer);
        
        System.out.println(maxVelocity);

        ArrayList<AccelerationAtTime> returnList=new ArrayList<AccelerationAtTime>();
        double currentVelocity=requester.getVelocity();
        double currentTime=0;
        ListIterator<MaxVelocity> limitIterator=maxVelocity.listIterator();
        while(limitIterator.hasNext())
        {
            MaxVelocity currentLimit=limitIterator.next();
            ListIterator<MaxVelocity> secondIterator=maxVelocity.listIterator(limitIterator.nextIndex());
            double currentMinBrakeDistance=Double.POSITIVE_INFINITY;
            double currentBrakeTime=0;
            double nextDistance=Double.POSITIVE_INFINITY;
            int currentLimitAdd=0;
            int targetLimitAdd=0;
            while(secondIterator.hasNext())
            {
                MaxVelocity nextLimit=secondIterator.next();
                if(nextDistance>nextLimit.distance)
                {
                    nextDistance=nextLimit.distance;
                }
                if(nextLimit.maxVelocity<currentLimit.maxVelocity)
                {
                    double tmpBrakeTime=(currentLimit.maxVelocity-nextLimit.maxVelocity)
                            /requester.getMaxDeceleration();
                    double tmpBrakeDistance=nextLimit.distance-currentLimit.distance
                            -requester.getLength()-nextLimit.maxVelocity*tmpBrakeTime
                            -requester.getMaxDeceleration()*tmpBrakeTime*tmpBrakeTime/2;

                    if(tmpBrakeDistance<currentMinBrakeDistance && tmpBrakeDistance<=nextDistance)
                    {
                        targetLimitAdd=currentLimitAdd;
                        currentMinBrakeDistance=tmpBrakeDistance;
                        currentBrakeTime=tmpBrakeTime;
                    }
                }
                ++currentLimitAdd;
            }
            
            if(currentVelocity>currentLimit.maxVelocity) {
                if(currentVelocity-currentLimit.maxVelocity>1e-3)
                throw new RuntimeException("Velocity too large");
            } else if (currentVelocity<currentLimit.maxVelocity) { //Accelerate
                returnList.add(new AccelerationAtTime(currentTime, requester.getMaxAcceleration()));
                double accelerationTime=(currentLimit.maxVelocity-currentVelocity)
                        /requester.getMaxAcceleration();
                double accelerationDistance=currentLimit.distance+currentVelocity*accelerationTime
                        +requester.getMaxAcceleration()*accelerationTime*accelerationTime/2;
                
                if(currentBrakeTime>0) {//If braking is required
                    if(accelerationDistance<currentMinBrakeDistance) { //Can accelerate fully
                        currentVelocity+=accelerationTime*requester.getMaxAcceleration();
                        currentTime+=accelerationTime;
                        returnList.add(new AccelerationAtTime(currentTime, 0));
                        currentTime+=(currentMinBrakeDistance-accelerationDistance)/currentVelocity;
                        returnList.add(new AccelerationAtTime(currentTime, -requester.getMaxDeceleration()));
                        currentTime+=currentBrakeTime;
                        currentVelocity-=currentBrakeTime*requester.getMaxDeceleration();
                        returnList.add(new AccelerationAtTime(currentTime, 0));
                        currentTime+=requester.getLength()/currentVelocity;
                    } else {
                        double deltaAccelerationTime=(currentLimit.maxVelocity-
                                Math.sqrt(currentLimit.maxVelocity*currentLimit.maxVelocity
                                -2*requester.getMaxDeceleration()*(accelerationDistance-currentMinBrakeDistance)))
                                /(requester.getMaxAcceleration()+requester.getMaxDeceleration());
                        System.out.println(currentLimit.maxVelocity*currentLimit.maxVelocity
                                -2*requester.getMaxDeceleration()*(accelerationDistance-currentMinBrakeDistance));
                        currentTime+=accelerationTime-deltaAccelerationTime;
                        currentVelocity+=(accelerationTime-deltaAccelerationTime)*requester.getMaxAcceleration();
                        returnList.add(new AccelerationAtTime(currentTime, -requester.getMaxDeceleration()));
                        System.out.println(returnList);
                        double deltaDecelerationTime=deltaAccelerationTime*requester.getMaxAcceleration()
                                /requester.getMaxDeceleration();
                        currentTime+=currentBrakeTime-deltaDecelerationTime;
                        currentVelocity-=(currentBrakeTime-deltaDecelerationTime)*requester.getMaxDeceleration();
                        returnList.add(new AccelerationAtTime(currentTime, 0));
                        currentTime+=requester.getLength()/currentVelocity;
                    }
                } else {//If no braking is required
                    if(accelerationDistance<nextDistance) {
                        returnList.add(new AccelerationAtTime(currentTime+accelerationTime, 0));
                        currentVelocity+=accelerationTime*requester.getMaxAcceleration();
                        currentTime+=accelerationTime+(nextDistance-accelerationDistance)/currentVelocity;
                    } else {
                        accelerationTime=(-currentVelocity+Math.sqrt(currentVelocity*currentVelocity
                                +2*requester.getMaxAcceleration()*(nextDistance-currentLimit.distance)))
                                /requester.getMaxAcceleration();
                        currentTime+=accelerationTime;
                        currentVelocity+=accelerationTime*requester.getMaxAcceleration();
                    }
                }
            } else {//Velocity is already at maximum
                returnList.add(new AccelerationAtTime(currentTime, 0));
                if(currentBrakeTime>0) { //If braking is required
                    double tmpTimeUntilBrake=currentMinBrakeDistance/currentVelocity;
                    returnList.add(new AccelerationAtTime(currentTime+tmpTimeUntilBrake,
                            -requester.getMaxDeceleration()));
                    currentTime+=tmpTimeUntilBrake+currentBrakeTime;
                    currentVelocity-=currentBrakeTime*requester.getMaxDeceleration();
                    returnList.add(new AccelerationAtTime(currentTime, 0));
                    currentTime+=requester.getLength()/currentVelocity;
                } else { //If no braking is required
                    currentTime+=(nextDistance-currentLimit.distance)/currentVelocity;
                }
            }

            //Jump over the already calculated parts
            for(int i=0;i<targetLimitAdd;++i)
            {
                limitIterator.next();
            }
            System.out.println(returnList);
            System.out.println(currentTime);
        }
        
        System.out.println(returnList);
        return returnList;
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

        @Override
        public int compareTo(MaxVelocity other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
