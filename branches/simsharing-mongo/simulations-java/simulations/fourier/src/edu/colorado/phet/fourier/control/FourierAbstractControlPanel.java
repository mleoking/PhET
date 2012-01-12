// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.module.FourierAbstractModule;


/**
 * FourierControlPanel is the control panel for all Fourier modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FourierAbstractControlPanel extends ControlPanel {

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

    private FourierAbstractModule _module; // module that this control panel is associated with

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param module
     */
    public FourierAbstractControlPanel( FourierAbstractModule module ) {
        super( module );
        getContentPanel().setInsets( new Insets( 0, 3, 0, 3 ) );
        _module = module;
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
        PhetFrame frame = PhetApplication.getInstance().getPhetFrame();
        if ( enabled ) {
            frame.setCursor( FourierConstants.WAIT_CURSOR );
        }
        else {
            frame.setCursor( FourierConstants.DEFAULT_CURSOR );
        }
    }
}
