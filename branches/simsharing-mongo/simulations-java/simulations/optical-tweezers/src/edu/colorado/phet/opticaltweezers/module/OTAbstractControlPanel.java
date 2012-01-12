// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module;

import java.awt.Font;
import java.awt.Insets;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;


/**
 * OTAbstractControlPanel is the base class for all control panels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTAbstractControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    protected static final Font TITLE_FONT = OTConstants.CONTROL_PANEL_TITLE_FONT;
    protected static final Font CONTROL_FONT = OTConstants.CONTROL_PANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private OTAbstractModule _module; // module that this control panel is associated with

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param module
     */
    public OTAbstractControlPanel( OTAbstractModule module ) {
        super();
        setInsets( new Insets( 0, 3, 0, 3 ) );
        _module = module;
        
        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the module that this control panel is part of.
     *
     * @return the module
     */
    public OTAbstractModule getModule() {
        return _module;
    }
}
