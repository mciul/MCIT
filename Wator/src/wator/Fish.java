/**
 * 
 */
package wator;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author mciul
 *
 */
public class Fish extends Pisces {
	private static int gestationTime = 10;
	
	/**
	 * Sets the time for a fish to give birth
	 * @param time   the minimum number of turns for a fish to give birth
	 */
	public static synchronized void setGestationTime(int time) {
		gestationTime = time;
	}

	/**
	 * Constructor with location and gestation counter
	 * @param loc        Where this fish ought to be if it is alive
	 * @param gestation  How many turns into gestation it is
	 */
	public Fish(Loc loc, int gestation) {
		super(loc, gestation);
	}

	/**
	 * Constructor with no arguments
	 * 
	 * calls Pisces constructor with no arguments
	 * @see wator.Pisces#Pisces()
	 */
	public Fish() {
		super();
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#isEdible()
	 */
	@Override
	public boolean isEdible() {
		return true;
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#getBaby()
	 */
	@Override
	public Fish getBaby() {
		resetGestation();
		return getLoc().addFish(0);
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#getClassName()
	 */
	@Override
	protected String getClassName() {
		return "Fish";
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#getGestationTime()
	 */
	@Override
	protected int getGestationTime() {
		return gestationTime;
	}
}
