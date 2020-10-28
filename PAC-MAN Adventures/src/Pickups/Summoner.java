/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import java.awt.Graphics;

import Game.Animator;
import Game.Tile;

/*
 * Summoner fruit instance
 */
public class Summoner extends Collectable {
	
	//Fields
	private Animator animator; // Summoner animator
	private final int SCORE = 5000; // Summoner collected score
	
	//Constructor
	public Summoner(Tile position)
	{
		super(position);
		this.score = SCORE;
		this.animator = new Animator("/images/collectable_sprites/summoner.png", 200, 5, false, false, false);
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(this.animator.sprite.getSubimage((this.animator.frame)*35, 0, 35, 30), position.column*25, position.row*25, 30, 25, null);
	}
}