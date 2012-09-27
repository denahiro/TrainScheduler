/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.sreng.schedule.components.stationary;

/**
 *
 * @author Denahiro
 */
public interface Linkable<T> {

    public abstract T getLinkTo();

    public abstract void setNextLink(T next);
}
