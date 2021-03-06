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

import Game.Animator;
import Game.Game;
import Entities.Pacman.PacmanState;
import Game.Tile;

/*
 * Defines a Purple Ghost instance - a ghost with freezing abilities.
 */
public class PurpleGhost extends Ghost implements ActionListener {
	
	public boolean shot; // Determines if the ghost currently shot.
	protected final int SPEED = 1; // Purple Ghost speed.

	//Constructor
	public PurpleGhost(Game game, Tile position) {
		super(game, position.column, position.row);
		this.hasWeapon = true;
		this.speed = SPEED;
		this.normalSpeed = SPEED;
		this.ammo = new ConcurrentLinkedQueue<Weapon>();
		this.shot = false;
		this.canShoot = false;
		this.animator = new Animator("/images/entity_sprites/purple_ghost.png", Animator.FLICK_DELAY, 1, false, true, false);
		this.shootingDelay = new Timer(1000-Game.difficulty*16, this);
	}
	
	/*
	 * Adds a new ammo to ammo queue by the current pacman location relative to the ghost.
	 */
	public void shoot()
	{
		if(canShoot & game.getPacman().getState() != PacmanState.DIZZY & this.state != GhostState.DIED & this.state != GhostState.FLEE & this.state != GhostState.FROZEN)
		{
			if(game.getPacman().getState() == PacmanState.NORMAL && this.position.row == game.getPacman().position.row || this.position.column == game.getPacman().position.column) {
				if(game.getPacman().getRow() < this.position.row)
					ammo.add(new Spell(this, this.position.column, this.position.row, UP));
				else if (game.getPacman().getRow() > this.position.row)
					ammo.add(new Spell(this, this.position.column, this.position.row, DOWN));
				else if (game.getPacman().getColumn() < this.position.column)
					ammo.add(new Spell(this, this.position.column, this.position.row, LEFT));
				else if (game.getPacman().getColumn() > this.position.column)
					ammo.add(new Spell(this, this.position.column, this.position.row, RIGHT));
				this.shot=true;

				for(Weapon spell : ammo) {
					boolean fired = ((Spell)spell).fired;
					if(!fired & game.getPacman().getState() == PacmanState.NORMAL) {
						spell.shoot();
					}
				}
				this.canShoot=false;
				shootingDelay.restart();
			}
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
		 if (dangerflick & animator.flickFrame)
			 g.drawImage(flick, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		 else if(this.state == GhostState.FLEE) {
			 g.drawImage(flee.sprite.getSubimage((flee.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		 else if(this.state == GhostState.DIED) {
			 g.drawImage(eyes.sprite.getSubimage((eyes.frame/2)*25, 0, 25, 25), column*25+this.animator.dx, row*25+this.animator.dy, 22, 22,null);
		 }
		else {
			g.drawImage(this.animator.sprite, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		}
		if(shot)
			for(Weapon spell : ammo) {
					spell.draw(g);
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
	public void hit(NormalPacman pacman) {
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			this.state = GhostState.DIED;
		}
		else if(this.state != GhostState.DIED) {
			pacman.dizzy();
			this.state = GhostState.RANDOM;
		}
	}

	@Override
	public void hit(EvilPacman pacman) {
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			this.state = GhostState.DIED;
		}
		else if(this.state != GhostState.DIED) {
			pacman.dizzy();
			this.state = GhostState.RANDOM;
		}
	}

	@Override
	public void hit(ShieldedPacman pacman) {
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			this.state = GhostState.DIED;
		}
		else if(this.state != GhostState.DIED) {
			pacman.dizzy();
			this.state = GhostState.RANDOM;
		}
	}

}

