/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import java.awt.Graphics;

/*
 * An interface to define properties and methods to a game weapon
 */
public interface Weapon {
	
	public boolean fired = false;
	public void shoot();
	public void checkForHit();
	public void dispose();
	public void draw(Graphics g);
}
