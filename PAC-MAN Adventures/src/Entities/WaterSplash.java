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
import Engine.GameMethods;
import Game.Tile;
import Game.TileTypes;
import Entities.Ghost.GhostState;

/*
 * Defines a watersplash instance shot by PurpleGhost
 */
public class WaterSplash extends Mover implements Weapon, Visitor, ActionListener{

	//Fields
	private int SPEED = 3; //Watersplash speed
	private BlueGhost ghost; // Relevant blue ghost instance
	private int direction; // Direction watersplash is shot at.
	private Tile position; // Current position on board.
	public boolean fired = true; // Determines if the watersplash is fired.
	Timer timer; // Moves the watersplash while active
	
	//Constructor
	public WaterSplash(BlueGhost ghost, int col, int row, int direction) {
		super(ghost.game);
		this.ghost = ghost;
		this.timer = new Timer(15, this);
		this.position = ghost.position;
		this.direction = direction;
		
		this.animator = new Animator("/images/weapons_sprites/watersplash.png", 20, 5, false, false, false);
		GameMethods.playSound("watersplash.wav");
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
	 * Disposes the current watersplash insatnce.
	 */
	public synchronized void dispose() {
			this.direction = -1;
			GameMethods.playSound("splash.wav");
			animator.setAnimation("/images/weapons_sprites/splash.png", 50, 11);
	}

	/*
	 * Determines movement in the current ammo direction.
	 */
	@Override
	public void movment() {
		if(ghost.shot && fired) {
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
					if(game.getCurrentMaze().board[position.column-1][position.row].type == TileTypes.WALL) {
						this.dispose();
						fired = false;
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
					if(game.getCurrentMaze().board[position.column][position.row-1].type == TileTypes.WALL) {
						this.dispose();
						fired = false;
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
						if(game.getCurrentMaze().board[position.column+1][position.row].type == TileTypes.WALL) {
							this.dispose();
							fired = false;
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
					if(game.getCurrentMaze().board[position.column][position.row+1].type == TileTypes.WALL) {
						this.dispose();
						fired = false;
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
				this.position.column*25+animator.dx, this.position.row*25+animator.dy, (direction == LEFT | direction == RIGHT) ? 35 : 25,(direction == RIGHT | direction == LEFT) ? 25 : 35, null);
		}
		else g.drawImage(this.animator.sprite.getSubimage((this.animator.frame/2)*25, 0, 25, 25), 
				this.position.column*25, this.position.row*25, 35, 35, null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		movment();
		if (direction == -1 && animator.frame == 11 ) 
			ghost.ammo.remove(this);
	}

	//Visitor pattern methods
	@Override
	public void hit(NormalPacman pacman) {
		pacman.freeze();	
		ghost.state = GhostState.RANDOM;
	}

	@Override
	public void hit(EvilPacman pacman) {
		if(ghost.state != GhostState.DIED & ghost.state != GhostState.FLEE) {
			GameMethods.playSound("freeze.wav");
			ghost.freeze();
			ghost.freezeTimer.start();
		}
	}

	@Override
	public void hit(ShieldedPacman pacman) {
		pacman.freeze();	
		ghost.state = GhostState.RANDOM;
	}

	@Override
	public void hit(Ghost ghost) {}
}
