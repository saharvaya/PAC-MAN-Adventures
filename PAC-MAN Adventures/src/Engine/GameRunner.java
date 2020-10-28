/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Engine;

import Engine.GameLoop;

/*
 * Runs the game, holds the main method.
 */
public class GameRunner {
	
	public static void main(String[] args) {
		start();
	}
	
	/*
	 * Starts a new game loop to initialize the game.
	 */
	static public void start()
	{
		GameLoop loop = new GameLoop();
		loop.run();
	}
}
