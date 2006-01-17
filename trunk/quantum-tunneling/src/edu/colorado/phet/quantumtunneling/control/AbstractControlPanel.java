/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.module.AbstractModule;


/**
 * AbstractControlPanel is the base class for all control panels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Default insets used in all EasyGridBagLayouts
    public static final Insets DEFAULT_INSETS = new Insets( 0, 0, 0, 0 );
    
    // Default amount of vertical space, see addVerticalSpace
    private static final int DEFAULT_VERTICAL_SPACE = 8;
    
    // Font style applied to titled borders
    protected static final int TITLED_BORDER_FONT_STYLE = Font.ITALIC;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractModule _module; // module that this control panel is associated with
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public AbstractControlPanel( AbstractModule module ) {
        super( module );
        setInsets( new Insets( 0, 3, 0, 3 ) );
        _module = module;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public AbstractModule getModule() {
        return _module;
    }
    
    //----------------------------------------------------------------------------
    // Add things to the control panel
    //----------------------------------------------------------------------------
    
    /**
     * Adds a separator to the control panel.
     */
    public void addSeparator() {
        addControlFullWidth( new JSeparator() );
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
        if ( space > 0 ) {
            JPanel spacePanel = new JPanel();
            spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
            spacePanel.add( Box.createVerticalStrut( space ) );
            addControlFullWidth( spacePanel );
        }
    }
    
    /**
     * Adds a Reset button to the control panel.
     * The button handler calls the module's reset method.
     */
    public void addResetButton() {
        JButton resetButton = new JButton( SimStrings.get( "button.reset" ) );
        resetButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent e ) {
                Frame frame = PhetApplication.instance().getPhetFrame();
                String message = SimStrings.get( "message.reset" );
                int option = DialogUtils.showConfirmDialog( frame, message );
                if ( option == JOptionPane.YES_OPTION ) {
                    setWaitCursorEnabled( true );
                    _module.reset();
                    setWaitCursorEnabled( false );
                }
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
        addControlFullWidth( fillerPanel );
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
            frame.setCursor( QTConstants.WAIT_CURSOR );
        }
        else {
            frame.setCursor( QTConstants.DEFAULT_CURSOR );
        }
    }
}
