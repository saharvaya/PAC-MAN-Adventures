/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Point;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import Game.Game.GameState;
import Pickups.Bananas;
import Pickups.Fruit;
import Pickups.Grapes;
import Pickups.Pineapple;
import Pickups.Strawberry;
import Engine.GameMethods;

/*
 * Main game view drawing canvas.
 */
public class GameView extends JPanel {
	
	//Fields
	private final Game game; // Related game instance
	private static final Dimension GAME_SIZE = new Dimension(Game.WIDTH, Game.HEIGHT); // Game view dimension

	//Constructor
	public GameView(Game game)
	{
		this.game= game;
		this.setBorder(new LineBorder(Color.BLACK, 8));
		this.setPreferredSize(GAME_SIZE);
		this.setMinimumSize(GAME_SIZE);
		this.setMaximumSize(GAME_SIZE);
		this.setLayout(new  GridBagLayout());
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.requestFocus();
	}
	
	/*
	 * Paints entire game view.
	 */
	public void paintGame(Graphics g)
	{
		game.getLevel().paint(g);
		if(game.getPacman() != null) {
			   game.getPacman().draw(g);
	   }
		if(game.getLevel().getGhosts() != null ) {
			for(int i=0; i<game.getLevel().getGhosts().size(); i++)
			{
				game.getLevel().getGhosts().get(i).paint(g);
			}
		}
	}
	
	/*
	 * Paints game ready state.
	 */
	public void ready(Graphics g)
	{
		g.setFont(GameMethods.font(30));
		String ready = "READY IN "+GameTimer.startCounter;
		Point location = new Point(GameMethods.centerString(ready, g), getHeight()/2);
		g.setColor(Color.YELLOW);
		g.drawString(ready, location.x+2, location.y);
		g.drawString(ready, location.x, location.y+2);
		g.drawString(ready, location.x-2, location.y);
		g.drawString(ready, location.x, location.y-2);
		g.setColor(Color.BLACK);
		g.drawString(ready, location.x, location.y);
	}
	
	/*
	 * Paints game level pass state.
	 */
	public void levelPass(Graphics g)
	{
		g.setColor(Color.ORANGE);
		g.fillRect((Game.WIDTH/2)-215, (Game.HEIGHT/2)-115, 430, 230);
		g.setColor(Color.BLACK);
		g.fillRect((Game.WIDTH/2)-200, (Game.HEIGHT/2)-100, 400, 200);
		g.setFont(GameMethods.font((Game.currentLevel == 4) ? 20 : 22));
		g.setColor(Color.YELLOW);
		String levelCleared = (Game.currentLevel == 4) ? "BONUS LEVEL CLEARED" : "LEVEL "+Game.currentLevel+" CLEARED";
		g.drawString(levelCleared, GameMethods.centerString(levelCleared, g), (Game.HEIGHT/2)-20);
		g.setFont(GameMethods.font(10));
		g.setColor(Color.WHITE);
		String cont = (game.getMenu().animator.frame % 2 == 0) ? "" : "PRESS SPACE OR ENTER TO CONTINUE...";
		g.drawString(cont, GameMethods.centerString(cont, g), (Game.HEIGHT/2)+20);
		g.setFont(GameMethods.font(10));
		String exit = "PRESS ESC TO QUIT.";
		g.drawString(exit, GameMethods.centerString(exit, g), (Game.HEIGHT/2)+45);
		if(Game.currentLevel == 3)
		{
			g.setColor(Color.ORANGE);
			g.setFont(GameMethods.font(14));
			String bonus = "NEXT LEVEL IS BONUS !";
			g.drawString(bonus, GameMethods.centerString(bonus, g), (Game.HEIGHT/2)+75);
		}
	}
	
	/*
	 * Paints game over state summary.
	 */
	private void gameOver(Graphics g) {
		g.setColor(Color.ORANGE);
		g.fillRect((Game.WIDTH/2)-215, (Game.HEIGHT/2)-150, 430, 300);
		g.setColor(Color.BLACK);
		g.fillRect((Game.WIDTH/2)-200, (Game.HEIGHT/2)-135, 400, 270);
		g.setFont(GameMethods.font(22));
		g.setColor(Color.YELLOW);
		String over = (Game.state == GameState.OVER) ? "GAME OVER" : "GAME WON";
		g.drawString(over, GameMethods.centerString(over, g), (Game.HEIGHT/2)-65);
		g.setColor(Color.YELLOW);
		g.setFont(GameMethods.font(18));
		g.drawString("SCORE:", (Game.WIDTH/2)-170, (Game.HEIGHT/2)-15);
		g.setColor(Color.WHITE);
		g.drawString(GameMethods.getPrintedScore(null), (Game.WIDTH/2)-20, (Game.HEIGHT/2)-15);
		printCollectedFruits(g);
		printClock(g);
		g.setFont(GameMethods.font(8));
		String cont ="PRESS SPACE OR ENTER TO RETURN TO MENU...";
		g.drawString(cont, GameMethods.centerString(cont, g), (Game.HEIGHT/2)+110);
		g.setFont(GameMethods.font(10));
		String exit = "PRESS ESC TO EXIT GAME";
		g.drawString(exit, GameMethods.centerString(exit, g), (Game.HEIGHT/2)+125);
	}
	
	/*
	 * Paints the total game collected fruits in the game summary.
	 */
	private void printCollectedFruits(Graphics g)
	{
		int pinapples=0, grapes=0, strawberries=0, bananas=0;
		for(Fruit fruit : game.getPacman().getCollectedFruits())
		{
			if(fruit instanceof Pineapple) pinapples++;
			else if(fruit instanceof Grapes) grapes++;
			else if(fruit instanceof Strawberry) strawberries++;
			else if(fruit instanceof Bananas) bananas++;
		}
		g.setFont(GameMethods.font(10));
		g.setColor(Color.YELLOW);
		g.drawString("COLLECTED FRUITS:", (Game.WIDTH/2)/2+30, (Game.HEIGHT/2)+20);
		g.setFont(GameMethods.font(10f));
		g.setColor(Color.WHITE);
		g.drawString(pinapples+"",(Game.WIDTH/2)/2+360, (Game.HEIGHT/2)+20);
		g.drawString(grapes+"", (Game.WIDTH/2)/2+320, (Game.HEIGHT/2)+20);
		g.drawString(strawberries+"", (Game.WIDTH/2)/2+280, (Game.HEIGHT/2)+20);
		g.drawString(bananas+"", (Game.WIDTH/2)/2+240, (Game.HEIGHT/2)+20);
		try {
			g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/pineapple.png")), (Game.WIDTH/2)/2+352, (Game.HEIGHT/2)+20, null);
			g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/grapes.png")), (Game.WIDTH/2)/2+312, (Game.HEIGHT/2)+20, null);
			g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/strawberry.png")), (Game.WIDTH/2)/2+272, (Game.HEIGHT/2)+20, null);
			g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/bananas.png")), (Game.WIDTH/2)/2+232, (Game.HEIGHT/2)+20, null);
		}
		catch(Exception e) {}
	}
	
	/*
	 * Paints the total game time in the game summary.
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
		g.setColor(Color.YELLOW);
		g.drawString("GAME TIME:", (Game.WIDTH/2)/2+30, (Game.HEIGHT/2)+70);
		g.setFont(GameMethods.font(10));
		g.setColor(Color.WHITE);
		g.drawString(secondsText, (Game.WIDTH/2)/2+280, (Game.HEIGHT/2)+70);
		g.drawString(minutesText, (Game.WIDTH/2)/2+230, (Game.HEIGHT/2)+70);
		g.drawString(hoursText, (Game.WIDTH/2)/2+180, (Game.HEIGHT/2)+70);
	}
	
	/*
	 * Paints game pause state.
	 */
	public void pause(Graphics g) {
		g.setFont(GameMethods.font(50));
		String pause = "GAME PAUSED";
		Point location = new Point(GameMethods.centerString(pause, g), (Game.HEIGHT/2));
		g.setColor(Color.ORANGE);
		g.drawString(pause, location.x+2, location.y);
		g.drawString(pause, location.x-2, location.y);
		g.drawString(pause, location.x, location.y+2);
		g.drawString(pause, location.x, location.y-2);
		g.setColor(Color.BLACK);
		g.drawString(pause, location.x, location.y);
	}
	
	/*
	 * Paints gave view in addition to the different game states.
	 */
	public void paint(Graphics g) {
  		paintGame(g);
		if(Game.state == GameState.READY) ready(g);
		if(Game.state == GameState.LEVEL_PASS) levelPass(g);
		if(Game.state == GameState.OVER || Game.state == GameState.WON) gameOver(g);
		if(Game.state == GameState.PAUSED) pause(g);
	}
}
