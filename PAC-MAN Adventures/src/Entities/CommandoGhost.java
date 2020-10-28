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
import Engine.GameMethods;
import Game.Game;
import Game.Tile;
import Pickups.TNT;

/*
 * Defines a Commando Ghost instance - a ghost with trapping abilities.
 */
public class CommandoGhost extends Ghost implements ActionListener {
	
	//Fields
	public boolean shot; // Determines if the ghost currently shot.
	protected final int SPEED = 1; // Commando Ghost speed.
	final int TRAP_DELAY = 10000-Game.difficulty*10; // Defines the time interval to lay traps in.
	private final int TNT_DROP_BY_TRAP_DELAY = 8; // Define the tiem to drop tnt.
	private int tntDrop = 0; 
	private TNT tnt; // Current tnt instance.

	//Constructor
	public CommandoGhost(Game game, Tile position) {
		super(game, position.column, position.row);
		this.hasWeapon = true;
		this.speed = SPEED;
		this.normalSpeed = SPEED;
		this.ammo = new ConcurrentLinkedQueue<Weapon>();
		this.canShoot = false;
		this.animator = new Animator("/images/entity_sprites/commando_ghost.png", Animator.FLICK_DELAY, 1, false, true, false);
		this.shootingDelay = new Timer(TRAP_DELAY, this);
	}
	
	/*
	 * Adds a new ammo to ammo queue by the current pacman location relative to the ghost.
	 */
	public void shoot()
	{
		if(canShoot & this.state != GhostState.DIED & this.state != GhostState.FLEE)
		{
			this.ammo.add(new SpikeTrap(game, this, this.position));
			this.shot = true;
			for(Weapon spikeTrap : ammo) {
				boolean fired = ((SpikeTrap)spikeTrap).fired;
				if(!fired) {
					spikeTrap.shoot();
				}
			}
			this.canShoot=false;
			shootingDelay.restart();
		}
	}

	public void actionPerformed(ActionEvent e) {
		boolean releasedTNT = false;
		tntDrop++;
		if(this.tnt == null && tntDrop % TNT_DROP_BY_TRAP_DELAY == 0) {
			dropTNT();
			releasedTNT=true;
		}
		else if(this.tnt != null && tntDrop == 2) {
			this.tnt = null;
			game.getLevel().disposeTNT(false);
		}
		if(this.state != GhostState.FLEE) {
			if(this.state == GhostState.CHASE)
				this.state = GhostState.RANDOM;
			this.canShoot = !canShoot;
			if(!releasedTNT) shoot();
		}
	}

	/*
	 * Sets a new TNT instance in the current level.
	 */
	private void dropTNT() {
		tntDrop=0;
		GameMethods.playSound("tnt_drop.wav");
		this.tnt = new TNT(this.position, this);
		game.getLevel().setTnt(tnt);
	}

	@Override
	public void paint(Graphics g) {
		 if (dangerflick & this.state != GhostState.DIED & animator.flickFrame)
			 g.drawImage(flick, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		 else if(this.state == GhostState.FLEE) {
			 g.drawImage(flee.sprite.getSubimage((flee.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		 else if(this.state == GhostState.DIED) {
			 g.drawImage(eyes.sprite.getSubimage((eyes.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 22, 22,null);
		 }
		else {
			g.drawImage(this.animator.sprite, column*25+animator.dx, row*25+animator.dy, 22, 22, null);
		}
		if(shot)
			for(Weapon spikeTrap : ammo) {
					spikeTrap.draw(g);
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
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			pacman.eatGhost(this);
		}
		else shoot();
	}

	@Override
	public void hit(ShieldedPacman pacman) {
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			pacman.eatGhost(this);
		}
		else shoot();
	}

	@Override
	public void hit(NormalPacman pacman) {
		if(this.state == GhostState.FLEE)
		{
			animator.resetOffset();
			pacman.eatGhost(this);
		}
		else shoot();
		
	}

}


