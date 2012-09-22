/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.mobile;

/**
 *
 * @author Denahiro
 */
public interface Power {

    public abstract void accelerate(double acceleration,double time);

    public abstract double getMaxAcceleration();

    public abstract double getMaxDeceleration();
}
