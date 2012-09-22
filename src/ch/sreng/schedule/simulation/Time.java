/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

/**
 *
 * @author Denahiro
 */
public class Time {
    private double timeFactor=0;
    private double currentTime;
    private double deltaTime=0;
    private long lastSystemTime;

    public double getTime()
    {
        return this.currentTime;
    }

    public double getDeltaTime()
    {
        return deltaTime;
    }

    public void setTimeFactor(double newFactor)
    {
        if(this.timeFactor==0)
        {
            this.advanceTime();
        }
        this.timeFactor=newFactor;
    }

    public void advanceTime()
    {
        long oldTime=this.lastSystemTime;
        this.lastSystemTime=System.currentTimeMillis();
        double elapsedTime=((double) this.lastSystemTime-oldTime)/1e6;
        this.deltaTime=elapsedTime*this.timeFactor;
        this.currentTime+=this.deltaTime;
    }

    public Time(double initialTime)
    {
        this.currentTime=initialTime;
        this.lastSystemTime=System.currentTimeMillis();
    }
}
