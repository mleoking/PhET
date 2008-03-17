/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;

/**
 * This class is a panel that displays the running time of the sim and also
 * presents a button that allows the user to reset the alpha decay.
 *
 * @author John Blanco
 */
public class AlphaRadiationTimerPanel extends JPanel {
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    JTextField _decayTimerReadout;
    JButton    _resetButton;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationTimerPanel() {
        
        // Add the border around the legend.
        
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysics2Resources.getString( "AlphaDecayControlPanel.ControlBorder" ),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetDefaultFont( Font.BOLD, 14 ),
                Color.BLUE );
        
        setBorder( titledBorder );

        // Set the layout type.
        setLayout( new GridBagLayout() );
        

        // Create and add the label for the timer.
        
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST,
                GridBagConstraints.NONE,
                new Insets( 5, 5, 5, 5 ), 0, 0 );

        add( new JLabel( NuclearPhysics2Resources.getString( "AlphaDecayControlPanel.RunningTimeLabel" )), gbc );
        
        // Create and add the text box that will display elapsed time.
        
        _decayTimerReadout = new JTextField();
        _decayTimerReadout.setEditable( false );
        _decayTimerReadout.setBackground( Color.white );
        _decayTimerReadout.setHorizontalAlignment( JTextField.RIGHT );
        _decayTimerReadout.setPreferredSize( new Dimension( 60, 30 ) );
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        add( _decayTimerReadout, gbc );

        // TODO: Need to hook this up to the timer.
        
        // Create and add the button for resetting the sim.
        
        _resetButton = new JButton( NuclearPhysics2Resources.getString( "AlphaDecayControlPanel.ResetButton" ) );

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 2;
        gbc.gridy = 2;
        
        add( _resetButton, gbc );
    }
}
