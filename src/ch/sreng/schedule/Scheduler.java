/**
 * 
 */
package ch.sreng.schedule;

import ch.sreng.schedule.simulation.SimulationDispatcher;
import java.io.IOException;
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
        try {
            SimulationDispatcher disp = new SimulationDispatcher("data/dispatch.xml");
            disp.run();
        } catch (IOException ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
//        DriveStrategy driveStrategy=new DriveStrategyBangBang();
//        SafetyStrategy safetyStrategy=new SafetyStrategy();
//        Train train1=new Train(driveStrategy,safetyStrategy,new SimplePower(),Color.RED);
////        Train train2=new Train(driveStrategy,safetyStrategy,new SimplePower(),Color.BLACK);
//
////        Station station1=new Station(550,0);
////        Station station2=new Station(100,0);
////
////        station1.setNextStation(station2);
////        station2.setNextStation(station1);
////
////        ArrayList<Linkable<TrackComponent>> tracks=new ArrayList<Linkable<TrackComponent>>();
////        tracks.add(new TrackSimple(0,20,0));
////        tracks.add(new TrackSimple(400,20,0));
////        tracks.add(station1);
//////        tracks.add(new TrackSimple(300,5));
////        tracks.add(new TrackSimple(600,15,8));
////        tracks.add(new TrackSimple(1000,15,0));
////        tracks.add(new TrackSimple(500,20,0));
////        tracks.add(new TrackSimple(200,10,0));
//////        tracks.add(new TrackSimple(100,5));
////        tracks.add(station2);
////        for(int i=0;i<tracks.size()-1;i++)
////        {
////            tracks.get(i).setNextLink(tracks.get(i+1).getLinkTo());
////        }
////        tracks.get(tracks.size()-1).setNextLink(tracks.get(0).getLinkTo());
//
//        try {
//            TrackFactory.TrackContainer track=TrackFactory.loadFile("alignmentCorridor2.csv");
//
//            train1.setInitialConditions(track.getFirstTrack(),track.getFirstStation(), 0, 0);
//    //        train2.setInitialConditions(tracks.get(0).getLinkTo(),station2, 550, 0);
//
//            Graph outGraphPos=new TimePosGraph(TimePosGraph.Units.LARGE);
//            Graph outGraphEnergy=new EnergyGraph(EnergyGraph.Units.LARGE);
//
//            TimetableMaster master=new TimetableMaster(1);
//            master.addOutputGraph(outGraphPos);
//            master.addOutputGraph(outGraphEnergy);
//            master.registerTrain(train1);
//    //        master.registerTrain(train2);
//    //        master.setTimeFactor(1);
//            for(int i=0;i<3000;i++)
//            {
//                master.doFrame();
//            }
//
//            PrintWriter output;
//            try {
//                output = new PrintWriter(new File("tmpPos.dat"));
//                outGraphPos.saveToWriter(output);
//                output.close();
//                output = new PrintWriter(new File("tmpEnergy.dat"));
//                outGraphEnergy.saveToWriter(output);
//                output.close();
//            } catch (FileNotFoundException ex) {
//                System.out.println("File not here.");
//            }
//
//    //        GraphPrinter myGraphPrinter=new GraphPrinter();
//            try {
//                GraphPrinter.print(outGraphPos, new File("tmpPos.emf"),new Dimension(400,300));
//                GraphPrinter.print(outGraphEnergy, new File("tmpEnergy.emf"),new Dimension(400,300));
//            } catch (FileNotFoundException ex) {
//                System.out.println("File not written.");
//            }
//        } catch(IOException ex) {
//            System.out.println("Unable to load file.");
//        }
    }
}
