/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import Game.Tile;

/*
 * Defines an abstract collectable item
 */
public abstract class Collectable {

	//Fields
	protected Tile position; // Collectable position
	public int score; // Collectable collect score
	
	//Constructor
	public Collectable(Tile position)
	{
		this.position = position;
	}

	//Getters and Setters
	public Tile getPosition() {
		return position;
	}

	public void setPosition(Tile position) {
		this.position = position;
	}
}
