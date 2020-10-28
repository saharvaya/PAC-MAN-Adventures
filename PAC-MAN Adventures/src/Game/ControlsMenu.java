/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import Engine.GameLoop;
import Engine.GameMethods;
import Entities.Ghost;

public class ControlsMenu extends JPanel {

	public final static Dimension CONTROLS_SIZE = new Dimension(Game.WIDTH,Game.GAME_SIZE.height-Game.HEIGHT-HUD.HUD_SIZE.height); // Controls menu dimensions
	private Game game; // Game instance
	//Swing components
	private JButton paceController; 
	private JButton soundButton;
	private JButton pauseButton;
	private JButton menuButton;
	private JButton exitButton;
	
	//Constructor
	public ControlsMenu(Game game) {
		this.game = game;
		
		this.setPreferredSize(CONTROLS_SIZE);
		this.setMinimumSize(CONTROLS_SIZE);
		this.setMaximumSize(CONTROLS_SIZE);
		this.setSize(CONTROLS_SIZE);
		this.setBackground(Color.BLACK);
		
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.paceController = new JButton("GAME SPEED X2");
		paceController.setBackground(Color.BLACK);
		paceController.setForeground(Color.YELLOW);
		paceController.setFont(GameMethods.font(9));
		paceController.setBorder(null);
		this.soundButton = new JButton("SOUNDS OFF");
		soundButton.setBackground(Color.BLACK);
		soundButton.setForeground(Color.YELLOW);
		soundButton.setFont(GameMethods.font(9));
		soundButton.setBorder(null);
		this.pauseButton = new JButton("PAUSE GAME");
		pauseButton.setBackground(Color.BLACK);
		pauseButton.setForeground(Color.YELLOW);
		pauseButton.setFont(GameMethods.font(9));
		pauseButton.setBorder(null);
		this.menuButton = new JButton("MAIN MENU");
		menuButton.setBackground(Color.BLACK);
		menuButton.setForeground(Color.YELLOW);
		menuButton.setFont(GameMethods.font(9));
		menuButton.setBorder(null);
		this.exitButton = new JButton("EXIT GAME");
		exitButton.setBackground(Color.BLACK);
		exitButton.setForeground(Color.YELLOW);
		exitButton.setFont(GameMethods.font(9));
		exitButton.setBorder(null);
		
		try {
			paceController.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/forward.png"))));
			soundButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/sound_on.png"))));
			pauseButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/pause.png"))));
		}
		catch (Exception e) {}
		
		this.setLayout(new GridBagLayout());      
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20,20,20,20);
		gbc.gridx = 0; gbc.gridy = 0;
		this.add(menuButton, gbc);
		gbc.gridx = 1; gbc.gridy = 0;
		this.add(paceController, gbc);
		gbc.gridx = 2; gbc.gridy = 0;
		this.add(soundButton, gbc);
		gbc.gridx = 3; gbc.gridy = 0;
		this.add(pauseButton, gbc);
		gbc.gridx = 4; gbc.gridy = 0;
		this.add(exitButton, gbc);
		
		paceController.addActionListener(new ButtonListener());
		soundButton.addActionListener(new ButtonListener());
		pauseButton.addActionListener(new ButtonListener());
		exitButton.addActionListener(new ButtonListener());
		menuButton.addActionListener(new ButtonListener());
	}
	
	/*
	 * Listens to different button clicks in the controls menu.
	 */
	private class ButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			Object comp = e.getSource();
			if(comp instanceof JButton) {
				if(((JButton) comp).getText() == "GAME SPEED X2") {
					GameMethods.playSound("button_click.wav");
					Game.paceController = 2;
					for(Ghost g : game.getLevel().getGhosts())
					{
						g.getAnimator().resetOffset();
						g.changeGameSpeed(true);
					}
					game.getPacman().getAnimator().resetOffset();
					game.getPacman().changeGameSpeed(true);
					game.getView().requestFocus();
					paceController.setText("GAME SPEED X1");
					try {
						paceController.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/backward.png"))));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else if(((JButton) comp).getText() == "GAME SPEED X1") {
					GameMethods.playSound("button_click.wav");
					Game.paceController = 1;
					for(Ghost g : game.getLevel().getGhosts())
					{
						g.getAnimator().resetOffset();
						g.changeGameSpeed(false);
					}
					game.getPacman().getAnimator().resetOffset();
					game.getPacman().changeGameSpeed(false);
					game.getView().requestFocus();
					paceController.setText("GAME SPEED X2");
					try {
						paceController.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/forward.png"))));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else if(((JButton) comp).getText() == "SOUNDS OFF") {
					GameMethods.playSound("button_click.wav");
					Game.sound = false;
					game.getView().requestFocus();
					soundButton.setText("SOUNDS ON");
					try {
						soundButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/sound_off.png"))));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else if(((JButton) comp).getText() == "SOUNDS ON") {
					GameMethods.playSound("button_click.wav");
					Game.sound = true;
					game.getView().requestFocus();
					soundButton.setText("SOUNDS OFF");
					try {
						soundButton.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/logos_icons_hud/sound_on.png"))));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else if(((JButton) comp).getText() == "PAUSE GAME") {
					GameMethods.playSound("button_click.wav");
					GameLoop.pause(true);
					game.getTimer().stop();
					game.getView().requestFocus();
					pauseButton.setText("UN-PAUSE GAME");
				}
				else if(((JButton) comp).getText() == "UN-PAUSE GAME") {
					GameMethods.playSound("button_click.wav");
					GameLoop.pause(false);
					game.getTimer().start();
					game.getView().requestFocus();
					pauseButton.setText("PAUSE GAME");
				}
				else if(((JButton) comp).getText() == "EXIT GAME") {
					GameMethods.playSound("button_click.wav");
					GameLoop.pause(true);
					game.getTimer().stop();
					game.getView().requestFocus();
					pauseButton.setText("UN-PAUSE GAME");
					game.checkForHighestScore();
					GameDialog dialog = new GameDialog(game, "ARE YOU SURE YOU WANT TO EXIT?", false, true, false, new Dimension(500, 200));
				}
				else if(((JButton) comp).getText() == "MAIN MENU") {
					GameMethods.playSound("button_click.wav");
					GameLoop.pause(true);
					game.getTimer().stop();
					game.getView().requestFocus();
					pauseButton.setText("UN-PAUSE GAME");
					game.checkForHighestScore();
					GameDialog dialog = new GameDialog(game, "ARE YOU SURE YOU WANT TO GO BACK TO MAIN MENU?", false, false, false, new Dimension(750, 200));
				}
			}
			
		}
		
	}

}
