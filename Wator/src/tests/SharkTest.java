/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import wator.Direction;
import wator.Fish;
import wator.Loc;
import wator.Shark;
import wator.WatorModel;

/**
 * @author Student
 * 
 */
public class SharkTest {
	WatorModel model;
	Shark testShark;
	final int width = 5;
	final int height = 4;
	final int term = 10;
	final int initialEnergy = 7;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = new WatorModel(width, height);
		Shark.setGestationTime(term);
		Shark.setInitialEnergy(initialEnergy);
		testShark = new Shark();
	}

	/**
	 * Test method for {@link wator.Shark#setInitialEnergy(int)}.
	 */
	@Test
	public void testSetInitialEnergy() {
		Shark.setInitialEnergy(2);
		testShark = new Shark();
		for (int i = 0; i < 2; i++) {
			assertFalse(testShark.isStarving());
			testShark.advanceAge();
		}
		assertTrue(testShark.isStarving());
	}

	/**
	 * Test method for {@link wator.Shark#isEdible()}.
	 */
	@Test
	public void testIsEdible() {
		assertFalse(testShark.isEdible());
	}

	/**
	 * Test method for {@link wator.Shark#availableMoves()}.
	 */
	@Test
	public void testAvailableMoves() {
		// neighboring sharks should stop moves
		HashSet<Loc> expected = new HashSet<Loc>();
		assertEquals(expected, new HashSet<Loc>(testShark.availableMoves()));
		Shark swimmer = model.addShark(1, 3, 2, false);
		Shark wShark = model.addShark(0, 3, 0, false);
		Shark nShark = model.addShark(1, 2, 0, false);
		Shark eShark = model.addShark(2, 3, 0, false);
		Shark sShark = model.addShark(1, 0, 0, false);
		expected.add(model.getLoc(1, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		expected.clear();
		wShark.die();
		expected.add(model.getLoc(0, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		sShark.die();
		expected.add(model.getLoc(1, 0));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		eShark.die();
		expected.add(model.getLoc(2, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		nShark.die();
		expected.add(model.getLoc(1, 2));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));

		// neighboring fish should be chosen over empty sea
		expected.clear();
		Fish wFish = model.addFish(0, 3, 0);
		expected.add(model.getLoc(0, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		Fish nFish = model.addFish(1, 2, 0);
		expected.add(model.getLoc(1, 2));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		Fish eFish = model.addFish(2, 3, 0);
		expected.add(model.getLoc(2, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		Fish sFish = model.addFish(1, 0, 0);
		expected.add(model.getLoc(1, 0));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));

		// combinations of fish, sharks, and empty sea
		sShark = model.addShark(1, 0, 0, false);
		expected.remove(model.getLoc(1, 0));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		wFish.die();
		expected.remove(model.getLoc(0, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
	}

	/**
	 * Test method for {@link wator.Shark#canOccupy(wator.Loc)}.
	 */
	@Test
	public void testCanOccupy() {
		Shark swimmer = model.addShark(2, 3, 0, false);
		Loc loc = model.getLoc(2, 3);
		for (Direction dir : Direction.values()) {
			Loc neighbor = loc.getNeighbor(dir);
			assertTrue(swimmer.canOccupy(neighbor));
			neighbor.addFish(0);
			assertTrue(swimmer.canOccupy(neighbor));
			neighbor.addShark(0, false);
			assertFalse(swimmer.canOccupy(neighbor));
		}
	}

	/**
	 * Test method for {@link wator.Pisces#moveTo(Loc)}.
	 */
	@Test
	public void testMoveTo() {
		Loc origin = model.getLoc(2, 3);
		Loc destination = model.getLoc(1, 3);
		Loc obstacle = model.getLoc(2, 2);
		Shark swimmer = origin.addShark(0, false);
		swimmer.advanceAge(); // make sure the shark isn't at full energy so we
								// know whether it ate

		// fail if moving to occupied space
		obstacle.addShark(1, false);
		assertFalse(swimmer.moveTo(obstacle));
		assertEquals("Loc(2,2,Shark(1,7))", obstacle.toString());

		// succeed if moving to empty space
		assertTrue(swimmer.moveTo(destination));
		assertEquals("Loc(1,3,Shark(1,6))", destination.toString());
		assertEquals("Loc(2,3,null)", origin.toString());

		// don't give birth if staying in place
		origin = model.getLoc(1, 1);
		swimmer = origin.addShark(term, false);
		swimmer.advanceAge();
		assertTrue(swimmer.moveTo(origin));
		assertEquals("Loc(1,1,Shark(" + (term + 1) + ",6))", origin.toString());

		// leave baby behind if moving
		destination = model.getLoc(1, 2);
		assertTrue(swimmer.moveTo(destination));
		assertEquals("Loc(1,2,Shark(0,6))", destination.toString());
		assertEquals("Loc(1,1,Shark(0,7))", origin.toString());

		// gain energy if eating
		origin = swimmer.getLoc();
		destination = model.getLoc(0, 2);
		destination.addFish(0);
		assertTrue(swimmer.moveTo(destination));
		assertEquals("Loc(0,2,Shark(0,7))", destination.toString());

		// starve - being dead is success but has no effect
		origin = swimmer.getLoc();
		while (!swimmer.isStarving()) {
			swimmer.advanceAge();
		}
		swimmer.die();
		destination = model.getLoc(0, 3);
		assertTrue(swimmer.moveTo(destination));
		assertEquals("Loc(0,2,null)", origin.toString());
		assertEquals("Loc(0,3,null)", destination.toString());
	}

	/**
	 * Test method for {@link wator.Shark#advanceAge()}.
	 */
	@Test
	public void testAdvanceAge() {
		for (int i = 0; i < initialEnergy; i++) {
			assertEquals("lost Shark(" + i + "," + (initialEnergy - i) + ")",
					testShark.toString());
			testShark.advanceAge();
		}
		assertEquals("lost Shark(" + initialEnergy + ",0)",
				testShark.toString());
	}

	/**
	 * Test method for {@link wator.Pisces#run()}.
	 */
	@Test
	public void testSwim() {
		// make a path that our test shark must follow
		Shark follower = model.addShark(0, 1, 0, false);
		model.addShark(1, 0, 0, false);
		model.addShark(2, 0, 0, false);
		model.addShark(3, 0, 0, false);
		model.addShark(1, 2, 0, false);
		model.addShark(2, 2, 0, false);
		model.addShark(4, 1, 0, false);
		model.addShark(3, 3, 0, false);
		model.addShark(4, 2, 0, false);

		// chase the shark into a corner
		Shark swimmer = model.addShark(1, 1, 0, false);
		Loc[] path = { model.getLoc(2, 1), model.getLoc(3, 1),
				model.getLoc(3, 2) };
		int gestation = 0;
		int energy = initialEnergy;
		for (Loc destination : path) {
			Loc origin = swimmer.getLoc();
			gestation++;
			energy--;
			swimmer.swim();
			assertEquals(swimmer, destination.getOccupant());
			assertEquals("Loc(" + destination.getX() + "," + destination.getY()
					+ ",Shark(" + gestation + "," + energy + "))",
					destination.toString());
			assertTrue(follower.moveTo(origin));
		}

		while (energy > 0) {
			// fish is now trapped, should go nowhere
			assertTrue(swimmer.isAlive());
			swimmer.swim();
			energy--;
		}
		assertFalse(swimmer.isAlive());
		assertEquals(null, path[2].getOccupant());

		// tired of testing this stuff
	}

	/**
	 * Test method for {@link wator.Shark#isStarving()}.
	 */
	@Test
	public void testIsStarving() {
		for (int i = 0; i < initialEnergy; i++) {
			assertFalse(testShark.isStarving());
			testShark.advanceAge();
		}
		assertTrue(testShark.isStarving());
	}

	/**
	 * Test method for {@link wator.Shark#getBaby()}.
	 */
	@Test
	public void testGetBaby() {
		Shark mother = model.addShark(0, 0, 1, false);
		assertEquals("Shark(1,7)", mother.toString());
		Shark baby = mother.getBaby();
		assertTrue(baby instanceof Shark);
		assertFalse(testShark == baby);
		// baby should be alive in mother's location
		assertEquals("Shark(0,7)", baby.toString());
		assertEquals(mother.getLoc(), baby.getLoc());
		// mother should be "dead" in same location with 0 gestation
		assertEquals("dead Shark(0,7)", mother.toString());
		assertEquals(model.getLoc(0, 0), mother.getLoc());
	}

	/**
	 * A shark with a null loc should not be able to have a baby
	 */
	@Test(expected = NullPointerException.class)
	public void testGetBabyLost() {
		testShark.getBaby();
	}

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("lost Shark(0,7)", testShark.toString());
		testShark.setLoc(model.getLoc(0, 0));
		assertEquals("dead Shark(0,7)", testShark.toString());
		testShark.getLoc().setOccupant(testShark);
		assertEquals("Shark(0,7)", testShark.toString());
		testShark.advanceAge();
		assertEquals("Shark(1,6)", testShark.toString());
	}

}
