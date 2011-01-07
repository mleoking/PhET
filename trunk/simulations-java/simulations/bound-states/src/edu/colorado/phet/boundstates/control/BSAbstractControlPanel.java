// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.Insets;

import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;


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
        setInsets( new Insets( 0, 3, 0, 3 ) ); // top, left, bottom, right
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
}
