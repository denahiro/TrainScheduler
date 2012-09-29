/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.output.Graph;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Denahiro
 */
public class TimetableMaster implements Master{

    private FixTime timer;

    private List<Graph> outputList=new ArrayList<Graph>();

    private List<Train> trains=new ArrayList<Train>();

    public TimetableMaster(double timestep)
    {
        this.timer=new FixTime(0,timestep);
    }

    public void doFrame() {
        this.registerState();
        this.timer.advanceTime();
        for(Train currentTrain: this.trains)
        {
            currentTrain.move(this.timer);
        }
    }

    public void addOutputGraph(Graph newOutput) {
        this.outputList.add(newOutput);
    }

    public void registerTrain(Train newTrain) {
        this.trains.add(newTrain);
    }

    public void unregisterTrain(Train oldTrain) {
        this.trains.remove(oldTrain);
    }

    private void registerState() {
        for(Train currentTrain: this.trains) {
            for(Graph currentGraph: this.outputList) {
                currentGraph.registerState(this.timer.getTime(), currentTrain);
            }
        }
    }
}
