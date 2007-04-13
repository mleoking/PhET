/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view.util;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * ViewUtils
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ViewUtils {

    /**
     * Add an etched, titled border to a JPanel
     *
     * @param panel
     * @param title
     */
    public static void setBorder( JPanel panel, String title ) {
        EtchedBorder etchedBorder = new EtchedBorder();
        panel.setBorder( BorderFactory.createTitledBorder( etchedBorder, title ) );
    }
}
