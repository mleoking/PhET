/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module;

import java.awt.Font;
import java.awt.Insets;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.glaciers.GlaciersConstants;


/**
 * GlaciersAbstractControlPanel is the base class for all control panels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GlaciersAbstractControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    protected static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;
    protected static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GlaciersAbstractModule _module; // module that this control panel is associated with

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param module
     */
    public GlaciersAbstractControlPanel( GlaciersAbstractModule module ) {
        super();
        setInsets( new Insets( 0, 3, 0, 3 ) );
        _module = module;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the module that this control panel is part of.
     *
     * @return the module
     */
    public GlaciersAbstractModule getModule() {
        return _module;
    }
}
