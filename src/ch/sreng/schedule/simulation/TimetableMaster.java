/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.procedure.SafetyStrategy;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Denahiro
 */
public class TimetableMaster implements Master{

//    private ArrayList<Train> trains=new ArrayList<Train>();

    private FixTime timer;

    private TrackComponent originTrack;

    private ArrayList<Double> times=new ArrayList<Double>();
    private HashMap<Train,ArrayList<Double>> trainPositions=new HashMap<Train, ArrayList<Double>>();
    private HashMap<Train,ArrayList<Double>> brickWallPositions=new HashMap<Train, ArrayList<Double>>();

    public TimetableMaster(double timestep,TrackComponent origo)
    {
        this.timer=new FixTime(0,timestep);
        this.originTrack=origo;
    }

    public void doFrame() {
        this.timer.advanceTime();
        for(Train currentTrain: trainPositions.keySet())
        {
            currentTrain.move(timer);
        }
        this.registerState();
    }

    public void registerTrain(Train newTrain) {
        this.trainPositions.put(newTrain,new ArrayList<Double>());
        this.brickWallPositions.put(newTrain,new ArrayList<Double>());
    }

    public void unregisterTrain(Train oldTrain) {
        this.trainPositions.remove(oldTrain);
        this.brickWallPositions.remove(oldTrain);
    }

    private void registerState()
    {
        this.times.add(this.timer.getTime());
        for(Train currentTrain: trainPositions.keySet())
        {
            double tmpPos=currentTrain.getRelativePosition(this.originTrack);
            this.trainPositions.get(currentTrain).add(tmpPos);
            this.brickWallPositions.get(currentTrain).add(tmpPos+
                    currentTrain.getSafetyStrategy().brickWallDistance(currentTrain, currentTrain.getVelocity())
                    +currentTrain.getLength());
        }
    }
}
