/**
 * 
 */
package ch.sreng.schedule;

import ch.sreng.schedule.components.mobile.SimplePower;
import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.components.stationary.Linkable;
import ch.sreng.schedule.components.stationary.Station;
import ch.sreng.schedule.components.stationary.TrackComponent;
import ch.sreng.schedule.components.stationary.TrackFactory;
import ch.sreng.schedule.components.stationary.TrackSimple;
import ch.sreng.schedule.output.EnergyGraph;
import ch.sreng.schedule.output.Graph;
import ch.sreng.schedule.output.GraphPrinter;
import ch.sreng.schedule.output.TimePosGraph;
import ch.sreng.schedule.procedure.DriveStrategy;
import ch.sreng.schedule.procedure.DriveStrategyBangBang;
import ch.sreng.schedule.procedure.SafetyStrategy;
import ch.sreng.schedule.simulation.Master;
import ch.sreng.schedule.simulation.SimulationMaster;
import ch.sreng.schedule.simulation.TimetableMaster;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        Train train1=new Train(driveStrategy,safetyStrategy,new SimplePower(),Color.RED);
//        Train train2=new Train(driveStrategy,safetyStrategy,new SimplePower(),Color.BLACK);

//        Station station1=new Station(550,0);
//        Station station2=new Station(100,0);
//
//        station1.setNextStation(station2);
//        station2.setNextStation(station1);
//
//        ArrayList<Linkable<TrackComponent>> tracks=new ArrayList<Linkable<TrackComponent>>();
//        tracks.add(new TrackSimple(0,20,0));
//        tracks.add(new TrackSimple(400,20,0));
//        tracks.add(station1);
////        tracks.add(new TrackSimple(300,5));
//        tracks.add(new TrackSimple(600,15,8));
//        tracks.add(new TrackSimple(1000,15,0));
//        tracks.add(new TrackSimple(500,20,0));
//        tracks.add(new TrackSimple(200,10,0));
////        tracks.add(new TrackSimple(100,5));
//        tracks.add(station2);
//        for(int i=0;i<tracks.size()-1;i++)
//        {
//            tracks.get(i).setNextLink(tracks.get(i+1).getLinkTo());
//        }
//        tracks.get(tracks.size()-1).setNextLink(tracks.get(0).getLinkTo());

        try {
            TrackFactory.TrackContainer track=TrackFactory.loadFile("alignmentCorridor1.csv");

            train1.setInitialConditions(track.getFirstTrack(),track.getFirstStation(), 0, 0);
    //        train2.setInitialConditions(tracks.get(0).getLinkTo(),station2, 550, 0);

            Graph outGraphPos=new TimePosGraph(TimePosGraph.Units.LARGE);
            Graph outGraphEnergy=new EnergyGraph(EnergyGraph.Units.LARGE);

            TimetableMaster master=new TimetableMaster(1);
            master.addOutputGraph(outGraphPos);
            master.addOutputGraph(outGraphEnergy);
            master.registerTrain(train1);
    //        master.registerTrain(train2);
    //        master.setTimeFactor(1);
            for(int i=0;i<10000;i++)
            {
                master.doFrame();
            }

            PrintWriter output;
            try {
                output = new PrintWriter(new File("tmpPos.dat"));
                outGraphPos.saveToWriter(output);
                output.close();
                output = new PrintWriter(new File("tmpEnergy.dat"));
                outGraphEnergy.saveToWriter(output);
                output.close();
            } catch (FileNotFoundException ex) {
                System.out.println("File not here.");
            }

    //        GraphPrinter myGraphPrinter=new GraphPrinter();
            try {
                GraphPrinter.print(outGraphPos, new File("tmpPos.emf"),new Dimension(400,300));
                GraphPrinter.print(outGraphEnergy, new File("tmpEnergy.emf"),new Dimension(400,300));
            } catch (FileNotFoundException ex) {
                System.out.println("File not written.");
            }
        } catch(IOException ex) {
            System.out.println("Unable to load file.");
        }
    }
}
