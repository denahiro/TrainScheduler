/**
 * 
 */
package ch.sreng.schedule.components.stationary;

import ch.sreng.schedule.components.mobile.Train;

/**
 * @author koenigst
 *
 */
public class BlockRecursive implements Blockable {

	/* (non-Javadoc)
	 * @see ch.sreng.schedule.components.stationary.Blockable#aquire(ch.sreng.schedule.components.mobile.Train)
	 */
	@Override
	public boolean aquire(Train requester) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see ch.sreng.schedule.components.stationary.Blockable#release(ch.sreng.schedule.components.mobile.Train)
	 */
	@Override
	public boolean release(Train requester) {
		// TODO Auto-generated method stub
		return false;
	}

}
