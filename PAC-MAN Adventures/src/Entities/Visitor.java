/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Entities;

/*
 * An interface to implement visitor pattern between different entities.
 */
public interface Visitor {

	public void hit(NormalPacman pacman);
	public void hit(EvilPacman pacman);
	public void hit(ShieldedPacman pacman);
	public void hit(Ghost ghost);
}
