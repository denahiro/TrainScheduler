/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.procedure;

import ch.sreng.schedule.components.mobile.Train;

/**
 *
 * @author Denahiro
 */
public class SafetyStrategy {
    private double reactionTime;
    private double minimalDistance;
    private double travelTimeSafetyFactor;

    public SafetyStrategy(double myReactionTime, double myMinimalDistance,double myTravelTimeSafetyFactor)
    {
        this.reactionTime=myReactionTime;
        this.minimalDistance=myMinimalDistance;
        this.travelTimeSafetyFactor=myTravelTimeSafetyFactor;
    }

    /**
     * @param requester Train for which the brick wall distance should be calculated.
     * @return Returns the brick wall distance calculated from the end of the train without the train length.
     */
    final public double brickWallDistance(Train requester,double currentVelocity){
        return requester.getLength()+this.minimalDistance+currentVelocity*this.reactionTime+
                0.5*currentVelocity*currentVelocity*requester.getMaxDeceleration();
    }

    final public double travelTimeSafetyWaitTime(double travelTime) {
        return this.travelTimeSafetyFactor*travelTime;
    }
}
