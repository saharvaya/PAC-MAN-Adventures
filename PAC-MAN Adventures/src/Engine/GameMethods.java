/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.UIManager;

import Game.Game;

/*
 * A Class that holds static game methods that used excessively.
 */
public class GameMethods
{
	/*
	 * Prints a given score or if null the current game score in a zeros format : 00000000
	 * @param playerScore - an integer score to print in the correct format.
	 * @return the string representation by the zeros format.
	 */
	public static String getPrintedScore(Integer playerScore)
	{
		StringBuilder stringBuilder = new StringBuilder();
		String emptyScore = "00000000";
		String score = (playerScore == null) ? Game.score+"" : playerScore+"";
		stringBuilder.insert(0, score);
		int zeros = emptyScore.length()-score.length();
		for(int i=0; i<zeros ;i++)
		{
			stringBuilder.insert(0, 0);
		}
		return stringBuilder.toString();
	}
	
	/*
	 * Plays game sound by given sound file path.
	 * @param soundName - the sound path to play.
	 */
	public static synchronized void playSound(final String soundName) {
		if(Game.sound) {
		   try {
			   Clip clip = AudioSystem.getClip();
			   AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(ResourceLoader.load("sounds/"+soundName)));
			    clip.open(inputStream);
			    clip.start(); 
			    
		   } catch (Exception e) {
		     System.err.println(e.getMessage());
		   }
		}
	}
	
	/*
	 * Centers a given string and prints to the given graphics instance.
	 * @param str - the string to center.
	 * @param g - a graphics instance to print centered on.
	 */
	public static int centerString(String str, Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g.create();
		FontMetrics fm = g2d.getFontMetrics();
		return (Game.WIDTH-fm.stringWidth(str))/2;
	}
	
	/*
	 * Sets the font of written text to the specific game font.
	 * @param fontSize - the size of the font.
	 * @return A font according to given font size in the game font format.
	 */
	public static Font font(float fontSize)
	{
		return ResourceLoader.gameFont.deriveFont(Font.BOLD, fontSize);
	}
	
	/*
	 * Sets the game look and feel (Button and panel background and foregrounds).
	 */
	public static void setGameLookAndFeel()
	{
        try {
        	UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            UIManager.put("OptionPane.background", Color.BLACK);
            UIManager.put("Panel.background", Color.BLACK);
            UIManager.put("OptionPane.messageForeground", Color.YELLOW);
            UIManager.put("OptionPane.messageFont", font(14));
            UIManager.put("OptionPane.buttonFont", font(12));
            UIManager.put("Button.background", Color.BLACK);
            UIManager.put("Button.foreground", Color.YELLOW);
		} catch (Exception e) {}
	}
	
	/*
	 * Initiates a loading dialog.
	 * @return A JDialog with a loading animation.
	 */
	public static JDialog showLoading() {
	    JLabel loadingAnimation = new JLabel(new ImageIcon(GameLoop.class.getResource("/animations/loading.gif")));
	    loadingAnimation.setText("<html><font size=\"6\" face=\"Consolas\" color=\"white\"><b>Initializing...</b></font><br/><font size=\"3\" face=\"Consolas\" color=\"white\">Please wait</font></html>");
	    loadingAnimation.setHorizontalTextPosition(JLabel.CENTER);
	    loadingAnimation.setVerticalTextPosition(JLabel.BOTTOM);
		JDialog loadingFrame = new JDialog();
		loadingFrame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		loadingFrame.setUndecorated(true);
		loadingFrame.getContentPane().setBackground(Color.BLACK);
	    loadingFrame.add(loadingAnimation);
	    loadingFrame.pack();
	    loadingFrame.setLocationRelativeTo(null);
	    loadingFrame.setAlwaysOnTop(true);
	    loadingFrame.repaint();
	 
	  return loadingFrame;
    }
}
