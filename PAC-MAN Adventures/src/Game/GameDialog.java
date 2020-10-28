/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Game;
 
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import Engine.GameLoop;
import Engine.GameMethods;

import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;
 
/*
 * Game dialog to enter nickname input or to present game pop-up messages.
 */
public class GameDialog extends JDialog implements ActionListener, PropertyChangeListener {
	
    private String typedText = null; // The input typed.
    private boolean userInput; // Determines if this game dialog will have user input
    private boolean exitDialog; // Determines it it an exit dialog
    private boolean confirmDialog; // Determines if it is a confirm dialog
    
    //Swing components
    private JTextField textField;
    private JOptionPane optionPane;
 
    //Button strings
    private String btnString1 = "OK";
    private String btnString2 = "CANCEL";
 
    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }
 
    /** Creates the reusable dialog. */
    public GameDialog(JFrame aFrame, String message, boolean userInput, boolean exitDialog, boolean confirmDialog, Dimension size) {
        super(aFrame, true);
        this.userInput = userInput;
        this.exitDialog = exitDialog;
        this.confirmDialog = confirmDialog;
 
        this.setUndecorated(true);
        textField = new JTextField(10);
        textField.setBackground(Color.BLACK);
        textField.setForeground(Color.ORANGE);
        textField.setFont(GameMethods.font(14));
        textField.setPreferredSize(new Dimension(this.getWidth(), 40));
        textField.setDocument(new JTextFieldLimit(18));
      
        String msgString = message;
        Object[] array = new Object[(userInput) ? 2 : 1];
    	array[0] = msgString;
        if(userInput) array[1] = textField;
 
        Object[] options = new Object[(confirmDialog) ? 1 : 2];
        options[0] = btnString1;
        if(!confirmDialog) options[1] = btnString2;
 
        optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
        optionPane.setBackground(Color.ORANGE);
        this.setForeground(Color.YELLOW);
        optionPane.setFont(GameMethods.font(16));
        this.setBackground(Color.ORANGE);
        optionPane.setPreferredSize(size);
 
        setContentPane(optionPane);
 
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        if(userInput) {
	        addComponentListener(new ComponentAdapter() {
	            public void componentShown(ComponentEvent ce) {
	                textField.requestFocusInWindow();
	            }
	        });
	        textField.addActionListener(this);
        }
 
	    this.pack();
	    optionPane.addPropertyChangeListener(this);
	    this.setLocationRelativeTo(aFrame);
	    this.setAlwaysOnTop(true);
    	this.toFront();
    	this.setVisible(true);
    }
 
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }
 
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
 
        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();
 
            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                return;
            }
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            if (btnString1.equals(value)) {
            	if(confirmDialog) clearAndHide();
            	else if(userInput) {
	                typedText = textField.getText();
	                String ucText = typedText.toUpperCase();
	                if (ucText == null | ucText.equals("")) {
	                    textField.selectAll();JOptionPane.showMessageDialog(GameDialog.this, "A VALID NICKNAME MUST BE AT LEAST ONE CHARACTER.","INPUT INVALID",JOptionPane.ERROR_MESSAGE);
	                    typedText = null;
	                    textField.requestFocusInWindow();
	                }
	                else clearAndHide();
            	}
            	else {
            		if(exitDialog) System.exit(0);
            		else GameLoop.restart();
            	}
            } else {
                typedText = null;
                clearAndHide();
            }
        }
    }
 
    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        textField.setText(null);
        setVisible(false);
    }
    
    /*
     * Controls text input length limitation
     */
    private class JTextFieldLimit extends PlainDocument {
    	  private int limit;

    	  JTextFieldLimit(int limit) {
    	   super();
    	   this.limit = limit;
    	   }

    	  public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
    	    if (str == null) return;

    	    if ((getLength() + str.length()) <= limit) {
    	      super.insertString(offset, str, attr);
    	    }
    	  }
	}
}
