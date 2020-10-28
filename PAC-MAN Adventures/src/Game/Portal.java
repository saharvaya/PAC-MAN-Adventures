/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;
import Entities.Mover;
import Entities.Pacman;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

import Engine.GameMethods;

/*
 * Describes a portal instance
 */
public class Portal implements ActionListener {

	private Animator animator; // Portal animator
	private Tile position; // Portal board position
	private Random randomizer; // Randomizer to be used to randomize exit portal
	private Maze maze; // Relevant maze instance
	private Timer timer; // Switched availability and activation states
	private int exitDirection; // Determines the exit direction from the portal
	boolean activated = false;
	boolean available = true;
	
	//Constructor
	public Portal(Tile position, Maze maze)
	{
		this.position=position;
		this.maze=maze;
		this.randomizer = new Random();
		this.timer = new Timer(800, this);
		this.animator = new Animator("/images/effects_sprites/portal.png", 150, 4, false, false, false);
	}

	/*
	 * Activates the current teleport and teleport tha pacman instance
	 */
	public void activate(Pacman pacman)
	{
		if(available) {
			GameMethods.playSound("teleport.wav");
			timer.start();
			available = false;
			activated =  true;
			ArrayList<Portal> portals = maze.getPortals();
			Portal next = portals.get(randomizer.nextInt(portals.size()));
			while (next == this)
				next = portals.get(randomizer.nextInt(portals.size()));
			pacman.setRequestedDirection(next.exitDirection+37);
			pacman.setColumn(next.position.column);
			pacman.setRow(next.position.row);
		}
	}
	
	public void paint(Graphics g)
	{	int state = activated ? 1 : 0;
		g.drawImage(this.animator.sprite.getSubimage((this.animator.frame)*25, state*25, 22, 25), position.column*25, position.row*25, null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		activated = false; 
		available = true;
		timer.stop();
	}
	
	//Getters and Setters
	public void setExitDirection()
	{
		this.exitDirection = getExitDirection();
	}
	
	/*
	 * Gets the correct exit direction from the teleport
	 */
	private int getExitDirection() {
		return maze.board[position.column][position.row-1].type.isPacmanPath() ? Mover.UP :
				maze.board[position.column][position.row+1].type.isPacmanPath() ? Mover.DOWN :
				maze.board[position.column+1][position.row].type.isPacmanPath() ? Mover.RIGHT : Mover.LEFT;
	}
	
	public Tile getPosition() {
		return position;
	}
}
