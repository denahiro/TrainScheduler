/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.mobile;

/**
 *
 * @author Denahiro
 */
public class SimplePower implements Power {

    private final double acceleration;
    private final double deceleration;

    public SimplePower(double myAcceleration, double myDeceleration)
    {
        this.acceleration=myAcceleration;
        this.deceleration=myDeceleration;
    }

    public void accelerate(double acceleration, double time)
    {
        
    }

    public double getMaxAcceleration()
    {
        return this.acceleration;
    }

    public double getMaxDeceleration()
    {
        return this.deceleration;
    }
}
