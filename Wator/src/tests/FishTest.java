/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import wator.Direction;
import wator.Fish;
import wator.Loc;
import wator.Pisces;
import wator.Shark;
import wator.WatorModel;

/**
 * @author Student
 * 
 */
public class FishTest {
	WatorModel model;
	Fish testFish;
	final int width = 5;
	final int height = 4;
	final int term = 3;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = new WatorModel(width, height);
		testFish = new Fish();
		Fish.setGestationTime(term);
	}

	/**
	 * Test method for {@link wator.Fish#isEdible()}.
	 */
	@Test
	public void testIsEdible() {
		assertTrue(testFish.isEdible());
	}

	/**
	 * Test method for {@link wator.Fish#getBaby()}.
	 */
	@Test
	public void testGetBaby() {
		Fish mother = model.addFish(0, 0, 1);
		assertEquals("Fish(1)", mother.toString());
		Fish baby = mother.getBaby();
		assertTrue(baby instanceof Fish);
		assertFalse(testFish == baby);
		// baby should be alive in mother's location
		assertEquals("Fish(0)", baby.toString());
		assertEquals(mother.getLoc(), baby.getLoc());
		// mother should be "dead" in same location with 0 gestation
		assertEquals("dead Fish(0)", mother.toString());
		assertEquals(model.getLoc(0, 0), mother.getLoc());
	}

	/**
	 * A fish with a null loc should not be able to have a baby
	 */
	@Test(expected = NullPointerException.class)
	public void testGetBabyLost() {
		testFish.getBaby();
	}

	/**
	 * Test method for {@link wator.Fish#setGestationTime()}.
	 */
	@Test
	public void testSetGestationTime() {
		Fish.setGestationTime(2);
		Shark.setGestationTime(100);
		for (int i = 0; i < 2; i++) {
			assertFalse(testFish.isFullTerm());
			testFish.advanceAge();
		}
		assertEquals("lost Fish(2)", testFish.toString());
		assertTrue(testFish.isFullTerm());
	}

	/**
	 * Test method for {@link wator.Pisces#Pisces(wator.Loc, int)}.
	 */
	@Test
	public void testFishLocInt() {
		Fish fish = new Fish(model.getLoc(0, 0), 11);
		assertTrue(fish instanceof Fish);
		assertEquals(model.getLoc(0, 0), fish.getLoc());
	}

	/**
	 * Test method for {@link wator.Pisces#Pisces()}.
	 */
	@Test
	public void testFish() {
		Fish fish = new Fish();
		assertTrue(fish instanceof Fish);
	}

	/**
	 * Test method for {@link wator.Pisces#getLoc()}.
	 */
	@Test
	public void testGetLoc() {
		assertEquals(null, testFish.getLoc());
	}

	/**
	 * Test method for {@link wator.Pisces#setLoc(Loc)}.
	 */
	@Test
	public void testSetLoc() {
		testFish.setLoc(model.getLoc(1, 1));
		assertEquals(model.getLoc(1, 1), testFish.getLoc());
		testFish.setLoc(null);
		assertEquals(null, testFish.getLoc());
	}

	/**
	 * Test method for {@link wator.Pisces#isFullTerm()}.
	 */
	@Test
	public void testIsFullTerm() {
		assertFalse(testFish.isFullTerm());
		for (int i = 0; i < term; i++) {
			assertFalse(testFish.isFullTerm());
			testFish.advanceAge();
		}
		assertEquals("lost Fish(" + term + ")", testFish.toString());
		assertTrue(testFish.isFullTerm());
		testFish.advanceAge();
	}

	/**
	 * Test method for {@link wator.Pisces#isAlive()}.
	 */
	@Test
	public void testIsAlive() {
		assertFalse(testFish.isAlive());
		testFish.setLoc(model.getLoc(0, 0));
		assertFalse(testFish.isAlive());
		model.getLoc(0, 0).setOccupant(testFish);
		assertTrue(testFish.isAlive());
	}

	/**
	 * Test method for {@link wator.Pisces#die()}.
	 */
	@Test
	public void testDie() {
		Fish fish = model.addFish(0, 0, 0);
		Loc loc = model.getLoc(0, 0);
		assertEquals(loc.getOccupant(), fish);
		assertTrue(fish.isAlive());
		fish.die();
		assertFalse(fish.isAlive());
		assertFalse(loc.getOccupant() == fish);

		// make sure that dying in the wrong place doesn't kill another fish
		loc.setOccupant(fish);
		assertTrue(fish.isAlive());
		Fish survivor = model.addFish(0, 0, 0);
		assertFalse(fish.isAlive());
		fish.die();
		assertTrue(survivor.isAlive());
	}

	/**
	 * Test method for {@link wator.Pisces#availableMoves()}.
	 */
	@Test
	public void testAvailableMoves() {
		HashSet<Loc> expected = new HashSet<Loc>();
		assertEquals(expected, new HashSet<Loc>(testFish.availableMoves()));
		Fish swimmer = model.addFish(1, 3, 2);
		Fish wFish = model.addFish(0, 3, 0);
		Fish nFish = model.addFish(1, 2, 0);
		Fish eFish = model.addFish(2, 3, 0);
		Fish sFish = model.addFish(1, 0, 0);
		expected.add(model.getLoc(1, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		expected.clear();
		wFish.die();
		expected.add(model.getLoc(0, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		sFish.die();
		expected.add(model.getLoc(1, 0));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		eFish.die();
		expected.add(model.getLoc(2, 3));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
		nFish.die();
		expected.add(model.getLoc(1, 2));
		assertEquals(expected, new HashSet<Loc>(swimmer.availableMoves()));
	}

	/**
	 * Test method for {@link wator.Pisces#canOccupy(wator.Loc)}.
	 */
	@Test
	public void testCanOccupy() {
		Loc loc = model.getLoc(2, 3);
		Fish swimmer = loc.addFish(0);
		assertTrue(swimmer.canOccupy(loc));
		for (Direction dir : Direction.values()) {
			Loc neighbor = loc.getNeighbor(dir);
			assertTrue(swimmer.canOccupy(neighbor));
			neighbor.addFish(0);
			assertFalse(swimmer.canOccupy(neighbor));
		}
	}

	/**
	 * Test method for {@link wator.Pisces#moveTo(Loc)}.
	 */
	@Test
	public void testMoveTo() {
		// fail if moving to occupied space
		Loc origin = model.getLoc(2, 3);
		Loc destination = model.getLoc(1, 3);
		Loc obstacle = model.getLoc(2, 2);

		Fish swimmer = origin.addFish(0);
		obstacle.addFish(1);
		assertFalse(swimmer.moveTo(obstacle));
		assertEquals("Loc(2,2,Fish(1))", obstacle.toString());

		// succeed if moving to empty space
		assertTrue(swimmer.moveTo(destination));
		assertEquals("Loc(1,3,Fish(0))", destination.toString());
		assertEquals("Loc(2,3,null)", origin.toString());

		// don't give birth if staying in place
		origin = model.getLoc(1, 1);
		swimmer = origin.addFish(term);
		assertTrue(swimmer.moveTo(origin));
		assertEquals("Loc(1,1,Fish(" + term + "))", origin.toString());

		// leave baby behind if moving
		destination = model.getLoc(1, 2);
		assertTrue(swimmer.moveTo(destination));
		assertEquals("Loc(1,2,Fish(0))", destination.toString());
		assertEquals("Loc(1,1,Fish(0))", origin.toString());

		// get eaten by shark - being dead is success but has no effect
		origin = swimmer.getLoc();
		origin.addShark(0, false);
		destination = model.getLoc(0, 2);
		assertTrue(swimmer.moveTo(destination));
		assertEquals("Loc(1,2,Shark(0,10))", origin.toString());
		assertEquals("Loc(0,2,null)", destination.toString());
	}

	/**
	 * Test method for {@link wator.Pisces#advanceAge()}.
	 */
	@Test
	public void testAdvanceAge() {
		for (int i = 1; i < 100; i++) {
			testFish.advanceAge();
			assertEquals("lost Fish(" + i + ")", testFish.toString());
		}
	}

	/**
	 * Test method for {@link wator.Pisces#run()}.
	 */
	@Test
	public void testSwim() {
		// make a path that our test fish must follow
		Fish follower = model.addFish(0, 1, 0);
		model.addFish(1, 0, 0);
		model.addFish(2, 0, 0);
		model.addFish(3, 0, 0);
		model.addFish(1, 2, 0);
		model.addFish(2, 2, 0);
		model.addFish(4, 1, 0);
		model.addFish(3, 3, 0);
		model.addFish(4, 2, 0);

		// chase the fish into a corner
		Fish swimmer = model.addFish(1, 1, 0);
		Loc[] path = { model.getLoc(2, 1), model.getLoc(3, 1),
				model.getLoc(3, 2) };
		int gestation = 0;
		for (Loc destination : path) {
			Loc origin = swimmer.getLoc();
			gestation++;
			swimmer.swim();
			assertEquals(swimmer, destination.getOccupant());
			assertEquals("Loc(" + destination.getX() + "," + destination.getY()
					+ ",Fish(" + gestation + "))", destination.toString());
			assertTrue(follower.moveTo(origin));
		}
		// fish is now trapped, should go nowhere
		swimmer.swim();
		assertEquals(path[2], swimmer.getLoc());

		// back off follower
		assertTrue(follower.moveTo(path[0]));

		// fish should now move back and leave a baby behind
		swimmer.swim();
		assertEquals(path[1], swimmer.getLoc());
		assertEquals("Loc(3,1,Fish(1))", path[1].toString());
		assertEquals("Loc(3,2,Fish(0))", path[2].toString());
	}

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("lost Fish(0)", testFish.toString());
		testFish.setLoc(model.getLoc(0, 0));
		assertEquals("dead Fish(0)", testFish.toString());
		testFish.getLoc().setOccupant(testFish);
		assertEquals("Fish(0)", testFish.toString());
	}

}
