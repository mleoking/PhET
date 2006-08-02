/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.util;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * ControlBorder
 * <p/>
 * Creates the type of border used for all panels in the control panel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlBorderFactory {
    private ControlBorderFactory() {
    }

    public static Border createBorder( String title ) {
        return BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title );
    }
}
