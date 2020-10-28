/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import Engine.GameMethods;
import Engine.ResourceLoader;
import Pickups.Fruit;

/*
 * Game bottom details panel
 */
public class HUD extends JPanel {
	
	//Fields
	private Game game; // Relevant game instance
	public final static Dimension HUD_SIZE = new Dimension(Game.WIDTH, Game.GAME_SIZE.height-Game.HEIGHT-50); // HUD dimensions
	private BufferedImage GAME_LOGO = null; // Game logo to appear in the HUD
	private BufferedImage lives; // Game lives image
	
	//Constructor
	public HUD(Game game)
	{
		this.game = game;
		try 
		{
			this.GAME_LOGO = ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/logo2_hud.png"));
			this.lives = ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/lives.png"));
		}
		catch(Exception e) {}
		
		this.setBorder(new LineBorder(Color.BLACK, 8));
		this.setPreferredSize(HUD_SIZE);
		this.setMinimumSize(HUD_SIZE);
		this.setMaximumSize(HUD_SIZE);
		this.setSize(HUD_SIZE);
		this.setDoubleBuffered(true);
		this.setFocusable(false);
		
		this.setLayout(new GridLayout(1, 2));
	}
	
	/*
	 * Paints HUD
	 */
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(GAME_LOGO, this.getWidth()- GAME_LOGO.getWidth()-45, 30, GAME_LOGO.getWidth()+20, GAME_LOGO.getHeight()+5, null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Consolas", Font.BOLD, 11));
	
		g.drawString("By Itay Bouganim & Sahar Vaya", this.getWidth()- GAME_LOGO.getWidth()-50, 20);
		g.setFont(GameMethods.font(10));
		g.setColor(Color.ORANGE);
		g.drawString("PACMAN MOOD: ", this.getWidth()- GAME_LOGO.getWidth()-75 , GAME_LOGO.getHeight()+55);
		g.setColor(Color.WHITE);
		g.drawString(game.getPacmanMood(), this.getWidth()- GAME_LOGO.getWidth()+55 , GAME_LOGO.getHeight()+55);
		
		g.setColor(Color.YELLOW);
		g.setFont(GameMethods.font(20));
		g.drawString("SCORE", 220, 40);
		g.setColor(Color.WHITE);
		g.drawString(GameMethods.getPrintedScore(null), 205, 70);
		
		g.setColor(Color.ORANGE);
		g.setFont(GameMethods.font(10));
		g.drawString("HIGHEST:", 200, 90);
		g.setColor(Color.WHITE);
		g.drawString(GameMethods.getPrintedScore(ResourceLoader.highestScores.peek().score), 290, 90);
		
		g.setColor(Color.YELLOW);
		g.setFont(GameMethods.font(20));
		g.drawString("LIVES", 20, 40);
		printLives(g);
		
		g.setFont(GameMethods.font(16));
		g.drawString("STATUS", 420, 35);
		g.setColor(Color.WHITE);
		g.drawString(game.getPacman().getStatus(), 420, 60);
		
		g.setFont(GameMethods.font(16));
		g.setColor(Color.YELLOW);
		g.drawString("LEVEL:", 420, 90);
		g.setColor(Color.WHITE);
		g.drawString(game.getLevel().levelNumber+"", 520, 90);
		
		g.setFont(GameMethods.font(8));
		g.drawString("COLLECTED:", 20, 98);
		printFruits(g);
		
		g.setFont(GameMethods.font(12));
		g.setColor(Color.YELLOW);
		g.drawString("GAME TIME:", 640, 109);
		printClock(g);
	}
	
	/*
	 * Paints game clock
	 */
	private void printClock(Graphics g)
	{
		int seconds = GameTimer.clockSeconds;
		int minutes = GameTimer.clockMinutes;
		int hours = GameTimer.clockHours;
		String secondsText = (seconds<10)? ": 0"+seconds : ": "+seconds;
		String minutesText = (minutes<10)? ": 0"+minutes : ": "+minutes;
		String hoursText = (hours<10)? " 0"+hours : " "+hours;
		
		g.setFont(GameMethods.font(10));
		g.setColor(Color.WHITE);
		g.drawString(secondsText, 720, 125);
		g.drawString(minutesText, 670, 125);
		g.drawString(hoursText, 630, 125);
	}
	
	/*
	 * paints game lives
	 */
	private void printLives(Graphics g) {
		for(int i=0; i<Game.lives ; i++)
		{
			int indent = ((lives.getWidth()+10)*i);
			g.drawImage(lives, 20+indent, 50, lives.getWidth()-1, lives.getHeight()-1, null);
		}
	}
	
	/*
	 * Paints fruits colelcted
	 */
	private void printFruits(Graphics g) {
		int index = 0;
		int excess = 0;	
		int fruitPrintLength = game.getPacman().getCollectedFruits().size()*25;
		int allowedSize = Game.WIDTH-GAME_LOGO.getWidth()-200;
		if(fruitPrintLength ==0) {
			g.setFont(GameMethods.font(8));
			g.drawString("NONE", 20, 112);
		}
		else if(fruitPrintLength > allowedSize) {
			excess = (fruitPrintLength/25) -  allowedSize/25;
			g.setFont(GameMethods.font(10));
			g.setColor(Color.WHITE);
			g.drawString("+"+excess, 20, 115);
			index= (excess < 10) ? 1 : (excess >= 10) ? 2 : (excess >= 100) ? 3 : 4;
		}
		for(int i=excess; i<game.getPacman().getCollectedFruits().size(); i++)
		{
			Fruit fruit = game.getPacman().getCollectedFruits().get(i);
			int indent = (fruit.getAnimator().sprite.getWidth()*index);
			g.drawImage(fruit.getAnimator().sprite, 20+indent, 100, fruit.getAnimator().sprite.getWidth()-1, fruit.getAnimator().sprite.getHeight()-1, null);
			index++;
		}
	}
}
