/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Engine.GameLoop;
import Engine.GameMethods;
import Engine.MazeLoader;
import Engine.ResourceLoader;
import Entities.EvilPacman;
import Entities.Ghost;
import Entities.Ghost.GhostState;
import Entities.NormalPacman;
import Entities.Pacman;
import Entities.Pacman.PacmanState;
import Entities.ShieldedPacman;
import Game.GameDialog;
import Pickups.Fruit;

public class Game extends JFrame{

	private Container container; // Main game window container
	private Menu menu; // Menu panel
	private ControlsMenu controls; //Controls menu panel
	private GameView view; // Game view panel - game canvas
	private HUD hud; // Bottom game details menu panel
	
	protected final String TITLE = "PAC-MAN Adventures"; // Game title
	//Game dimensions
	public static final int WIDTH =800;
	public static final int HEIGHT = 800;
	protected final static Dimension GAME_SIZE = new Dimension(800, 1000); // Window dimension
	
	//Game states
	public static enum GameState { MENU, READY, PLAY, LEVEL_PASS, OVER, WON, PAUSED }
	public static enum PacmanMovmentState { CONTINUOUS, ON_DEMAND }
	public static GameState state;
	public PacmanMovmentState movement;
	
	private GameTimer timer; // Main game timer - controls effect duration and renewal of items.
	private Pacman pacman; // Current game pacman instance
	protected boolean isOver; // Determines if the game is over
	public boolean started = false; // Determines if the game has started

	//Static game details
	public static int score = 0;
	public static int lives = 3;
	public static int difficulty = 0;
	public static int currentLevel = 1;
	public static int paceController = 1;
	public static boolean sound = true; // Sound state
	
	public int ROWS, COLUMNS; // Game rows and coulmns
	private Level level; // Current game level
	private Maze initialMaze; // Starting game maze
	private static ArrayList<Integer> unVisitedMazes; // Mazes options to visit.
	
	private SwitchLevelListener levelSwitcher; // Listens to key presses in the passage of levels.
	private DeveloperListener developerListner; // Adds option for developers and checkers to switch levels programmatically by pressing CTRL+P

	//Constructor
	public Game()
	{
		this.view = new GameView(this);
		this.hud = new HUD(this);
		this.container = new JPanel();
		this.levelSwitcher = new SwitchLevelListener(this);
		
		state = GameState.MENU;
		this.timer = new GameTimer(this);
		unVisitedMazes = new ArrayList<Integer>();
		for(int i=1; i<=MazeLoader.getMazeCount(); i++)
			unVisitedMazes.add(i);
		
		this.menu = new Menu(this);
		this.controls = new ControlsMenu(this);

		ROWS = HEIGHT/Tile.TILE_SIZE;
		COLUMNS = WIDTH/Tile.TILE_SIZE;
		
		this.setPreferredSize(GAME_SIZE);
		this.setMinimumSize(GAME_SIZE);
		this.setMaximumSize(GAME_SIZE);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(new ImageIcon(getClass().getResource("/icons/game_icon.png")).getImage());
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setTitle(TITLE);
		container.setLayout(new GridBagLayout());      
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 0; gbc.gridy = 0;
		container.setBackground(Color.BLACK);
		
		menu.setFocusable(true);
		
		container.add(menu, gbc);
		container.requestFocus();
		
		this.setContentPane(container);
		this.pack();
	}

	/*
	 * Starts a new game
	 */
	public void start()
	{
		this.started=true;
		state = GameState.READY;
		container.removeAll();
		container.setLayout(new GridBagLayout());      
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 0; gbc.gridy = 0;
		container.add(controls, gbc);
		gbc.gridx = 0; gbc.gridy = 1;
		container.add(view, gbc);
		gbc.gridx = 0; gbc.gridy = 2;
		container.add(hud, gbc);
		this.view.removeKeyListener(levelSwitcher);
		this.view.addKeyListener(new DeveloperListener(this));
		view.addKeyListener(this.pacman);
		this.setContentPane(container);
		view.setFocusable(true);
		view.requestFocus();
		timer.start();
	}
	
	/*
	 * Starts a new level,
	 */
	public void startNewLevel()
	{
		Random rand = new Random();
		ArrayList<Fruit> collectedFruits = new ArrayList<Fruit>();
		switch(currentLevel)
		{
		case 1:
			this.pacman = new NormalPacman(this);
			this.level = new Level(this, 1);
			this.level.setFruitAmount(2, 1, 0, 2);
			this.level.setMaze(initialMaze);
			unVisitedMazes.remove((Integer)initialMaze.mazeNumber);
			break;
		case 2:
			collectedFruits = this.pacman.getCollectedFruits();
			this.pacman = new ShieldedPacman(this);
			this.level = new Level(this, 2);
			this.level.setMaze(MazeLoader.getMazes().get(unVisitedMazes.get(rand.nextInt(unVisitedMazes.size()))));
			unVisitedMazes.remove((Integer)this.level.getMaze().mazeNumber);
			this.level.setFruitAmount(4, 2, 1, 4);
			break;
		case 3:
			collectedFruits = this.pacman.getCollectedFruits();
			this.pacman = new EvilPacman(this);
			this.level = new Level(this, 3);
			this.level.setMaze(MazeLoader.getMazes().get(unVisitedMazes.get(rand.nextInt(unVisitedMazes.size()))));
			unVisitedMazes.remove((Integer)this.level.getMaze().mazeNumber);
			level.setFruitAmount(5, 3, 2, 5);
			break;
		case 4:
			collectedFruits = this.pacman.getCollectedFruits();
			this.pacman = new NormalPacman(this);
			this.level = new Level(this, 4);
			this.level.setMaze(MazeLoader.getMazes().get(unVisitedMazes.get(rand.nextInt(unVisitedMazes.size()))));
			unVisitedMazes.remove((Integer)this.level.getMaze().mazeNumber);
			level.setFruitAmount(6, 4, 3, 6);
			break;
		}
		this.level.generateGhosts();
		setPacman(collectedFruits);
	}
	
	/*
	 * Sets the game pcaman instance by current level
	 */
	public void setPacman(ArrayList<Fruit> collectedFruits)
	{
		this.pacman.setState(PacmanState.NORMAL);
		this.pacman.setStatus("NORMAL");
		this.pacman.setAnimator();
		this.pacman.setMaze(this.level.getMaze());
		this.pacman.setCoordinates(this.level.getMaze().pacmanPosition);
		this.pacman.setPosition(this.level.getMaze().board[this.level.getMaze().pacmanPosition.x][this.level.getMaze().pacmanPosition.y]);
		this.pacman.getAnimator().resetOffset();
		this.pacman.setCollectedFruits(collectedFruits);
	}
	
	/*
	 * Adds an integer score to the game score.
	 */
	public void addScore(int score)
	{
		if(Game.score + score < 0)
			Game.score=0;
		else Game.score+=score;
	}
	
	/*
	 * Pops game dialog input box and checks if current game score entered the high scores table.
	 */
	public void checkForHighestScore()
	{	
		Score playerScore = enterNickname();
		if(playerScore != null) {
			ResourceLoader.highestScores.add(playerScore);
			Collections.sort(ResourceLoader.highestScores);
			Collections.reverse(ResourceLoader.highestScores);
			ResourceLoader.highestScores.removeLast();
			if(playerScore != null && ResourceLoader.highestScores.getFirst().equals(playerScore))
				highestScoreSet(playerScore);
			ResourceLoader.writeHighestScores();
		}
	}
	
	/*
	 * Called if the player managed to set the highest score.
	 */
	private void highestScoreSet(Score playerScore) {
		Score highest = ResourceLoader.highestScores.getFirst();
		if(highest.name.equals(playerScore.name))
		{
			GameDialog dialog = new GameDialog(this, "CONGATULATIONS! YOU HAVE SET THE HIGHEST SCORE!", false, false, true, new Dimension(750, 200));
		}
	}

	/*
	 * Pops a Game Dialog for the user to enter nickname to be potentially inserted to the high scores table.
	 * @return a Score instance with the scoring details, nickname, score and fruit count. 
	 */
	public Score enterNickname()
	{
		String input;
		GameDialog dialog = new GameDialog(this, "GAME OVER ! ENTER NICKNAME:", true, false, false, new Dimension(460, 200));
        String showInputDialog = dialog.getValidatedText();
	    input = String.valueOf(showInputDialog);
	    if(input != null & !input.equals("null")) {
	    	Score newScore = new Score(input, score, pacman.getCollectedFruits());
			return newScore;
	    }
	    else return null;
	}

	/*
	 * Decreases player game lives.
	 * If it is the last lives remaining calls the lose method and check for high score, if not calls the hurt method.
	 */
	public void decreaseLives() {
		if(Game.lives == 1) {
			for(Ghost g : level.getGhosts()) {
				g.stopShootingDelay();
			}
			this.pacman.setState(PacmanState.DEAD);
			repaint();
			GameLoop.pause(true);
			timer.stop();
			lose();
			view.addKeyListener(new GameOverListener(this));
			checkForHighestScore();
		}
		else hurt();
		
	}

	/*
	 * Decreases game lives restarts all game ghosts to scatter.
	 */
	private void hurt() {
		GameMethods.playSound("pacman_dead.wav");
		lives--;
		this.pacman.setState(PacmanState.DEAD);
		for(Ghost g : level.getGhosts()) {
			if(g.getState() != GhostState.CAGED && g.getState() != GhostState.FLEE && g.getState() != GhostState.DIED && g.getState() != GhostState.FROZEN )
				g.setState(GhostState.SCATTER);
		}
		timer.startPacmanDeadDelay();
	}


	/*
	 * Changes game state to over.
	 */
	private static void lose() {
		Game.paceController=1;
		Game.lives = 0;
		GameMethods.playSound("game_over.wav");
		Game.state = GameState.OVER;
	}

	/*
	 * Starts god-mode in which all ghosts are vulnerable and pacman can eat them.
	 */
	public void godMode() {
		for(Ghost g : this.level.getGhosts())
		{
			if(g.getState() != GhostState.DIED & g.getState() != GhostState.CAGED) {
				g.getAnimator().resetOffset();
				g.setState(GhostState.FLEE);
			}
		}
		GameTimer.startGodMode();
	}

	/*
	 * Moves all game entities.
	 */
	public void move() {
		if(!level.won & state == GameState.PLAY) {
			pacman.movment();
			for(Ghost g : this.level.getGhosts())
			{
				g.movment();
			}
		}
	}
	
	/*
	 * Starts the next level if there are more levels to pass.
	 * If all level passes changes game state to Won and checks for high score.
	 */
	public void levelPass() {
		if(currentLevel < 4) {
			currentLevel++;
			GameTimer.levelRelativeScore += Game.score;
			startNewLevel();
			timer.restartTimer();
			start();
		}
		else {
			Game.state = GameState.WON;
			timer.stop();
			this.view.removeKeyListener(levelSwitcher);
			view.addKeyListener(new GameOverListener(this));
			checkForHighestScore();
		}
		this.repaint();
	}

	/*
	 * Changes the window current active listeners and resets states in level switch.
	 */
	public void switchLevel() {
		timer.stop();
		pacman.setState(PacmanState.NORMAL);
		for(Ghost g : level.getGhosts()) {
			g.stopShootingDelay();
			g.changeGameSpeed(false);
		}
		level.won = true;
		Game.state = GameState.LEVEL_PASS;
		this.view.removeKeyListener(this.pacman);
		this.view.addKeyListener(levelSwitcher);
		this.repaint();
	}
	
	public void paintComponent(Graphics g) {
		view.paint(g);
		hud.paint(g);
	}
	
	/*
	 * Private class to control key presses while on level switch state.
	 */
	private class SwitchLevelListener implements KeyListener{

		Game game;
		
		public SwitchLevelListener(Game game)
		{
			this.game=game;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
				levelPass();
			}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				for(Ghost g : level.getGhosts()) {
					g.stopShootingDelay();
				}
				Game.state = Game.GameState.OVER;
				repaint();
				checkForHighestScore();
				game.view.removeKeyListener(this);
				game.view.addKeyListener(new GameOverListener(game));
			}
			
		}
		@Override
		public void keyTyped(KeyEvent e) {	}
		@Override
		public void keyReleased(KeyEvent e) {}
	}
	
	/*
	 * A listener to add CRTL+P level pass level switch - for development purposes.
	 */
	private class DeveloperListener implements KeyListener{

		Game game;
		
		public DeveloperListener(Game game)
		{
			this.game=game;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_P) {
				game.view.removeKeyListener(levelSwitcher);
				game.level.wonLevel();
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyReleased(KeyEvent e) {}
	}
	
	/*
	 * Private class to control key presses while on game over state.
	 */
	private class GameOverListener implements KeyListener{

		Game game;
		
		public GameOverListener(Game game)
		{
			this.game=game;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
				GameLoop.restart();
			}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				GameDialog dialog = new GameDialog(game, "ARE YOU SURE YOU WANT TO EXIT?", false, true, false, new Dimension(500, 200));
			}
			
		}
		
		@Override
		public void keyTyped(KeyEvent e) {	}
		@Override
		public void keyReleased(KeyEvent e) {}
	}
	
	//Getters and Setters
	public boolean isOver()
	{
		return isOver;
	}
	
	public GameView getView() {
		return view;
	}

	public void setView(GameView view) {
		this.view = view;
	}
	
	public Maze getCurrentMaze()
	{
		return this.level.getMaze();
	}
	
	public Level getLevel() {
		return level;
	}

	public String getPacmanMood() {
		return (this.pacman instanceof EvilPacman) ? "EVIL" : (this.pacman instanceof ShieldedPacman) ? "SHIELDED" : "NORMAL";
	}
	
	public Menu getMenu() {
		return menu;
	}
	
	public GameTimer getTimer() {
		return timer;
	}
	
	public Pacman getPacman() {
		return pacman;
	}
	
	public static ArrayList<Integer> getUnVisitedMazes() {
		return unVisitedMazes;
	}


	public static void setUnVisitedMazes(ArrayList<Integer> unVisitedMazes) {
		Game.unVisitedMazes = unVisitedMazes;
	}

	
	public void setInitialMaze(Maze initialMaze) {
		this.initialMaze = initialMaze;
	}
}
