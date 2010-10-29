/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.developer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;

/**
 * Dialog for use in the developer controls that allows the user to select a
 * single problem type for display to the user on the game tab.  This was
 * requested to make it easier to test users' responses to the various types
 * of problems presented in the game.
 *
 * @author John Blanco
 */
public class ProblemTypeSelectionDialog extends PaintImmediateDialog {

    public ProblemTypeSelectionDialog( Frame frame ) {
        super( frame, "Select Problem Types Allowed" );
        setPreferredSize( new Dimension(300, 400) );
        setLayout( new GridLayout(7, 1) );
        add( new JCheckBox( "Prob type 1" ) );
        add( new JCheckBox( "Prob type 2" ) );
        add( new JCheckBox( "Prob type 3" ) );
        add( new JCheckBox( "Prob type 4" ) );
        add( new JCheckBox( "Prob type 5" ) );
        add( new JCheckBox( "Prob type 6" ) );
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add( new JButton("Check All") );
        buttonPanel.add( new JButton("Clear All") );
        add( buttonPanel );
        pack();
    }
}
