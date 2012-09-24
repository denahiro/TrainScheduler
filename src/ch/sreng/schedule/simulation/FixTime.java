/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

/**
 *
 * @author Denahiro
 */
public class FixTime implements Time {
    private double currentTime;
    private double deltaTime;

    public FixTime(double initialTime, double timestep)
    {
        this.currentTime=initialTime;
        this.deltaTime=timestep;
    }

    public double getTime()
    {
        return this.currentTime;
    }

    public double getDeltaTime()
    {
        return deltaTime;
    }

    public void advanceTime()
    {
        this.currentTime+=this.deltaTime;
    }
}
