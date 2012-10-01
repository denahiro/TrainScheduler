/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

/**
 *
 * @author Denahiro
 */
public interface Time {

    public abstract void advanceTime();

    public abstract double getDeltaTime();

    public abstract double getTime();

}
