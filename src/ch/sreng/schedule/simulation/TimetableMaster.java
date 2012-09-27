/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.output.Graph;
import ch.sreng.schedule.procedure.SafetyStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Denahiro
 */
public class TimetableMaster implements Master{

//    private ArrayList<Train> trains=new ArrayList<Train>();

    private FixTime timer;

//    private TrackComponent originTrack;
    private Graph output;

    private List<Train> trains=new ArrayList<Train>();

//    private ArrayList<Double> times=new ArrayList<Double>();
//    private HashMap<Train,ArrayList<Double>> trainPositions=new HashMap<Train, ArrayList<Double>>();
//    private HashMap<Train,ArrayList<Double>> brickWallPositions=new HashMap<Train, ArrayList<Double>>();

    public TimetableMaster(double timestep,Graph outputGraph)
    {
        this.timer=new FixTime(0,timestep);
        this.output=outputGraph;
    }

    public void doFrame() {
        this.registerState();
        this.timer.advanceTime();
        for(Train currentTrain: this.trains)
        {
            currentTrain.move(this.timer);
        }
    }

    public void registerTrain(Train newTrain) {
        this.trains.add(newTrain);
    }

    public void unregisterTrain(Train oldTrain) {
        this.trains.remove(oldTrain);
    }

    private void registerState()
    {
        for(Train currentTrain: this.trains)
        {
            this.output.registerState(this.timer.getTime(), currentTrain);
        }
    }
}
