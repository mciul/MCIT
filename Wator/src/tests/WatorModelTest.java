/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import org.junit.After;
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
public class WatorModelTest {
	WatorModel model;
	int width;
	int height;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		width = 5;
		height = 4;
		model = new WatorModel(width, height);
	}

	/**
	 * Test method for {@link wator.WatorModel#WatorModel(int, int)}.
	 */
	@Test
	public void testWatorModel() {
		WatorModel model = new WatorModel(10, 10);
		assertTrue(model instanceof WatorModel);
	}

	/**
	 * Test method for {@link wator.WatorModel#getAge()}.
	 */
	@Test
	public void testGetAge() {
		assertEquals(0, model.getAge());
	}

	/**
	 * Test method for {@link wator.WatorModel#getLoc(int, int)}.
	 */
	@Test
	public void testGetLocIntInt() {
		Loc lastLoc = model.getLoc(4, 3);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 5; x++) {
				Loc actual = model.getLoc(x, y);
				assertTrue(actual instanceof Loc);
				assertFalse(lastLoc==actual);
				lastLoc = actual;
			}
		}
	}

	/**
	 * Test method for
	 * {@link wator.WatorModel#getLoc(wator.Loc, wator.Direction)}.
	 */
	@Test
	public void testGetLocLocDirection() {
		Loc origin = model.getLoc(0, 0);
		assertEquals(model.getLoc(width-1, 0), model.getLoc(origin, Direction.WEST));
		assertEquals(model.getLoc(1, 0), model.getLoc(origin, Direction.EAST));
		assertEquals(model.getLoc(0, height-1), model.getLoc(origin, Direction.NORTH));
		assertEquals(model.getLoc(0, 1), model.getLoc(origin, Direction.SOUTH));
		
		origin = model.getLoc(1, 2);
		assertEquals(model.getLoc(0, 2), model.getLoc(origin, Direction.WEST));
		assertEquals(model.getLoc(2, 2), model.getLoc(origin, Direction.EAST));
		assertEquals(model.getLoc(1, 1), model.getLoc(origin, Direction.NORTH));
		assertEquals(model.getLoc(1, 3), model.getLoc(origin, Direction.SOUTH));

		origin = model.getLoc(width-1, height-1);
		assertEquals(model.getLoc(width-2, height-1), model.getLoc(origin, Direction.WEST));
		assertEquals(model.getLoc(0, height-1), model.getLoc(origin, Direction.EAST));
		assertEquals(model.getLoc(width-1, height-2), model.getLoc(origin, Direction.NORTH));
		assertEquals(model.getLoc(width-1, 0), model.getLoc(origin, Direction.SOUTH));
	}

	/**
	 * Test method for {@link wator.WatorModel#animate()}.
	 */
	@Test
	public void testAnimate() {
		Fish fish1 = model.addFish(0, 0, 0);
		Shark shark1 = model.addShark(2, 2, 0, false);
		model.animate();
		assertTrue(Pisces.waitForAll());
		assertFalse(model.getLoc(0, 0).getOccupant()==fish1);
		assertFalse(model.getLoc(2,2).getOccupant()==shark1);
	}

	/**
	 * Test method for {@link wator.WatorModel#addFish(int, int, int)}
	 */
	@Test
	public void testAddFish() {
		Pisces occupant = model.addFish(0, 2, 11);
		assertEquals(occupant, model.getLoc(0, 2).getOccupant());
		assertTrue(occupant instanceof Fish);
		assertEquals("Fish(11)", occupant.toString());
		assertEquals(model.getLoc(0, 2), occupant.getLoc());
	}

	/**
	 * Test method for {@link wator.WatorModel#addShark(int, int, int, boolean)}
	 */
	@Test
	public void testAddShark() {
		Pisces occupant = model.addShark(0, 2, 11, false);
		assertEquals(occupant, model.getLoc(0, 2).getOccupant());
		assertTrue(occupant instanceof Shark);
		assertEquals("Shark(11,10)", occupant.toString());
		assertEquals(model.getLoc(0, 2), occupant.getLoc());
	}
}
