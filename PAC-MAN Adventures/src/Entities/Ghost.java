/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import Engine.GameMethods;
import Game.Animator;
import Game.Game;
import Game.Maze;
import Entities.Pacman.PacmanState;
import Game.Tile;
import Game.TileTypes;

/*
 * Defines an abstract ghost class with all ghost movement methods and ghost state management.
 */
public abstract class Ghost extends Mover implements Visitor, Visitable {

	//Fields
	public enum GhostState { CAGED, SCATTER, RANDOM, CHASE, FLEE, DIED, FROZEN, DISAPEAR; } // Ghosts state enumeration.
	protected GhostState state; // Current ghost state.
	Random randomizer; // Randomizer to randomize different choices in class.

	protected static BufferedImage flick; // Danger flick image.
	protected static Animator flee; // Ghost flee animation.
	protected static Animator eyes; // Ghost dead animation.
	protected static Animator sleeping; // Ghost caged animation.
	protected static Animator confused; // Ghost corner delay animation.
	protected boolean dangerflick = false;
	
	protected int column, row; // Current ghost column and row across the maze board.
	private boolean hasPath; // Determines if a ghost has path to the target tile on board.

	protected int initialMovementCounter = 0; // Counts the time to determine if ghost initial movement is done.
	protected Timer stateTimer; // Changed ghost state from Random to Chase
	protected Timer initialMovementTimer; // Counts the initial movement time.
	private Tile corner; // Corner that the ghost chase in Scatter state.
	private Tile spawn; // Initial ghost spawn location.
	protected boolean canShoot; // Determines if a ghost can currently shoot.
	protected Timer shootingDelay; // Changes the canShoot flag.
	private Maze maze; // Current ghost maze.
	protected boolean hasWeapon; // Determines if the current ghost instance has weapon.
	protected Queue<Weapon> ammo; // Current ghost ammunition.
	
	//Constructor
	public Ghost(Game game, int col, int row) {
		super(game);
		this.randomizer = new Random();
		this.maze = game.getCurrentMaze();
		this.column=col;
		this.row = row;
		this.speed = -1;
		this.spawn = maze.board[col][row];
		this.position = maze.board[col][row];
		this.game=game;
		this.hasPath = false;
		this.state = GhostState.CAGED;
		this.corner = getCorner();
		
		currentDirection = requestedDirection = randomizer.nextInt(getDirections().size());
		
		this.stateTimer = new Timer(10000, new StateChanger());
		this.initialMovementTimer = new Timer(1000, new InitialMovementTimer(this));
		
		initiateAnimators();
		stateTimer.start();
	}
	
	/*
	 * Initiates all ghosts different animators.
	 */
	private void initiateAnimators()
	{
		if (flick == null) { try {
			flick =  ImageIO.read(getClass().getResourceAsStream("/images/effects_sprites/flick_danger.png"));
		} catch (IOException e) {
			e.printStackTrace();
		} }
		if(flee == null) { flee = new Animator("/images/entity_sprites/flee.png", 100, 11, false, false, false); }
		if(eyes == null) { eyes = new Animator("/images/entity_sprites/eyes.png", 100, 5, false, false, false); }
		if(sleeping == null) { sleeping = new Animator("/images/effects_sprites/sleeping.png", 100, 5, false, false, false); }
		if(confused == null) { confused = new Animator("/images/effects_sprites/confused.png", 100, 3, false, false, false); }
	}
	
	/*
	 * Governs the different ghost movements states.
	 */
	public void movment()
	{
		checkForEncounter();
		if(hasWeapon && initialMovementCounter>=10) initialMovementTimer.stop();
		else if (!hasWeapon && initialMovementCounter>=3) initialMovementTimer.stop();
		if(state != GhostState.CAGED & state != GhostState.DISAPEAR & state != GhostState.FROZEN) {
			if(state == GhostState.SCATTER)
			{
				chase(corner);
				if(initialMovementCounter==0 & position.equals(corner)) initialMovementTimer.start();
				if(position.equals(corner) & initialMovementCounter >= 2) {
					state = GhostState.CHASE;
				}
			}
			else if (state == GhostState.FLEE)
				flee();
			else if (state == GhostState.CHASE & game.getPacman().getState() != PacmanState.DEAD)
				chase(game.getPacman().position);
			else if(state == GhostState.RANDOM)
				randomMovement();
			else if(state == GhostState.DIED)
				regenerate();
			else if (state == GhostState.DISAPEAR)
				randomMovement();
			else this.state = GhostState.RANDOM;
			
			if(this.state != GhostState.FLEE & this.state != GhostState.DIED)
				normalSpeed();
			checkForEncounter();
		}
	}
	
	/*
	 * Moves the ghost to the wanted direction determined by constant directions definition in Mover class.
	 * @param direction - the direction to move to.
	 */
	@Override
	public int move(int direction)
	{
		switch (direction) {
			case LEFT:
				if (column > 0 && maze.board[column-1][row].type.isGhostPath()) {
					animator.animateMovement(LEFT, this.speed);
					currentDirection = LEFT;
					if(currentDirection  == LEFT && animator.dx==-Math.round(25/speed)*speed) {
						animator.resetOffset();
						position = maze.board[column-1][row];
						column -=1;
						if (state == GhostState.RANDOM) randomMove();

					}
					return MOVE_SUCCESS;
				}
			case UP:
				if (row > 0 && maze.board[column][row-1].type.isGhostPath()) {
					
					animator.animateMovement(UP, this.speed);
					currentDirection =UP;
					if(currentDirection  == UP && animator.dy==-Math.round(25/speed)*speed) {
						animator.resetOffset();
						position = maze.board[column][row-1];
						row -=1;
						if (state == GhostState.RANDOM) randomMove();

					}
					return MOVE_SUCCESS;
				}
			case RIGHT:
				if (column < game.COLUMNS-1 && maze.board[column+1][row].type.isGhostPath()) {
					{
						animator.animateMovement(RIGHT, this.speed);
						currentDirection  = RIGHT;
						if(currentDirection  == RIGHT && animator.dx==Math.round(25/speed)*speed) {
							animator.resetOffset();
							position = maze.board[column+1][row];
							column += 1;
							if (state == GhostState.RANDOM) randomMove();

						}
					}
					return MOVE_SUCCESS;
				}
			case DOWN:
				if (row < game.ROWS-1 && maze.board[column][row+1].type.isGhostPath()) 
				{
					animator.animateMovement(DOWN, this.speed);
					currentDirection = DOWN;
					if(currentDirection  == DOWN && animator.dy==Math.round(25/speed)*speed) {
						animator.resetOffset();
						position = maze.board[column][row+1];
						row += 1;
						if (state == GhostState.RANDOM) randomMove();	
					}
					return MOVE_SUCCESS;
					}
		}
			return MOVE_FAIL;
	}
	
	/*
	 * Gets a corner for the ghost to chase.
	 * @return a Corner Tile for a ghost to chase.
	 */
	private Tile getCorner() {
		ArrayList<Tile> corners = game.getCurrentMaze().getGhostCorners();
		Tile corner = null;
		if(corners.size() != 0) {
		corner = corners.get(randomizer.nextInt(corners.size()));
		maze.getGhostCorners().remove(corner);
		}
		else corner = maze.getCorners().get(randomizer.nextInt(game.getCurrentMaze().getCorners().size()));
		return corner;
	}

	/*
	 * Chases a certain tile using Breadth-First-Search.
	 * @param tile - a board Tile to chase.
	 */
	protected void chase(Tile tile) {
		checkCompletedArming();
		if(!hasPath) {
			LinkedList<Tile> moves = searchBFS(position, tile); //Find Path to requested tile by using BFS Graph search
			if(moves == null)
				this.state = GhostState.RANDOM;
			else if(!this.position.equals(tile)) moveInPath(moves);
		}
	}
	
	/*
	 * A Breadth-First Graph search.
	 * @param startNode - starting Tile of the search.
	 * @param - destination Tile for the search.
	 * @return a List of Tiles that describe to path from the start tile to destination tile, in no path found return null.
	 */
	private LinkedList<Tile> searchBFS(Tile startNode, Tile goalNode) {
		  // list of visited nodes
		  LinkedList<Tile> closedList = new LinkedList<Tile>();
			  
		  // list of nodes to visit (sorted)
		  LinkedList<Tile> openList = new LinkedList<Tile>();
		  openList.add(startNode);
		  startNode.pathParent = null;
			  
		  while (!openList.isEmpty()) {
			    Tile node = (Tile)openList.removeFirst();
			    if (node == goalNode) {
			      // path found!
			      return constructPath(goalNode);
			    }
			    else if(node.type != TileTypes.WALL) {
			      closedList.add(node);
			      
		      // add neighbors to the open list
				      Iterator<Tile> i = node.neighbours.iterator();
				      while (i.hasNext()) {
				    	  Tile neighborNode = (Tile)i.next();
					        if (!closedList.contains(neighborNode) && !openList.contains(neighborNode)) 
					        {
					          neighborNode.pathParent = node;
					          openList.add(neighborNode);
					        }
				      }
			    	}
		  }
		  // no path found
		  return null;
		}
	
	/*
	 * Builds for to be used in the Breadth-First-Search
	 * @return a List of Tile describe the requested path.
	 */
	private LinkedList<Tile> constructPath(Tile node) {
		LinkedList<Tile> path = new LinkedList<Tile>();
		  while (node.pathParent != null) {
		    path.addFirst(node);
		    node = node.pathParent;
		  }
		  return path;
	}

	/*
	 * Moves the ghost in the path constructed by the BFS method.
	 * @param moves - a list of tiles describes the path to move in.
	 */
	private void moveInPath(LinkedList<Tile> moves) {
		for(Tile move : moves) {
			if(move.column > this.position.column)
				if(moveToTile(move, RIGHT) == MOVE_SUCCESS)
					continue;
				else 
				{ hasPath=false;
					break; }
			if(move.column < this.position.column)
				if(moveToTile(move, LEFT) == MOVE_SUCCESS)
					continue;
				else 
				{ hasPath=false;
					break; }
			if(move.row < this.position.row)
				if(moveToTile(move, UP) == MOVE_SUCCESS) 
					continue;
				else 
				{ hasPath=false;
					break; }
			if(move.row > this.position.row)
				if(moveToTile(move, DOWN) == MOVE_SUCCESS) 
					continue;
				else 
				{ hasPath=false;
					break; }
		}
	}
	
	/*
	 * Moves ghost to a current tile in a certain direction.
	 * @param tile - tile to move to.
	 * @dir - the direction to try to move in.
	 * @return MOVE_SUCCESS or MOVE_SUCCESS - mover integer constants.
	 */
	private int moveToTile(Tile tile, int dir)
	{
		if(!this.position.equals(tile))
		{
			if(move(dir) == MOVE_SUCCESS)
				currentDirection = dir;
			if(this.position.equals(tile))
				return MOVE_SUCCESS;
		}
		return MOVE_FAIL;
	}
	
	/*
	 * Checks if the ghost encountered pacman.
	 */
	private void checkForEncounter()
	{
		if(this.position.equals(game.getPacman().position))
			game.getPacman().impact(this);
	}

	/*
	 * Moves in a random manner.
	 */
	protected void randomMovement()
	{
		checkCompletedArming();
		if (this.move(currentDirection) == MOVE_SUCCESS) {
			requestedDirection = currentDirection;
		} 
		else {
			randomMove();
			this.move(currentDirection);
		}
	}
	
	/*
	 * Changes the current direction the ghost is headed to a random possible move.
	 * @return the current direction after the random choice.
	 */
	public int randomMove() {  
		ArrayList<Integer> moves = getPossibleMoves(false);
		int move = moves.get(randomizer.nextInt(moves.size()));
		currentDirection = move;
		return currentDirection;
	}

	/*
	 * Defines behavior for the ghost after the ghost is dead.
	 */
	private void regenerate() {
		checkCompletedArming();
		game.getLevel().openCloseDoor(false);
		this.speed = 3*Game.paceController;
		if(this.position.equals(spawn)) {
			normalSpeed();
			this.state = GhostState.CHASE;
			chase(game.getPacman().position);
		}	
		else {
			this.chase(spawn);
		}
	}

	/*
	 * Defines behavior for the ghost in FLEE state - random movement.
	 */
	private void flee() {
		checkCompletedArming();
		this.speed = 1*Game.paceController;
		randomMovement();
	}

	/*
	 * Checks if pacman is near the ghost.
	 * Being near is defines by the game difficulty, on easy difficulty will check if the ghost is 2 tiles from pacman (South-North or West-South).
	 */
	private boolean pacmanNear()
	{
		return ( Math.abs(game.getPacman().position.column-this.position.column)<=(2+Game.difficulty) || Math.abs(game.getPacman().position.row-this.position.row)<=(2+Game.difficulty));
	}

	/*
	 * If the ghost has weapon, checks if the ghost has managed to arm or was interrupted during arming process. 
	 */
	protected void checkCompletedArming()
	{
		if(hasWeapon && (this.state != GhostState.FLEE & this.state != GhostState.DIED) && (initialMovementCounter==0 & !position.equals(corner))) 
		{
			this.state = GhostState.SCATTER;
			initialMovementCounter=0;
		}
	}

	public abstract void normalSpeed();  // Abstracy method to return ghost to its normal game speed.
	
	/*
	 * Private class to change the state of the ghost from random to chase according to the distance from pacman.
	 */
	private class StateChanger implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (state == GhostState.RANDOM & game.getPacman().getState() != PacmanState.DEAD)
				state = GhostState.CHASE;
			else if (state == GhostState.CHASE && !pacmanNear()) 
				state = GhostState.RANDOM;
		}
	}
	
	/*
	 * If the ghost has weapon starts the shooting delay.
	 */
	protected void startShootingDelay()
	{
		if(hasWeapon) {
			this.shootingDelay.start();
			GameMethods.playSound("armed.wav");
		}
	}
	
	/*
	 * Stops the ghost shooting delay.
	 */
	public void stopShootingDelay()
	{
		if(hasWeapon)
			shootingDelay.stop();
	}
	
	/*
	 * Method implemented by the visitor pattern to define impact from a visitor.
	 */
	@Override
	public void impact(Visitor visitor) {
		visitor.hit(this);
	}
	
	/*
	 * Gets the possible moves on board from the current ghost location.
	 * @param allowBacktrack - defines if to allow ghost to go back in track.
	 * @return a list of possible directions. (Integer defined by mover class)
	 */
	private ArrayList<Integer> getPossibleMoves(boolean allowBacktrack)
	{
		ArrayList<Integer> moves = getDirections();
		if(!allowBacktrack)
			moves.remove((Integer)reversedDirections[currentDirection]);
		return moves;
	}
	
	/*
	 * Gets all current possible directions that are passable from current ghost state.
	 * @return a list of possible passable movement directions.
	 */
	private ArrayList<Integer> getDirections()
	{
		ArrayList<Integer> moves = new ArrayList<Integer>();
		if(this.state != GhostState.RANDOM) {
			if(maze.board[position.column][position.row-1].type.isGhostPath())
				moves.add(UP); // UP
			if(maze.board[position.column][position.row+1].type.isGhostPath())
				moves.add(DOWN); // DOWN
			if(maze.board[position.column+1][position.row].type.isGhostPath())
				moves.add(RIGHT); // RIGHT
			if(maze.board[position.column-1][position.row].type.isGhostPath())
				moves.add(LEFT); // LEFT
		}
		else {  // Prevent entering back to spawn if currently in random movement mode.
			if(maze.board[position.column][position.row-1].type.isGhostPath() && maze.board[position.column][position.row-1].type != TileTypes.SPAWN)
				moves.add(UP); // UP
			if(maze.board[position.column][position.row+1].type.isGhostPath() && maze.board[position.column][position.row-1].type != TileTypes.SPAWN)
				moves.add(DOWN); // DOWN
			if(maze.board[position.column+1][position.row].type.isGhostPath() && maze.board[position.column][position.row-1].type != TileTypes.SPAWN)
				moves.add(RIGHT); // RIGHT
			if(maze.board[position.column-1][position.row].type.isGhostPath() && maze.board[position.column][position.row-1].type != TileTypes.SPAWN)
				moves.add(LEFT); // LEFT
		}
		if(moves.size() == 0) {
			this.state = GhostState.CHASE;
			return getDirections();
		}
		else return moves;
	}

	/*
	 * Paints the current ghost instance with opacity. 0.3 from original image.
	 */
	protected void disapear(Graphics g) {
		Graphics2D graphics = (Graphics2D) g.create();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		((Graphics) graphics).drawImage(this.animator.sprite, position.column*25+this.animator.dx, position.row*25+this.animator.dy, 22, 22, null);
		graphics.dispose();
	}

	public abstract void paint(Graphics g);
	
	/*
	 * Private class to control initial ghost movement counter.
	 */
	private class InitialMovementTimer implements ActionListener {
		
		private Ghost g;
		
		public InitialMovementTimer(Ghost g)
		{
			this.g=g;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			initialMovementCounter++;
			if(initialMovementCounter==4) {
				startShootingDelay();
				dangerflick = true;
			}
			else if(initialMovementCounter==9)
				dangerflick = false;
		}
	}
	
	//Getters and Setters
	public GhostState getState() {
		return state;
	}

	public Queue<Weapon> getAmmo() {
		return ammo;
	}

	public void setState(GhostState state) {
		this.state = state;
	}
}
