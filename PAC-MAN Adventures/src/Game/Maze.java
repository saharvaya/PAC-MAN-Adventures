/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import Pickups.*;

/*
 * Describes a game maze representation
 */
public class Maze {
	
	//Fields
	public Tile[][] board; // Tile matrix board representation
	public int pathTiles; // Maze current amount of path tiles.
	public Point pacmanPosition; //Maze pacman initial position
	private BufferedImage template; //Maze logical template image
	private BufferedImage fullMaze; // Maze image representation
	
	public int COINS_TO_PLACE = 240; //Amount of coins to spread on board
	
	//Special maze collectables
	private ConcurrentLinkedQueue<Fruit> fruits;
	private ConcurrentLinkedQueue<Coin> coins;
	private ConcurrentLinkedQueue<EnergyPill> energyPills;
	
	//Special maze tiles
	public ArrayList<Tile> door;
	private ArrayList<Portal> portals;
	private ArrayList<Tile> corners;
	private ArrayList<Tile> ghostCorners;
	private ArrayList<Tile> ghostSpawn;
	private ArrayList<Tile> openSpots;
	private ArrayList<Tile> walls;

	public int mazeNumber; // Current maze number

	//Constructor
	public Maze(BufferedImage template, BufferedImage fullMaze, int mazeNumber)
	{
		this.pathTiles = 0;
		this.template = template;
		this.fullMaze = fullMaze;
		this.board = new Tile[Game.WIDTH/Tile.TILE_SIZE][Game.HEIGHT/Tile.TILE_SIZE];
		this.ghostSpawn = new ArrayList<Tile>();
		this.fruits = new ConcurrentLinkedQueue<Fruit>();
		this.coins = new ConcurrentLinkedQueue<Coin>();
		this.energyPills = new ConcurrentLinkedQueue<EnergyPill>();
		this.corners = new ArrayList<Tile>();
		this.ghostCorners = new ArrayList<Tile>();
		this.portals = new ArrayList<Portal>();
		this.door = new ArrayList<Tile>();
		this.openSpots = new ArrayList<Tile>();
		this.walls = new ArrayList<Tile>();

		this.mazeNumber = mazeNumber;
	}

	//Getters and Setters
	public Point getPacmanPosition() {
		return pacmanPosition;
	}

	public void setPacmanPosition(Point pacmanPosition) {
		this.pacmanPosition = pacmanPosition;
	}
	
	public ArrayList<Portal> getPortals() {
		return portals;
	}
	
	public ConcurrentLinkedQueue<Coin> getCoins() {
		return coins;
	}
	
	public ConcurrentLinkedQueue<EnergyPill> getEnergyPills() {
		return energyPills;
	}
	
	public BufferedImage getTemplate() {
		return template;
	}

	public void setTemplate(BufferedImage template) {
		this.template = template;
	}

	public BufferedImage getFullMaze() {
		return fullMaze;
	}

	public void setFullMaze(BufferedImage fullMaze) {
		this.fullMaze = fullMaze;
	}

	public int getPathTiles() {
		return pathTiles;
	}

	public ConcurrentLinkedQueue<Fruit> getFruits() {
		return fruits;
	}

	public ArrayList<Tile> getGhostSpawn() {
		return ghostSpawn;
	}

	public ArrayList<Tile> getOpenSpots() {
		return openSpots;
	}

	public ArrayList<Tile> getWalls() {
		return walls;
	}

	public void setFruits(ConcurrentLinkedQueue<Fruit> fruits) {
		this.fruits = fruits;
	}

	public ArrayList<Tile> getGhostCorners() {
		return ghostCorners;
	}

	public ArrayList<Tile> getCorners() {
		return corners;
	}
}
