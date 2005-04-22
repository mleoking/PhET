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
    
    public static final int VERTICAL_SPACE = 15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FaradayModule _module;
    
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
    }
    
    //----------------------------------------------------------------------------
    // Add things to the control panel
    //----------------------------------------------------------------------------
    
    /**
     * Adds a default amout of vertical space to the control panel,
     * as specified by VERTICAL_SPACE.
     * 
     * @param space the amount of space, in pixels
     */
    public void addVerticalSpace() {
        addVerticalSpace( VERTICAL_SPACE );
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
        addFullWidth( spacePanel );
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
        add( resetButton );
    }
}
