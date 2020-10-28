/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import java.awt.Graphics;
import java.util.concurrent.ConcurrentLinkedQueue;

import Game.Animator;
import Game.Game;
import Entities.Pacman.PacmanState;
import Engine.GameMethods;
import Game.Tile;

/*
 * Defines a Green Ghost instance - a ghost with poisoning abilities abilities.
 */
public class GreenGhost extends Ghost  {
	
	//Fields
	public boolean shot; // Determines if the ghost currently shot.
	protected final int SPEED = 2; // Green Ghost speed.
	private Animator poisonGas; // The poison gas animator
	private boolean releasedPosion=false; //Determines if currently released poison

	//Constructor
	public GreenGhost(Game game, Tile position) {
		super(game, position.column, position.row);
		this.hasWeapon = false;
		this.speed = SPEED;
		this.normalSpeed = SPEED;
		this.ammo = new ConcurrentLinkedQueue<Weapon>();
		this.animator = new Animator("/images/entity_sprites/green_ghost.png", Animator.FLICK_DELAY, 1, false, true, false);
		this.poisonGas = new Animator("/images/weapons_sprites/poison_gas.png", 80, 13, false, true, false);
		poisonGas.stopAnimation(true);
	}	

	@Override
	public void paint(Graphics g) {
		 if(this.state == GhostState.FLEE) {
			 g.drawImage(flee.sprite.getSubimage((flee.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		 else if(this.state == GhostState.DIED) {
			 g.drawImage(eyes.sprite.getSubimage((eyes.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		else {
			g.drawImage(this.animator.sprite, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		}
		 if(this.state == GhostState.CAGED)
			 g.drawImage(sleeping.sprite.getSubimage((sleeping.frame/2)*25, 0, 25, 25), column*25, row*25-10, 22, 17, null);
		 if(this.state != GhostState.FLEE && initialMovementCounter<2 & initialMovementTimer.isRunning()) 
			 g.drawImage(confused.sprite.getSubimage((confused.frame/2)*20, 0, 20, 20), column*25+2, row*25-12, 15, 15, null);
		 if(releasedPosion & poisonGas.frame != 13) {
			 g.drawImage(this.poisonGas.sprite.getSubimage((this.poisonGas.frame/2)*45, 0, 45, 45), 
					 game.getPacman().position.column*25+game.getPacman().getAnimator().dx, game.getPacman().position.row*25+game.getPacman().getAnimator().dy, 45, 45, null);
		 }
		 else if(poisonGas.frame == 13) poisonGas.stopAnimation(true);
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
			pacman.eatGhost(this);
		}
		else if(pacman.getState() != PacmanState.POISONED & this.state != GhostState.FLEE & this.state != GhostState.DIED) {
			this.poisonGas.frame = 0;
			poisonGas.stopAnimation(false);
			releasedPosion = true;
			game.getLevel().generateElixir();
			GameMethods.playSound("poison_gas.wav");
			pacman.setState(PacmanState.POISONED);
			pacman.poisonTimer.start();
			this.state = GhostState.SCATTER;
		}
		else if(pacman.state == PacmanState.POISONED & this.state != GhostState.FLEE & this.state != GhostState.DIED) {
				this.state = GhostState.RANDOM;
		}
	}

	@Override
	public void hit(EvilPacman pacman) {
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			pacman.eatGhost(this);
		}
		else if(pacman.getState() != PacmanState.POISONED & this.state != GhostState.FLEE & this.state != GhostState.DIED) {
			this.poisonGas.frame = 0;
			poisonGas.stopAnimation(false);
			releasedPosion = true;
			game.getLevel().generateElixir();
			GameMethods.playSound("poison_gas.wav");
			pacman.setState(PacmanState.POISONED);
			pacman.poisonTimer.start();
			this.state = GhostState.SCATTER;
		}
		else if(pacman.state == PacmanState.POISONED & this.state != GhostState.FLEE & this.state != GhostState.DIED) {
				this.state = GhostState.RANDOM;
		}
	}

	@Override
	public void hit(ShieldedPacman pacman) {
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			pacman.eatGhost(this);
		}
		else if(pacman.getState() != PacmanState.POISONED & this.state != GhostState.FLEE & this.state != GhostState.DIED) {
			this.poisonGas.frame = 0;
			poisonGas.stopAnimation(false);
			releasedPosion = true;
			game.getLevel().generateElixir();
			GameMethods.playSound("poison_gas.wav");
			pacman.setState(PacmanState.POISONED);
			pacman.poisonTimer.start();
			this.state = GhostState.SCATTER;
		}
		else if(pacman.state == PacmanState.POISONED & this.state != GhostState.FLEE & this.state != GhostState.DIED) {
				this.state = GhostState.RANDOM;
		}
	}

}