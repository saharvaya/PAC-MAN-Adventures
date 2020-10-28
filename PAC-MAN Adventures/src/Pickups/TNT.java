/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import java.awt.Graphics;

import Game.Animator;
import Engine.GameMethods;
import Game.Tile;
import Entities.CommandoGhost;
import Entities.SpikeTrap;
import Entities.Weapon;

/*
 * TNT fruit instance
 */
public class TNT extends Collectable {
	
	//Fields
	private Animator animator; // TNT animator
	private final int SCORE = 3000; // TNT collected score
	private CommandoGhost ghost; // Relevant Commando ghost instance
	
	//Cosntructor
	public TNT(Tile position, CommandoGhost ghost)
	{
		super(position);
		this.ghost = ghost;
		this.score = SCORE;
		this.animator = new Animator("/images/collectable_sprites/TNT.png", 200, 3, false, false, false);
	}
	
	/*
	 * Explode all current spike traps instances if collected
	 */
	public void explodeTraps()
	{
		GameMethods.playSound("explode_traps.wav");
		for(Weapon trap : ghost.getAmmo())
		{
			SpikeTrap spiketrap = (SpikeTrap) trap;
			spiketrap.explode();
		}
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(this.animator.sprite.getSubimage((this.animator.frame)*25, 0, 25, 25), position.column*25, position.row*25, 22, 22, null);
	}
}
