/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.BarMagnetModule;
import edu.colorado.phet.faraday.FaradayConfig;

/**
 * BarMagnetControlPanel is the control panel for the "Two Coils" simulation module.
 * This control panel currently has no controls, but does contain the default PhET logo
 * graphic and Help buttons.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetControlPanel extends ControlPanel {

    public static final String FLIP_POLARITY_COMMAND = "FLIP_POLARITY_COMMAND";
    
    /**
     * Sole constructor.
     * 
     * @param module the module that this control panel is associated with.
     */
    public BarMagnetControlPanel( BarMagnetModule module ) {

        super( module );

        JPanel fillerPanel = new JPanel();
        fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
        fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );

        // WORKAROUND: ControlPanel doesn't display anything unless we give it a dummy control pane.
        this.setControlPane( fillerPanel );
        
        // Bar Magnet panel
        JPanel barMagnetPanel = new JPanel();
        {
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( SimStrings.get( "barMagnetPanel.title" ) );
            Font defaultFont = barMagnetPanel.getFont();
            Font font = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );
            border.setTitleFont( font );

            barMagnetPanel.setBorder( border );
            barMagnetPanel.setLayout( new BoxLayout( barMagnetPanel, BoxLayout.Y_AXIS ) );

            // Strength slider
            
            // Flip Polarity button
            JButton flipPolarityButton = new JButton( SimStrings.get( "flipPolarityButton.label" ) );
            flipPolarityButton.setActionCommand( FLIP_POLARITY_COMMAND );
            flipPolarityButton.addActionListener( module );
            barMagnetPanel.add( flipPolarityButton );
        }
        
        // Pickup Coil panel
        JPanel pickupCoilPanel = new JPanel();
        {
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( SimStrings.get( "pickupCoilPanel.title" ) );
            Font defaultFont = pickupCoilPanel.getFont();
            Font font = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );
            border.setTitleFont( font );

            pickupCoilPanel.setBorder( border );
            pickupCoilPanel.setLayout( new BoxLayout( pickupCoilPanel, BoxLayout.Y_AXIS ) );
        }
        
        //  Layout so that control groups fill horizontal space.
        JPanel panel = new JPanel();
        {
            BorderLayout layout = new BorderLayout();
            layout.setVgap( 20 ); // vertical space between control groups
            panel.setLayout( layout );
            panel.add( fillerPanel, BorderLayout.NORTH );
            panel.add( barMagnetPanel, BorderLayout.CENTER );
            panel.add( pickupCoilPanel, BorderLayout.SOUTH );
        }
        super.setControlPane( panel );
    }

}