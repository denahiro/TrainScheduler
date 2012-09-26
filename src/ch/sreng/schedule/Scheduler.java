/**
 * 
 */
package ch.sreng.schedule;

import ch.sreng.schedule.components.mobile.SimplePower;
import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackSimple;
import ch.sreng.schedule.procedure.DriveStrategy;
import ch.sreng.schedule.procedure.DriveStrategyBangBang;
import ch.sreng.schedule.procedure.SafetyStrategy;
import ch.sreng.schedule.simulation.SimulationMaster;
import java.util.ArrayList;

/**
 * @author koenigst
 *
 */
public class Scheduler {

    /**
     * @param args
     */
    public static void main(String[] args) {
        DriveStrategy driveStrategy=new DriveStrategyBangBang();
        SafetyStrategy safetyStrategy=new SafetyStrategy(1, 30);
        Train train1=new Train(driveStrategy,safetyStrategy,new SimplePower(1,1), 20,89);
        Train train2=new Train(driveStrategy,safetyStrategy,new SimplePower(1,1), 20,63);

        ArrayList<TrackSimple> tracks=new ArrayList<TrackSimple>();
        tracks.add(new TrackSimple(100,15));
        tracks.add(new TrackSimple(10,20));
        tracks.add(new TrackSimple(50,15));
        tracks.add(new TrackSimple(100,20));
        tracks.add(new TrackSimple(100,0));
        tracks.add(new TrackSimple(100,20));
        for(int i=0;i<tracks.size()-1;i++)
        {
            tracks.get(i).setNextTrack(tracks.get(i+1));
        }
        tracks.get(tracks.size()-1).setNextTrack(tracks.get(1));

        train1.setInitialConditions(tracks.get(0), 0, 15);
//        train2.setInitialConditions(tracks.get(0), 350, 90/3.6);

        SimulationMaster master=new SimulationMaster();
        master.registerTrain(train1);
//        master.registerTrain(train2);
        master.setTimeFactor(1);
//        while(true)
        {
            master.doFrame();
        }
    }
}
