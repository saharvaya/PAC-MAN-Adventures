/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Game.Animator;
import Game.Game;
import Entities.Pacman.PacmanState;
import Game.Tile;

/*
 * Defines a Yellow Ghost instance - an ordinary ghost
 */
public class YellowGhost extends Ghost implements Visitable, Visitor, ActionListener {
	
	//Fields
	protected final int SPEED = 2; // Yellow Ghost speed.
	private Timer disapearTimer; // Controls the time a yellow ghost disappears

	//Constructor
	public YellowGhost(Game game, Tile position) {
		super(game, position.column, position.row);
		this.hasWeapon = false;
		this.speed = SPEED;
		this.normalSpeed = SPEED;
		this.disapearTimer = new Timer(5000, this);
		this.animator = new Animator("/images/entity_sprites/yellow_ghost.png", Animator.FLICK_DELAY, 1, false, false, false);
	}

	@Override
 	public void paint(Graphics g) {
		 if(this.state == GhostState.FLEE) {
			 g.drawImage(flee.sprite.getSubimage((flee.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		 else if(this.state == GhostState.DIED) {
			 g.drawImage(eyes.sprite.getSubimage((eyes.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		 else if(this.state ==GhostState.DISAPEAR)
			 this.disapear(g);
		 else g.drawImage(this.animator.sprite, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		 
		 if(this.state == GhostState.CAGED)
			 g.drawImage(sleeping.sprite.getSubimage((sleeping.frame/2)*25, 0, 25, 25), column*25, row*25-10, 22, 17, null);
		 if(this.state != GhostState.FLEE && initialMovementCounter<2 & initialMovementTimer.isRunning()) 
			 g.drawImage(confused.sprite.getSubimage((confused.frame/2)*20, 0, 20, 20), column*25+2, row*25-12, 15, 15, null);
	}

	
	@Override
	public void normalSpeed() {
		this.speed = SPEED*Game.paceController;	
	}

	//Visitor pattern methods
	public void hit(Ghost ghost) {}

	public void hit(EvilPacman pacman) {
		if(this.state != GhostState.DIED) {
			animator.resetOffset();
			pacman.eatGhost(this);
		}
	}

	public void hit(ShieldedPacman pacman) {
		if(pacman.getState() != PacmanState.DEAD) {
			if(this.state == GhostState.FLEE)
			{
				animator.resetOffset();
				pacman.eatGhost(this);
			}
			else if(this.state == GhostState.DISAPEAR) {} //DO NOTHING	
			 else if(this.state != GhostState.DIED) {
				 this.state = GhostState.DISAPEAR; 
				 this.disapearTimer.start();
			 }
		}
	}

	public void hit(NormalPacman pacman) {
		if(pacman.getState() != PacmanState.DEAD) {
			if(this.state == GhostState.FLEE)
			{
				animator.resetOffset();
				pacman.eatGhost(this);
			}
			else if(this.state != GhostState.DIED) game.decreaseLives();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.state = GhostState.SCATTER;
		this.disapearTimer.restart();
		this.disapearTimer.stop();
	}
}

