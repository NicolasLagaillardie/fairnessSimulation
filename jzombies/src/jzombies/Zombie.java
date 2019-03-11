/**
 * 
 */
package jzombies;

/**
 * @author Lagai
 *
 */
public class Zombie {
	
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public Zombie(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
}
