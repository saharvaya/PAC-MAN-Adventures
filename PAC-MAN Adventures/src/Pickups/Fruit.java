/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import Game.Animator;
import Game.Tile;

/*
 * Abstract class to define an abstract fruit instance 
 */
public abstract class Fruit extends Collectable implements ActionListener {
	
	//Fields
	protected Animator animator; // Fruit animator
	private boolean disolve; // Determines if current fruit needs to be dissolved.
	private static BufferedImage flick; // Fruit flicker image
	protected Timer timer; // Controls fruit flickering time
	
	//Constructor
	public Fruit(Tile position) {
		super(position);
		this.disolve = false;
		if(flick == null)
			try {
				flick =	ImageIO.read(getClass().getResourceAsStream("/images/effects_sprites/flick_gift.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		this.timer = new Timer(2500, this);
	}

	public void paint(Graphics g) {
		if(disolve)
		{
			Graphics2D graphics = (Graphics2D) g.create();
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.animator.opacity));
			((Graphics) graphics).drawImage(this.animator.sprite.getSubimage((this.animator.frame)*25, 0, 25, 25), position.column*25, position.row*25, null);
			graphics.dispose();
		}
		else if (animator.flickFrame)
			g.drawImage(flick, position.column*25, position.row*25, 20, 20, null);
		else {
			g.drawImage(this.animator.sprite.getSubimage((this.animator.frame/2)*25, 0, 25, 25), position.column*25, position.row*25, null);
		}
	}

	//Getters and Setters
	public boolean isDisolve() {
		return disolve;
	}
	
	public void setDisolve(boolean disolve) {
		this.disolve = disolve;
	}

	public Animator getAnimator() {
		return animator;
	}
}
