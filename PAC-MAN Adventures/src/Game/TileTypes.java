/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

/*
 * Enumeration to define different tile types across board
 */
public enum TileTypes {

	WALL(0, false, false),
	PATH(1, true, true),
	COIN(2, true, true),
	DOOR(3, false,false),
	SPAWN(4, false, true),
	CORNER(5, true, true),
	PORTAL(6, true, false);
	
	private int value; // Integer value for the specific tile type
	private boolean pacmanPath; // Determines if the tile type is passable for pacman entity
	private boolean ghostPath; // Determines if the tile type is passable for ghost entity
	
	//Constructor
	private TileTypes(int value, boolean isPacmanPath, boolean isGhostPath) {
		this.value = value;
		this.pacmanPath = isPacmanPath;
		this.ghostPath = isGhostPath;
	}

	//Getters ans Setters
	public int getValue() {
		return value;
	}

	public boolean isPacmanPath() {
		return pacmanPath;
	}

	public void setPacmanPath(boolean pacmanPath) {
		this.pacmanPath = pacmanPath;
	}

	public boolean isGhostPath() {
		return ghostPath;
	}

	public void setGhostPath(boolean ghostPath) {
		this.ghostPath = ghostPath;
	}

}
