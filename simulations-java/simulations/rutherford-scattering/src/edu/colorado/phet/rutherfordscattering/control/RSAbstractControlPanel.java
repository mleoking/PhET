/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.rutherfordscattering.RSResources;
import edu.colorado.phet.rutherfordscattering.module.RSAbstractModule;


/**
 * AbstractControlPanel is the base class for all control panels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class RSAbstractControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private RSAbstractModule _module; // module that this control panel is associated with

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param module
     */
    public RSAbstractControlPanel( RSAbstractModule module ) {
        super();
        int minWidth = RSResources.getInt( "int.minControlPanelWidth", 200 );
        setMinimumWidth( minWidth );
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
    public RSAbstractModule getModule() {
        return _module;
    }
}
