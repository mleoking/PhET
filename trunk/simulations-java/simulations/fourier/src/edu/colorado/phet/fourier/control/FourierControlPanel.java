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

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.module.FourierModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

    // Default insets used in all EasyGridBagLayouts
    public static final Insets DEFAULT_INSETS = new Insets( 0, 0, 0, 0 );

    // Default amount of vertical space, see addVerticalSpace
    private static final int DEFAULT_VERTICAL_SPACE = 8;

    // Font style applied to titled borders
    protected static final int TITLED_BORDER_FONT_STYLE = Font.ITALIC;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierModule _module; // module that this control panel is associated with

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
        getContentPanel().setInsets( new Insets( 0, 3, 0, 3 ) );
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
        JButton resetButton = new JButton( FourierResources.getString( "Reset.button" ) );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setWaitCursorEnabled( true );
                _module.reset();
                setWaitCursorEnabled( false );
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
        PhetFrame frame = NonPiccoloPhetApplication.instance().getPhetFrame();
        if ( enabled ) {
            frame.setCursor( FourierConstants.WAIT_CURSOR );
        }
        else {
            frame.setCursor( FourierConstants.DEFAULT_CURSOR );
        }
    }
}
