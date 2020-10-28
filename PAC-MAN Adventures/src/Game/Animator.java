/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;
import static Entities.Mover.DOWN;
import static Entities.Mover.LEFT;
import static Entities.Mover.RIGHT;
import static Entities.Mover.UP;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Timer;

/*
 * Helps animate objects and entities in the game.
 * Helps achieve smooth movement.
 */
public class Animator implements ActionListener {

	//Fields
	public static final int FLICK_DELAY = 200; // Time in millis to switch between flickering animations
	Timer timer; // Times the animation - updates the frames.
	public BufferedImage sprite; // The sprite to animate
	public int dx; // X axis small increase
	public int dy; // Y axis small increase
	public int frame, delay; // Delay between frame switch and current animation frame
	public float opacity; // The current sprite opacity
	boolean disolvable; // Determines if the animation should disSolve
	public boolean flicker; // Determines if the animation should flicker
	public boolean flickFrame; // Determines if currently on flicker frame or normal frame.
	public boolean floatable; // Determines if the animation should float
	int frames; //The total amount of frames for the animation
	private boolean stop = false; // Determines whether to stop the animation
	
	//Constructor
	public Animator(String spritePath, int delay , int frames, boolean disolvable, boolean flickerable, boolean floatable)
	{
		frame = 0;
		dx = dy =0;
		if(spritePath != null) {
			try {
				this.sprite = ImageIO.read(getClass().getResourceAsStream(spritePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.disolvable = disolvable;
		this.flicker = flickerable;
		this.flickFrame = false;
		this.delay = delay;
		this.floatable = floatable;
		this.opacity = 1f;
		this.frames = frames;
		this.timer = new Timer(delay, this);
		timer.start();
	}


	/*
	 * Animates movement in a certain direction by increasing or decreasing its location by a small amount.
	 * @param direction - direction to move at
	 * @param speed - the incremental change in location by the speed of characters
	 */
	public void animateMovement(int direction, int speed)
	{
		switch (direction) {
			case RIGHT: dx+=speed; break;
			case LEFT: dx-=speed; break;
			case UP: dy-=speed; break;
			case DOWN: dy+=speed; break;	
		}
	}
	
	/*
	 * Stops the animation
	 * @param stop - Determines whether to stop or start animation.
	 */
	public void stopAnimation(boolean stop)
	{
		this.stop = stop;
	}
	
	/*
	 * Resets dx and dy values to center animation in tile.
	 */
	public void resetOffset()
	{
		this.dx=dy=0;
	}
	
	/*
	 * Updates the current frame while animation is running
	 */
	private void update() {
		if(!stop) {
			if (frame >= frames) {
				frame = 0;
			}
			else frame++;
		}
	}
	
	/*
	 * Decreases the opacity of current sprite to create a disolving effect
	 */
	private void disolve()
	{
		this.opacity = (opacity >= 0.13f) ? opacity-0.013f : 0.0f;
	}
	
	/*
	 * Changes current frame to flicker frame and the opposite
	 */
	private void flicker()
	{
		if (flickFrame == false) {
			flickFrame = true;
		}
		else flickFrame = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.update();
		if(disolvable) disolve();
		if(flicker) flicker();
		if(floatable) setFloat();
	}

	//Getters and Setters
	public void setAnimation(String spritePath, int delay, int frames) {
		this.frames = frames;
		this.timer.setDelay(delay);
		try {
			this.sprite = ImageIO.read(getClass().getResourceAsStream(spritePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer.restart();
	}
	
	private void setFloat() {
		this.dy-=1;
	}
	
	public BufferedImage getSprite() {
		return sprite;
	}
}
