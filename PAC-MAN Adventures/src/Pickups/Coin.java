/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Game.Tile;

/*
 * Game coin instance
 */
public class Coin extends Collectable {
	
	//Fields
	private BufferedImage coinImage; // Coin image
	private final int SCORE = 10; // Coin collected score
	
	//Constructor
	public Coin(Tile position)
	{
		super(position);
		this.score = SCORE;
		try {
			this.coinImage = ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/coin.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean equals(Coin other) {
		return this.position.equals(other.position);
	}
	
	public void paintCoins(Graphics g)
	{
		g.drawImage(coinImage, position.column*25, position.row*25, 25, 25, null);
	}
}
