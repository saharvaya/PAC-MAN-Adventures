/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.Timer;

import Engine.GameMethods;
import Entities.Pacman.PacmanState;
import Entities.Ghost;
import Entities.Ghost.GhostState;

/*
 * Main game timer to control effects, ghosts release and renewal of game collectables.
 */
public class GameTimer extends Timer {

	//Fields
	private final static int delay = 1000; // One second
	private static Game game; // Running game
	// Controls the pacman dead to regeneration timer 
	private Timer pacmanDeadDelay; 
	private DeadDelayListener pacmanDeadListener;
	
	private static boolean initialGeneration; //Determines if it initial board generation time.
	private static boolean startBeep; //Determines if to play start beep sound.
	
	public static int clockSeconds=0, clockMinutes=0, clockHours=0; // HUD clock counters
	public static boolean godMode = false; // Determines if game god mode is on
	
	//Counter for different time intervals
	private static int effectTimer=0; 
	public static int levelRelativeScore;
	private static int ghostVulnerableTimer=0;
	private static int initialTimer;
	private static int pillGenerationTimer;
	private static int summonerGenerationTimer;
	static int startCounter=4;
	
	//Constant timer interval to renew collectable items
	private final static int FRUIT_INITIAL_GENERATION_TIME = 10;
	private final static int FRUIT_DISSOLVING_TIME = 5;
	private final static int FRUIT_GENERATION_TIME = 7;
	private final static int SUMMONER_GENERATION_TIME = 35+Game.difficulty;
	private final static int SUMMONER_DISPOSAL_TIME = 8;
	private final static int PILL_GENERATION_TIME = 20*((Game.difficulty==0) ? 1 : Game.difficulty);
	private final static int DOOR_OPENING_TIME = 3;
	
	//Constant timer interval to contgrol effects
	private final static int GHOST_VULNERABLE_TIME = 8;
	private final static int FREEZE_EFFECT_TIME = 2;
	private final static int DIZZY_EFFECT_TIME = 5;
	
	//Constructor
	public GameTimer(Game game)
	{
		super(delay, new TimerListener());
		this.pacmanDeadListener = new DeadDelayListener();
		initialGeneration = true;
		startBeep = true;
		GameTimer.game = game;
		GameTimer.initialTimer=0;
		GameTimer.pillGenerationTimer=0;
		GameTimer.levelRelativeScore=0;
		pacmanDeadDelay = new Timer(3000, pacmanDeadListener);
		pacmanDeadListener.setTimer(pacmanDeadDelay);
	}
	
	/*
	 * Controls initial maze generation operations,
	 * initial fruit generation time and the opening of the ghosts cage.
	 */
	private static void initialGenerations() {
		
		boolean finished = false;
		if(initialTimer == DOOR_OPENING_TIME) {
			GameMethods.playSound("ghost_door_open.wav");
			game.getLevel().openCloseDoor(false);
			game.getLevel().getGhosts().get(0).setState(GhostState.SCATTER);
		}
		if(initialTimer == FRUIT_INITIAL_GENERATION_TIME)
		{
			game.getLevel().generateFruits();
			game.getLevel().fruitsAvailable = true;
			finished = true;
		}
		
		if(finished) {initialGeneration = false; initialTimer = 0; }
	}
	
	/*
	 * Controls the generation and regeneration of fruits board.
	 */
	private static void generateFruits()
	{
		if(!game.getLevel().fruitsAvailable && initialTimer % FRUIT_GENERATION_TIME == 0)
		{
			initialTimer = 0;
			game.getLevel().generateFruits();
			game.getLevel().fruitsAvailable = true;
		}
		else if(game.getLevel().fruitsAvailable & !game.getLevel().fruitsVisible())
		{
			initialTimer = 0;
			game.getLevel().removeFruits();
			game.getLevel().fruitsAvailable = false;
		}
		else if(game.getLevel().fruitsAvailable && initialTimer % FRUIT_DISSOLVING_TIME == 0) {
			initialTimer = 0;
			game.getLevel().disolveFruits();
		}
	}
	
	public void startPacmanDeadDelay() {
		pacmanDeadDelay.start();
	}
	
	/*
	 * Restarts the game timer - used to switch level.
	 */
	public void restartTimer()
	{
		initialGeneration = true;
		startBeep = true;
		startCounter = 4;
		effectTimer=0;
		pillGenerationTimer=0;
		summonerGenerationTimer=0;
		ghostVulnerableTimer=0;
		initialTimer = 0;
		this.restart();
	}
	
	/*
	 * Resets all timer values - used to start a new game.
	 */
	public void resetTimers()
	{
		initialGeneration = true;
		startBeep = true;
		startCounter = 4;
		effectTimer=0;
		pillGenerationTimer=0;
		levelRelativeScore=0;
		summonerGenerationTimer=0;
		ghostVulnerableTimer=0;
		initialTimer = 0;
		clockSeconds =0; clockMinutes=0; clockHours=0;
	}
	
	/*
	 * Updates HUD clock
	 */
	private static void updateClock() {
		clockSeconds ++;
		if(clockSeconds == 60){
			clockSeconds = 0;
			clockMinutes ++;
			if(clockMinutes == 60){
				clockMinutes = 0;
				clockHours ++;
			}
		}
	}
	
	/*
	 * Starts game god-mode
	 */
	public static void startGodMode() {
		godMode = true;
		ghostVulnerableTimer = 0;
	}
	
	/*
	 * A listener to update all different counters every second
	 */
	private static class TimerListener implements ActionListener{
	public void actionPerformed(ActionEvent e) {
			Object comp = e.getSource();
			if(comp instanceof Timer)
			{
				if(startCounter == 1) {
					updateClock();
					if(startBeep) {
						GameMethods.playSound("start_beep.wav");
						startBeep = false;
					}
					Game.state = Game.GameState.PLAY;
					initialTimer++;
					ghostsReleaseManager();
					if(godMode) manageVulnerableGhosts();
					manageEffects();
					if(initialGeneration) initialGenerations();
					else {
						generateFruits();
						generateEnergyPills();
						if(game.getPacman().getMinion() == null) {
							if(game.getLevel().getSummoner() == null) generateSummoner();
							else if (game.getLevel().getSummoner() != null) disposeSummoner();
						}
					}
				}
				else {
					GameMethods.playSound("countdown_beep.wav");
					startCounter--;
				}
			}
		}

	/*
	 * Controls the generation and regeneration of energy pills on board.
	 */
	private void generateEnergyPills() {
		if((game.getLevel().getMaze().getEnergyPills().size() == 0) && (pillGenerationTimer != 0 & pillGenerationTimer % PILL_GENERATION_TIME == 0))
		{
			pillGenerationTimer = 0;
			game.getLevel().generatePills();
		}
		else if(game.getLevel().getMaze().getEnergyPills().size() == 0) pillGenerationTimer++;
	}
	
	/*
	 * Controls the generation and regeneration of summoner on board.
	 */
	private void generateSummoner() {
		if(summonerGenerationTimer != 0 & summonerGenerationTimer  % SUMMONER_GENERATION_TIME == 0)
		{
			summonerGenerationTimer = 0;
			game.getLevel().generateSummoner();
		}
		else summonerGenerationTimer++;
	}
	
	/*
	 * Controls the disposal of summoner on board.
	 */
	private void disposeSummoner()
	{
		if(summonerGenerationTimer != 0 & summonerGenerationTimer  % SUMMONER_DISPOSAL_TIME == 0)
		{
			summonerGenerationTimer = 0;
			game.getLevel().disposeSummoner(false);
		}
		else summonerGenerationTimer++;
	}
	
	/*
	 * Manages ghosts release by relative level game score.
	 */
	public static void ghostsReleaseManager() {
		int ghostsCount = game.getLevel().getGhosts().size();
		if(Game.score-levelRelativeScore >= 1000-Game.difficulty*15 & ghostsCount > 1 && game.getLevel().getGhosts().get(1).getState() == GhostState.CAGED) // Release second ghost - gained 1000 points in level
			game.getLevel().getGhosts().get(1).setState(GhostState.SCATTER);
		if(Game.score-levelRelativeScore >= 2500-Game.difficulty*30 & ghostsCount > 2 && game.getLevel().getGhosts().get(2).getState() == GhostState.CAGED) // Release third ghost - gained 2500 points in level
			game.getLevel().getGhosts().get(2).setState(GhostState.SCATTER);
		if(Game.score-levelRelativeScore >= 4000-Game.difficulty*60 & ghostsCount > 3 && game.getLevel().getGhosts().get(3).getState() == GhostState.CAGED) // Release fourth ghost - gained 4000 points in level
			game.getLevel().getGhosts().get(3).setState(GhostState.SCATTER);
	}
	
	/*
	 * Manages the vulnerable ghosts state effect time.
	 */
	public void manageVulnerableGhosts()
	{
		if(ghostVulnerableTimer >= GHOST_VULNERABLE_TIME) {
			for(Ghost g : game.getLevel().getGhosts()) {
				if(g.getState() == GhostState.FLEE) {
					g.getAnimator().resetOffset();
					g.normalSpeed();
					g.setState(GhostState.CHASE);
				}
			}
			game.getPacman().setStatus("NORMAL");
			ghostVulnerableTimer = 0;
			godMode = false;
		}
		else ghostVulnerableTimer++;
	}
	
	/*
	 * Manages different pacman effects timings
	 */
	public static void manageEffects()
	{
		if(game.getPacman().getState() == PacmanState.FREEZE & effectTimer >= FREEZE_EFFECT_TIME) {
			game.getPacman().setState(PacmanState.NORMAL);
			game.getPacman().setStatus("NORMAL");
			effectTimer=0;
		}
		else if (game.getPacman().getState() == PacmanState.FREEZE) effectTimer++;
		else if(game.getPacman().getState() == PacmanState.DIZZY & effectTimer >= DIZZY_EFFECT_TIME) {
			game.getPacman().setState(PacmanState.NORMAL);
			game.getPacman().setStatus("NORMAL");
			effectTimer=0;
		}
		else if (game.getPacman().getState() == PacmanState.DIZZY) effectTimer++;
		}
	}
	
	/*
	 * Private class to control the delay between pacman died and regeneration.
	 */
	private static class DeadDelayListener implements ActionListener{

		private Timer timer;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			GameMethods.playSound("pacman_regenerate.wav");
				game.setPacman(game.getPacman().getCollectedFruits());
				game.getPacman().setCurrentDirection(((new Random()).nextInt(2) == 0) ? KeyEvent.VK_LEFT : KeyEvent.VK_RIGHT);
				this.timer.restart();
				this.timer.stop();
		}
		
		public void setTimer(Timer timer)
		{
			this.timer=timer;
		}
	}
}
