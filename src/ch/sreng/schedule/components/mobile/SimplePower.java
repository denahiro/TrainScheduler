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

    private final double BASE_POWER_CONSUMPTION=300;
    private final double LINEAR_DRAG_COEFF=100;
    private final double QUADRATIC_DRAG_COEFF=10;
    private final double TRAIN_WEIGHT=120000;

    private double currentPowerConsumption=this.BASE_POWER_CONSUMPTION;

    public SimplePower(double myAcceleration, double myDeceleration)
    {
        this.acceleration=myAcceleration;
        this.deceleration=myDeceleration;
    }

    public void calculatePowerConsumption(List<Double> times, List<Double> velocities)
    {
        this.currentPowerConsumption=this.BASE_POWER_CONSUMPTION;
        ListIterator<Double> timeIt=times.listIterator();
        ListIterator<Double> veloIt=velocities.listIterator();
        double lastTime=timeIt.next();
        double lastVelo=veloIt.next();
        while(timeIt.hasNext() && veloIt.hasNext()) {
            double currentTime=timeIt.next();
            double currentVelo=veloIt.next();

            this.currentPowerConsumption+=this.LINEAR_DRAG_COEFF*(currentVelo+lastVelo)/2;

            double deltaV=currentVelo-lastVelo;
            this.currentPowerConsumption+=this.QUADRATIC_DRAG_COEFF*(lastVelo*lastVelo
                    +deltaV*lastVelo+deltaV*deltaV/3);

            this.currentPowerConsumption+=this.TRAIN_WEIGHT*Math.max((currentVelo*currentVelo/2
                    -lastVelo*lastVelo)/(currentTime-lastTime),0);

            lastTime=currentTime;
            lastVelo=currentVelo;
        }
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
