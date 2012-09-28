/**
 * 
 */
package ch.sreng.schedule;

import ch.sreng.schedule.components.mobile.SimplePower;
import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.Linkable;
import ch.sreng.schedule.components.stationary.Station;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.components.stationary.TrackSimple;
import ch.sreng.schedule.output.GraphPrinter;
import ch.sreng.schedule.output.TrainInfoGraph;
import ch.sreng.schedule.procedure.DriveStrategy;
import ch.sreng.schedule.procedure.DriveStrategyBangBang;
import ch.sreng.schedule.procedure.SafetyStrategy;
import ch.sreng.schedule.simulation.Master;
import ch.sreng.schedule.simulation.SimulationMaster;
import ch.sreng.schedule.simulation.TimetableMaster;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
        SafetyStrategy safetyStrategy=new SafetyStrategy(1, 30,0.07);
        Train train1=new Train(driveStrategy,safetyStrategy,new SimplePower(1,1), 20,89);
        Train train2=new Train(driveStrategy,safetyStrategy,new SimplePower(1,1), 20,63);

        Station station1=new Station(250,10);
        Station station2=new Station(100,30);

        station1.setNextStation(station2);
        station2.setNextStation(station1);

        ArrayList<Linkable<TrackComponent>> tracks=new ArrayList<Linkable<TrackComponent>>();
        tracks.add(new TrackSimple(0,20));
        tracks.add(new TrackSimple(200,20));
        tracks.add(station1);
//        tracks.add(new TrackSimple(300,5));
        tracks.add(new TrackSimple(350,15));
        tracks.add(new TrackSimple(450,15));
        tracks.add(new TrackSimple(500,20));
        tracks.add(new TrackSimple(200,10));
//        tracks.add(new TrackSimple(100,5));
        tracks.add(station2);
        for(int i=0;i<tracks.size()-1;i++)
        {
            tracks.get(i).setNextLink(tracks.get(i+1).getLinkTo());
        }
        tracks.get(tracks.size()-1).setNextLink(tracks.get(0).getLinkTo());

        train1.setInitialConditions(tracks.get(0).getLinkTo(),station1, 0, 0);
        train2.setInitialConditions(tracks.get(0).getLinkTo(),station2, 350, 0);

        TrainInfoGraph outGraph=new TrainInfoGraph();

        Master master=new TimetableMaster(0.1,outGraph);
        master.registerTrain(train1);
        master.registerTrain(train2);
//        master.setTimeFactor(1);
        for(int i=0;i<10000;i++)
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

        GraphPrinter myGraphPrinter=new GraphPrinter();
        try {
            myGraphPrinter.print(outGraph, new File("tmp2.emf"),new Dimension(300,300));
        } catch (FileNotFoundException ex) {
            System.out.println("File not written.");
        }
    }
}
