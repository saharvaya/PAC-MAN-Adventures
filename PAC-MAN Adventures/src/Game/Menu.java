/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

import Engine.GameMethods;
import Engine.MazeLoader;
import Engine.ResourceLoader;

/*
 * Main game menu
 */
public class Menu extends JPanel implements KeyListener {
	
	//Fields
	Game game; // relevant game instance
	static Clip clip; // Used to play sounds
	private int main=0, movement=1, map =2, difficulty =3, play=4, high_scores=5; // Menu modes
	private int state; // Current menu mode
	private BufferedImage logo; // Game logo image
	Animator animator; //Menu animator
	int dx=0, dy=0; // Pixels offset
	
	//Constructor
	public Menu(Game game)
	{
		this.game=game;
		this.state = 0;
		try {
			this.animator = new Animator("/images/logos_icons_hud/logo2.png", 180, 7, false, false, false);
			this.logo = ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/logo2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.addKeyListener(this);
		this.setPreferredSize(new Dimension(800,950));
		this.setMinimumSize(new Dimension(800,950));
		this.setMaximumSize(new Dimension(800,950));

		this.setFocusable(true);
		this.requestFocusInWindow();
		this.setDoubleBuffered(true);
		this.requestFocus();
		menuMusic();
	}
	
	/*
	 * Paints menu
	 */
	public void paint(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Game.WIDTH, 950);
		g.drawImage(logo, (Game.WIDTH-logo.getWidth())/2, 30, logo.getWidth(), logo.getHeight(), null);
		g.setFont(GameMethods.font(20f));
		
		if(state == main) mainScreen(g);
		else if(state == movement) movementScreen(g);
		else if(state == map)	mapScreen(g);
		else if(state == difficulty) difficultyScreen(g);
		else if(state == high_scores) highScoresScreen(g);
		else if(state == play)
		{
			g.dispose();
		}
	}
	
	/*
	 * Paints menu main screen.
	 */
	private void mainScreen(Graphics g)
	{
		g.setColor(Color.ORANGE);
		g.setFont(GameMethods.font(22f));
		String welcome = "WELCOME TO PAC-MAN ADVENTURES!";
		g.drawString(welcome, GameMethods.centerString(welcome, g), logo.getHeight() + 135);
		g.setFont(GameMethods.font(15f));
		g.setColor(Color.YELLOW);
		String select = "USE ARROW KEYS TO SELECT:";
		g.drawString(select, GameMethods.centerString(select, g), logo.getHeight() + 170);
		g.setFont(GameMethods.font(20f));
		g.setColor(Color.WHITE);
		g.drawString(">>", (Game.WIDTH-logo.getWidth())/2+70, logo.getHeight() + 250+dy);
		g.drawString("NEW GAME", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 250);
		g.drawString("HIGHEST SCORES", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 275);
		String space = (animator.frame % 2 == 0) ? "" : "PRESS SPACE OR ENTER TO START...";
		g.drawString(space, GameMethods.centerString(space, g), logo.getHeight() + 500);
	}
	
	/*
	 * Paints game difficulty screen
	 */
	private void difficultyScreen(Graphics g)
	{
		g.setFont(GameMethods.font(20f));
		g.setColor(Color.ORANGE);
		g.drawString("CHOOSE DIFFICULTY:", (Game.WIDTH-logo.getWidth())/2+65, logo.getHeight() + 100);
		g.setFont(GameMethods.font(12f));
		g.setColor(Color.YELLOW);
		g.drawString("USE ARROW KEYS", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 125);
		
		g.setFont(GameMethods.font(18f));
		g.setColor(Color.WHITE);
		g.drawString(">>", (Game.WIDTH-logo.getWidth())/2+70, logo.getHeight() + 250+dy);
		g.drawString("EASY", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 250);
		
		g.drawString("MEDIUM", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 275);
		
		g.drawString("HARD", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 300);
		
		g.drawString("IMPOSSIBLE", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 325);
		
		g.drawString("HELL ON EARTH", (Game.WIDTH-logo.getWidth())/2+125, logo.getHeight() + 350);
		g.setFont(GameMethods.font(12f));
		g.drawString("PRESS BACKSPACE TO RETURN", Game.WIDTH-350, Game.GAME_SIZE.height-80);
	}
	
	/*
	 * Prints high scores screen
	 */
	private void highScoresScreen(Graphics g)
	{
		g.setColor(Color.ORANGE);
		g.setFont(GameMethods.font(22f));
		String highScores = "HIGHEST SCORES:";
		g.drawString(highScores, GameMethods.centerString(highScores, g), logo.getHeight() + 135);
		g.setColor(Color.YELLOW);
		g.setFont(GameMethods.font(20f));
		g.drawString("NAME:", (Game.WIDTH-logo.getWidth())/2-30, logo.getHeight() + 200);
		g.drawString("SCORE:", (Game.WIDTH-logo.getWidth())/2+265, logo.getHeight() + 200);
		g.setFont(GameMethods.font(14f));
		g.drawString("COLLECTED:", (Game.WIDTH-logo.getWidth())/2+440, logo.getHeight() + 190);
		g.setColor(Color.WHITE);
		dy=30;
		int i=1;
		for(Score score : ResourceLoader.highestScores)
		{	
			g.setFont(GameMethods.font(14f));
			if(i==1) {
				g.setColor(Color.ORANGE);
				g.drawString("HIGH", 8, logo.getHeight() + 200+dy);
			}
			else g.setColor(Color.WHITE);
			g.setFont(GameMethods.font(14f));
			g.drawString(i+".", (Game.WIDTH-logo.getWidth())/2-75, logo.getHeight() + 200+dy);
			g.drawString(score.name, (Game.WIDTH-logo.getWidth())/2-30, logo.getHeight() + 200+dy);
			g.drawString(GameMethods.getPrintedScore(score.score), (Game.WIDTH-logo.getWidth())/2+265, logo.getHeight() + 200+dy);
			g.setFont(GameMethods.font(10f));
			g.drawString(score.pinapples+"", (Game.WIDTH-logo.getWidth())/2+440, logo.getHeight() + 200+dy);
			g.drawString(score.grapes+"", (Game.WIDTH-logo.getWidth())/2+490, logo.getHeight() + 200+dy);
			g.drawString(score.strawberries+"", (Game.WIDTH-logo.getWidth())/2+540, logo.getHeight() + 200+dy);
			g.drawString(score.bananas+"", (Game.WIDTH-logo.getWidth())/2+590, logo.getHeight() + 200+dy);
			try {
				g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/pineapple.png")), (Game.WIDTH-logo.getWidth())/2+420, logo.getHeight() + 182+dy, 20, 20, null);
				g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/grapes.png")), (Game.WIDTH-logo.getWidth())/2+470, logo.getHeight() + 182+dy, 20, 20, null);
				g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/strawberry.png")), (Game.WIDTH-logo.getWidth())/2+520, logo.getHeight() + 182+dy, 20, 20, null);
				g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/collectable_sprites/bananas.png")), (Game.WIDTH-logo.getWidth())/2+570, logo.getHeight() + 182+dy, 20, 20, null);
			}
			catch(Exception e) {}
			dy+=30;
			i++;
		}
		dy=0;
		g.setFont(GameMethods.font(12f));
		g.drawString("PRESS BACKSPACE OR SPACE TO RETURN", Game.WIDTH-480, Game.GAME_SIZE.height-80);
	}
	
	/*
	 * Prints map choice screen
	 */
	private void mapScreen(Graphics g)
	{
		g.setFont(GameMethods.font(20f));
		g.setColor(Color.ORANGE);
		String map = "CHOOSE INITIAL MAP:";
		g.drawString(map, GameMethods.centerString(map, g), logo.getHeight() + 100);
		g.setFont(GameMethods.font(12f));
		g.setColor(Color.YELLOW);
		String arrows = "<  USE ARROW KEYS  >";
		g.drawString(arrows, GameMethods.centerString(arrows, g), logo.getHeight() + 125);
		g.setFont(GameMethods.font(12f));
		g.setColor(Color.WHITE);
		int indent = -100;
		for(Maze maze : MazeLoader.getMazes().values())
		{
			g.drawImage(maze.getFullMaze(), indent+dx, 300, 300, 300, null);
			g.drawString((maze.mazeNumber == 1) ? "HAUNTED CASTLE" : (maze.mazeNumber == 2) ? "JUNGLE MAZE" : (maze.mazeNumber == 3) ? "SPACE FRENZY" : (maze.mazeNumber == 4) ? "TREASURE ISLAND" : "BACK IN TIME", indent+dx+75, 620);
			indent+=340;
		}
		String choose = "CHOOSE ▼";
		g.drawString(choose, GameMethods.centerString(choose, g),290);
		
		String cont = (animator.frame % 2 == 0) ? "" : "PRESS SPACE OR ENTER TO CONTINUE...";
		g.drawString(cont, GameMethods.centerString(cont,g), logo.getHeight() + 700);
		g.setFont(GameMethods.font(12f));
		g.drawString("PRESS BACKSPACE TO RETURN", Game.WIDTH-350, Game.GAME_SIZE.height-80);
	}
	
	/*
	 * Prints movement type choice screen
	 */
	private void movementScreen(Graphics g)
	{
		g.setFont(GameMethods.font(20f));
		g.setColor(Color.ORANGE);
		String map = "CHOOSE PLAYER MOVMENT MODE:";
		g.drawString(map, GameMethods.centerString(map, g), logo.getHeight() + 100);
		g.setFont(GameMethods.font(12f));
		g.setColor(Color.YELLOW);
		String arrows = "USE ARROW KEYS";
		g.drawString(arrows, GameMethods.centerString(arrows, g), logo.getHeight() + 125);
		g.setFont(GameMethods.font(12f));
		g.setColor(Color.WHITE);
		g.setFont(GameMethods.font(20f));
		g.setColor(Color.WHITE);
		g.drawString(">>", (Game.WIDTH-logo.getWidth())/2+30, logo.getHeight() + 250+dy);
		g.drawString("CONTINUOUS (EASIER)", (Game.WIDTH-logo.getWidth())/2+80, logo.getHeight() + 250);
		
		g.drawString("ON CLICK (HARDER)", (Game.WIDTH-logo.getWidth())/2+80, logo.getHeight() + 275);
		g.setFont(GameMethods.font(12f));
		g.drawString("PRESS BACKSPACE TO RETURN", Game.WIDTH-350, Game.GAME_SIZE.height-80);
	}

	/*
	 * Controls menu key presses
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(state == main) {
			 if(e.getKeyCode() == KeyEvent.VK_UP) {
				 GameMethods.playSound("menu_select.wav");
					dy-=25;
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					GameMethods.playSound("menu_select.wav");
					dy+=25;
				}
			 if(dy<=0)
				 dy=0;
			 else if (dy>=25)
				 dy=25;
			if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				GameMethods.playSound("menu_choice.wav");
				if(dy==0) state =movement;
				else state = high_scores;
				dy=0;
			}
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				state = main;
		}
		else if(state == movement) {
			 if(e.getKeyCode() == KeyEvent.VK_UP) {
				 GameMethods.playSound("menu_select.wav");
					dy-=25;
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					GameMethods.playSound("menu_select.wav");
					dy+=25;
				}
			 if(dy<=0)
				 dy=0;
			 else if (dy>=25)
				 dy=25;
			if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				GameMethods.playSound("menu_choice.wav");
				if(dy==0) game.movement = Game.PacmanMovmentState.CONTINUOUS;
				else game.movement = Game.PacmanMovmentState.ON_DEMAND;
				dy=0;
				this.state = map;
			}
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				state = main;
		}
		else if(state == map) {
			 if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				 GameMethods.playSound("menu_select.wav");
				dx+=300;
			}
			else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				GameMethods.playSound("menu_select.wav");
				dx-=300;
			}
			else if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
				GameMethods.playSound("menu_choice.wav");
				game.setInitialMaze(MazeLoader.getMazes().get((dx == -900) ? 5 : (dx == -300) ? 3 : (dx == 0) ? 2 : (dx == 300) ? 1 : 4));
				state = difficulty;
			}
			if(dx >= 300)
				dx = 300;
			else if(dx <= -900)
				dx = -900;
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				state = main;
		}
		else if(state == difficulty) {
			 if(e.getKeyCode() == KeyEvent.VK_UP) {
				 GameMethods.playSound("menu_select.wav");
					dy-=25;
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					GameMethods.playSound("menu_select.wav");
					dy+=25;
				}
			 if(dy<=0)
				 dy=0;
			 else if (dy>=100)
				 dy=100;
			if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
				GameMethods.playSound("menu_choice.wav");
					Game.difficulty = (dy==0) ? 0 : (dy ==25) ? 8 : (dy==50) ? 15 : (dy==75) ? 22 : 31;
					state = play;
					clip.stop();
					game.startNewLevel();
					game.start();
				}
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				state = map;
		}
		else if(state == high_scores) {
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE | e.getKeyCode() == KeyEvent.VK_SPACE) {
				GameMethods.playSound("menu_choice.wav");
				state = main;
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	/*
	 * Plays background menu music in a loop.
	 */
	public static synchronized void menuMusic() {
		   try {
			   	clip = AudioSystem.getClip();
			   AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(ResourceLoader.load("sounds/menu_music.wav")));
			    clip.open(inputStream);
			    clip.loop(Clip.LOOP_CONTINUOUSLY);
			    clip.start(); 
			    
		   } catch (Exception e) {
		     System.err.println(e.getMessage());
		   }
	}
}
