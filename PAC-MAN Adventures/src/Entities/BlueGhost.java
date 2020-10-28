/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.Timer;

import Engine.GameMethods;
import Entities.Pacman.PacmanState;
import Game.Animator;
import Game.Game;
import Game.Tile;

/*
 * Defines a Blue Ghost instance - a ghost with freezing abilities.
 */
public class BlueGhost extends Ghost implements ActionListener {
	
	//Fields
	public boolean shot; // Determines if the ghost currently shot.
	protected final int SPEED = 2; // Blue Ghost speed.
	Timer freezeTimer; // Timer to determine the ghost freezing time.

	//Constructor
	public BlueGhost(Game game, Tile position) {
		super(game, position.column, position.row);
		this.hasWeapon = true;
		this.speed = SPEED;
		this.normalSpeed = SPEED;
		this.ammo = new ConcurrentLinkedQueue<Weapon>();
		this.shot = false;
		this.canShoot = false;
		this.animator = new Animator("/images/entity_sprites/blue_ghost.png", Animator.FLICK_DELAY, 1, false, true, false);
		this.shootingDelay = new Timer(700-Game.difficulty*16, this);
		this.freezeTimer = new Timer(3000, new freezeListner(this));
	}
	
	/*
	 * Adds a new ammo to ammo queue by the current pacman location relative to the ghost.
	 */
	public void shoot()
	{
		if(canShoot & this.state != GhostState.DIED & this.state != GhostState.DISAPEAR & this.state != GhostState.FLEE & this.state != GhostState.FROZEN 
				& game.getPacman().getState() != PacmanState.FREEZE & game.getPacman().getState() != PacmanState.POISONED)
		{
			if(game.getPacman().getState() == PacmanState.NORMAL & this.position.row == game.getPacman().position.row || this.position.column == game.getPacman().position.column) {	
				if(game.getPacman().getRow() < this.position.row)
					ammo.add(new WaterSplash(this, this.position.column, this.position.row, UP));
				else if (game.getPacman().getRow() > this.position.row)
					ammo.add(new WaterSplash(this, this.position.column, this.position.row, DOWN));
				else if (game.getPacman().getColumn() < this.position.column)
					ammo.add(new WaterSplash(this, this.position.column, this.position.row, LEFT));
				else if (game.getPacman().getColumn() > this.position.column)
					ammo.add(new WaterSplash(this, this.position.column, this.position.row, RIGHT));
				this.shot=true;
			}
				for(Weapon watersplash : ammo) {
					boolean fired = ((WaterSplash)watersplash).fired;
					if(!fired & game.getPacman().getState() == PacmanState.NORMAL) {
						watersplash.shoot();
					}
				}
				this.canShoot=false;
				shootingDelay.restart();
		}
	}
	
	/*
	 * Defines freezing status for the current ghost.
	 */
	public void freeze() {
		if(state != GhostState.FROZEN) {
			this.state = GhostState.FROZEN;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(this.state != GhostState.FLEE) {
			this.canShoot = !canShoot;
			shoot();
		}
	}

	@Override
	public void paint(Graphics g) {
		 if (dangerflick & animator.flickFrame & this.state != GhostState.DIED)
				g.drawImage(flick, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		 else if(this.state == GhostState.FLEE) {
			 g.drawImage(flee.sprite.getSubimage((flee.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		 else if(this.state == GhostState.DIED) {
			 g.drawImage(eyes.sprite.getSubimage((eyes.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		 else if(this.state == GhostState.FROZEN) {
			 		g.drawImage(this.animator.sprite, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
					g.drawImage(Mover.frozenAnimator.sprite.getSubimage((Mover.frozenAnimator.frame/2)*25, 0, 25, 25), column*25+animator.dx-3, row*25+animator.dy-3, 30, 30, null);
			}
		else {
			g.drawImage(this.animator.sprite, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		}
		if(shot)
			for(Weapon watersplash : ammo) {
					watersplash.draw(g);
			}
		
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
	@Override
	public void hit(Ghost ghost) {}

	@Override
	public void hit(EvilPacman pacman) {
		if(this.state != GhostState.DIED & this.state != GhostState.FLEE & this.state != GhostState.FROZEN & game.getPacman().getState() != PacmanState.FREEZE) {
			GameMethods.playSound("freeze.wav");
			this.freeze();
			this.freezeTimer.start();
		}
		else if(this.state != GhostState.DIED & this.state == GhostState.FLEE) {
			animator.resetOffset();
			pacman.eatGhost(this);
		}
	}

	@Override
	public void hit(ShieldedPacman pacman) {
		if(this.state != GhostState.DIED) {
			if(this.state == GhostState.FLEE)
			{
				animator.resetOffset();
				pacman.eatGhost(this);
			}
			else if(game.getPacman().getState() == PacmanState.NORMAL) {
				pacman.freeze();	
				this.state = GhostState.RANDOM;
			}
			else this.state = GhostState.RANDOM;
		}
	}

	@Override
	public void hit(NormalPacman pacman) {
		if(this.state != GhostState.DIED) {
			if(this.state == GhostState.FLEE)
			{
				animator.resetOffset();
				pacman.eatGhost(this);
			}
			else if(game.getPacman().getState() == PacmanState.NORMAL) {
				pacman.freeze();	
				this.state = GhostState.RANDOM;
			}
			else this.state = GhostState.RANDOM;
		}
	}
	
	/*
	 * Private class the control the freeze ghost status.
	 */
	private class freezeListner implements ActionListener{

		private BlueGhost ghost;
		
		public freezeListner(BlueGhost g)
		{
			this.ghost=g;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			this.ghost.state = GhostState.RANDOM;
			freezeTimer.restart();
			freezeTimer.stop();
		}
	}

}
