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
import Engine.GameMethods;
import Game.Tile;

/*
 * Defines a spike trap instance used by the commando ghost
 */
public class SpikeTrap implements Weapon, Visitor, ActionListener {

	//Fields
	private Tile position; // Spiketrap location on board
	private Game game; // Relevant game instance
	private Animator animator; // Animates the spiketrap
	private CommandoGhost ghost; // Relevant Commando Ghost instance
	boolean fired; // Determines if the trap is layed
	Timer timer; //Controls disposal time
	private int disposeTimer; // Counts time to disposal
	private boolean exploded = false; //Determined if the trap has exploded
	public static Animator explode; //Animates a trap explosion
	
	//Constructor
	public SpikeTrap(Game game, CommandoGhost ghost, Tile position)
	{
		this.game = game;
		this.ghost = ghost;
		this.position = position;
		this.disposeTimer = 0;
		this.timer = new Timer(50, this);
		this.animator = new Animator("/images/weapons_sprites/spike_trap.png", 50, 7, false, false, false);
		if(explode == null) explode = new Animator("/images/weapons_sprites/explosion.png", 50, 13, false, false, false);
		explode.stopAnimation(true);
		this.fired= true;
		GameMethods.playSound("spike_trap.wav");
		timer.start();
	}
	
	/*
	 * Explodes the current spiek trap instance.
	 */
	public void explode()
	{
		this.exploded = true;
		if (animator.frame == 13) {
			this.dispose();
		}
	}
	
	@Override
	public void shoot() {
		ghost.shot=true;
	}

	/*
	 * Disposes the current spike trap insatnce.
	 */
	@Override
	public void dispose() {
		if(!exploded) GameMethods.playSound("trap_disolve.wav");
		this.ghost.ammo.remove(this);
		timer.stop();
	}
	
	/*
	 * Checks for interaction with pacman
	 */
	public void checkForHit()
	{
		if(this.position.column == game.getPacman().position.column && this.position.row == game.getPacman().position.row) {
			this.dispose();
			game.getPacman().impact(this);
		}
	}

	@Override
	public void draw(Graphics g) {
		if(exploded) g.drawImage(explode.sprite.getSubimage((explode.frame/2)*25, 0, 25, 25), 
				this.position.column*25, this.position.row*25, 25, 25, null);
		else g.drawImage(this.animator.sprite.getSubimage((this.animator.frame/2)*30, 0, 30, 30), 
					this.position.column*25, this.position.row*25, 30, 30, null);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (exploded & explode.frame == 13) ghost.ammo.remove(this);
		else if(exploded) explode.stopAnimation(false);
		checkForHit();
		if(disposeTimer != 0 && disposeTimer == ghost.TRAP_DELAY/10) {
			this.dispose();
			disposeTimer = 0;
		}
		else disposeTimer++;
	}

	//Visitor pattern methods
	@Override
	public void hit(Ghost ghost) {}

	@Override
	public void hit(NormalPacman pacman) {
		if(pacman.state != PacmanState.DEAD) {
			game.decreaseLives();
		}
	}

	@Override
	public void hit(EvilPacman pacman) {
		if(pacman.state != PacmanState.DEAD) {
			game.decreaseLives();
		}
	}

	@Override
	public void hit(ShieldedPacman pacman) {
		if(pacman.state != PacmanState.DEAD) {
			game.decreaseLives();
		}
	}

}
