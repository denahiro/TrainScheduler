/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.procedure;

import ch.sreng.schedule.components.mobile.Train;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Denahiro
 */
public class SafetyStrategy {
    private static Double REACTION_TIME;
    private static Double MINIMAL_DISTANCE;
    private static Double TRAVEL_TIME_SAFETY_FACTOR;

    final private static String SOURCE_FILE="data/safetyStrategy.ini";
    private static void loadIni() {
        if(REACTION_TIME==null) {
            try {
                BufferedReader sourceReader=new BufferedReader(new FileReader(SOURCE_FILE));
                String currentLine=sourceReader.readLine();
                while(currentLine!=null) {
                    String[] splitLine=currentLine.split("=");
                    if(splitLine[0].equalsIgnoreCase("reactionTime")) {
                        REACTION_TIME=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("minimalDistance")) {
                        MINIMAL_DISTANCE=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("travelTimeSafetyFactor")) {
                        TRAVEL_TIME_SAFETY_FACTOR=Double.parseDouble(splitLine[1]);
                    }
                    currentLine=sourceReader.readLine();
                }
            } catch(IOException ex) {
                System.out.println("Unable to load \"safetyStrategy.ini\"");
            }
        }
    }

    public SafetyStrategy()
    {
        loadIni();
    }

    /**
     * @param requester Train for which the brick wall distance should be calculated.
     * @return Returns the brick wall distance calculated from the end of the train without the train length.
     */
    final public double brickWallDistance(Train requester,double currentVelocity){
        return MINIMAL_DISTANCE+currentVelocity*REACTION_TIME+
                0.5*currentVelocity*currentVelocity*requester.getMaxDeceleration();
    }

    final public double travelTimeSafetyWaitTime(double travelTime) {
        return TRAVEL_TIME_SAFETY_FACTOR*travelTime;
    }
}
