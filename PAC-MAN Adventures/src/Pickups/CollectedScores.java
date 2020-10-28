/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Pickups;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.Timer;

import Game.Animator;
import Entities.Pacman;
import Engine.GameMethods;
import Game.Tile;

/*
 * A visible collected scores instance
 */
public class CollectedScores{
	
	public Queue<Score> visibleScores; // All current visible scores on board
	
	public CollectedScores() {
		this.visibleScores = new ConcurrentLinkedQueue<Score>();
	}
	
	/*
	 * Adds a score to visible score list be a collectable
	 */
	public void addScore(Collectable c)
	{
		Score score = new Score(c, c.position);
		visibleScores.add(score);
	}
	
	/*
	 * Adds a score to visible scores by pacman position and the score amount in integer
	 */
	public void addScore(int amount , Pacman pacman)
	{
		Score score = new Score(amount, pacman.getPosition());
		visibleScores.add(score);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .8f);
	    g2.setComposite(c);
		for(Score score : visibleScores)
			score.paint(g2);
		g2.dispose();
	}

	/*
	 * Private class to define individual visible score
	 */
	private class Score implements ActionListener {
		//Fields
		Collectable c; // The collectable collected to achieve the score
		Tile position; // Score appearance position
		Animator animator; // Score animator
		Timer timer; // Visible score disposal timer.
		private int amount; // Score points amount
		
		//Constructors
		public Score(Collectable c, Tile position)
		{
			this.c=c;
			this.amount = 0;
			this.position = position;
			this.animator = new Animator(null, 30, 0, false, false, true);
			timer = new Timer(500, this);
			timer.start();
		}
		
		public Score(int amount, Tile position)
		{
			this.amount = amount;
			this.position = position;
			this.animator = new Animator(null, 30, 0, false, false, true);
			timer = new Timer(1500, this);
			timer.start();
		}
		
		/*
		 * Sets the visible score font size by score amount
		 */
		public void setFontSize(Graphics g)
		{
			int score = this.c.score;
			int fontSize = (score <= 10) ? 8 : (score >= 50 & score < 100) ? 12 : (score >= 100 & score < 200) ? 14 :
							(score >= 200 & score < 300) ? 16 : (score >= 300 & score < 400) ? 18 : (score >= 400 & score <= 500) ? 20 : 22;
			g.setFont(GameMethods.font(fontSize));
		}
		
		public void paint(Graphics g)
		{
			if(amount == 0) {
				setFontSize(g);
				g.setColor(Color.BLACK);
				g.drawString(this.c.score+"", position.column*25-1, position.row*25+1+animator.dy);
				g.drawString(this.c.score+"", position.column*25+1, position.row*25+1+animator.dy);
				g.drawString(this.c.score+"", position.column*25+1, position.row*25-1+animator.dy);
				g.drawString(this.c.score+"", position.column*25-1, position.row*25-1+animator.dy);
				g.setColor((g.getFont().getSize() > 20) ? Color.ORANGE :Color.WHITE);
				g.drawString(this.c.score+"", position.column*25, position.row*25+animator.dy);
			}
			else if(amount < 0){
				g.setColor(Color.BLACK);
				g.setFont(GameMethods.font(16));
				g.drawString(this.amount+"", position.column*25-1, position.row*25+1+animator.dy);
				g.drawString(this.amount+"", position.column*25+1, position.row*25+1+animator.dy);
				g.drawString(this.amount+"", position.column*25+1, position.row*25-1+animator.dy);
				g.drawString(this.amount+"", position.column*25-1, position.row*25-1+animator.dy);
				g.setColor(Color.RED);
				g.drawString(this.amount+"", position.column*25, position.row*25+animator.dy);
			}
			else if(amount > 0){
				g.setColor(Color.BLACK);
				g.setFont(GameMethods.font(18));
				g.drawString(this.amount+"", position.column*25-1, position.row*25+1+animator.dy);
				g.drawString(this.amount+"", position.column*25+1, position.row*25+1+animator.dy);
				g.drawString(this.amount+"", position.column*25+1, position.row*25-1+animator.dy);
				g.drawString(this.amount+"", position.column*25-1, position.row*25-1+animator.dy);
				g.setColor(Color.ORANGE);
				g.drawString(this.amount+"", position.column*25, position.row*25+animator.dy);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			visibleScores.remove(this);
		}
	}



}
