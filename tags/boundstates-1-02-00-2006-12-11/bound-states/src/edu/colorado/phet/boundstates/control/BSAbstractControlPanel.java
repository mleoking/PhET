/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.boundstates.util.DialogUtils;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSAbstractControlPanel is the base class for all control panels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAbstractModule _module; // module that this control panel is associated with
    private JButton _resetButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public BSAbstractControlPanel( BSAbstractModule module ) {
        super();
        setInsets( new Insets( 0, 3, 0, 3 ) );
        _module = module;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the module that this control panel is associated with.
     * 
     * @return the module
     */
    public BSAbstractModule getModule() {
        return _module;
    }
    
    //----------------------------------------------------------------------------
    // Add things to the control panel
    //----------------------------------------------------------------------------
    
    /**
     * Gets the reset button, usually for attaching a help item to it.
     * 
     * @return the reset button
     */
    public JButton getResetButton() {
        return _resetButton;
    }
    
    /**
     * Adds a Reset button to the control panel.
     * The button handler calls the module's reset method.
     */
    public void addResetButton() {
        _resetButton = new JButton( SimStrings.get( "button.reset" ) );
        _resetButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent e ) {
                Frame frame = PhetApplication.instance().getPhetFrame();
                String message = SimStrings.get( "message.reset" );
                int option = DialogUtils.showConfirmDialog( frame, message, JOptionPane.YES_NO_OPTION );
                if ( option == JOptionPane.YES_OPTION ) {
                    _module.reset();
                }
            }
        } );
        addControl( _resetButton );
    }
    
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
}
