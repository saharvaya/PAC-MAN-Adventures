package Game;

/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
import java.util.ArrayList;

import Pickups.Bananas;
import Pickups.Fruit;
import Pickups.Grapes;
import Pickups.Pineapple;
import Pickups.Strawberry;

/*
 * Describes a game player score
 */
public class Score implements Comparable<Score> {
	
	public int score; // Game score
	public String name; // Player name
	public int pinapples=0 , strawberries=0, bananas=0, grapes=0; // Fruits colelcted amounts
	
	//Constructor
	public Score(String name, int score, ArrayList<Fruit> collectedFruits)
	{
		this.score = score;
		this.name = name;
		
		for(Fruit fruit : collectedFruits)
		{
			if(fruit instanceof Pineapple) pinapples++;
			else if(fruit instanceof Strawberry) strawberries++;
			else if(fruit instanceof Grapes) grapes++;
			else if(fruit instanceof Bananas) bananas++;
		}
	}
	
	public boolean equals(Score other)
	{
		return (this.score == other.score) && (this.name.equals(other.name));
	}
	
	@Override
	public int compareTo(Score other)
	{
		return this.score-other.score;
	}

	public int getScore() {
		return score;
	}
}
