package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	
	private static final double WIDTH = 100;
	private static final double HEIGHT = WIDTH / 2;
	private static final Font TEXT_FONT = new PhetFont(18);
	private static final Color BORDER_COLOR = Color.BLACK;
	private static final int BORDER_THICKNESS = 2;

	/**
	 * Constructor.
	 */
	public AgeGuessingNode() {
		
		// Create the panel that will contain all the components.
		JPanel ageGuessingNodePanel = new VerticalLayoutPanel();
		ageGuessingNodePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_THICKNESS) );
		
		// Create the sub-panel that will contain the text field for entering
		// the age and the units label.
		JPanel ageEntryPanel = new JPanel();
		JTextField ageEntryField = new JTextField(15);
		ageEntryField.setFont(TEXT_FONT);
		JLabel textEntryFieldLabel = new JLabel(NuclearPhysicsStrings.READOUT_UNITS_YRS);
		textEntryFieldLabel.setFont(TEXT_FONT);
		ageEntryPanel.add(ageEntryField);
		ageEntryPanel.add(textEntryFieldLabel);
		
		// Create the sub-panel that contains the button. 
		JPanel checkAgeButtonPanel = new JPanel();
		JButton checkAgeButton = new JButton(NuclearPhysicsStrings.CHECK_AGE);
		checkAgeButton.setFont(TEXT_FONT);
		checkAgeButtonPanel.add(checkAgeButton);
		
		// Add the sub-panels to the overall panel.
		ageGuessingNodePanel.add(ageEntryPanel);
		ageGuessingNodePanel.add(checkAgeButtonPanel);
		
		// Wrap the whole thing in a PSwing and add it to the node.
		PSwing ageGuessingNodePanelPSwing = new PSwing(ageGuessingNodePanel);
		addChild(ageGuessingNodePanelPSwing);
	}
}
