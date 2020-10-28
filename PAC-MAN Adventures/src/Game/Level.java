/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import Pickups.*;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import Engine.GameMethods;
import Entities.BlueGhost;
import Entities.CommandoGhost;
import Entities.Ghost;
import Entities.GreenGhost;
import Entities.PurpleGhost;
import Entities.RedGhost;
import Entities.YellowGhost;

/*
 * Describes a game level.
 */
public class Level {
	
	//Fields
	private Game game;  // Relevant game instance
	private Maze maze; // Level maze
	private ArrayList<Ghost> ghosts; // Level ghosts
	public int levelNumber; // Current level number
	public boolean won = false; // Determines if level won.
	private boolean doorClosed = true; // Determines if ghosts cage is closed.
	private Random randomizer; // Used to randomize items locations.
	
	boolean fruitsAvailable; // Determines if level fruits re currently available.

	private CollectedScores visibleScores; // Current visible scores
	
	private TNT tnt; // TNT instance in level
	private Elixir elixir; // Elxiir instance in level
	private Summoner summoner; // Summoner instance in level
	private int pinapples, strawberries, bananas, grapes; // Different level fruit amounts
	
	//Constructor
	public Level(Game game, int levelNumber)
	{
		this.game = game;
		this.visibleScores = new CollectedScores();
		this.fruitsAvailable = false;
		this.randomizer = new Random();
		this.levelNumber = levelNumber;
	}
	
	/*
	 * Generates all ghosts and assigned them by the current level number
	 */
	public void generateGhosts()
	{
		this.ghosts = new ArrayList<Ghost>();
		Ghost ghost1 = new YellowGhost(game, getGhostLocation());
		Ghost ghost2 = new BlueGhost(game, getGhostLocation());
		Ghost ghost3 = new RedGhost(game, getGhostLocation());
		Ghost ghost4 = new PurpleGhost(game, getGhostLocation());
		Ghost ghost5 = new CommandoGhost(game, getGhostLocation());
		Ghost ghost6 = new GreenGhost(game, getGhostLocation());
		
		if(levelNumber == 1) {			//LEVEL 1 GHOSTS
			this.ghosts.add(ghost1);   //YellowGhost - fast
		}
		else if (levelNumber == 2)		//LEVEL 2 GHOSTS
		{
			this.ghosts.add(ghost1);	//YellowGhost - fast
			this.ghosts.add(ghost2);	//BlueGhost - freezing
			this.ghosts.add(ghost6);	//GreeneGhost - toxic poisoner
		}
		if (levelNumber == 3)			//LEVEL 3 GHOSTS
		{
			this.ghosts.add(ghost1);	//YellowGhost - fast
			this.ghosts.add(ghost2);	//BlueGhost - freezing
			this.ghosts.add(ghost3);	//RedGhost - fire shooter
			this.ghosts.add(ghost4);	//PurpleGhost - spell caster (inverts controls)
		}
		if (levelNumber == 4)			//LEVEL 4 GHOSTS (BONUS LEVEL)
		{
			this.ghosts.add(ghost1);	//YellowGhost - fast
			this.ghosts.add(ghost2);	//BlueGhost - freezing
			this.ghosts.add(ghost3);	//RedGhost - fire shooter
			this.ghosts.add(ghost5);	//CommandoGhost - trapper	
		}
	}

	/*
	 * Generates elixir on board
	 */
	public void generateElixir() {
		
		elixir = new Elixir(maze.getOpenSpots().get(randomizer.nextInt(maze.getOpenSpots().size())));
	}
	
	/*
	 * Disposes elixir on board
	 */
	public void disposeElixir() {
		GameMethods.playSound("elixir.wav");
		visibleScores.addScore(elixir);
		game.addScore(elixir.score);
		this.elixir = null;
	}
	
	/*
	 * Generates summoner on board
	 */
	public void generateSummoner()
	{
		GameMethods.playSound("summoner.wav");
		summoner = new Summoner(maze.getOpenSpots().get(randomizer.nextInt(maze.getOpenSpots().size())));
	}
	
	/*
	 * Disposes summoner on board
	 */
	public void disposeSummoner(boolean collected) {
		if(collected) {
			visibleScores.addScore(summoner);
			game.addScore(summoner.score);
		}
		this.summoner=null;
	}
	
	/*
	 * Disposes tnt on board
	 */
	public void disposeTNT(boolean collected) {
		if(collected) {
			visibleScores.addScore(tnt);
			game.addScore(tnt.score);
		}
		this.tnt = null;
	}
	
	/*
	 * Generates energy pills on board
	 */
	public void generatePills() {
		for(int i=0; i<4; i++) {
			Tile corner = maze.getCorners().get(i);
			maze.getEnergyPills().add(new EnergyPill(corner));
			maze.board[corner.column][corner.row].setType(TileTypes.CORNER);
		}
	}

	/*
	 * Generates fruits on board
	 */
	public void generateFruits()
	{
		for(int i=0; i<pinapples; i++) {
			int location = randomizer.nextInt(maze.getOpenSpots().size());
			maze.getFruits().add(new Pineapple(maze.getOpenSpots().get(location)));
			maze.getOpenSpots().remove(location);
		}
		for(int i=0; i<strawberries; i++) {
			int location = randomizer.nextInt(maze.getOpenSpots().size());
			maze.getFruits().add(new Strawberry(maze.getOpenSpots().get(location)));
			maze.getOpenSpots().remove(location);
		}
		for(int i=0; i<bananas; i++) {
			int location = randomizer.nextInt(maze.getOpenSpots().size());
			maze.getFruits().add(new Bananas(maze.getOpenSpots().get(location)));
			maze.getOpenSpots().remove(location);
		}
		for(int i=0; i<grapes; i++) {
			int location = randomizer.nextInt(maze.getOpenSpots().size());
			maze.getFruits().add(new Grapes(maze.getOpenSpots().get(location)));
			maze.getOpenSpots().remove(location);
		}
	}
	
	/*
	 * Stars the fruits dissolving until disappearance
	 */
	public void disolveFruits()
	{
		for(Fruit fruit : maze.getFruits()) {
			fruit.setDisolve(true);
			fruit.getAnimator().disolvable=true;
		}
	}
	
	/*
	 * Removes all fruits from board
	 */
	public void removeFruits()
	{
		for(Fruit fruit : maze.getFruits()) {
			maze.getOpenSpots().add(fruit.getPosition());
		}
		maze.setFruits(new ConcurrentLinkedQueue<Fruit>());
	}
	
	/*
	 * Determines if  fruits are still visible on board
	 */
	public boolean fruitsVisible()
	{
		int fruitCount=0;
		for(Fruit fruit : maze.getFruits()) {
			if(fruit.getAnimator().opacity == 0.0f)
				fruitCount++;
		}
		if(fruitCount == this.getMaze().getFruits().size())
			return false;
		else return true;
	}
	
	/*
	 * Gets a randomized initial ghost spawn place.
	 */
	public Tile getGhostLocation()
	{
		int index = randomizer.nextInt(maze.getGhostSpawn().size());
		Tile ghost = maze.getGhostSpawn().get(index);
		maze.getGhostSpawn().remove(index);
		return ghost;
	}
	
	/*
	 * Determines if the level is won
	 */
	public void wonLevel() {
		
		won=true;
		if(levelNumber != 4) GameMethods.playSound("level_pass.wav");
		else GameMethods.playSound("game_won.wav");
		game.switchLevel();
	}
	
	/*
	 * Remove collectable item from board and adds the relevant game score for the item collected.
	 * adds the relevant visible score to show in game for the item collected.
	 */
	public void remove(Collectable c, ConcurrentLinkedQueue<?> list)
	{
		list.remove(c);
		visibleScores.addScore(c);
		game.addScore(c.score);
	}
	
	/*
	 * Changes door tiel to path tiles - opens the door.
	 */
	public void openCloseDoor(boolean close)
	{
		if(close) {
			for(Tile d :maze.door)
				d.setType(TileTypes.DOOR);
				doorClosed = true;
		}
		else {
			for(Tile d :maze.door)
				d.setType(TileTypes.PATH);
			doorClosed = false;
		}
	}
	
	/*
	 * Paints level will all the current collectables
	 */
	public void paint(Graphics g)
	{
		 g.drawImage(maze.getFullMaze(), 0, 0, Game.WIDTH, Game.HEIGHT, null);
		for (Tile tile : maze.door) {
			try {
				if(tile.type == TileTypes.DOOR) g.drawImage(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/patterns/door.png"))).getImage(), tile.column*25, tile.row*25, 25, 25, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (Coin coin : maze.getCoins())
			coin.paintCoins(g);
		for (EnergyPill pill : maze.getEnergyPills())
			pill.paintPill(g);
		for (Portal portal : maze.getPortals())
			portal.paint(g);
		for (Fruit fruit : maze.getFruits())
			fruit.paint(g);
		
		visibleScores.paint(g);
		if(summoner != null) summoner.paint(g);
		if(this.tnt != null) tnt.paint(g);
		if(this.elixir != null) elixir.paint(g);
	}

	//Getters and Setters
	public void setFruitAmount(int pinapples, int strawberries, int bananas, int grapes)
	{
		this.pinapples = pinapples;
		this.strawberries = strawberries;
		this.bananas = bananas;
		this.grapes = grapes;
	}
	
	public CollectedScores getVisibleScores() {
		return visibleScores;
	}

	public void setVisibleScores(CollectedScores visibleScores) {
		this.visibleScores = visibleScores;
	}
	
	public void setGhosts(ArrayList<Ghost> ghosts)
	{
		this.ghosts = ghosts;
	}
	
	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	public Maze getMaze() {
		return maze;
	}

	public TNT getTnt() {
		return tnt;
	}

	public void setTnt(TNT tnt) {
		this.tnt = tnt;
	}


	public Elixir getElixir() {
		return elixir;
	}


	public void setElixir(Elixir elixir) {
		this.elixir = elixir;
	}
	
	public ArrayList<Ghost> getGhosts() {
		return ghosts;
	}

	public Summoner getSummoner() {
		return summoner;
	}
}
