/**
 * 
 */
package wator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Student
 * 
 */
public class WatorModel extends Observable {
	private int age;
	private Loc[][] map;
	final private int width;
	final private int height;
	final private WorldView view;

	public static Random rng = new Random();

	/**
	 * Constructor
	 * 
	 * Creates a world map with width and height specifications Also initializes
	 * age (time counter)
	 * 
	 * @param width
	 *            the number of columns (longitude) on the map
	 * @param height
	 *            the number of rows (latitude) on the map
	 */
	public WatorModel(int width, int height) {
		view = null;
		this.width = width;
		this.height = height;
		age = 0;
		map = new Loc[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				map[x][y] = new Loc(x, y, this);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Tell how many turns have passed in this world
	 * 
	 * @return number of elapsed turns
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Returns the location at the specified x,y coordinates
	 * 
	 * @param x
	 *            longitude coordinate
	 * @param y
	 *            latitude coordinate
	 * @return the location object at the given coordinates
	 */
	public Loc getLoc(int x, int y) {
		return map[x][y];
	}

	/**
	 * Returns the location next to the origin in the specified direction
	 * 
	 * @param origin
	 *            the place to start
	 * @param dir
	 *            the direction to go from the start
	 * @return the adjacent location
	 */
	public Loc getLoc(Loc origin, Direction dir) {
		int x = (origin.getX() + dir.getDx()) % width;
		int y = (origin.getY() + dir.getDy()) % height;
		if (x < 0) {
			x += width;
		}
		if (y < 0) {
			y += height;
		}
		return getLoc(x, y);
	}

	/**
	 * Do one frame of animation
	 */
	public void animate() {
		// TODO: do paused check in Wator?
		setChanged();
		notifyObservers();
	}

	/**
	 * A setup method Places a new fish at the specified x,y coordinates
	 * 
	 * @param x
	 * @param y
	 * @param gestation
	 */
	public Fish addFish(int x, int y, int gestation) {
		return getLoc(x, y).addFish(gestation);
	}

	/**
	 * A setup method Places a new shark at the specified x,y coordinates
	 * 
	 * @param x
	 * @param y
	 * @param gestation
	 */
	public Shark addShark(int x, int y, int gestation, boolean randomizeEnergy) {
		return getLoc(x, y).addShark(gestation, randomizeEnergy);
	}

	/**
	 * After addFish and addShark create a creature, they should add it using
	 * this method
	 */
	private void addPisces(Pisces occupant) {
		// TODO: stub
	}

	public void reset(int fishCount, int sharkCount) {
		ArrayList<Loc> availableLocs = new ArrayList<Loc>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				availableLocs.add(getLoc(x, y));
			}
		}
		Fish dummyFish = new Fish();
		Shark dummyShark = new Shark();
		Collections.shuffle(availableLocs);
		int i = 0;
		while (sharkCount > 0) {
			availableLocs.get(i++).addShark(rng.nextInt(dummyShark.getGestationTime()), true);
			sharkCount--;
		}
		while (fishCount > 0) {
			availableLocs.get(i++).addFish(rng.nextInt(dummyFish.getGestationTime()));
			fishCount--;
		}
		while (i < availableLocs.size()) {
			availableLocs.get(i++).setOccupant(null);
		}
	}
}
