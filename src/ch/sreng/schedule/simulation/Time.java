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

    void advanceTime();

    double getDeltaTime();

    double getTime();

}
