/**
 * 
 */
package wator;

/**
 * @author mciul
 *
 */
public enum Direction {
	NORTH, EAST, SOUTH, WEST;
	
	/**
	 * Get the x offset of each direction
	 * @return   0 if direction is vertical, 1 if east(right), -1 if west(left)
	 */
	public int getDx() {
		if (this==EAST) {
			return 1;
		}
		if (this==WEST) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * Get the y offset of each direction
	 * @return   0 if direction is horizontal, -1 if north(up), 1 if south(down)
	 */
	public int getDy() {
		if (this==NORTH) {
			return -1;
		}
		if (this==SOUTH) {
			return 1;
		}
		return 0;
	}
}
