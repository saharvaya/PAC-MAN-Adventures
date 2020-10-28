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
 * Bananas fruit instance
 */
public class Bananas extends Fruit {

	//Fields
	private final int SCORE = 400; //Bananas collected score
	
	//Constructor
	public Bananas(Tile position)
	{
		super(position);
		this.score = SCORE;
		this.animator = new Animator("/images/collectable_sprites/bananas.png", Animator.FLICK_DELAY, 0, false, true, false);
		this.animator.flickFrame=false;
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.animator.flicker = false;
		this.animator.flickFrame = false;
		timer.stop();
	}
}
