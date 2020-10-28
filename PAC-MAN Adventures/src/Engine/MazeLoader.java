/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Engine;

import Pickups.*;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import Game.Maze;
import Game.Portal;
import Game.Tile;
import Game.TileTypes;

/*
 * Loads all mazes and stores them in the relevant static HashMap.
 */
public class MazeLoader {
	
	private static HashMap<Integer, Maze> mazes; // Holds all mazes by key maze number.
	private static final int MAZE_COUNT = 5; // Total maze count to load.

	//Constructor
	public MazeLoader()
	{
		mazes = new HashMap<Integer, Maze>();
	}
	
	/*
	 * Loads all mazes.
	 */
	public void load()
	{
		for(int i=1; i<=MAZE_COUNT; i++)
		{
			readMaze(i);
		}
	}
	
	/*
	 * Reads maze from pixelated images by pixel colors and initialized the game board matrix.
	 * @param index - current maze index to read.
	 */
	private void readMaze(int index)
	{
		try {
			Maze maze = new Maze(ImageIO.read(ResourceLoader.load("mazes/maze"+index+".logic.png")), ImageIO.read(ResourceLoader.load("mazes/maze"+index+".png")), index);
			int mazewidth = maze.getTemplate().getWidth();
			int mazeheight = maze.getTemplate().getHeight();
			int pixels[] = new int[mazewidth*mazeheight];
			maze.getTemplate().getRGB(0, 0, 32, 32, pixels, 0, 32);
            
	        for (int y = 0;  y< 32; y++) {
	            for (int x = 0; x < 32; x++) {
	            	int color = pixels[x + (y*32)];
	            	switch(color) {
	            	case -16777216:  //Black pixels
	            		maze.board[x][y] = new Tile(TileTypes.PATH, x, y); // Black pixels - pathes
	            		maze.getOpenSpots().add(maze.board[x][y]);
	            		maze.pathTiles++;
	            		break;
	            	case -1: {
	            		maze.board[x][y] = new Tile(TileTypes.WALL, x, y); //White pixels - walls
	            		maze.getWalls().add(maze.board[x][y]);
	            		}
	            		break;
	            	case -10240:  //Yellow pixels
	            		maze.board[x][y] = new Tile(TileTypes.PATH, x, y);
	            		maze.pacmanPosition = new Point(x,y); //Pacman starting position - Yellow pixels
	            		maze.getOpenSpots().add(maze.board[x][y]);
	            		maze.pathTiles++;
	            		break;
	            	case -65536:  maze.board[x][y] = new Tile(TileTypes.DOOR, x, y); //Red pixels - door
            					maze.door.add(maze.board[x][y]);
	            		break;
	            	case -11731200: maze.board[x][y] = new Tile(TileTypes.SPAWN, x, y); //Green pixels - ghost spawn
	            			maze.getGhostSpawn().add(maze.board[x][y]);
	            		break;
	            	case -16739073: maze.board[x][y] = new Tile(TileTypes.CORNER, x, y); //Blue pixels - board corners
	            		break;
	            	case -12058369: maze.board[x][y] = new Tile(TileTypes.PORTAL, x, y); //Purple pixels - portal positions
	            					maze.getPortals().add(new Portal(maze.board[x][y], maze));
            			break;
            		default: maze.board[x][y] = new Tile(TileTypes.PATH, x, y);
	            		maze.getOpenSpots().add(maze.board[x][y]);
	            		maze.pathTiles++;
            			break;
	            	}
	            }
	        }
	        setPortalsDirections(maze);
	        generateCoinsAndPills(maze);
	        setNeighbours(maze);
	        mazes.put(index, maze);
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Sets for each tile in the maze board matrix an adjacency list.
	 * @param maze - maze to create adjacency list for.
	 */
	private void setNeighbours(Maze maze) {
		  for (int y = 0;  y< 32; y++) {
	            for (int x = 0; x < 32; x++) {
	            maze.board[x][y].setNeighbours(maze);
	            }
		  }
	}

	/*
	 * Sets all portal exit directions.
	 * @param maze - a maze to set portal directions for.
	 */
	private static void setPortalsDirections(Maze maze)
	{
		for(Portal portal : maze.getPortals())
			portal.setExitDirection();
	}
	
	/*
	 * Generates all coins and pills for a certain maze.
	 * @maze - a maze to generate coin and pills on.
	 */
	private static void generateCoinsAndPills(Maze maze)
	{
		int skip = (int) Math.ceil((double)maze.pathTiles/maze.COINS_TO_PLACE);  // Tile skip between coins.
		  for (int y = 0;  y< 32; y++)
	            for (int x = 0; x < 32; x++) {
	            if (coinPlacmentRestrictions(maze , x, y, skip))  //Placing coins
	            {
	            	maze.getCoins().add(new Coin(maze.board[x][y]));
	            	maze.getOpenSpots().remove(maze.board[x][y]);
	            	maze.board[x][y] = new Tile(TileTypes.COIN, x ,y);
	            	maze.COINS_TO_PLACE--;
	            }
	            else if (maze.board[x][y].type == TileTypes.CORNER) //Placing Energy pills
	            {
	            	maze.getOpenSpots().remove(maze.board[x][y]);
	            	maze.board[x][y] = new Tile(TileTypes.CORNER, x, y);
					maze.getGhostCorners().add(maze.board[x][y]);
	            	maze.getCorners().add(maze.board[x][y]);
	            	maze.getEnergyPills().add(new EnergyPill(maze.board[x][y]));
	            }
        }
	  Random rand = new Random(); // If not all coins was placed, randomize spots for remaining coins.
	  while (maze.COINS_TO_PLACE != 0) { 
		  int col = rand.nextInt(31);
		  int row = rand.nextInt(31);
		  if(maze.board[col][row].type == TileTypes.PATH && maze.pacmanPosition.x != col & maze.pacmanPosition.y != row)
		  {
			  	maze.getOpenSpots().remove(maze.board[col][row]);
            	maze.getCoins().add(new Coin(maze.board[col][row]));
            	maze.board[col][row] = new Tile(TileTypes.COIN, col ,row);
            	maze.COINS_TO_PLACE--;
		  }
	  }
	}
	
	/*
	 * Restrictions for coin placement.
	 * @param maze - current maze to position coins on.
	 * @param x - current maze board column.
	 * @param y - current maze board row.
	 * @param skip - current skip parameter between coins.
	 */
	private static boolean coinPlacmentRestrictions(Maze maze, int x, int y, int skip)
	{
		return maze.board[x][y].type == TileTypes.PATH 
				&& (x % skip == 0 | y % skip== 0) & !(x % skip == 0 & y % skip == 0) 
				&& (maze.pacmanPosition.x != x & maze.pacmanPosition.y != y)
				& maze.COINS_TO_PLACE > 0 ;
	}

	//Getters and Setters
	public static int getMazeCount() {
		return MAZE_COUNT;
	}

	public static HashMap<Integer, Maze> getMazes() {
		return mazes;
	}
}
