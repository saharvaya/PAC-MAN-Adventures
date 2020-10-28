/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import Game.Animator;
import Game.Game;

/*
 * Defines a normal pacman instance
 */
public class NormalPacman extends Pacman implements Visitable {

	public NormalPacman(Game game) {
		super(game);
	}
	
	public void setAnimator()
	{
		this.animator = new Animator("/images/entity_sprites/pacman_normal.png", 50, 7, false, false, false);
	}
	
	//Visitor pattern methods
	public void impact(Visitor visitor) {
		if(this.state != PacmanState.DEAD)
			visitor.hit(this);
	}
}
