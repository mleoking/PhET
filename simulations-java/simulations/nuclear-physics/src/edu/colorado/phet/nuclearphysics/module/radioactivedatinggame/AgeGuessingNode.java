package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class presents a dialog to the user and allows them to guess the age
 * of a datable item.
 * 
 * @author John Blanco
 */
public class AgeGuessingNode extends PNode {
	
	private static final Font TEXT_FONT = new PhetFont(18);
	private static final Color BORDER_COLOR = Color.BLACK;
	private static final int BORDER_THICKNESS = 2;
	private static final int AGE_ENTRY_FIELD_COLUMNS = 12;
	
	private ArrayList<Listener> _listeners = new ArrayList<Listener>();
	private JFormattedTextField _ageEntryField;

	/**
	 * Constructor.
	 */
	public AgeGuessingNode(DatableItem item) {
		
		// Create the panel that will contain all the components.
		VerticalLayoutPanel ageGuessingNodePanel = new VerticalLayoutPanel();
		ageGuessingNodePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICKNESS) );
		
		// Create the sub-panel that will contain the text field for entering
		// the age and the units label.
		JPanel ageEntryPanel = new JPanel();
		_ageEntryField = new JFormattedTextField( NumberFormat.getNumberInstance() );
		_ageEntryField.setColumns(AGE_ENTRY_FIELD_COLUMNS);
		_ageEntryField.setFont(TEXT_FONT);
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
		
		// Create the sub-panel that contains the button. 
		JPanel checkAgeButtonPanel = new JPanel();
		final JButton checkAgeButton = new JButton(NuclearPhysicsStrings.CHECK_AGE);
		checkAgeButton.setFont(TEXT_FONT);
		checkAgeButtonPanel.add(checkAgeButton);
		
		// Register to send the user's guess when the button is pushed.
		checkAgeButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				submitGuess();
			}
		});
		
		// Add the sub-panels to the overall panel.
        ageGuessingNodePanel.setFillNone();
        ageGuessingNodePanel.add(new TitleComponent(item.getName()));
		ageGuessingNodePanel.add(ageEntryPanel);
		ageGuessingNodePanel.add(checkAgeButtonPanel);
		
		// Wrap the whole thing in a PSwing and add it to the node.
		PSwing ageGuessingNodePanelPSwing = new PSwing(ageGuessingNodePanel);
		addChild(ageGuessingNodePanelPSwing);
	}

    public static class TitleComponent extends JLabel{
        public TitleComponent( String text ) {
            super( text );
            setFont( TEXT_FONT );
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
