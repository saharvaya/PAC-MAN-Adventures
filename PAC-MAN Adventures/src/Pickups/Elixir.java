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
 * Defines a collectable Elixir instance
 */
public class Elixir extends Collectable {

	//Fields
	private Animator animator; // Elixir animator
	private final int SCORE = 10; // Elixir colelcted score
	
	//Constructor
	public Elixir(Tile position)
	{
		super(position);
		this.score = SCORE;
		this.animator = new Animator("/images/collectable_sprites/elixir.png", 150, 3, false, false, false);
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(this.animator.sprite.getSubimage((this.animator.frame)*25, 0, 25, 25), position.column*25, position.row*25, null);
	}
}