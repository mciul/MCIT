/**
 * 
 */
package wator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * @author mciul
 *
 */
public class Shark extends Pisces {
	private static int gestationTime = 10;
	private static int initialEnergy = 10;
	private int energy;
	private Pisces victim;   // a hack to make sure Sharks know if they've eaten
	
	final static Random rng = new Random();
	
	/**
	 * Sets the time for a shark to give birth
	 * @param time  the minimum number of turns for a shark to give birth
	 */
	public synchronized static void setGestationTime(int time) {
		gestationTime = time;
	}
	
	/**
	 * Sets the initial energy (number of turns a shark can live after eating or being born)
	 * for all sharks
	 * @param energy
	 */
	public synchronized static void setInitialEnergy(int energy) {
		initialEnergy = energy;
	}
	
	/**
	 * No-Argument Constructor
	 */
	public Shark() {
		this(null, 0, false);
	}
	
	/**
	 * Two-argument constructor for map addShark method
	 * 
	 * Creates a shark in the specified location with the specified gestation count
	 * Sets energy to initialEnergy
	 * 
	 * @param origin       where this shark is
	 * @param gestation    how many turns of gestation it has already had
	 */
	public Shark(Loc origin, int gestation, boolean randomizeEnergy) {
		super(origin, gestation);
		if (randomizeEnergy) {
			energy = rng.nextInt(initialEnergy);
		} else {
			energy = initialEnergy;
		}
	}
	
	/**
	 * Tells whether this shark is due to die of starvation, based on its energy
	 * @return   true if this shark must die of starvation this turn
	 */
	public boolean isStarving() {
		return energy<=0;
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#advanceAge()
	 */
	public void advanceAge() {
		super.advanceAge();
		energy--;
		if (isStarving()) {
			die();
		}
	}
	
	/* (non-Javadoc)
	 * @see wator.Pisces#availableMoves()
	 */
	@Override
	public ArrayList<Loc> availableMoves() {
		ArrayList<Loc> moves = new ArrayList<Loc>();
		if (!isAlive()) {
			return moves;
		}
		// prefer destinations where we can eat the occupant
		for (Direction dir: Direction.values()) {
			Loc move = getLoc().getNeighbor(dir);
			if (move.getOccupant()!=null && move.getOccupant().isEdible()) {
				moves.add(move);
			}
		}
		if (moves.size()>0) {
			return moves;
		}
		return super.availableMoves();
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#canOccupy(wator.Loc)
	 */
	public boolean canOccupy(Loc destination) {
		// We depend on this being called within a synchronized block
		// by Pisces.moveTo(destination)
		// Recording victim here makes sure that we really did 
		// eat the fish in the location we moved to
		victim = destination.getOccupant();
		if (super.canOccupy(destination)) {
			return true;
		}
		return victim.isEdible();
	}
	
	/* (non-Javadoc)
	 * @see wator.Pisces#isEdible()
	 */
	@Override
	public boolean isEdible() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#getBaby()
	 */
	@Override
	public Shark getBaby() {
		resetGestation();
		return getLoc().addShark(0, false);
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#moveTo(wator.Loc)
	 */
	@Override
	public boolean moveTo(Loc destination) {
		if (!super.moveTo(destination)) {
			return false;
		}
		// The victim should be set by canOccupy,
		// from within a synchronized block in Pisces.moveTo(destination).
		// If we get this far and victim is another creature, 
		// then we know we ate it.
		if (victim!=null && victim!=this) {
			energy = initialEnergy;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see wator.Pisces#getClassName()
	 */
	@Override
	protected String getClassName() {
		return "Shark";
	}
	
	/* (non-Javadoc)
	 * @see wator.Pisces#getPropertiesString()
	 */
	@Override
	protected String getPropertiesString() {
		return super.getPropertiesString() + "," + energy;
	}

	/* (non-Javadoc)
	 * @see wator.Pisces#getGestationTime()
	 */
	@Override
	protected int getGestationTime() {
		return gestationTime;
	}
}
