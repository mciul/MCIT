/**
 * 
 */
package tests;

import static org.junit.Assert.*;

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
public class LocTest {
	WatorModel model;
	int width;
	int height;
	Loc testLoc;
	
	final private Direction N = Direction.NORTH;
	final private Direction S = Direction.SOUTH;
	final private Direction E = Direction.EAST;
	final private Direction W = Direction.WEST;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		width = 5;
		height = 4;
		model = new WatorModel(width, height);
		testLoc = new Loc(0, 0, model);
	}

	/**
	 * Test method for {@link wator.Loc#Loc(int, int, wator.WatorModel)}.
	 */
	@Test
	public void testLoc() {
		Loc loc = new Loc(0, 0, model);
		assertTrue(loc instanceof Loc);
	}

	/**
	 * Test method for {@link wator.Loc#getX()}.
	 */
	@Test
	public void testGetX() {
		for (int x = 0; x < width; x++) {
			Loc loc = new Loc(x, 0, model);
			assertEquals(x, loc.getX());
		}
	}

	/**
	 * Test method for {@link wator.Loc#getY()}.
	 */
	@Test
	public void testGetY() {
		for (int y = 0; y < height; y++) {
			Loc loc = new Loc(0, y, model);
			assertEquals(y, loc.getY());
		}
	}

	/**
	 * Test method for {@link wator.Loc#getOccupant()}.
	 */
	@Test
	public void testGetOccupant() {
		assertEquals(null, testLoc.getOccupant());
	}

	/**
	 * Test method for {@link wator.Loc#setOccupant(wator.Pisces)}.
	 */
	@Test
	public void testSetOccupant() {
		int observers = 0;
		assertEquals(observers, model.countObservers());
		testLoc.setOccupant(new Fish());
		assertEquals(++observers, model.countObservers());
		assertTrue(testLoc.getOccupant() instanceof Fish);
		testLoc.setOccupant(new Shark());
		assertEquals(observers, model.countObservers());
		assertTrue(testLoc.getOccupant() instanceof Shark);
		testLoc.setOccupant(null);
		assertEquals(--observers, model.countObservers());
		assertEquals(null, testLoc.getOccupant());
	}

	/**
	 * Test method for {@link wator.Loc#sortLocs(wator.Loc)}.
	 */
	@Test
	public void testSortLocs() {
		Loc[] locOrder = { new Loc(0, 0, model), new Loc(1, 0, model),
				new Loc(3, 0, model), new Loc(0, 1, model),
				new Loc(2, 1, model), new Loc(4, 1, model),
				new Loc(1, 2, model), new Loc(3, 2, model),
				new Loc(4, 2, model), new Loc(0, 3, model),
				new Loc(1, 3, model), new Loc(4, 3, model) };
		for (int i = 0; i < locOrder.length; i++) {
			for (int j = i; j < locOrder.length; j++) {
				Loc[] correctOrder = { locOrder[i], locOrder[j] };
				assertArrayEquals(correctOrder,
						locOrder[i].sortLocs(locOrder[j]));
				assertArrayEquals(correctOrder,
						locOrder[j].sortLocs(locOrder[i]));
			}
		}
	}

	/**
	 * Test method for {@link wator.Loc#addFish(int)}
	 */
	@Test
	public void testAddFish() {
		Pisces occupant = testLoc.addFish(11);
		assertEquals(occupant, testLoc.getOccupant());
		assertTrue(occupant instanceof Fish);
		assertEquals("Fish(11)", occupant.toString());
		assertEquals(testLoc, occupant.getLoc());
	}

	/**
	 * Test method for {@link wator.Loc#addShark(int, boolean)}
	 */
	@Test
	public void testAddShark() {
		Pisces occupant = testLoc.addShark(11, false);
		assertEquals(occupant, testLoc.getOccupant());
		assertTrue(occupant instanceof Shark);
		assertEquals("Shark(11,10)", occupant.toString());
		assertEquals(testLoc, occupant.getLoc());
	}

	/**
	 * Test method for {@link wator.Loc#getNeighbor(Direction)}
	 */
	@Test
	public void testGetNeighbor() {
		assertEquals(model.getLoc(width - 1, 0), testLoc.getNeighbor(W));
		assertEquals(model.getLoc(1, 0), testLoc.getNeighbor(E));
		assertEquals(model.getLoc(0, height - 1), testLoc.getNeighbor(N));
		assertEquals(model.getLoc(0, 1), testLoc.getNeighbor(S));

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Loc origin = model.getLoc(x, y);
				assertEquals(origin, origin.getNeighbor(N).getNeighbor(S));
				assertEquals(origin, origin.getNeighbor(S).getNeighbor(N));
				assertEquals(origin, origin.getNeighbor(W).getNeighbor(E));
				assertEquals(origin, origin.getNeighbor(E).getNeighbor(W));
			}
		}
	}

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("Loc(0,0,null)", testLoc.toString());
		testLoc.addFish(3);
		assertEquals("Loc(0,0,Fish(3))", testLoc.toString());
		testLoc.addFish(2);
		assertEquals("Loc(0,0,Fish(2))", testLoc.toString());
		Loc otherLoc = new Loc(4, 3, model);
		assertEquals("Loc(4,3,null)", otherLoc.toString());
	}

}
