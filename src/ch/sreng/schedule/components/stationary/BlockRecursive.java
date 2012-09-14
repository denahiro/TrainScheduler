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

	private Train blocker=null;
	
	/* (non-Javadoc)
	 * @see ch.sreng.schedule.components.stationary.Blockable#aquire(ch.sreng.schedule.components.mobile.Train)
	 */
	@Override
	public boolean aquire(Train requester)
	{
		if (this.blocker==null || this.blocker==requester)
		{
			this.blocker=requester;
			return true;
		}
		else
		{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see ch.sreng.schedule.components.stationary.Blockable#release(ch.sreng.schedule.components.mobile.Train)
	 */
	@Override
	public boolean release(Train requester) {
		if (this.blocker==requester)
		{
			this.blocker=null;
			return true;
		}
		else
		{
			return false;
		}
	}

}
