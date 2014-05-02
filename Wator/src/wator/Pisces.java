/**
 * 
 */
package wator;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author mciul
 * 
 */
public abstract class Pisces implements Runnable, Observer {
	private Loc loc;
	private int gestationCounter;
	private static ExecutorService pool = Executors.newFixedThreadPool(10);

	// for debugging:
	private int deadCounter;
	private static int maxDeadCount = 0;

	/**
	 * A blocking function needed for some tests
	 * 
	 * @return true if everything terminated within 1 second
	 */
	public static boolean waitForAll() {
		boolean result;
		pool.shutdown();
		try {
			result = (pool.awaitTermination(1, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			return false;
		}
		if (result) {
			pool = Executors.newFixedThreadPool(10);
		}
		return result;
	}

	/**
	 * Defines the number of it takes for a creature of this class to give birth
	 * 
	 * This is static data for each subclass, which Java won't let us formalize
	 * here.
	 * 
	 * @return the gestation time for this class
	 */
	protected abstract int getGestationTime();

	/**
	 * Explicit constructor
	 * 
	 * @param loc
	 * @param gestationCounter
	 */
	Pisces(Loc loc, int gestationCounter) {
		setLoc(loc);
		this.gestationCounter = gestationCounter;
		deadCounter = 0;
	}

	/**
	 * No-argument constructor
	 * 
	 */
	Pisces() {
		this(null, 0);
	}

	/**
	 * Tells whether this object can be eaten (by a Shark)
	 * 
	 * @return true if this creature can be eaten (i.e. it's a fish)
	 */
	abstract public boolean isEdible();

	/**
	 * Returns the location of this creature This answer is more for
	 * convenience, to avoid searching the entire map. It is not authoritative.
	 * 
	 * To be sure this creature is where it says it is, call
	 * this.getLoc().getOccupant().
	 * 
	 * If the result of that call is not this, then this creature is considered
	 * dead.
	 * 
	 * @return the loc where this creature was last
	 */
	public Loc getLoc() {
		return loc;
	}

	/**
	 * Sets the recorded location of this creature.
	 * 
	 * This property is not considered authoritative - to truly move a creature,
	 * set the occupant of a loc.
	 * 
	 * This method must be called to make sure the creature is "alive" in that
	 * loc - it must have the same value to be alive, and anyway if the loc
	 * isn't set correctly, the creature won't be able to find its place to swim
	 * around the world.
	 * 
	 * @param loc
	 *            the location where this creature should be if it is alive
	 */
	public void setLoc(Loc loc) {
		this.loc = loc;
	}

	/**
	 * Tells whether this creature is ready to give birth based on the amount of
	 * time that has passed
	 * 
	 * @return true if this creature should leave a baby behind when moving
	 */
	public boolean isFullTerm() {
		return gestationCounter >= getGestationTime();
	}

	/**
	 * Reset the gestation counter
	 * 
	 * Necessary when giving birth to a new creature
	 */
	protected void resetGestation() {
		this.gestationCounter = 0;
	}

	/**
	 * Tells whether this creature still lives in the world.
	 * 
	 * A creature is considered alive if it truly occupies the location it says
	 * it does, otherwise it is considered dead.
	 * 
	 * So a fish is killed by having a shark come and occupy the place where it
	 * is located - the fact that the shark is there serves as a message.
	 * 
	 * @return whether or not this creature still occupies its last location
	 */
	public boolean isAlive() {
		if (loc == null) {
			return false;
		}
		if (loc.getOccupant() == this) {
			deadCounter = 0;
		} else {
			deadCounter++;
		}
		return deadCounter == 0;
	}

	/**
	 * Causes this creature to be considered dead
	 * 
	 * This is a little tricky, which is why it is handled here.
	 * 
	 * It must cease to occupy its location, which requires a lock to update the
	 * location.
	 */
	public void die() {
		if (!isAlive()) {
			return;
		}
		synchronized (loc) {
			if (loc.getOccupant() == this) {
				loc.setOccupant(null);
			}
		}
	}

	/**
	 * Returns all possible locations that this creature could move to This
	 * method is somewhat general, but it should work for fish.
	 * 
	 * If a fish is not alive, it should return an empty set. If a fish is alive
	 * but can't go anywhere, it should return the set that contains only the
	 * fish's current location.
	 * 
	 * Sharks should override it so they will eat fish whenever possible.
	 * 
	 * @return the set of possible destinations for the next move
	 */
	public ArrayList<Loc> availableMoves() {
		ArrayList<Loc> moves = new ArrayList<Loc>();
		if (!isAlive()) {
			return moves;
		}
		for (Direction dir : Direction.values()) {
			Loc move = loc.getNeighbor(dir);
			if (canOccupy(move)) {
				moves.add(move);
			}
		}
		if (moves.size() == 0) {
			moves.add(loc);
		}
		return moves;
	}

	/**
	 * Check whether this creature can go to the specified location Here we only
	 * check if the location is empty, but Sharks should override to allow a
	 * move to a fish's location.
	 * 
	 * This will be called first from availableMoves then later from swim() to
	 * make sure the destination hasn't changed before it was locked
	 * 
	 * @return whether or not this creature can go to the specified location
	 */
	public boolean canOccupy(Loc destination) {
		if (isAlive() && destination == getLoc()) {
			// we can always stay where we are
			return true;
		}
		return destination.getOccupant() == null;
	}

	/**
	 * Returns a new instance of this class.
	 * 
	 * Subclasses should return instances of their own class, initialized to
	 * their own location, 0 gestation and any other settings required for a
	 * newborn.
	 * 
	 * getBaby is also responsible for resetting gestation on this creature.
	 * 
	 * @return the baby of this creature
	 */
	abstract public Pisces getBaby();

	/**
	 * Move this creature from its origin to a new destination This method is
	 * called from the run() method Leave a baby behind if it's time, starting a
	 * thread for the baby
	 * 
	 * @return true if the creature successfully moved or died, false if we
	 *         should try again
	 */
	public boolean moveTo(Loc destination) {
		Loc[] lockedLocs = loc.sortLocs(destination);
		// always synchronize origin and destination in the same order to avoid
		// deadlocks
		synchronized (lockedLocs[0]) {
			synchronized (lockedLocs[1]) {
				if (!isAlive()) {
					return true;
				}
				if (!canOccupy(destination)) {
					return false;
				}
				if (destination != loc) {
					if (isFullTerm()) {
						loc.setOccupant(getBaby());
					} else {
						loc.setOccupant(null);
					}
				}
				destination.setOccupant(this);
				setLoc(destination);
			}
		}
		return true;
	}

	/**
	 * Call once whenever time passes in the world Updates gestation counter
	 * Sharks should override to update starvation counter
	 */
	public void advanceAge() {
		gestationCounter++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (isAlive()) {
			swim();
		} else {
			if (deadCounter > maxDeadCount) {
				maxDeadCount = deadCounter;
				System.out
						.println("This "
								+ this
								+ " has run while dead "
								+ deadCounter
								+ " times, setting a new record. Current loc is "
								+ loc);
			}
		}
	}

	/**
	 * The core behavior of this creature, called every turn by run()
	 */
	public void swim() {
		boolean moved = false;
		while (!moved) {
			ArrayList<Loc> moves = availableMoves();
			moved = moveTo(moves.get(WatorModel.rng.nextInt(moves.size())));
		}
		advanceAge();
	}

	/**
	 * Provides a simplified class name such as "Pisces"
	 * 
	 * @return the name of this creature´s class
	 */
	abstract protected String getClassName();

	/**
	 * Provides a string describing properties of this creature.
	 * 
	 * Shark should override this to give energy
	 * 
	 * @return a comma-separated list of property values
	 */
	protected String getPropertiesString() {
		return "" + gestationCounter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String description = new String("");
		if (loc == null) {
			description += "lost ";
		} else if (!isAlive()) {
			description += "dead ";
		}
		return description + getClassName() + "(" + getPropertiesString() + ")";
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (deadCounter > 0 && !isAlive()) {
			System.out.println("This " + this + " has updated while dead "
					+ deadCounter + " times. Current loc is " + loc
					+ ". Maximum dead count is " + maxDeadCount);
		}
		pool.submit(this);
	}
}
