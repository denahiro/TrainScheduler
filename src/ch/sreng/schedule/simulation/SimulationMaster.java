/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackComponent;
import java.util.ArrayList;

/**
 *
 * @author Denahiro
 */
public class SimulationMaster implements Master {

    private RealTime timer=new RealTime(0);
    private double frameRate=20;

    private ArrayList<Train> trains=new ArrayList<Train>();
    private ArrayList<TrackComponent> tracks;

    public double getTime() {
        return this.timer.getTime();
    }

    public void doFrame()
    {
        this.timer.advanceTime();
        for(Train currentTrain: trains)
        {
            currentTrain.move(timer);
        }
        this.timer.limitFrameRate(this.frameRate);
    }

    public void start()
    {
        this.setTimeFactor(1);
    }

    public void stop()
    {
        this.setTimeFactor(0);
    }

    public void setTimeFactor(double factor)
    {
        this.timer.setTimeFactor(factor);
    }

    public void registerTrain(Train newTrain)
    {
        this.trains.add(newTrain);
    }

    public void unregisterTrain(Train oldTrain)
    {
        this.trains.remove(oldTrain);
    }
}
