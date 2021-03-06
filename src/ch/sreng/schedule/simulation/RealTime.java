/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

/**
 *
 * @author Denahiro
 */
public class RealTime implements Time {
    private double timeFactor=0;
    private double currentTime;
    private double deltaTime=0;
    private long lastSystemTime;

    public RealTime(double initialTime)
    {
        this.currentTime=initialTime;
        this.lastSystemTime=System.currentTimeMillis();
    }

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

    public void advanceTime() {
        long oldTime=this.lastSystemTime;
        this.lastSystemTime=System.currentTimeMillis();
        double elapsedTime=((double) this.lastSystemTime-oldTime)/1e3;
        this.deltaTime=elapsedTime*this.timeFactor;
        this.currentTime+=this.deltaTime;
    }

    public void limitFrameRate(double maxRate) {
        long elapsedTime=System.currentTimeMillis()-this.lastSystemTime;
        if(elapsedTime<Math.round(1000/maxRate)) {
            try {
                Thread.sleep(Math.round(1000/maxRate-elapsedTime));
            }
            catch(InterruptedException e) {
                
            }
        }
    }
}
