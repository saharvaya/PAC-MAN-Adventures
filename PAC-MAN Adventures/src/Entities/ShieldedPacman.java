/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import Game.Animator;
import Game.Game;

/*
 * Defines a shilded pacman instance
 */
public class ShieldedPacman extends Pacman implements Visitable {

	public ShieldedPacman(Game game) {
		super(game);
	}
	
	public void setAnimator() {
		this.animator = new Animator("/images/entity_sprites/pacman_shielded.png", 50, 7, false, false, false);
	}

	@Override
	public void impact(Visitor visitor) {
		if(this.state != PacmanState.DEAD)
			visitor.hit(this);
	}	
}
