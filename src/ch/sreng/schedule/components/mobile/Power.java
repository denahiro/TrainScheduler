/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.mobile;

import java.util.List;

/**
 *
 * @author Denahiro
 */
public interface Power {

    public abstract void calculatePowerConsumption(List<Double> times, List<Double> velocities);

    public abstract double getMaxAcceleration();

    public abstract double getMaxDeceleration();

    public abstract double getPowerConsumption();
}
