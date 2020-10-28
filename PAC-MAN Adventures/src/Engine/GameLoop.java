/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Engine;

import Game.Game;
import Game.Game.GameState;

import javax.swing.JDialog;

import Entities.Ghost;

/*
 * Class that handles all game operations.
 * Initializes all game components, moves entities and renders view.
 */
public class GameLoop implements Runnable {
	
	private static Game game; // The current game running
	private static JDialog loading; // Loading dialog frame.
	private static boolean initialized = false; // Determines if the game is initialized.
	public static boolean paused = false; // Determines if that game is currently paused.

	public GameLoop () {}
	
	/*
	 * Main thread operation.
	 * Ticks all game operations.
	 */
	public void run(){

	    init(); // Initialize components.
	    
	    int fps = 60; //number of updates per second.
	    double tickPerSecond = 1000000000/fps;
	    double delta = 0;
	    long now;
	    long lastTime = System.nanoTime();

	    if(initialized) {
		    while(!game.isOver()){
			        now = System.nanoTime();
			        delta += (now - lastTime)/tickPerSecond;
			        lastTime = now;
		
			        if(delta >= 1){
			            if(!paused) tick();
			            render();
			            delta--;
			        }
		    }
		    if(game.isOver()) restart();
	    }
	}

	/*
	 * Initializes game components and starts a new game instance.
	 */
	private void init() {
		loading = GameMethods.showLoading();
		loading.setVisible(true);
		if(!initialized) {
        	ResourceLoader loader = new ResourceLoader();
            loader.load();
            MazeLoader mazes = new MazeLoader();
            mazes.load();
            GameMethods.setGameLookAndFeel();
        	game = new Game();
  	   		game.setVisible(true);
            initialized = true;
            loading.dispose();
		}
	}
	
	/*
	 * Restarts a new game.
	 */
	public static void restart()
	{
		initialized = false;
		loading.pack();
		loading.setVisible(true);
		game.dispose();
		if(!initialized) {
	        MazeLoader mazes = new MazeLoader();
	        mazes.load();
			game = new Game();
			game.getTimer().resetTimers();
			Game.sound = true;
			Game.currentLevel = 1;
			Game.score = 0;
			Game.lives = 3;
			Game.paceController=1;
	   		game.setVisible(true);
	   		GameLoop.paused=false;
	        initialized = true;
	        loading.dispose();
		}
	}
	
	/*
	 * Pauses current active game, switched game state to paused.
	 */
	public static void pause(boolean pause)
	{
		GameLoop.paused=pause;
		Game.state = GameState.PAUSED;
	}

	/*
	 * Main game tick.
	 * Moves all game entities and animations.
	 */
	private void tick(){
		if(Game.state == GameState.PLAY) {
			game.move();
		}
	}

	/*
	 * Renders entire game view.
	 */
	private void render(){
  		if(Game.state == GameState.PLAY || Game.state == GameState.READY || Game.state == GameState.LEVEL_PASS || Game.state == GameState.PAUSED || Game.state == GameState.OVER) {
			game.repaint();
		}
		else if(Game.state == GameState.MENU) {
			game.getMenu().repaint();
			game.getMenu().requestFocus();
		}	
	}
}
