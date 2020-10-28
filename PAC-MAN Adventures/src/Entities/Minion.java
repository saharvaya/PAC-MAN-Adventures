/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

import java.awt.Graphics;
import java.util.Random;

import Game.Animator;
import Game.Game;
import Engine.GameMethods;

/*
 * Defines a minion helper for pacman.
 */
public class Minion extends Ghost implements Visitor{

	//Fields
	protected final int SPEED = 3; // Minion speed
	private Pacman pacman; // Current pacman to help to.
	private Ghost target; // The current ghost target to chase.
	
	//Constructor
	public Minion(Game game, Pacman pacman, int col, int row) {
		super(game, col, row);
		this.pacman = pacman;
		this.stateTimer.stop();
		this.position = game.getCurrentMaze().board[col][row];
		this.animator = new Animator("/images/entity_sprites/minion.png", 50, 3, false, true, false);
		this.randomizer = new Random();
		this.speed=SPEED;
		this.target = this.game.getLevel().getGhosts().get(randomizer.nextInt(this.game.getLevel().getGhosts().size()));
		GameMethods.playSound("minion_spawn.wav");
	}

	/*
	 * Defines the movement for the minion.
	 * Chases current target.
	 */
	@Override
	public void movment()
	{
		this.chase(target.position);
		if(this.column == target.column && this.row == target.row)
			target.impact(this);
	}
	
	@Override
	public void normalSpeed() {}

	@Override
	public void paint(Graphics g) {
		g.drawImage(this.animator.sprite.getSubimage((this.animator.frame)*30, 0, 30, 25), column*25+this.animator.dx, row*25+this.animator.dy, 35, 30, null);
	}
	
	//Visitor pattern methods
	@Override
	public void hit(Ghost ghost) {
		GameMethods.playSound("eat_ghost.wav");
		ghost.state = GhostState.DIED;
		ghost.animator.resetOffset();
		int countGhostsDead = 0;
		for(Ghost g : game.getLevel().getGhosts()) {
			if(g.state != GhostState.DIED)
				this.target = g;
			else countGhostsDead++;
		}
		if(countGhostsDead == game.getLevel().getGhosts().size())
			this.pacman.disposeMinion();
	}

	@Override
	public void hit(NormalPacman pacman) {}

	@Override
	public void hit(EvilPacman pacman) {}

	@Override
	public void hit(ShieldedPacman pacman) {}
}
