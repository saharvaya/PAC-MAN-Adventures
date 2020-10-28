/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import Game.Animator;
import Game.Tile;
import java.awt.Graphics;

/*
 * Defines an Energy Pill instance
 */
public class EnergyPill extends Collectable {

	//Fields
	private Animator animator; // Energy pill animator
	private final int SCORE = 50; // Energy pill collected score
	
	//Constructor
	public EnergyPill(Tile position)
	{
		super(position);
		this.score = SCORE;
		this.animator = new Animator("/images/collectable_sprites/energypill.png", 200, 3, false, false, false);
	}
	
	public void paintPill(Graphics g)
	{
		g.drawImage(this.animator.sprite.getSubimage((this.animator.frame)*25, 0, 25, 25), position.column*25, position.row*25, null);
	}
}
