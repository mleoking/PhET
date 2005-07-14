/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.module.FourierModule;


/**
 * FourierControlPanel is the control panel for all Fourier modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FourierControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Default amount of vertical space, see addVerticalSpace
    private static final int DEFAULT_VERTICAL_SPACE = 8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierModule _module; // module that this control panel is associated with
    private Cursor _saveCursor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public FourierControlPanel( FourierModule module ) {
        super( module );
        _module = module;
    }
    
    //----------------------------------------------------------------------------
    // reset
    //----------------------------------------------------------------------------
    
    public abstract void reset();
    
    //----------------------------------------------------------------------------
    // Add things to the control panel
    //----------------------------------------------------------------------------
    
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
        addControl( resetButton );
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
        addFullWidth( fillerPanel );
    }
    
    //----------------------------------------------------------------
    // Cursor control methods
    //----------------------------------------------------------------
    
    /**
     * Turns the wait cursor on and off.
     * 
     * @param enabled true or false
     */
    public void setWaitCursorEnabled( boolean enabled ) {
        PhetFrame frame = PhetApplication.instance().getPhetFrame();
        if ( enabled ) {
            _saveCursor = frame.getCursor();
            frame.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        }
        else if ( _saveCursor != null ) {
            frame.setCursor( _saveCursor );
        }
    }
}
