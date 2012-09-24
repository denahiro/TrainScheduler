/**
 * 
 */
package ch.sreng.schedule.procedure;

import ch.sreng.schedule.components.mobile.Train;
import ch.sreng.schedule.simulation.Time;
import java.util.List;

/**
 * @author koenigst
 *
 */
public interface DriveStrategy {
    public abstract List<AccelerationAtTime> getAccelerationProfile(Train requester,Time timer);

    final public class AccelerationAtTime implements Comparable<AccelerationAtTime>{
        public final double time;
        public final double acceleration;
        public AccelerationAtTime(double myTime,double myAcceleration) {
            this.time=myTime;
            this.acceleration=myAcceleration;
        }
        
        @Override
        public String toString()
        {
            return "["+Double.toString(this.time)+","+Double.toString(this.acceleration)+"]";
        }

        public int compareTo(AccelerationAtTime other) {
            return Double.compare(this.time, other.time);
        }
    }
}
