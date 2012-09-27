/**
 * 
 */
package ch.sreng.schedule;

import ch.sreng.schedule.components.mobile.SimplePower;
import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.TrackSimple;
import ch.sreng.schedule.output.TrainInfoGraph;
import ch.sreng.schedule.procedure.DriveStrategy;
import ch.sreng.schedule.procedure.DriveStrategyBangBang;
import ch.sreng.schedule.procedure.SafetyStrategy;
import ch.sreng.schedule.simulation.Master;
import ch.sreng.schedule.simulation.SimulationMaster;
import ch.sreng.schedule.simulation.TimetableMaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        tracks.add(new TrackSimple(0,20));
        tracks.add(new TrackSimple(200,20));
        tracks.add(new TrackSimple(450,15));
        tracks.add(new TrackSimple(500,20));
        tracks.add(new TrackSimple(200,10));
        tracks.add(new TrackSimple(100,20));
        for(int i=0;i<tracks.size()-1;i++)
        {
            tracks.get(i).setNextTrack(tracks.get(i+1));
        }
        tracks.get(tracks.size()-1).setNextTrack(tracks.get(0));

        train1.setInitialConditions(tracks.get(0), 0, 10);
//        train2.setInitialConditions(tracks.get(0), 200, 10);

        TrainInfoGraph outGraph=new TrainInfoGraph();

        Master master=new TimetableMaster(0.1,outGraph);
        master.registerTrain(train1);
//        master.registerTrain(train2);
//        master.setTimeFactor(1);
        for(int i=0;i<1000;i++)
        {
            master.doFrame();
        }

        PrintWriter output;
        try {
            output = new PrintWriter(new File("tmp.dat"));
            outGraph.saveToWriter(output);
            output.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not here.");
        }

    }
}
