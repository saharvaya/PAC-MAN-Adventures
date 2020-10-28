/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import java.awt.event.ActionEvent;

import Game.Animator;
import Game.Tile;

/*
 * Pineapple fruit instance
 */
public class Pineapple extends Fruit {

	//Fields
	private final int SCORE = 100; //Pineapple collected score
	
	//Constructor
	public Pineapple(Tile position)
	{
		super(position);
		this.score = SCORE;
		this.animator = new Animator("/images/collectable_sprites/pineapple.png", Animator.FLICK_DELAY, 0, false, true, false);
		this.animator.flickFrame=false;
		this.timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.animator.flicker = false;
		this.animator.flickFrame = false;
		timer.stop();
	}
}
