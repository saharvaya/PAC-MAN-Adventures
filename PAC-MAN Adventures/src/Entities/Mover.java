/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import Game.Animator;
import Game.Game;
import Game.Tile;

/*
 * Abstract class to define basic movement properties for all moving game objects and entities.
 */
public abstract class Mover {
	
	protected Game game; // Current game instance
	protected Tile position; // Current mover position
	protected int speed; // Mover movement speed
	protected int normalSpeed; // The normal speed for the mover if speed changed during runtime
	protected static Animator frozenAnimator; // Frozen state animator
	protected Animator animator; // Mover animator
	protected int requestedDirection, currentDirection; // Current movement direction and requested enxt move direction
	public static final int LEFT=0, UP=1, RIGHT=2, DOWN=3; // Moving directions game constants
	protected static final int directions[] = { LEFT, UP, RIGHT, DOWN }; // Array of moving directions
	protected static final int reversedDirections[] = { RIGHT, DOWN, LEFT, UP }; // Array of the opposite moving directions
	protected static int MOVE_SUCCESS = 1, MOVE_FAIL = 0; // Defines constant to determine if a move is a success or a fail.
	
	//Constructor
	public Mover(Game game)
	{
		this.game=game;
		frozenAnimator = new Animator("/images/effects_sprites/frozen.png", Animator.FLICK_DELAY, 3, false, true, false);
	}
	
	/*
	 * abstract movement methods for all movers to override
	 */
	public abstract void movment();
	public abstract int move(int direction);
	
	/*
	 * Changes the mover speed according to the game current pace control.
	 * @param faster - determines if the pace is faster or normal.
	 */
	public void changeGameSpeed(boolean faster)
	{
		if(faster) this.speed = this.speed*Game.paceController;
		else this.speed = normalSpeed;
	}
	
	//Getters and Setters
	public Animator getAnimator() {
		return animator;
	}
	
	public int getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(int currentDirection) {
		this.currentDirection = currentDirection;
	}

	public Tile getPosition() {
		return position;
	}

}
