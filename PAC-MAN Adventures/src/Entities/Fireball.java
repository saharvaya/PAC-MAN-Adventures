/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Game.Animator;
import Game.Game;
import Entities.Pacman.PacmanState;
import Engine.GameMethods;
import Game.Tile;

/*
 * Defines a fireball instance shot by RedGhost
 */
public class Fireball extends Mover implements Weapon, Visitor, ActionListener{

	//Fields
	private Animator animator; 
	private int SPEED = 4; //Fireball speed
	private RedGhost ghost; // The relevant red ghost instance
	private int direction; // Direction fireball is shot at.
	private Tile position; // Current position on board.
	public boolean fired = true; // Determines if the firaballs is fired.
	Timer timer; // Moves the fireball while active
	
	//Constructor
	public Fireball(RedGhost ghost, int col, int row, int direction) {
		super(ghost.game);
		this.ghost = ghost;
		this.timer = new Timer(15, this);
		this.position = ghost.position;
		this.direction = direction;
		this.animator = new Animator("/images/weapons_sprites/fireball.png", 30, 4, false, false, false);
		GameMethods.playSound("fireball.wav");
		timer.start();
	}
	
	/*
	 * Weapon interface method.
	 */
	@Override
	public void shoot() {
		this.fired = true;
		ghost.shot=true;
	}
	
	/*
	 * Disposes the current fireball insatnce.
	 */
	public synchronized void dispose() {
			this.direction = -1;
			GameMethods.playSound("explosion.wav");
			animator.setAnimation("/images/weapons_sprites/explosion.png", 50, 13);
	}

	/*
	 * Determines movement in the current ammo direction.
	 */
	@Override
	public void movment() {
		if(ghost.shot) {
			if(direction == RIGHT)
				move(RIGHT);
			else if(direction == LEFT)
				move(LEFT);
			else if(direction == UP)
				move(UP);
			else if(direction == DOWN)
				move(DOWN);
		}
	}

	/*
	 * Moves the current ammo in that requested direction.
	 * @param direction - direction to move at.
	 */
	@Override
	public int move(int direction) {
		switch (direction) {
		case LEFT:
		{
				animator.animateMovement(LEFT, SPEED);
				if(animator.dx==-Math.round(25/SPEED)*SPEED) {
					animator.resetOffset();
					if(position.column <= 0) {
						this.dispose();
						break;
					}
					else {
						position = game.getCurrentMaze().board[position.column-1][position.row];
						checkForHit();
					}

				}
				return MOVE_SUCCESS;
			}
		case UP:
		{
				animator.animateMovement(UP, SPEED);
				if(animator.dy==-Math.round(25/SPEED)*SPEED) {
					animator.resetOffset();
					if(position.row <= 0) {
						this.dispose();
						break;
					}
					else {
						position = game.getCurrentMaze().board[position.column][position.row-1];
						checkForHit();
					}

				}
				return MOVE_SUCCESS;
			}
		case RIGHT:
			{
					animator.animateMovement(RIGHT, SPEED);
					if(animator.dx==Math.round(25/SPEED)*SPEED) {
						animator.resetOffset();
						if(position.column >= (Game.WIDTH/Tile.TILE_SIZE)-2) {
							this.dispose();
						}
						else {
							position = game.getCurrentMaze().board[position.column+1][position.row];
							checkForHit();
						}

					}
				return MOVE_SUCCESS;
			}
		case DOWN:
			{
				animator.animateMovement(DOWN, SPEED);
				if(animator.dy==Math.round(25/SPEED)*SPEED) {
					animator.resetOffset();
					if(position.row >= (Game.HEIGHT/Tile.TILE_SIZE)-2) {
						this.dispose();
						break;
					}
					else { 
						position = game.getCurrentMaze().board[position.column][position.row+1];
						checkForHit();
					}

				}
				return MOVE_SUCCESS;
				}
		}
		return MOVE_FAIL;
	}
	
	/*
	 * Checks for hit with the current pacman location,
	 */
	public void checkForHit()
	{
		if(this.position.column == game.getPacman().position.column && this.position.row == game.getPacman().position.row) {
			this.dispose();
			game.getPacman().impact(this);
		}
	}

	@Override
	public void draw(Graphics g) {
		if(direction != -1) {
			g.drawImage(this.animator.sprite.getSubimage((this.animator.frame/2)*25, direction*25, 25, 25), 
				this.position.column*25+animator.dx, this.position.row*25+animator.dy, (direction == LEFT | direction == RIGHT) ? 35 : 18,(direction == RIGHT | direction == LEFT) ? 18 : 35, null);
		}
		else g.drawImage(this.animator.sprite.getSubimage((this.animator.frame/2)*25, 0, 25, 25), 
				this.position.column*25, this.position.row*25, 30, 30, null);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		movment();
		if(direction == -1)
			if (animator.frame == 13) ghost.ammo.remove(this);
	}

	//Visitor pattern methods
	@Override
	public void hit(Ghost ghost) {}

	@Override
	public void hit(NormalPacman pacman) {
		if(pacman.state != PacmanState.DEAD)
			game.decreaseLives();
	}

	@Override
	public void hit(EvilPacman pacman) {
		if(pacman.state != PacmanState.DEAD)
			game.decreaseLives();
	}

	@Override
	public void hit(ShieldedPacman pacman) {
		if(pacman.state != PacmanState.DEAD)
			game.decreaseLives();
	}
}
