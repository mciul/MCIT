/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import wator.Direction;

/**
 * @author mciul
 * 
 */
public class DirectionTest {
	final Direction N = Direction.NORTH;
	final Direction S = Direction.SOUTH;
	final Direction E = Direction.EAST;
	final Direction W = Direction.WEST;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link wator.Direction#getDx()}.
	 */
	@Test
	public void testGetDx() {
		assertEquals(0, N.getDx());
		assertEquals(0, S.getDx());
		assertEquals(1, E.getDx());
		assertEquals(-1, W.getDx());
	}

	/**
	 * Test method for {@link wator.Direction#getDy()}.
	 */
	@Test
	public void testGetDy() {
		assertEquals(-1, N.getDy());
		assertEquals(1, S.getDy());
		assertEquals(0, E.getDy());
		assertEquals(0, W.getDy());
	}

}
