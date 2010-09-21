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
import javax.swing.border.EtchedBorder;
import java.awt.*;

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

    /**
     * Creates a border that is suitable for a top-level control pane in the
     * control panel
     *
     * @param title
     * @return a border
     */
    public static Border createPrimaryBorder( String title ) {
        return BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED,
                                                                                   Color.white, Color.black ),
                                                 title );
    }

    /**
     * Creates a border that is suitable for a second-level control pane in the
     * control panel
     *
     * @param title
     * @return a border
     */
    public static Border createSecondaryBorder( String title ) {
        return BorderFactory.createTitledBorder( title );
    }
}
