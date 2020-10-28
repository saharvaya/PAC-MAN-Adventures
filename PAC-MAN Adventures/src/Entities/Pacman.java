/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;
import Pickups.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import Game.Animator;
import Game.Game;
import Game.Game.PacmanMovmentState;
import Game.Maze;
import Game.Portal;
import Engine.GameMethods;
import Game.Tile;
import Game.TileTypes;
import Entities.Ghost;
import Entities.Ghost.GhostState;
import Entities.Mover;
import Entities.Visitable;
import Entities.Minion;

/*
 * Abstract class to define all pacman controls and methods
 */
public abstract class Pacman extends Mover implements Visitable, KeyListener, ActionListener
{
	//Fields
	private Animator dizzyAnimator; // Dizzy state animator
	private Animator deadAnimator; // Dead state animator
	private Animator poisonedAnimator; // Poisoned state animator
	public enum PacmanState { NORMAL, FREEZE, DEAD, DIZZY, POISONED }; // Enumartion to define pacman states
	protected PacmanState state; // Current pacman state
	private int column, row; // current pacman row and column
	private Maze maze; // Current maze 
	private Timer timer; // Timer to reset requested direction after a key press
	private String status; //String to describe the current pacman status
	private ArrayList<Fruit> collectedFruits; // A list of all fruits collected by pacman
	protected BufferedImage sword; // Pacman sword image for god-mode
	protected Minion minion; // Current minion instance
	private Timer minionDisposalTimer; // A timer to determine the disposal time of a minion
	Timer poisonTimer; // A timer to count time poisoned
	private int poisonCounter = 20; // Initial poision tiem count in seconds
	private MinionDisposalListener minionDisposal; // A action listner to check for minion disposal
	
	//Constructor
	public Pacman(Game game) {
		super(game);
		this.state = PacmanState.NORMAL;
		this.status = "NORMAL";
		this.speed = ((game.movement == PacmanMovmentState.CONTINUOUS) ? 2 : 4);
		this.normalSpeed = speed;
		this.minion=null;

		this.timer = new Timer(600,this);
		this.minionDisposal = new MinionDisposalListener(this);
		this.minionDisposalTimer = new Timer(12000, minionDisposal);
		minionDisposal.setTimer(minionDisposalTimer);
		this.poisonTimer = new Timer (1000, new PoisonListener(this));
		this.collectedFruits = new ArrayList<Fruit>();
		currentDirection = requestedDirection = ((new Random()).nextInt(2) == 0) ? KeyEvent.VK_LEFT : KeyEvent.VK_RIGHT;
		setAnimators();
		timer.start();
	}
	
	/*
	 * Sets all different state animators
	 */
	private void setAnimators()
	{
		try {
			this.sword = ImageIO.read(getClass().getResourceAsStream("/images/weapons_sprites/sword.png"));
			this.dizzyAnimator = new Animator("/images/effects_sprites/dizzy.png", 40, 5, false, true, false);
			this.deadAnimator = new Animator("/images/entity_sprites/pacman_dead.png", 100, 10, false, true, false);
			this.poisonedAnimator = new Animator("/images/effects_sprites/poison_slime.png", 50, 9, false, true, false);
		} catch (IOException e) {
		}
	}
	

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (37 <= key && key <= 40) {
			if(this.state == PacmanState.DIZZY) {
				requestedDirection = (key == 37 | key == 38) ? key+2 : key-2;
			}
			else requestedDirection = key;
			if(game.movement == PacmanMovmentState.ON_DEMAND && Game.state == Game.GameState.PLAY) {
				currentDirection = requestedDirection;
				move(currentDirection);
			}
			timer.restart();
		}
		
	}
	
	/*
	 * Handles the continues movment state, move in the current direction unless changed by the end-user.
	 * Checks for each step for action that can be done in current location.
	 */
	public void movment()
	{
		if(this.state != PacmanState.FREEZE) {
			if(game.movement == PacmanMovmentState.CONTINUOUS) {
				if (this.move(requestedDirection) == MOVE_SUCCESS) {
					currentDirection = requestedDirection;
				} else {
					this.move(currentDirection);
				}
			}
			action();
		}
		if(this.minion != null) this.minion.movment();
	}
	
	/*
	 * For each step checks for different operations in current tile.
	 * If it is a portal tile - teleports
	 * If it is a collectable tile, eats the collectable.
	 */
	private void action()
	{
		if(this.position.type == TileTypes.PORTAL)
			teleport();
		else eat();
	}
	
	/*
	 * Checks if current position is a portal position.
	 * if it is a portal poision activate portal to teleport.
	 */
	public void teleport()
	{
		for(Portal portal : maze.getPortals())
		{
			if(this.position.equals(portal.getPosition())) //FIX PORTAL ACTIVATION IN ON_DEMAND MODE
				portal.activate(this);
		}
	}
	
	/*
	 * Checks if there are any collectables to eat in current position.
	 */
	public void eat()
	{
		if (this.position.type == TileTypes.COIN) {
			eatCoins();
		}
		else if (this.position.type == TileTypes.CORNER) { // Energy Pill
			eatPills();
		}
		else if (game.getLevel().getSummoner() != null && this.position.equals(game.getLevel().getSummoner().getPosition()))
		{
			game.getLevel().disposeSummoner(true);
			this.minion = new Minion(game, this, this.position.column, this.position.row);
			minionDisposalTimer.start();
		}
		else if (game.getLevel().getTnt() != null && this.position.equals(game.getLevel().getTnt().getPosition())) {
			game.getLevel().getTnt().explodeTraps();
			game.getLevel().disposeTNT(true);
		}
		else if (game.getLevel().getElixir() != null && this.position.equals(game.getLevel().getElixir().getPosition())) {
			poisonTimer.stop();
			poisonCounter = 20;
			this.state = PacmanState.NORMAL;
			game.getLevel().disposeElixir();
		}
		else eatFruits();
	}
	
	/*
	 * Checks for fruit in current location.
	 * If coin is present changes the coin tile to path and removes current fruit from maze.
	 */
	private void eatFruits()
	{
		for (Fruit fruit : maze.getFruits())
		{
			if(fruit != null && this.position.equals(fruit.getPosition()))
			{
				GameMethods.playSound("fruit.wav");
				maze.getOpenSpots().add(fruit.getPosition());
				this.collectedFruits.add(fruit);
				game.getLevel().remove(fruit, maze.getFruits());
			}
		}
	}
	
	/*
	 * Checks for coin in current location.
	 * If coin is present changes the coin tile to path and removes current coin from maze.
	 * Checks if all coins have been eaten - if no more coins left calls wonLevel game method.
	 */
	private void eatCoins()
	{
		maze.board[column][row].setType(TileTypes.PATH);
		for (Coin coin : maze.getCoins())  ///////////////EFFICENCY
		{
			if(this.position.equals(coin.getPosition())) {
				GameMethods.playSound("coin.wav");
				maze.getOpenSpots().add(coin.getPosition());
				game.getLevel().remove(coin, maze.getCoins());
				if(maze.getCoins().size() == 0)
					game.getLevel().wonLevel();   //WON LEVEL
			}
		}
	}
	
	/*
	 * Checks for pill in current location.
	 * If pill is present changes the pill tile to path and removes current pill from maze.
	 * Calls game godMode method to change all ghost state to FLEE.
	 */
	private void eatPills()
	{
		maze.board[position.column][row].setType(TileTypes.PATH);
		for (EnergyPill pill :maze.getEnergyPills())
		{
			if(this.position.equals(pill.getPosition()))
			{
				GameMethods.playSound("energy.wav");
				maze.getOpenSpots().add(pill.getPosition());
				game.getLevel().remove(pill, maze.getEnergyPills());
				this.status = "GOD-MODE";
				game.godMode();
			}
		}
	}

	/*
	 * Moves pacman to the requested direction determined by constant directions definition in Mover class.
	 * @param direction - the direction to move to - requested by user key press.
	 */
	@Override
	public int move(int direction)   ////MOVE THE METHOD BACK
	{
		if(this.state != PacmanState.FREEZE & this.state != PacmanState.DEAD) {
			switch (direction) {
			case KeyEvent.VK_LEFT:  // 37
				if (column > 0 && maze.board[column-1][row].type.isPacmanPath()) {
					animator.animateMovement(LEFT, speed);	
					if(direction == KeyEvent.VK_LEFT && animator.dx==-Math.round(25/speed)*speed) {
						animator.resetOffset();
						position = maze.board[column-1][row];
						column -=1;
					}
					return MOVE_SUCCESS;
				}
				break;
			case KeyEvent.VK_UP:   // 38
				if (row > 0 && maze.board[column][row-1].type.isPacmanPath()) {
					animator.animateMovement(UP, speed);	
					if(direction == KeyEvent.VK_UP && animator.dy==-Math.round(25/speed)*speed) {
						animator.resetOffset();
						position = maze.board[column][row-1];
						row -=1;
					}
					return MOVE_SUCCESS;
				}
				break;
			case KeyEvent.VK_RIGHT: // 39
				if (column < game.COLUMNS-1 && maze.board[column+1][row].type.isPacmanPath()) {
					{
						animator.animateMovement(RIGHT, speed);					
						if(direction == KeyEvent.VK_RIGHT && animator.dx==Math.round(25/speed)*speed) {
							animator.resetOffset();
							position = maze.board[column+1][row];
							column += 1;
						}
					}
					return MOVE_SUCCESS;
				}
				break;
			case KeyEvent.VK_DOWN:  // 40
				if (row < game.ROWS-1 && maze.board[column][row+1].type.isPacmanPath()) 
				{
					animator.animateMovement(DOWN, speed);	
					if(direction == KeyEvent.VK_DOWN && animator.dy==Math.round(25/speed)*speed) {
						animator.resetOffset();
						position = maze.board[column][row+1];
						row += 1;
						
					}
					return MOVE_SUCCESS;
				}
				break;
			}
		}
		return MOVE_FAIL;
	}
	

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public void draw(Graphics g)
	{
		if(this.state == PacmanState.DEAD) {
			if(this.deadAnimator.frame == 10) this.deadAnimator.stopAnimation(true);
			g.drawImage(this.deadAnimator.sprite.getSubimage((this.deadAnimator.frame/2)*30, 0, 30, 30), column*25, row*25, null);
		}
		else {
			this.deadAnimator.stopAnimation(false);
			this.deadAnimator.frame=0;
			if(this.state == PacmanState.FREEZE) {
				g.drawImage(this.animator.sprite.getSubimage(0, (currentDirection-37)*22, 22, 22), column*25+animator.dx, row*25+animator.dy, null);
					g.drawImage(Mover.frozenAnimator.sprite.getSubimage((Mover.frozenAnimator.frame/2)*25, 0, 25, 25), column*25+animator.dx-3, row*25+animator.dy-3, 30, 30, null);
			}
			else try{
				g.drawImage(this.animator.sprite.getSubimage((this.animator.frame/2)*22, (currentDirection == -1) ? 0 : (currentDirection-37)*22, 22, 22), column*25+animator.dx, row*25+animator.dy, null);
			}
			catch(Exception e) { g.drawImage(this.animator.sprite.getSubimage((this.animator.frame/2)*22, 0, 22, 22), column*25+animator.dx, row*25+animator.dy, null); }
			if(state == PacmanState.DIZZY)
				g.drawImage(this.dizzyAnimator.sprite.getSubimage((this.dizzyAnimator.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy-9, 30, 20, null);
			if(this.state == PacmanState.POISONED) {
				drawPosionCounter(g);
				g.drawImage(this.poisonedAnimator.sprite.getSubimage((this.poisonedAnimator.frame/2)*25, 0, 25, 25), column*25+animator.dx, row*25+animator.dy, 25, 25, null);
			}
			if(this.status == "GOD-MODE") g.drawImage(this.sword.getSubimage((currentDirection == -1) ? 0 :(currentDirection-37)*22, 0, 22, 22), column*25+animator.dx, row*25+animator.dy, 30, 30, null);
		}
		if(minion != null) minion.paint(g);
	}
	
	/*
	 * Draws poison counter and effect image whiel pacman is poisoned
	 */
	private void drawPosionCounter(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.setFont(GameMethods.font(12));
		g.drawString(this.poisonCounter+"", position.column*25-1+animator.dx+5, position.row*25-4+animator.dy);
		g.drawString(this.poisonCounter+"", position.column*25+1+animator.dx+5, position.row*25-4+animator.dy);
		g.drawString(this.poisonCounter+"", position.column*25+1+animator.dx+5, position.row*25-6+animator.dy);
		g.drawString(this.poisonCounter+"", position.column*25-1+animator.dx+5, position.row*25-6+animator.dy);
		g.setColor(Color.GREEN);
		g.drawString(this.poisonCounter+"", position.column*25+animator.dx+5, position.row*25-5+animator.dy);
		try {
			g.drawImage(ImageIO.read(getClass().getResourceAsStream("/images/effects_sprites/poisoned.png")), position.column*25+animator.dx-15, position.row*25-5+animator.dy-14, 15, 15, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		requestedDirection = 0;
	}

	/*
	 * Disposes current active minion instance
	 */
	public void disposeMinion()
	{
		GameMethods.playSound("minion_disolve.wav");
		this.minion = null;
		this.minionDisposalTimer.stop();
	}

	/*
	 * Initiates a freeze effect on pacman
	 */
	public void freeze() {
		if(this.state != PacmanState.FREEZE) {
			GameMethods.playSound("freeze.wav");
			this.state = PacmanState.FREEZE;
			status = "FROZEN";
			game.addScore(-10);
			game.getLevel().getVisibleScores().addScore(-10, this);
		}
	}

	/*
	 * Initiates a dizzy effect on pacman
	 */
	public void dizzy() {
		if(this.state != PacmanState.DIZZY) {
			GameMethods.playSound("spell_cast.wav");
			this.state = PacmanState.DIZZY;
			status = "DIZZY";
			game.addScore(-100);
			game.getLevel().getVisibleScores().addScore(-100, this);
		}
	}

	/*
	 * Changes the ghost state to dead and adds the relevant score to game score
	 */
	public void eatGhost(Ghost g) {
		GameMethods.playSound("eat_ghost.wav");
		game.getLevel().getVisibleScores().addScore(1000, this);
		game.addScore(1000);
		g.state = GhostState.DIED;
	}

	
	/*
	 * Minion disposal timer - disposes minion.
	 */
	private class MinionDisposalListener implements ActionListener
	{
		private Pacman pacman;
		private Timer timer;
		
		public MinionDisposalListener(Pacman pacman)
		{
			this.pacman = pacman;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			this.pacman.disposeMinion();
		}
		
		public void setTimer(Timer timer)
		{
			this.timer = timer;
		}
	}
	
	/*
	 * Poision time listener - manages effect while pacman state is poisioned
	 */
	private class PoisonListener implements ActionListener
	{
		private Pacman pacman;
		
		public PoisonListener(Pacman pacman)
		{
			this.pacman = pacman;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(pacman.state == PacmanState.POISONED) {
				game.addScore(-5);
				game.getLevel().getVisibleScores().addScore(-5, pacman);
				poisonCounter--;
				if(poisonCounter == 0) {
					poisonTimer.stop();
					poisonCounter = 20;
					game.decreaseLives();
				}
			}
		}
		
	}
	
	//Visitor pattern methods
	public abstract void impact(Visitor visitor);

	//Getters and Setters
	public void setDirection(int direction) {
		this.currentDirection = direction;	
	}
	
	public void setPosition(Tile position)
	{
		this.position = position;
	}
	
	public void setCoordinates(Point point)
	{
		this.column = point.x;
		this.row = point.y;
	}
	
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	public abstract void setAnimator();
	
	public PacmanState getState() {
		return state;
	}
	
	public void setState(PacmanState state) {
		this.state = state;
	}
	

	public String getStatus() {
		return status;
	}

	public ArrayList<Fruit> getCollectedFruits() {
		return collectedFruits;
	}

	public Minion getMinion() {
		return minion;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setRequestedDirection(int direction) {
		this.requestedDirection = direction;
	}

	public int getColumn() {
		return column;
	}

	public void setCollectedFruits(ArrayList<Fruit> collectedFruits) {
		this.collectedFruits = collectedFruits;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getRow() {
		return row;
	}
}
