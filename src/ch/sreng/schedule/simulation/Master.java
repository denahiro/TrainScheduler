/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.simulation;

import ch.sreng.schedule.components.mobile.Train;

/**
 *
 * @author Denahiro
 */
public interface Master {

    void doFrame();

    void registerTrain(Train newTrain);

    void unregisterTrain(Train oldTrain);

}
