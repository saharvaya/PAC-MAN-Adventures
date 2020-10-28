/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.util.ArrayList;
import java.util.List;

/*
 * Defines a game tile, the game board is built by tiles and they defines the current board position properties
 */
public class Tile{
	
	//Fields
	public static final int TILE_SIZE = 25; //Tile size in pixels (width and height)
	public TileTypes type; // Tile type
	public int row, column; // The tile row and column
	public List<Tile> neighbours; // Neighboring tiles - to be used in BFS
	public int x, y; // x and y position in pixels
	public Tile pathParent;	 // The tile parent in a specific path - Used in calculation of BFS
	
	//Constructor
	public Tile(TileTypes type, int column, int row){
		//super(column*25, row*25, TILE_SIZE, TILE_SIZE);
		this.type = type;
		this.row = row;
		this.column = column;
		this.x = column* Tile.TILE_SIZE;
		this.y = row* Tile.TILE_SIZE;
	}
	
	public boolean equals(Tile other)
	{
		return this.column==other.column && this.row==other.row;
	}
	
	public void setNeighbours(Maze maze)
	{
		this.neighbours = getNeighbours(maze);
	}
	
	/*
	 * Gets all tile neighboring tiles
	 * @param maze - the relevant maze instance
	 * @return a list of neighboring tiles.
	 */
	private ArrayList<Tile> getNeighbours(Maze maze)
	{
		ArrayList<Tile> neighbours = new ArrayList<Tile>();
		if(this.column < 31)
			neighbours.add(maze.board[column+1][row]);
		if(this.column > 0)
			neighbours.add(maze.board[column-1][row]);
		if(this.row < 31)
			neighbours.add(maze.board[column][row+1]);
		if(this.row > 0)
			neighbours.add(maze.board[column][row-1]);
	
		return neighbours;
	}

	@Override
	public boolean equals(Object obj) {
		
		return this.equals((Tile)obj);
	}

	@Override
	public int hashCode() {
		return this.column*25-row*10+this.row*25*column*5;
	}

	@Override
	public String toString()
	{
		return " "+type.getValue()+" ";
	}

	public void setType(TileTypes type) {
		this.type = type;
	}

}
