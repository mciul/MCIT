/**
 * 
 */
package wator;

import java.awt.Graphics;
import java.util.Observable;

/**
 * @author mciul
 * 
 */
public class Loc {
	final private int x;
	final private int y;
	final private WatorModel world;

	// occupant is the authoritative record of who is in this location.
	// Pisces objects have a loc field that normally reflects this location,
	// but if the two values do not sync, the creature is considered dead
	// and whatever occupant is recorded in this Loc is considered the true
	// occupant.
	private Pisces occupant;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            the x coordinate (longitude) of this location
	 * @param y
	 *            the y coordinate (latitude) of this location
	 * @param world
	 *            the world that this location exists in
	 */
	public Loc(int x, int y, WatorModel world) {
		this.x = x;
		this.y = y;
		this.world = world;
		occupant = null;
	}

	/**
	 * Get the X coordinate (longitude) of this location
	 * 
	 * @return the X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the Y coordinate (latitude) of this location
	 * 
	 * @return the Y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Return the true occupant of this location Even if another creature has
	 * this location as a loc property, it is not considered to be here unless
	 * it is the return value of Loc.getOccupant().
	 * 
	 * @return the true occupant of this location, or null if empty
	 */
	public Pisces getOccupant() {
		return occupant;
	}

	/**
	 * Set a creature as the occupant of this location
	 * 
	 * @param newOccupant
	 *            the creature moving in, or null if vacating
	 */
	public void setOccupant(Pisces newOccupant) {
		synchronized (this) {
			Pisces oldOccupant = occupant;
			occupant = newOccupant;
			if (oldOccupant != newOccupant) {
				if (oldOccupant != null) {
					world.deleteObserver(oldOccupant);
				}
				if (newOccupant != null) {
					world.addObserver(newOccupant);
				}
			}
		}
	}

	/**
	 * Create a new live Fish in this location
	 * 
	 * @return the newly created fish
	 */
	public synchronized Fish addFish(int gestation) {
		Fish fish = new Fish(this, gestation);
		setOccupant(fish);
		return fish;
	}

	/**
	 * Create a new live Shark in this location
	 * 
	 * @return the newly created shark
	 */
	public synchronized Shark addShark(int gestation, boolean randomizeEnergy) {
		Shark shark = new Shark(this, gestation, randomizeEnergy);
		setOccupant(shark);
		return shark;
	}

	/**
	 * Returns two locations sorted into a consistent order to prevent deadlocks
	 * 
	 * @param other
	 *            the location besides this one
	 * @return an array of this and other, sorted
	 */
	public Loc[] sortLocs(Loc other) {
		Loc[] order = { this, other };
		if (y < other.getY() || y == other.getY() && x <= other.getX()) {
			return order;
		}
		order[0] = other;
		order[1] = this;
		return order;
	}

	/**
	 * Provides the neighboring loc in the direction specified
	 * 
	 * @param dir
	 *            the direction to go from this location
	 * @return the Loc found in the direction specified
	 */
	public Loc getNeighbor(Direction dir) {
		return world.getLoc(this, dir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Loc(" + x + "," + y + "," + occupant + ")";
	}

}
