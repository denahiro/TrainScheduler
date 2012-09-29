/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.mobile;

import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Denahiro
 */
public class SimplePower implements Power {

    private final double acceleration;
    private final double deceleration;

    private final double BASE_POWER_CONSUMPTION=3000;
    private final double LINEAR_DRAG_COEFF=30000;
    private final double QUADRATIC_DRAG_COEFF=500;
    private final double TRAIN_WEIGHT=120000;

    private double currentPowerConsumption=this.BASE_POWER_CONSUMPTION;

    public SimplePower(double myAcceleration, double myDeceleration)
    {
        this.acceleration=myAcceleration;
        this.deceleration=myDeceleration;
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
                double stepPowerConsumption=this.LINEAR_DRAG_COEFF*(currentVelo+previousVelo)/2;

                double deltaV=currentVelo-previousVelo;
                stepPowerConsumption+=this.QUADRATIC_DRAG_COEFF*(previousVelo*previousVelo
                        +deltaV*previousVelo+deltaV*deltaV/3);

                stepPowerConsumption+=this.TRAIN_WEIGHT*(currentVelo*currentVelo/2
                        -previousVelo*previousVelo/2)/(currentTime-previousTime);

                stepPowerConsumption*=(currentTime-previousTime);

                this.currentPowerConsumption+=Math.max(stepPowerConsumption,0);
            }

            previousTime=currentTime;
            previousVelo=currentVelo;
        }

        this.currentPowerConsumption/=(previousTime-firstTime);
        this.currentPowerConsumption+=this.BASE_POWER_CONSUMPTION;
    }

    public double getMaxAcceleration()
    {
        return this.acceleration;
    }

    public double getMaxDeceleration()
    {
        return this.deceleration;
    }

    public double getPowerConsumption() {
        return this.currentPowerConsumption;
    }
}
