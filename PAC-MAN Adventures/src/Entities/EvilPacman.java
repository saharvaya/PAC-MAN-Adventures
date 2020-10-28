/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import Game.Animator;
import Game.Game;

/*
 * Defines a evil pacman instance
 */
public class EvilPacman extends Pacman implements Visitable {

	//Constructor
	public EvilPacman(Game game) {
		super(game);
	}
	
	public void setAnimator()
	{
		this.animator = new Animator("/images/entity_sprites/pacman_evil.png", 50, 7, false, false, false);
	}
	
	//Visitor pattern methods
	@Override
	public void impact(Visitor visitor) {
		if(this.state != PacmanState.DEAD)
			visitor.hit(this);
	}	
}
