/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.mobile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Denahiro
 */
public class SimplePower implements Power {

    private static Double ACCELERATION=null;
    private static Double DECELERATION=null;

    private static Double BASE_POWER_CONSUMPTION=null;
    private static Double TRAIN_WEIGHT=null;
    private static Double LINEAR_DRAG_COEFF=null;
    private static Double QUADRATIC_DRAG_COEFF=null;
    private static Double EFFICIENCY=null;

    private double currentPowerConsumption;

    final private static String SOURCE_FILE="data/mobile/simplePower.ini";
    private static void loadIni() {
        if(BASE_POWER_CONSUMPTION==null) {
            try {
                BufferedReader sourceReader=new BufferedReader(new FileReader(SOURCE_FILE));
                String currentLine=sourceReader.readLine();
                while(currentLine!=null) {
                    String[] splitLine=currentLine.split("=");
                    if(splitLine[0].equalsIgnoreCase("acceleration")) {
                        ACCELERATION=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("deceleration")) {
                        DECELERATION=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("basePowerConsumption")) {
                        BASE_POWER_CONSUMPTION=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("trainWeight")) {
                        TRAIN_WEIGHT=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("linearDragCoefficient")) {
                        LINEAR_DRAG_COEFF=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("quadraticDragCoefficient")) {
                        QUADRATIC_DRAG_COEFF=Double.parseDouble(splitLine[1]);
                    } else if(splitLine[0].equalsIgnoreCase("efficiency")) {
                        EFFICIENCY=Double.parseDouble(splitLine[1]);
                    }
                    currentLine=sourceReader.readLine();
                }
            } catch(IOException ex) {
                System.out.println("Unable to load \"simplePower.ini\"");
            }
        }
    }

    public SimplePower() {
        loadIni();
        this.currentPowerConsumption=BASE_POWER_CONSUMPTION;
    }

    public void calculatePowerConsumption(List<Double> times, List<Double> velocities)
    {
        this.currentPowerConsumption=0;
        ListIterator<Double> timeIt=times.listIterator();
        ListIterator<Double> veloIt=velocities.listIterator();
        double previousTime=timeIt.next();
        double previousVelo=veloIt.next();
        double firstTime=previousTime;
        while(timeIt.hasNext() && veloIt.hasNext()) {
            double currentTime=timeIt.next();
            double currentVelo=veloIt.next();

            if(currentTime-previousTime>0) {
                double stepPowerConsumption=TRAIN_WEIGHT*LINEAR_DRAG_COEFF
                        *(currentVelo+previousVelo)/2;

                double deltaV=currentVelo-previousVelo;
                stepPowerConsumption+=TRAIN_WEIGHT*QUADRATIC_DRAG_COEFF
                        *(previousVelo*previousVelo+deltaV*previousVelo+deltaV*deltaV/3);

                stepPowerConsumption+=TRAIN_WEIGHT*(currentVelo*currentVelo/2
                        -previousVelo*previousVelo/2)/(currentTime-previousTime);

                stepPowerConsumption*=(currentTime-previousTime)/EFFICIENCY;

                this.currentPowerConsumption+=Math.max(stepPowerConsumption,0);
            }

            previousTime=currentTime;
            previousVelo=currentVelo;
        }

        this.currentPowerConsumption/=(previousTime-firstTime);
        this.currentPowerConsumption+=BASE_POWER_CONSUMPTION;
    }

    public double getMaxAcceleration()
    {
        return ACCELERATION;
    }

    public double getMaxDeceleration()
    {
        return DECELERATION;
    }

    public double getPowerConsumption() {
        return this.currentPowerConsumption;
    }
}
