package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class presents a dialog to the user and allows them to guess the age
 * of a datable item.
 * 
 * @author John Blanco
 */
public class AgeGuessingNode extends PNode {
	
	private static final Font TEXT_FONT = new PhetFont(18);
	private static final int BORDER_THICKNESS = 2;
	private static final int AGE_ENTRY_FIELD_COLUMNS = 12;
	private static final Color BACKGROUND_COLOR = NuclearPhysicsConstants.RADIOACTIVE_DATING_GAME_CONTROL_PANEL_COLOR;
	private static final double ENLARGEMENT_FACTOR = 0.1;
	
	private ArrayList<Listener> _listeners = new ArrayList<Listener>();
	private JFormattedTextField _ageEntryField;

	/**
	 * Constructor.
	 */
	public AgeGuessingNode(DatableItem item) {
		
		// Create the background.
		PPath background = new PPath();
		background.setStroke(new BasicStroke(BORDER_THICKNESS));
		background.setPaint(BACKGROUND_COLOR);
		addChild(background);
		
		// Add the textual label.
		PText title = new PText(item.getName());
		title.setFont(TEXT_FONT);
		background.addChild(title);
		
		// Create the sub-panel that will contain the edit box for entering
		// the age, plus the units label.
		JPanel ageEntryPanel = new JPanel();
		ageEntryPanel.setBackground(BACKGROUND_COLOR);
		_ageEntryField = new JFormattedTextField( NumberFormat.getNumberInstance() );
		_ageEntryField.setColumns(AGE_ENTRY_FIELD_COLUMNS);
		_ageEntryField.setFont(TEXT_FONT);
		_ageEntryField.setBorder(BorderFactory.createEtchedBorder());
		JLabel textEntryFieldLabel = new JLabel(NuclearPhysicsStrings.READOUT_UNITS_YRS);
		textEntryFieldLabel.setFont(TEXT_FONT);
		ageEntryPanel.add(_ageEntryField);
		ageEntryPanel.add(textEntryFieldLabel);
		
		// Add a handler to catch when the user hits the "enter" key.
		_ageEntryField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				submitGuess();
			}
		});
		
		// Wrap the age entry panel in a PSwing.
		PSwing ageEntryPSwing = new PSwing(ageEntryPanel);
		background.addChild(ageEntryPSwing);
		
		// Create the sub-panel that contains the button. 
		final JButton checkAgeButton = new JButton(NuclearPhysicsStrings.CHECK_AGE);
		checkAgeButton.setFont(TEXT_FONT);
		checkAgeButton.setBackground(BACKGROUND_COLOR);
		
		// Register to send the user's guess when the button is pushed.
		checkAgeButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				submitGuess();
			}
		});
		
		// Wrap the button in a PSwing.
		PSwing buttonPSwing = new PSwing(checkAgeButton);
		background.addChild(buttonPSwing);
		
		// Lay out the node.  NOTE TO FUTURE MAINTAINERS - This was originally
		// coded in a way that used Swing's layout, and put everything on a
		// panel, but it looked weird.  The title was cut off, the edges
		// would appear partially occluded, etc.  Hence the hand-rolled layout
		// code that follows.
		double width = Math.max(title.getFullBoundsReference().width,
				Math.max(ageEntryPSwing.getFullBoundsReference().width, buttonPSwing.getFullBoundsReference().width));
		width = width * (1 + ENLARGEMENT_FACTOR);
		double height = title.getFullBoundsReference().height + ageEntryPSwing.getFullBoundsReference().height
			+ buttonPSwing.getFullBoundsReference().height;
		double yOffset = height * (ENLARGEMENT_FACTOR / 2);
		height = height * (1 + ENLARGEMENT_FACTOR);
		background.setPathToRectangle(0, 0, (float)width, (float)height);
		title.setOffset(width / 2 - title.getFullBoundsReference().width / 2, yOffset);
		ageEntryPSwing.setOffset(width / 2 - ageEntryPSwing.getFullBoundsReference().width / 2,
				title.getFullBoundsReference().getMaxY());
		buttonPSwing.setOffset(width / 2 - buttonPSwing.getFullBoundsReference().width / 2,
				ageEntryPSwing.getFullBoundsReference().getMaxY());
	}

    public static class TitleComponent extends JPanel{
        public TitleComponent( String text ) {
        	FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
        	setLayout(layout);
        	JLabel label = new JLabel(text);
            label.setFont( TEXT_FONT );
            add(label);
        }
    }
	
	public boolean requestFocus(){
		return _ageEntryField.requestFocusInWindow();
	}
	
	public void addListener(Listener listener) {
	    if ( !_listeners.contains( listener )){
	        _listeners.add( listener );
	    }
	}
	
	/**
	 * Clean up any memory references that this node may have to other
	 * objects.  This is generally done to avoid memory leaks.
	 */
	public void removeListener(Listener listener){
		
		if (_listeners.remove(listener)){
			System.err.println(getClass().getName() + "- Warning: attempt to remove unregistered listener.");
		}
	}
	
	/**
	 * Submit the age guess by interpreting the value and notifying any
	 * listeners.
	 */
	private void submitGuess(){
		
		double ageGuessInYears;
		
		// Interpret the data in the text field.
		if (_ageEntryField.getValue() != null){
			
			ageGuessInYears = ((Number)_ageEntryField.getValue()).doubleValue();
		}
		else{
			ageGuessInYears = Double.NaN;
		}
		
		// Let listeners know that the guess was submitted.
		notifyGuessSubmitted(ageGuessInYears);
	}
	
    /**
     * Notify all listeners that the user has submitted a guess.
     */
    protected void notifyGuessSubmitted(double ageGuess){
        
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).guessSubmitted(ageGuess);
        }
    }
    


	/**
	 * Listener interface.
	 */
	static public interface Listener {
		
		/**
		 * Inform listeners that the user has submitted a guess of the age.
		 * 
		 * @param ageGuess - Age in years.  This uses years instead of
		 * milliseconds (which is the convention in the other portions of
		 * the simulation) because we were having rounding errors.
		 */
		public void guessSubmitted(double ageGuess);
	}
}
