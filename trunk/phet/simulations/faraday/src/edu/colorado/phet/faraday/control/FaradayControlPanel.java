/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.module.FaradayModule;


/**
 * FaradayControlPanel is the control panel for all Faraday modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Default amount of vertical space, see addVerticalSpace
    private static final int DEFAULT_VERTICAL_SPACE = 8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FaradayModule _module; // module that this control panel is associated with
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public FaradayControlPanel( FaradayModule module ) {
        super( module );
        _module = module;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "ControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );
    }
    
    //----------------------------------------------------------------------------
    // Add things to the control panel
    //----------------------------------------------------------------------------
    
    /**
     * Sets the minumum width of the control panel.
     * 
     * @param minimumWidth
     */
    public void setMinumumWidth( int minimumWidth ) {
        JPanel fillerPanel = new JPanel();
        fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
        fillerPanel.add( Box.createHorizontalStrut( minimumWidth ) );
        addControlFullWidth( fillerPanel );
    }
    
    /**
     * Adds a default amout of vertical space to the control panel,
     * as specified by VERTICAL_SPACE.
     */
    public void addVerticalSpace() {
        addVerticalSpace( DEFAULT_VERTICAL_SPACE );
    }
    
    /**
     * Adds vertical space to the control panel.
     * 
     * @param space the amount of space, in pixels
     */
    public void addVerticalSpace( int space ) {
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        addControlFullWidth( spacePanel );
    }
    
    /**
     * Adds a Reset button to the control panel.
     * The button handler calls the module's reset method.
     */
    public void addResetButton() {
        JButton resetButton = new JButton( SimStrings.get( "Reset.button" ) );
        resetButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent e ) {
                _module.reset();
            }
        } );
        addControl( resetButton );
    }
}
