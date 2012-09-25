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

    public SafetyStrategy(double myReactionTime, double myMinimalDistance)
    {
        this.reactionTime=myReactionTime;
        this.minimalDistance=myMinimalDistance;
    }

    /**
     * @param requester Train for which the brick wall distance should be calculated.
     * @return Returns the brick wall distance calculated from the end of the train without the train length.
     */
    final public double brickWallDistance(Train requester,double currentVelocity)
    {
        return requester.getLength()+this.minimalDistance+currentVelocity*this.reactionTime+
                0.5*currentVelocity*currentVelocity*requester.getMaxDeceleration();
    }
}
